package com.niffy.AndEngineLockStepEngine.threads.nio;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connection {
    // ===========================================================
    // Constants
    // ===========================================================
    @SuppressWarnings("unused")
    private final Logger log = LoggerFactory.getLogger(Connection.class);

    // ===========================================================
    // Fields
    // ===========================================================
    protected InetSocketAddress mAddress;
    protected SocketChannel mSocketChannel;

    // ===========================================================
    // Constructors
    // ===========================================================

    public Connection(final InetSocketAddress pAddress, final SocketChannel pSocketChannel) {
        this.mAddress = pAddress;
        this.mSocketChannel = pSocketChannel;
    }

    public InetSocketAddress getAddress() {
        return mAddress;
    }

    public void setAddress(InetSocketAddress pAddress) {
        this.mAddress = pAddress;
    }

    public SocketChannel getSocketChannel() {
        return mSocketChannel;
    }

    public void setSocketChannel(SocketChannel pSocketChannel) {
        this.mSocketChannel = pSocketChannel;
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

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
