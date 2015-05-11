package com.niffy.AndEngineLockStepEngine.threads.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.niffy.AndEngineLockStepEngine.flags.ITCFlags;
import com.niffy.AndEngineLockStepEngine.misc.IHandlerMessage;
import com.niffy.AndEngineLockStepEngine.misc.WeakThreadHandler;

public class BaseSocketThread extends HandlerThread implements IBaseSocketThread, IHandlerMessage {
    protected final AtomicBoolean mRunning = new AtomicBoolean(false);
    protected final AtomicBoolean mTerminated = new AtomicBoolean(false);
    // ===========================================================
    // Constants
    // ===========================================================
    private final Logger log = LoggerFactory.getLogger(BaseSocketThread.class);
    // ===========================================================
    // Fields
    // ===========================================================
    protected WeakThreadHandler<IHandlerMessage> mParentHandler;
    protected Socket mSocket;
    protected WeakThreadHandler<IHandlerMessage> mHandler;
    protected String mName;
    protected Looper mLooper;

    // ===========================================================
    // Constructors
    // ===========================================================

    public BaseSocketThread(final WeakThreadHandler<IHandlerMessage> pParentThreadHandler, final Socket pSocket,
                            final String pName, final Looper pLooper) {
        super(pName);
        this.mSocket = pSocket;
        this.mParentHandler = pParentThreadHandler;
        if (pName != null) {
            this.mName = pName;
            this.setName(pName);
        } else {
            this.setName(this.getClass().getName());
        }
        this.mLooper = pLooper;
        // this.mHandler = new WeakThreadHandler<IHandlerMessage>(this,
        // this.mLooper.getMainLooper());
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public WeakThreadHandler<IHandlerMessage> getHandler() {
        return mHandler;
    }

    @Override
    public void run() {
        Looper.prepare();
        this.mHandler = new WeakThreadHandler<IHandlerMessage>(this, Looper.myLooper());

        log.debug("{} socket thread running", this.mName);
        this.mRunning.set(true);
        while (!Thread.interrupted() && this.mRunning.get() && !this.mTerminated.get()) {
            try {
                InputStream in = this.mSocket.getInputStream();
                DataInputStream dis = new DataInputStream(in);
                final byte[] data = this.readBytes(dis);
                if (data != null) {
                    this.postToMainThread(data);
                }
            } catch (IOException e) {
                log.error("Could not read in bytes from socket", e);
                this.postErrorToTCPThread();
                this.interrupt();
            }
        }

        Looper.loop();
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================
    protected byte[] readBytes(final DataInputStream pDis) throws IOException {
        int len = pDis.readInt();
        byte[] data = new byte[len];
        if (len > 0) {
            pDis.readFully(data);
            return data;
        } else {
            return null;
        }
    }

    protected void postToMainThread(final byte[] pData) {
        // String rawData = Arrays.toString(pData);
        // log.debug("{} postToMainThread Raw Data: {}", this.mName, rawData);
        Bundle bundle = new Bundle();
        bundle.putString("ip", this.mSocket.getInetAddress().getHostAddress());
        bundle.putByteArray("data", pData);
        Message msg = this.mParentHandler.obtainMessage();
        msg.setData(bundle);
        msg.what = ITCFlags.TCP_CLIENT_INCOMMING;
        this.mParentHandler.sendMessage(msg);
    }

    protected void send(final byte[] pData) throws IOException {
        final OutputStream pOut = this.mSocket.getOutputStream();
        final DataOutputStream pDos = new DataOutputStream(pOut);
        pDos.writeInt(pData.length);
        pDos.write(pData);
        pDos.flush();
        pOut.flush();
        // String rawData = Arrays.toString(pData);
        // log.debug("{} send Raw Data: {}", this.mName, rawData);
    }

    @Override
    public void handlePassedMessage(Message pMessage) {
        log.debug("{} Handling client socket message from parent", this.mName);
        Bundle bundle;
        switch (pMessage.what) {
            case ITCFlags.TCP_CLIENT_OUTGOING:
                bundle = pMessage.getData();
                try {
                    this.send(bundle.getByteArray("data"));
                } catch (IOException e) {
                    log.error("Could not send data to client", e);
                }
                break;
            case ITCFlags.NETWORK_TCP_SHUTDOWN_SOCKET:
                this.shutdownThread();
                break;
        }
    }

    protected void postErrorToTCPThread() {
        Message msg = this.mParentHandler.obtainMessage();
        msg.what = ITCFlags.NETWORK_TCP_EXCEPTION;
        Bundle bundle = new Bundle();
        bundle.putString("ip", this.mSocket.getInetAddress().getHostAddress());
        this.mParentHandler.sendMessage(msg);
    }

    protected void shutdownThread() {
        try {
            if (!this.mTerminated.getAndSet(true)) {
                this.mRunning.getAndSet(false);
                this.mSocket.close();
                this.interrupt();
            }
        } catch (IOException e) {
            log.error("Error shutting down socket thread", e);
        }
    }
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
