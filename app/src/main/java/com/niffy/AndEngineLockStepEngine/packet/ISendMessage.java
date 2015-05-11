package com.niffy.AndEngineLockStepEngine.packet;

import java.net.InetAddress;

import com.niffy.AndEngineLockStepEngine.flags.MessageFlag;
import com.niffy.AndEngineLockStepEngine.messages.IMessage;

public interface ISendMessage {
    /**
     * Send a message to a client. This should be called from
     * {@link IPacketHandler}
     *
     * @param pAddress {@link InetAddress} of client to contact.
     * @param pMessage Message to send, <b>MUST</b> extend {@link IMessage}
     * @param pTCP     <code>true</code> to use TCP <code>false</code> to send as UDP
     * @return {@link IPacketHandler} should have handled this.
     */
    public <T extends IMessage> int sendMessage(final InetAddress pAddress, final T pMessage, final boolean pTCP);

    /**
     * Obtain a message.
     *
     * @param pFlag {@link Integer} of flag from {@link MessageFlag}
     * @return message which extends {@link IMessage}, to use
     * @throws MessagePoolException
     */
    public IMessage obtainMessage(final int pFlag);

    /**
     * Recycle a message
     *
     * @param pMessage {@link IMessage} to put back into the pool
     */
    public <T extends IMessage> void recycleMessage(final T pMessage);
}
