package com.niffy.AndEngineLockStepEngine.threads.udp;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Bundle;
import android.os.Looper;
import android.os.Message;

import com.niffy.AndEngineLockStepEngine.flags.ErrorCodes;
import com.niffy.AndEngineLockStepEngine.flags.ITCFlags;
import com.niffy.AndEngineLockStepEngine.messages.IMessage;
import com.niffy.AndEngineLockStepEngine.misc.IHandlerMessage;
import com.niffy.AndEngineLockStepEngine.misc.WeakThreadHandler;
import com.niffy.AndEngineLockStepEngine.options.IBaseOptions;
import com.niffy.AndEngineLockStepEngine.threads.CommunicationThread;

public class UDPCommunicationThread extends CommunicationThread {
    // ===========================================================
    // Constants
    // ===========================================================
    private final Logger log = LoggerFactory.getLogger(UDPCommunicationThread.class);

    // ===========================================================
    // Fields
    // ===========================================================
    protected int mBufferSize = 512;
    protected DatagramSocket mSocket = null;

    // ===========================================================
    // Constructors
    // ===========================================================

    public UDPCommunicationThread(final InetAddress pAddress, WeakThreadHandler<IHandlerMessage> pCaller, final IBaseOptions pOptions)
            throws SocketException {
        super("UDPThread", pAddress, pCaller, pOptions);
        this.mBufferSize = this.mBaseOptions.getNetworkBufferSize();
        try {
            this.mSocket = new DatagramSocket(this.mBaseOptions.getUDPPort());
        } catch (SocketException e) {
            log.error("Socket exception on creating datagram socket", e);
            throw e;
        }
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public void run() {
        Looper.prepare();
        this.mRunning.set(true);
        this.mHandler = new WeakThreadHandler<IHandlerMessage>(this, Looper.myLooper());

        if (!this.mSentRunningMessage) {
            Message msg = this.mCallerThreadHandler.obtainMessage();
            msg.what = ITCFlags.UDP_THREAD_START;
            this.mCallerThreadHandler.sendMessage(msg);
        }
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_DEFAULT);
        while (!Thread.interrupted() && this.mRunning.get() && !this.mTerminated.get()) {
            String pAddress = null;
            byte[] pData = null;
            try {
                byte[] buf = new byte[this.mBufferSize];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                this.mSocket.receive(packet);
                pAddress = packet.getAddress().toString();
                pData = packet.getData();

                //TODO do we need to have streams here?
                //final ByteArrayInputStream bInput = new ByteArrayInputStream(packet.getData());
                //final DataInputStream dInput = new DataInputStream(bInput);

                this.mPacketHandler.reconstructData(packet.getAddress().toString(), packet.getData());
            } catch (IOException e) {
                log.error("Error reading in UDP data", e);
                this.networkMessageFailure(pAddress, pData, ITCFlags.NETWORK_RECIEVE_FAILURE,
                        ErrorCodes.COULD_NOT_RECEIVE);
            }
        }
        Looper.loop();
    }

    @SuppressWarnings("unused")
    @Override
    public void handlePassedMessage(Message pMessage) {
        log.debug("Handling message: {}", pMessage.what);
        super.handlePassedMessage(pMessage);
        Bundle bundle;
        String ip;
        byte[] data;
        switch (pMessage.what) {
            case ITCFlags.CLIENT_CONNECTED:
                bundle = pMessage.getData();
                ip = bundle.getString("ip");
                this.clientJoin(ip);
                break;
            case ITCFlags.CLIENT_DISCONNECTED:
                bundle = pMessage.getData();
                ip = bundle.getString("ip");
                this.clientDisconect(ip);
                break;
        }
    }

    public <T extends IMessage> int sendMessage(InetAddress pAddress, T pMessage) {
        while (!Thread.interrupted() && this.mRunning.get() && !this.mTerminated.get()) {
            byte[] buf = new byte[this.mBufferSize];
            final ByteArrayOutputStream bOutput = new ByteArrayOutputStream();
            final DataOutputStream dOutput = new DataOutputStream(bOutput);
            try {
                pMessage.write(dOutput);
                dOutput.flush();
                buf = bOutput.toByteArray();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, pAddress, this.mBaseOptions.getUDPPort());
                this.mSocket.send(packet);
            } catch (IOException e) {
                log.error("Could not send packet", e);
                this.networkMessageFailure(pAddress.toString(), buf, ITCFlags.NETWORK_SEND_MESSAGE_FAILURE,
                        ErrorCodes.COULD_NOT_SEND);
            }
        }
        return 0;
    }

    @Override
    protected void producePoolItems() {
        super.producePoolItems();
    }

    @Override
    protected void sendMessageWithPacketHandler(final int pIntended, byte[] pAddress, byte[] pData) {
        super.sendMessageWithPacketHandler(pIntended, pAddress, pData);
    }

    @Override
    protected void sendMessageWithPacketHandler(final int pIntended, byte[] pData) {
        super.sendMessageWithPacketHandler(pIntended, pData);
    }

    @Override
    public void terminate() {
        if (!this.mTerminated.getAndSet(true)) {
            this.mRunning.getAndSet(false);
            this.mSocket.close();
        }
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================
    protected void clientJoin(final String pAddress) {
        try {
            InetAddress addressCast = InetAddress.getByName(pAddress);
            this.addClient(addressCast);
        } catch (UnknownHostException e) {
            log.error("Could not join client as could not cast address: {}", pAddress, e);
        }
    }

    protected void clientDisconect(final String pAddress) {
        try {
            InetAddress addressCast = InetAddress.getByName(pAddress);
            this.removeClient(addressCast);
        } catch (UnknownHostException e) {
            log.error("Could not disconnect client as could not cast address: {}", pAddress, e);
        }
    }
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
