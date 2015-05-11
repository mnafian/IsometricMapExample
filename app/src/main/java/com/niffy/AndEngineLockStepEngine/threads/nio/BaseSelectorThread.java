package com.niffy.AndEngineLockStepEngine.threads.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Bundle;
import android.os.Message;

import com.niffy.AndEngineLockStepEngine.exceptions.ClientDoesNotExist;
import com.niffy.AndEngineLockStepEngine.flags.ITCFlags;
import com.niffy.AndEngineLockStepEngine.misc.IHandlerMessage;
import com.niffy.AndEngineLockStepEngine.misc.WeakThreadHandler;
import com.niffy.AndEngineLockStepEngine.options.IBaseOptions;
import com.niffy.AndEngineLockStepEngine.threads.BaseCommunicationThread;

/**
 * Heavily based on James Greefield <a
 * href=""http://rox-xmlrpc.sourceforge.net/niotut/> ROX Java NIO Tutorial</a>
 *
 * @author Paul Robinson
 * @author <a href="mailto:nio@flat502.com">James Greenfield</a>
 * @since 11 May 2013 15:09:48
 * <p/>
 * see href=""http://rox-xmlrpc.sourceforge.net/niotut/> ROX Java NIO
 * Tutorial</a>
 */
public abstract class BaseSelectorThread extends BaseCommunicationThread implements ISelectorThread {
    protected static final int DefaultBufferCapacity = 8192;
    // ===========================================================
    // Constants
    // ===========================================================
    private final Logger log = LoggerFactory.getLogger(BaseSelectorThread.class);
    // ===========================================================
    // Fields
    // ===========================================================
    protected Selector mSelector;
    protected int mBufferCapacity = 8192;
    protected ByteBuffer readBuffer = ByteBuffer.allocate(8192);
    protected List<ChangeRequest> mPendingChanges = new LinkedList<ChangeRequest>();
    protected Map<InetAddress, ArrayList<ByteBuffer>> mPendingData = new HashMap<InetAddress, ArrayList<ByteBuffer>>();
    protected HashMap<InetAddress, Connection> mChannelMap = new HashMap<InetAddress, Connection>();
    /**
     * Any {@link InetAddress} in here is pending a closure, so do not add
     * anymore requests to send.
     */
    protected ArrayList<InetAddress> mPendingClosure = new ArrayList<InetAddress>();

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * Uses the default buffer capacity of {@link #DefaultBufferCapacity}
     *
     * @param pName    name of thread
     * @param pAddress {@link InetSocketAddress} of client.
     * @param pCaller  {@link WeakThreadHandler} to pass messages to.
     * @param pOptions {@link IBaseOptions} to use
     * @throws IOException when calling {@link #initSelector()}
     */
    public BaseSelectorThread(final String pName, final InetAddress pAddress,
                              WeakThreadHandler<IHandlerMessage> pCaller, final IBaseOptions pOptions) throws IOException {
        this(pName, pAddress, pCaller, pOptions, DefaultBufferCapacity);
    }

    /**
     * @param pName           name of thread
     * @param pAddress        {@link InetSocketAddress} of client.
     * @param pCaller         {@link WeakThreadHandler} to pass messages to.
     * @param pOptions        {@link IBaseOptions} to use
     * @param pBufferCapacity What size should the buffer capacity to read and write.
     * @throws IOException when calling {@link #initSelector()}
     */
    public BaseSelectorThread(final String pName, final InetAddress pAddress,
                              WeakThreadHandler<IHandlerMessage> pCaller, final IBaseOptions pOptions, final int pBufferCapacity)
            throws IOException {
        super(pName, pAddress, pCaller, pOptions);
        this.mAddress = pAddress;
        this.mBufferCapacity = pBufferCapacity;
        ByteBuffer.allocate(this.mBufferCapacity);
        this.mSelector = this.initSelector();
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces IBaseThread
    // ===========================================================

    @Override
    public void terminate() {
        /*
         * TODO how should we terminate the thread?
		 * Need some structured way of closing connections.
		 * Perhaps inform, then close, and check in error exceptions?
		 */
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces ISelectorThread
    // ===========================================================
    @Override
    public void removeClient(InetAddress pAddress) {
        SocketChannel channel = null;

        synchronized (this.mPendingClosure) {
            this.mPendingClosure.add(pAddress);
        }

        synchronized (this.mChannelMap) {
            if (this.mChannelMap.containsKey(pAddress)) {
                Connection con = this.mChannelMap.get(pAddress);
                channel = con.getSocketChannel();
            }
        }

        if (channel != null) {
            synchronized (this.mPendingChanges) {
                this.mPendingChanges.add(new ChangeRequest(channel, ChangeRequest.REMOVECLIENT,
                        SelectionKey.OP_CONNECT, pAddress, null));
            }
        }
    }

    @Override
    public boolean containsClient(InetAddress pAddress) {
        synchronized (this.mChannelMap) {
            return this.mChannelMap.containsKey(pAddress);
        }
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    /**
     * This creates a new {@link Selector} to use. All concrete classes should
     * call <code>super.initSelector()</code>
     *
     * @return {@link Selector} to be used
     * @throws IOException when calling {@link SelectorProvider#openSelector()}
     */
    protected Selector initSelector() throws IOException {
        Selector socketSelector = SelectorProvider.provider().openSelector();
        return socketSelector;
    }

    protected void finishConnection(SelectionKey pKey) throws IOException {

    }

    protected void accept(SelectionKey pKey) throws IOException {

    }

    protected void read(SelectionKey pKey) throws IOException, ClientDoesNotExist {

    }

    /**
     * Write to a channel
     *
     * @param pKey {@link SelectionKey} which needs an operation performing on
     * @throws IOException
     * @throws CancelledKeyException when a call to {@link SelectionKey} which has been cancelled.
     */
    protected void write(SelectionKey pKey) throws IOException, CancelledKeyException {

    }

    /**
     * @param pKey     {@link SelectionKey} to cancel
     * @param pChannel {@link AbstractSelectableChannel} to close
     * @throws IOException when {@link AbstractSelectableChannel#close()} is called
     */
    protected void handleConnectionFailure(SelectionKey pKey, AbstractSelectableChannel pChannel,
                                           final InetAddress pAddress) throws IOException {
        log.warn("A connection failure has occured. : {}", pAddress);
        log.warn("Cancel key: {}", pKey.toString());
        pKey.cancel();
        log.warn("Closing channel: {}", pChannel.toString());
        pChannel.close();
        Message msg = this.mCallerThreadHandler.obtainMessage();
        msg.what = ITCFlags.CLIENT_ERROR;
        Bundle data = new Bundle();
        data.putString("ip", pAddress.toString());
        msg.setData(data);
        this.mCallerThreadHandler.sendMessage(msg);
    }

    protected void handleConnectionShutdown(SelectionKey pKey, AbstractSelectableChannel pChannel,
                                            final InetAddress pAddress) throws IOException, ClientDoesNotExist {
        log.warn("Shuting down connection cleanly: {} ", pAddress);
        pChannel.close();
        pKey.cancel();
        synchronized (this.mChannelMap) {
            if (this.mChannelMap.containsKey(pAddress)) {
                this.mChannelMap.remove(pAddress);
            } else {
                final String pMessage = "Went to shut down channel and key cleanly for: " + pAddress.toString()
                        + " but not in channel map";
                throw new ClientDoesNotExist(pMessage);
            }
        }
        Message msg = this.mCallerThreadHandler.obtainMessage();
        msg.what = ITCFlags.CLIENT_DISCONNECTED;
        Bundle data = new Bundle();
        data.putString("ip", pAddress.toString());
        msg.setData(data);
        this.mCallerThreadHandler.sendMessage(msg);
    }

    protected void handleChangeRequest(final ChangeRequest pChangeRequest) {

    }

    protected void createQueue(final Connection pConnection) {
        synchronized (this.mPendingData) {
            ArrayList<ByteBuffer> queue = this.mPendingData.get(pConnection.getAddress());
            if (queue == null) {
                queue = new ArrayList<ByteBuffer>();
                this.mPendingData.put(pConnection.getAddress().getAddress(), queue);
            }
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
