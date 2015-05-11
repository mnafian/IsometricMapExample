package com.niffy.AndEngineLockStepEngine.threads.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Bundle;
import android.os.Message;

import com.niffy.AndEngineLockStepEngine.exceptions.ClientDoesNotExist;
import com.niffy.AndEngineLockStepEngine.flags.ITCFlags;
import com.niffy.AndEngineLockStepEngine.misc.IHandlerMessage;
import com.niffy.AndEngineLockStepEngine.misc.WeakThreadHandler;
import com.niffy.AndEngineLockStepEngine.options.IBaseOptions;
import com.niffy.AndEngineLockStepEngine.threads.CommunicationHandler;

public class ServerSelector extends BaseSelectorThread implements IServerSelector {
    // ===========================================================
    // Constants
    // ===========================================================
    private final Logger log = LoggerFactory.getLogger(ServerSelector.class);

    // ===========================================================
    // Fields
    // ===========================================================
    protected ServerSocketChannel mTCPChannel;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * @see {@link BaseSelectorThread#BaseSelectorThread(String, java.net.InetAddress, WeakThreadHandler, IBaseOptions, int)}
     */
    public ServerSelector(final String pName, final InetAddress pAddress,
                          WeakThreadHandler<IHandlerMessage> pCaller, final IBaseOptions pOptions) throws IOException {
        super(pName, pAddress, pCaller, pOptions);
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    @Override
    public void run() {
        log.debug("Running TCP Selector Thread");
        this.mRunning.set(true);
        Message msg = this.mCallerThreadHandler.obtainMessage();
        msg.what = ITCFlags.TCP_SERVER_SELECTOR_START;
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
                        if (key.isAcceptable()) {
                            this.accept(key);
                        } else if (key.isReadable()) {
                            this.read(key);
                        } else if (key.isWritable()) {
                            this.write(key);
                        }
                    } catch (IOException e) {
                        log.error("IOException on key operation", e);
                    } catch (ClientDoesNotExist e) {
                        log.error("Client does not exist!");
                    }
                }
            } catch (Exception e) {
                log.error("Exception in main loop", e);
            }
        }
    }

    @Override
    protected Selector initSelector() throws IOException {
        Selector found = super.initSelector();

        // Create a new non-blocking server socket channel
        this.mTCPChannel = ServerSocketChannel.open();
        this.mTCPChannel.configureBlocking(false);

        // Bind the server socket to the specified address and port
        //this.mTCPChannel.socket().bind(this.mAddress);

        // Register the server socket channel, indicating an interest in
        // accepting new connections
        this.mTCPChannel.register(found, SelectionKey.OP_ACCEPT);

        return found;
    }

    /**
     * This will send a message to the {@link CommunicationHandler} to inform of
     * a new client using {@link ITCFlags#NEW_CLIENT_CONNECTED}
     *
     * @see com.niffy.AndEngineLockStepEngine.threads.nio.BaseSelectorThread#accept(java.nio.channels.SelectionKey)
     */
    @Override
    protected void accept(SelectionKey pKey) throws IOException {
        log.debug("accept");
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) pKey.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        Socket socket = socketChannel.socket();
        socketChannel.configureBlocking(false);
        socketChannel.register(this.mSelector, SelectionKey.OP_READ);
        Connection con = new Connection((InetSocketAddress) socket.getRemoteSocketAddress(), socketChannel);
        this.mChannelMap.put(con.getAddress().getAddress(), con);
        pKey.attach(con);
        Message msg = this.mCallerThreadHandler.obtainMessage();
        msg.what = ITCFlags.NEW_CLIENT_CONNECTED;
        Bundle data = new Bundle();
        final String pIP = con.getAddress().getAddress().getHostAddress();
        data.putString("ip", pIP);
        msg.setData(data);
        this.mCallerThreadHandler.sendMessage(msg);
    }

    @Override
    protected void read(SelectionKey pKey) throws IOException, ClientDoesNotExist {
        SocketChannel socketChannel;
        InetSocketAddress address;
        String connectionIP;
        Connection con = (Connection) pKey.attachment();
        if (con != null) {
            socketChannel = con.getSocketChannel();
            address = con.getAddress();
            connectionIP = address.getAddress().getHostAddress();
        } else {
            socketChannel = (SocketChannel) pKey.channel();
            address = (InetSocketAddress) socketChannel.socket().getRemoteSocketAddress();
            connectionIP = address.getAddress().getHostAddress();
            log.warn("Could not get Connection attachment for IP: {}", connectionIP);
        }
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
        msg.what = ITCFlags.TCP_CLIENT_INCOMMING;
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
        /*
		 * TODO do we need to write on a server selector?
		 */
        SocketChannel socketChannel;
        String connectionIP;
        InetSocketAddress address;
        Connection con = (Connection) pKey.attachment();
        if (con != null) {
            socketChannel = con.getSocketChannel();
            connectionIP = con.getAddress().getAddress().getHostAddress();
            address = con.getAddress();
        } else {
            socketChannel = (SocketChannel) pKey.channel();
            address = (InetSocketAddress) socketChannel.socket().getRemoteSocketAddress();
            connectionIP = address.getAddress().getHostAddress();
            log.warn("Could not get Connection attachment for IP: {}", connectionIP);
        }

        synchronized (this.mPendingData) {
            ArrayList<ByteBuffer> queue = this.mPendingData.get(address);

            // Write until there's not more data ...
            while (!queue.isEmpty()) {
                ByteBuffer buf = (ByteBuffer) queue.get(0);
                socketChannel.write(buf);
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
            case ChangeRequest.REMOVECLIENT:
                try {
                    this.handleConnectionShutdown(pChangeRequest.mChannel.keyFor(this.mSelector), pChangeRequest.mChannel,
                            pChangeRequest.mAddress);
                } catch (IOException e) {
                    log.error("Could not shut downconnection.", e);
                } catch (ClientDoesNotExist e) {
                    log.error("Could not shut downconnection.", e);
                }
                break;
        }
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
