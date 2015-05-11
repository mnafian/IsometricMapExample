package com.niffy.AndEngineLockStepEngine;

import java.net.InetAddress;
import java.util.ArrayList;

import com.niffy.AndEngineLockStepEngine.misc.IHandlerMessage;
import com.niffy.AndEngineLockStepEngine.packet.ISendMessage;
import com.niffy.AndEngineLockStepEngine.threads.ICommunicationHandler;

public interface ILockstepNetwork extends IHandlerMessage, ISendMessage {
    /**
     * Set the current main communication thread to use. <br>
     *
     * @param pThread
     */
    public void setMainCommunicationThread(final ICommunicationHandler pThread);

    public void addClient(final InetAddress pAddress);

    public ArrayList<InetAddress> getClients();

    public void removeClient(final InetAddress pAddress);

    public boolean allRunning();

    public void connectTo(final String pAddress);

}
