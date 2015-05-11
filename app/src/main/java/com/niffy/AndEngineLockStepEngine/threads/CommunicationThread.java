package com.niffy.AndEngineLockStepEngine.threads;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Bundle;
import android.os.Message;

import com.niffy.AndEngineLockStepEngine.flags.ErrorCodes;
import com.niffy.AndEngineLockStepEngine.flags.ITCFlags;
import com.niffy.AndEngineLockStepEngine.flags.IntendedFlag;
import com.niffy.AndEngineLockStepEngine.flags.MessageFlag;
import com.niffy.AndEngineLockStepEngine.messages.IMessage;
import com.niffy.AndEngineLockStepEngine.messages.MessageAck;
import com.niffy.AndEngineLockStepEngine.messages.MessageAckMulti;
import com.niffy.AndEngineLockStepEngine.messages.MessageClientDisconnect;
import com.niffy.AndEngineLockStepEngine.messages.MessageClientJoin;
import com.niffy.AndEngineLockStepEngine.messages.MessageEncapsulated;
import com.niffy.AndEngineLockStepEngine.messages.MessageError;
import com.niffy.AndEngineLockStepEngine.messages.MessageMigrate;
import com.niffy.AndEngineLockStepEngine.messages.MessageOutOfSyncWith;
import com.niffy.AndEngineLockStepEngine.messages.MessagePing;
import com.niffy.AndEngineLockStepEngine.messages.MessagePingAck;
import com.niffy.AndEngineLockStepEngine.messages.MessagePingHighest;
import com.niffy.AndEngineLockStepEngine.messages.pool.MessagePool;
import com.niffy.AndEngineLockStepEngine.messages.pool.MessagePoolTags;
import com.niffy.AndEngineLockStepEngine.misc.IHandlerMessage;
import com.niffy.AndEngineLockStepEngine.misc.WeakThreadHandler;
import com.niffy.AndEngineLockStepEngine.options.IBaseOptions;
import com.niffy.AndEngineLockStepEngine.packet.IPacketHandler;
import com.niffy.AndEngineLockStepEngine.packet.PacketHandler;

public abstract class CommunicationThread extends BaseCommunicationThread implements ICommunicationThread {
    // ===========================================================
    // Constants
    // ===========================================================
    private final Logger log = LoggerFactory.getLogger(CommunicationThread.class);

    // ===========================================================
    // Fields
    // ===========================================================
    protected IPacketHandler mPacketHandler;
    protected ArrayList<InetAddress> mClients;
    protected MessagePool<IMessage> mMessagePool;
    protected boolean mListenerThreadRunning = false;
    protected boolean mSentRunningMessage = false;

    // ===========================================================
    // Constructors
    // ===========================================================
    public CommunicationThread(final String pName, final InetAddress pAddress,
                               WeakThreadHandler<IHandlerMessage> pCaller, final IBaseOptions pOptions) {
        super(pName, pAddress, pCaller, pOptions);
        this.mClients = new ArrayList<InetAddress>();
        this.mPacketHandler = new PacketHandler(this, this.mBaseOptions);
        this.mMessagePool = new MessagePool<IMessage>();
        this.producePoolItems();
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    @Override
    public void handlePassedMessage(Message pMessage) {
        Bundle bundle;
        String ip;
        byte[] data;
        int intended;
        byte[] TCP = new byte[0];
        switch (pMessage.what) {
            case ITCFlags.SEND_MESSAGE:
                bundle = pMessage.getData();
                ip = bundle.getString("ip");
                intended = bundle.getInt("intended", -1);
                data = bundle.getByteArray("data");
                this.sendMessageWithPacketHandler(intended, ip, data, TCP);
                break;
            case ITCFlags.LOCKSTEP_INCREMENT:
                bundle = pMessage.getData();
                final int pStep = bundle.getInt("step");
                this.mPacketHandler.lockstepIncrement(pStep);
                break;
            case ITCFlags.CONNECT_TO:
                bundle = pMessage.getData();
                final String pAddress = bundle.getString("ip");
                this.connect(pAddress);
                break;
            case ITCFlags.TCP_CLIENT_INCOMMING:
                bundle = pMessage.getData();
                ip = bundle.getString("ip");
                data = bundle.getByteArray("data");
                this.mPacketHandler.reconstructData(ip, data);
                break;
            case ITCFlags.UDP_INCOMMING:
                bundle = pMessage.getData();
                ip = bundle.getString("ip");
                data = bundle.getByteArray("data");
                this.mPacketHandler.reconstructData(ip, data);
                break;
        }
    }

    @Override
    public void windowNotEmpty(InetAddress pAddress) {
        if (this.mCallerThreadHandler != null) {
            Message pMessage = this.mCallerThreadHandler.obtainMessage();
            pMessage.what = ITCFlags.NETWORK_ERROR;
            Bundle bundle = new Bundle();
            bundle.putString("ip", pAddress.toString());
            bundle.putInt("error", ErrorCodes.CLIENT_WINDOW_NOT_EMPTY);
            pMessage.setData(bundle);
            this.mCallerThreadHandler.sendMessage(pMessage);
            this.sendErrorMessage(ErrorCodes.CLIENT_WINDOW_NOT_EMPTY, IntendedFlag.NETWORK);
        }
    }

    @Override
    public void addClient(InetAddress pAddress) {
        this.mClients.add(pAddress);
        this.mPacketHandler.addClient(pAddress);
    }

    @Override
    public ArrayList<InetAddress> getClients() {
        return this.mClients;
    }

    @Override
    public void removeClient(InetAddress pAddress) {
        this.mClients.remove(pAddress);
        this.mPacketHandler.removeClient(pAddress);
    }

    /**
     * Leave implementation to the subclasses
     *
     * @return <code>0</code> as the {@link IPacketHandler} should have
     * generated this
     * @see com.niffy.AndEngineLockStepEngine.packet.ISendMessage sendMessage(java.net.InetAddress,
     * com.niffy.AndEngineLockStepEngine.messages.IMessage)
     */
    @Override
    public <T extends IMessage> int sendMessage(InetAddress pAddress, T pMessage, boolean pTCP) {
        return 0;
    }

    @Override
    public IMessage obtainMessage(int pFlag) {
        return this.mMessagePool.obtainMessage(pFlag);
    }

    @Override
    public <T extends IMessage> void recycleMessage(T pMessage) {
        this.mMessagePool.recycleMessage(pMessage);
    }

    @Override
    public void handleErrorMessage(InetAddress pAddress, MessageError pMessage) {
        final int pErrorCode = pMessage.getErrorCode();
        if (pErrorCode == ErrorCodes.CLIENT_WINDOW_NOT_EMPTY) {
            Message msg = this.mCallerThreadHandler.obtainMessage();
            msg.what = ITCFlags.CLIENT_WINDOW_NOT_EMPTY;
            Bundle bundle = new Bundle();
            bundle.putString("ip", pAddress.toString());
            msg.setData(bundle);
            this.mCallerThreadHandler.sendMessage(msg);
            this.sendOutOfSyncMessage(pAddress);
        }
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    /**
     * Send message to a client.
     *
     * @param pIntended {@link Integer} Who the message is intended for.
     * @param pAddress  {@link InetAddress} as {@link String} or <code>null</code> if
     *                  to everyone
     * @param pData     {@link Byte} array of the message to be encapsulated.
     * @param pTCP      {@link Boolean} <code>true</code> to send by TCP
     *                  <code>false</code> by UDP
     */
    protected void sendMessageWithPacketHandler(final int pIntended, final String pAddress, final byte[] pData,
                                                final byte[] pTCP) {
        if (pAddress == null) {
            this.sendMessageWithPacketHandler(pIntended, pData, pTCP);
        } else {
            try {
                InetAddress pAddressCast = InetAddress.getByName(pAddress);
                this.sendMessageWithPacketHandler(pIntended, pData);
            } catch (UnknownHostException e) {
                log.error("Could not cast: {} to an InetAddress", pAddress, e);
                this.networkMessageFailure(pAddress, pData, ITCFlags.NETWORK_SEND_MESSAGE_FAILURE,
                        ErrorCodes.COULD_NOT_CAST_INETADDRESS);
            }
        }
    }

    /**
     * Send to someone specific.
     *
     * @param pIntended {@link Integer} Who the message is intended for. If
     *                  <code>-1</code> then {@link MessageFlag LOCKSTEP_CLIENT} is
     *                  used
     * @param pData     {@link Byte} array of the message to be encapsulated.
     */
    protected void sendMessageWithPacketHandler(final int pIntended, final byte[] pData) {
        MessageEncapsulated pMessage = (MessageEncapsulated) this.obtainMessage(MessageFlag.ENCAPSULATED);
        pMessage.setData(pData);
        boolean pTCP = true;
        if (pTCP) {
            pMessage.setRequireAck(false);
        } else {
            pMessage.setRequireAck(true);
        }
        if (pIntended != -1) {
            pMessage.setIntended(pIntended);
        } else {
            pMessage.setIntended(IntendedFlag.LOCKSTEP_CLIENT);
        }
        InetAddress pAddress = null;
        this.mPacketHandler.sendMessage(pAddress, pMessage, pTCP);
    }

    /**
     * Send to all
     *
     * @param pIntended {@link Integer} Who the message is intended for. If
     * @param pData     {@link Byte} array of the message to be encapsulated.
     * @param pTCP
     */
    protected void sendMessageWithPacketHandler(final int pIntended, final byte[] pData, final byte[] pTCP) {
        final int pClientCount = this.mClients.size();
        for (int i = 0; i < pClientCount; i++) {
            this.sendMessageWithPacketHandler(pIntended, pData);
        }
    }

    protected void sendErrorMessage(final int pError, final int pIntended) {
        MessageError pMessage = (MessageError) this.obtainMessage(MessageFlag.ERROR);
        pMessage.setErrorCode(pError);
        pMessage.setRequireAck(true);
        pMessage.setIntended(pIntended);
        final int pClientCount = this.mClients.size();
        for (int i = 0; i < pClientCount; i++) {
            this.mPacketHandler.sendMessage(this.mClients.get(i), pMessage, true); /* Error so send over tcp?*/
        }
        this.recycleMessage(pMessage);
    }

    protected void networkMessageFailure(final String pAddress, final byte[] pData, final int pITCFlag,
                                         final int pErrorCode) {
        Message msg = this.mCallerThreadHandler.obtainMessage();
        msg.what = pITCFlag;
        Bundle bundle = new Bundle();
        bundle.putString("ip", pAddress);
        bundle.putInt("error", pErrorCode);
        bundle.putByteArray("data", pData);
        msg.setData(bundle);
        this.mCallerThreadHandler.sendMessage(msg);
    }

    protected void sendOutOfSyncMessage(final InetAddress pAddress) {
        MessageOutOfSyncWith pMessage = (MessageOutOfSyncWith) this.obtainMessage(MessageFlag.CLIENT_OUT_OF_SYNC);
        pMessage.setRequireAck(true);
        pMessage.setIntended(IntendedFlag.LOCKSTEP);
        pMessage.setWhoIsOutOfSync(pAddress.toString());
        pMessage.setSender(this.mAddress.toString());
        final int pClientCount = this.mClients.size();
        for (int i = 0; i < pClientCount; i++) {
            this.mPacketHandler.sendMessage(this.mClients.get(i), pMessage, true); /* send over TCP*/
        }
        this.recycleMessage(pMessage);
    }

    protected void connect(final String pAddress) {
        /* Leave implementation to subclass, well TCP*/
    }

    /**
     * Produce the pool items required
     */
    protected void producePoolItems() {
        Integer pGetIntialSize = this.mBaseOptions.getPoolProperties(MessagePoolTags.ACK_INITIAL_STRING);
        Integer pGetGrowth = this.mBaseOptions.getPoolProperties(MessagePoolTags.ACK_GROWTH_STRING);
        int pInitialSize = (pGetIntialSize != -1) ? pGetIntialSize : MessagePoolTags.ACK_INITIAL_INT;
        int pGrowth = (pGetGrowth != -1) ? pGetGrowth : MessagePoolTags.ACK_INITIAL_INT;
        int pFlag = MessageFlag.ACK;
        Class<? extends IMessage> pMessageClass = MessageAck.class;
        this.mMessagePool.registerMessage(pFlag, pMessageClass, pInitialSize, pGrowth);

        pGetIntialSize = this.mBaseOptions.getPoolProperties(MessagePoolTags.ACK_INITIAL_STRING);
        pGetGrowth = this.mBaseOptions.getPoolProperties(MessagePoolTags.ACK_GROWTH_STRING);
        pInitialSize = (pGetIntialSize != -1) ? pGetIntialSize : MessagePoolTags.ACK_INITIAL_INT;
        pGrowth = (pGetGrowth != -1) ? pGetGrowth : MessagePoolTags.ACK_INITIAL_INT;
        pFlag = MessageFlag.ACK_MULTI;
        pMessageClass = MessageAckMulti.class;
        this.mMessagePool.registerMessage(pFlag, pMessageClass, pInitialSize, pGrowth);

        pGetIntialSize = this.mBaseOptions.getPoolProperties(MessagePoolTags.ERROR_INITIAL_STRING);
        pGetGrowth = this.mBaseOptions.getPoolProperties(MessagePoolTags.ERROR_GROWTH_STRING);
        pInitialSize = (pGetIntialSize != -1) ? pGetIntialSize : MessagePoolTags.ERROR_INITIAL_INT;
        pGrowth = (pGetGrowth != -1) ? pGetGrowth : MessagePoolTags.ERROR_INITIAL_INT;
        pFlag = MessageFlag.ERROR;
        pMessageClass = MessageError.class;
        this.mMessagePool.registerMessage(pFlag, pMessageClass, pInitialSize, pGrowth);

        pGetIntialSize = this.mBaseOptions.getPoolProperties(MessagePoolTags.PING_INITIAL_STRING);
        pGetGrowth = this.mBaseOptions.getPoolProperties(MessagePoolTags.PING_GROWTH_STRING);
        pInitialSize = (pGetIntialSize != -1) ? pGetIntialSize : MessagePoolTags.PING_INITIAL_INT;
        pGrowth = (pGetGrowth != -1) ? pGetGrowth : MessagePoolTags.PING_INITIAL_INT;
        pFlag = MessageFlag.PING;
        pMessageClass = MessagePing.class;
        this.mMessagePool.registerMessage(pFlag, pMessageClass, pInitialSize, pGrowth);

        pGetIntialSize = this.mBaseOptions.getPoolProperties(MessagePoolTags.PING_INITIAL_STRING);
        pGetGrowth = this.mBaseOptions.getPoolProperties(MessagePoolTags.PING_GROWTH_STRING);
        pInitialSize = (pGetIntialSize != -1) ? pGetIntialSize : MessagePoolTags.PING_INITIAL_INT;
        pGrowth = (pGetGrowth != -1) ? pGetGrowth : MessagePoolTags.PING_INITIAL_INT;
        pFlag = MessageFlag.PING_ACK;
        pMessageClass = MessagePingAck.class;
        this.mMessagePool.registerMessage(pFlag, pMessageClass, pInitialSize, pGrowth);

        pGetIntialSize = this.mBaseOptions.getPoolProperties(MessagePoolTags.PING_INITIAL_STRING);
        pGetGrowth = this.mBaseOptions.getPoolProperties(MessagePoolTags.PING_GROWTH_STRING);
        pInitialSize = (pGetIntialSize != -1) ? pGetIntialSize : MessagePoolTags.PING_INITIAL_INT;
        pGrowth = (pGetGrowth != -1) ? pGetGrowth : MessagePoolTags.PING_INITIAL_INT;
        pFlag = MessageFlag.PING_HIGHEST;
        pMessageClass = MessagePingHighest.class;
        this.mMessagePool.registerMessage(pFlag, pMessageClass, pInitialSize, pGrowth);

        pGetIntialSize = this.mBaseOptions.getPoolProperties(MessagePoolTags.MIGRATE_INITIAL_STRING);
        pGetGrowth = this.mBaseOptions.getPoolProperties(MessagePoolTags.MIGRATE_GROWTH_STRING);
        pInitialSize = (pGetIntialSize != -1) ? pGetIntialSize : MessagePoolTags.MIGRATE_INITIAL_INT;
        pGrowth = (pGetGrowth != -1) ? pGetGrowth : MessagePoolTags.MIGRATE_INITIAL_INT;
        pFlag = MessageFlag.MIGRATE;
        pMessageClass = MessageMigrate.class;
        this.mMessagePool.registerMessage(pFlag, pMessageClass, pInitialSize, pGrowth);

        pGetIntialSize = this.mBaseOptions.getPoolProperties(MessagePoolTags.CLIENT_JOIN_INITIAL_STRING);
        pGetGrowth = this.mBaseOptions.getPoolProperties(MessagePoolTags.CLIENT_JOIN_GROWTH_STRING);
        pInitialSize = (pGetIntialSize != -1) ? pGetIntialSize : MessagePoolTags.CLIENT_JOIN_INITIAL_INT;
        pGrowth = (pGetGrowth != -1) ? pGetGrowth : MessagePoolTags.CLIENT_JOIN_INITIAL_INT;
        pFlag = MessageFlag.CLIENT_JOIN;
        pMessageClass = MessageClientJoin.class;
        this.mMessagePool.registerMessage(pFlag, pMessageClass, pInitialSize, pGrowth);

        pGetIntialSize = this.mBaseOptions.getPoolProperties(MessagePoolTags.CLIENT_DISCONNECTED_INITIAL_STRING);
        pGetGrowth = this.mBaseOptions.getPoolProperties(MessagePoolTags.CLIENT_DISCONNECTED_GROWTH_STRING);
        pInitialSize = (pGetIntialSize != -1) ? pGetIntialSize : MessagePoolTags.CLIENT_DISCONNECTED_INITIAL_INT;
        pGrowth = (pGetGrowth != -1) ? pGetGrowth : MessagePoolTags.CLIENT_DISCONNECTED_INITIAL_INT;
        pFlag = MessageFlag.CLIENT_DISCONNECTED;
        pMessageClass = MessageClientDisconnect.class;
        this.mMessagePool.registerMessage(pFlag, pMessageClass, pInitialSize, pGrowth);

        pGetIntialSize = this.mBaseOptions.getPoolProperties(MessagePoolTags.ENCAPSULATED_INITIAL_STRING);
        pGetGrowth = this.mBaseOptions.getPoolProperties(MessagePoolTags.ENCAPSULATED_GROWTH_STRING);
        pInitialSize = (pGetIntialSize != -1) ? pGetIntialSize : MessagePoolTags.ENCAPSULATED_INITIAL_INT;
        pGrowth = (pGetGrowth != -1) ? pGetGrowth : MessagePoolTags.ENCAPSULATED_INITIAL_INT;
        pFlag = MessageFlag.ENCAPSULATED;
        pMessageClass = MessageEncapsulated.class;
        this.mMessagePool.registerMessage(pFlag, pMessageClass, pInitialSize, pGrowth);

        pGetIntialSize = this.mBaseOptions.getPoolProperties(MessagePoolTags.CLIENT_OUT_OF_SYNC_INITIAL_STRING);
        pGetGrowth = this.mBaseOptions.getPoolProperties(MessagePoolTags.CLIENT_OUT_OF_SYNC_GROWTH_STRING);
        pInitialSize = (pGetIntialSize != -1) ? pGetIntialSize : MessagePoolTags.CLIENT_OUT_OF_SYNC_INITIAL_INT;
        pGrowth = (pGetGrowth != -1) ? pGetGrowth : MessagePoolTags.CLIENT_OUT_OF_SYNC_INITIAL_INT;
        pFlag = MessageFlag.CLIENT_OUT_OF_SYNC;
        pMessageClass = MessageOutOfSyncWith.class;
        this.mMessagePool.registerMessage(pFlag, pMessageClass, pInitialSize, pGrowth);
    }
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
