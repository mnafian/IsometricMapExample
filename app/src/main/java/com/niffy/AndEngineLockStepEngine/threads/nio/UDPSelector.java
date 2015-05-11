package com.niffy.AndEngineLockStepEngine.threads.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Bundle;
import android.os.Message;

import com.niffy.AndEngineLockStepEngine.exceptions.ClientDoesNotExist;
import com.niffy.AndEngineLockStepEngine.exceptions.ClientPendingClosure;
import com.niffy.AndEngineLockStepEngine.exceptions.NotConnectedToClient;
import com.niffy.AndEngineLockStepEngine.flags.ITCFlags;
import com.niffy.AndEngineLockStepEngine.misc.IHandlerMessage;
import com.niffy.AndEngineLockStepEngine.misc.WeakThreadHandler;
import com.niffy.AndEngineLockStepEngine.options.IBaseOptions;

public class UDPSelector extends BaseSelectorThread implements IClientSelector {
    // ===========================================================
    // Constants
    // ===========================================================
    private final Logger log = LoggerFactory.getLogger(UDPSelector.class);

    // ===========================================================
    // Fields
    // ===========================================================
    protected DatagramChannel mUDP;
    protected int mUDPPort;
    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * @see {@link BaseSelectorThread#BaseSelectorThread(String, InetAddress, WeakThreadHandler, IBaseOptions, int)}
     */
    public UDPSelector(final String pName, final InetAddress pAddress,
                       WeakThreadHandler<IHandlerMessage> pCaller, final IBaseOptions pOptions) throws IOException {
        super(pName, pAddress, pCaller, pOptions);
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces BaseSelectorThread
    // ===========================================================
    @Override
    public void run() {
        log.debug("Running UDP Selector Thread");
        this.mRunning.set(true);
        Message msg = this.mCallerThreadHandler.obtainMessage();
        msg.what = ITCFlags.UDP_CLIENT_SELECTOR_START;
        this.mCallerThreadHandler.sendMessage(msg);
        while (true) {
            try {
                // Process any pending changes
                synchronized (this.mPendingChanges) {
                    Iterator<ChangeRequest> changes = this.mPendingChanges.iterator();
                    while (changes.hasNext()) {
                        ChangeRequest change = (ChangeRequest) changes.next();
                        this.handleChangeRequest(change);
                    }
                    this.mPendingChanges.clear();
                }

                // Wait for an event one of the registered channels
                this.mSelector.select();

                // Iterate over the set of keys for which events are available
                Iterator<SelectionKey> selectedKeys = this.mSelector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    SelectionKey key = (SelectionKey) selectedKeys.next();
                    selectedKeys.remove();

                    if (!key.isValid()) {
                        continue;
                    }

                    // Check what event is available and deal with it
                    try {
                        if (key.isReadable()) {
                            this.read(key);
                        } else if (key.isWritable()) {
                            this.write(key);
                        }
                    } catch (IOException e) {
                        log.error("IOException on key operation", e);
                    }
                }
            } catch (Exception e) {
                log.error("Exception in main loop", e);
            }
        }
    }

    @Override
    protected Selector initSelector() throws IOException {
        Selector socketSelector = super.initSelector();
        this.mUDP = DatagramChannel.open();
        this.mUDP.configureBlocking(false);
        //this.mUDP.socket().bind(this.mAddress);
        this.mUDP.register(socketSelector, SelectionKey.OP_READ);
        //this.mUDPPort = this.mAddress.getPort();
        return socketSelector;
    }

    @Override
    protected void read(SelectionKey pKey) throws IOException, ClientDoesNotExist {
        DatagramChannel socketChannel;
        InetSocketAddress address;
        String connectionIP;
        socketChannel = (DatagramChannel) pKey.channel();
        address = (InetSocketAddress) socketChannel.receive(this.readBuffer);
        connectionIP = address.getAddress().getHostAddress();
        this.readBuffer.clear();

        // Attempt to read off the channel
        int numRead = -1;
        try {
            numRead = socketChannel.read(this.readBuffer);
        } catch (AsynchronousCloseException e) {
            log.error("AsynchronousCloseException", e);
            this.handleConnectionFailure(pKey, socketChannel, address.getAddress());
        } catch (NotYetConnectedException e) {
            log.error("NotYetConnectedException", e);
            this.handleConnectionFailure(pKey, socketChannel, address.getAddress());
        } catch (ClosedChannelException e) {
            log.error("ClosedChannelException", e);
            this.handleConnectionFailure(pKey, socketChannel, address.getAddress());
            this.removeClient(address.getAddress());
        } catch (IOException e) {
            log.error("IOException", e);
            // The remote forcibly closed the connection, cancel
            // the selection key and close the channel.
            this.handleConnectionFailure(pKey, socketChannel, address.getAddress());
            return;
        }

        if (numRead == -1) {
            // Remote entity shut the socket down cleanly. Do the
            // same from our end and cancel the channel.
            /* TODO check that closing socketChannel is ok,
             * we were doing pKey.channel().close()
			 */
            this.handleConnectionShutdown(pKey, socketChannel, address.getAddress());
            return;
        }

        byte[] dataIn = new byte[numRead];
        System.arraycopy(this.readBuffer.array(), 0, dataIn, 0, numRead);

        Message msg = this.mCallerThreadHandler.obtainMessage();
        msg.what = ITCFlags.UDP_INCOMMING;
        Bundle data = new Bundle();
        data.putString("ip", connectionIP);
        data.putByteArray("data", dataIn);
        msg.setData(data);
        this.mCallerThreadHandler.sendMessage(msg);
    }

    /**
     * @throws IOException           due to {@link SocketChannel#write(ByteBuffer)} call
     * @throws CancelledKeyException
     * @see com.niffy.AndEngineLockStepEngine.threads.nio.BaseSelectorThread#write(java.nio.channels.SelectionKey)
     */
    @Override
    protected void write(SelectionKey pKey) throws IOException, CancelledKeyException {
        DatagramChannel socketChannel;
        String connectionIP;
        socketChannel = (DatagramChannel) pKey.channel();
        InetSocketAddress address = (InetSocketAddress) socketChannel.socket().getRemoteSocketAddress();
        connectionIP = address.getAddress().getHostAddress();
        InetSocketAddress target = (InetSocketAddress) pKey.attachment();

        synchronized (this.mPendingData) {
            ArrayList<ByteBuffer> queue = this.mPendingData.get(target.getAddress());

            // Write until there's not more data ...
            while (!queue.isEmpty()) {
                ByteBuffer buf = (ByteBuffer) queue.get(0);
                socketChannel.send(buf, target);
                if (buf.remaining() > 0) {
                    // ... or the socket's buffer fills up
                    break;
                }
                queue.remove(0);
            }

            if (queue.isEmpty()) {
                // We wrote away all data, so we're no longer interested
                // in writing on this socket. Switch back to waiting for
                // data.
                pKey.interestOps(SelectionKey.OP_READ);
            }
        }
    }

    @Override
    protected void handleChangeRequest(ChangeRequest pChangeRequest) {
        switch (pChangeRequest.mType) {
            case ChangeRequest.CHANGEOPS:
                SelectionKey key = pChangeRequest.mChannel.keyFor(this.mSelector);
                if (key == null) {
                    log.error("Could not change channel operations for. Null key {} ", pChangeRequest.mChannel.toString());
                } else {
                    try {
                        key.interestOps(pChangeRequest.mOps);
                    } catch (IllegalArgumentException e) {
                        log.error("IllegalArgumentException", e);
                    /* TODO handle this, clean up pending data and pending changes?
					 * And remove from any collections
					 */
                    } catch (CancelledKeyException e) {
                        log.error("CancelledKeyException", e);
					/* TODO handle this, clean up pending data and pending changes?
					 * And remove from any collections
					 */
                    }
                }
                break;
            case ChangeRequest.REGISTER:
                try {
                    pChangeRequest.mChannel.register(this.mSelector, pChangeRequest.mOps);
                } catch (ClosedChannelException e) {
                    log.error("ClosedChannelException", e);
				/* TODO handle this, clean up pending data and pending changes?
				 * And remove from any collections
				 */
                } catch (CancelledKeyException e) {
                    log.error("CancelledKeyException", e);
				/* TODO handle this, clean up pending data and pending changes?
				 * And remove from any collections
				 */
                }
                break;
        }
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces IClientSelector
    // ===========================================================

    @Override
    public void send(InetAddress pAddress, byte[] pData) throws NotConnectedToClient, ClientDoesNotExist,
            ClientPendingClosure, IOException {
        InetSocketAddress fullAddress = new InetSocketAddress(pAddress, this.mUDPPort);
        DatagramChannel channel = this.initiateConnection(fullAddress);
        this.sendMessage(channel, pData, fullAddress);
    }

    /**
     * Does nothing for UDP
     *
     * @see com.niffy.AndEngineLockStepEngine.threads.nio.IClientSelector#connectTo(java.net.InetSocketAddress)
     */
    @Override
    public void connectTo(InetSocketAddress pAddress) throws IOException {
        //this.initiateConnection(pAddress);
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================
    protected DatagramChannel initiateConnection(final InetSocketAddress pAddress) throws IOException {
        DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);

        synchronized (this.mPendingChanges) {
            this.mPendingChanges.add(new ChangeRequest(channel, ChangeRequest.REGISTER, SelectionKey.OP_READ, pAddress
                    .getAddress(), pAddress));
        }
        return channel;
    }

    public void sendMessage(DatagramChannel pSocket, byte[] data, InetSocketAddress addr) {
        synchronized (this.mPendingData) {
            ArrayList<ByteBuffer> queue = this.mPendingData.get(addr.getAddress());
            if (queue == null) {
                queue = new ArrayList<ByteBuffer>();
                this.mPendingData.put(addr.getAddress(), queue);
            }
            queue.add(ByteBuffer.wrap(data));
        }

        this.mSelector.wakeup();
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
