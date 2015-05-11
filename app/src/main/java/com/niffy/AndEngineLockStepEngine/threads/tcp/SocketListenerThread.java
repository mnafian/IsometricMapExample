package com.niffy.AndEngineLockStepEngine.threads.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Looper;
import android.os.Message;

import com.niffy.AndEngineLockStepEngine.flags.ITCFlags;
import com.niffy.AndEngineLockStepEngine.misc.IHandlerMessage;
import com.niffy.AndEngineLockStepEngine.misc.WeakThreadHandler;
import com.niffy.AndEngineLockStepEngine.options.IBaseOptions;
import com.niffy.AndEngineLockStepEngine.threads.BaseCommunicationThread;

public class SocketListenerThread extends BaseCommunicationThread {
    // ===========================================================
    // Constants
    // ===========================================================
    private final Logger log = LoggerFactory.getLogger(SocketListenerThread.class);

    // ===========================================================
    // Fields
    // ===========================================================
    protected TCPCommunicationThread mParent;
    protected ServerSocket mServerTCPSocket;

    // ===========================================================
    // Constructors
    // ===========================================================

    public SocketListenerThread(final String pName, final InetAddress pAddress,
                                WeakThreadHandler<IHandlerMessage> pCaller, final IBaseOptions pOptions,
                                final TCPCommunicationThread pTCPThread) {
        super(pName, pAddress, pCaller, pOptions);
        this.mParent = pTCPThread;
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    @Override
    public void run() {
        Looper.prepare();
        this.mRunning.set(true);
        this.mHandler = new WeakThreadHandler<IHandlerMessage>(this, Looper.myLooper());
        Message msg = this.mCallerThreadHandler.obtainMessage();
        msg.what = ITCFlags.TCP_LISTENER_THREAD_START;
        this.mCallerThreadHandler.sendMessage(msg);

        try {
            this.mServerTCPSocket = new ServerSocket(this.mBaseOptions.getTCPPort(), 5);
            log.debug("Server Socket Created");
        } catch (IOException e) {
            log.error("Server TCP socket IOExcetion", e);
        }
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_DEFAULT);
        while (!Thread.interrupted() && this.mRunning.get() && !this.mTerminated.get()) {
            try {
                log.debug("Accepting socket..");
                final Socket client = this.mServerTCPSocket.accept();
                if (client.isConnected()) {
                    InetAddress pClientAddress = client.getInetAddress();
                    BaseSocketThread clientThread = new BaseSocketThread(this.getHandler(), client,
                            pClientAddress.toString(), Looper.getMainLooper());
                    clientThread.start();
                    this.mParent.addNewSocketClient(pClientAddress, clientThread);
                }
            } catch (IOException e) {
                log.error("Error with accepting on TCP socket", e);
            }
        }
        Looper.loop();
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    @Override
    public void handlePassedMessage(Message pMessage) {

    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
