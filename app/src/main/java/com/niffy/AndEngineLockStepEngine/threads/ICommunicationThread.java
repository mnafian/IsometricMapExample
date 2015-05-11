package com.niffy.AndEngineLockStepEngine.threads;

import java.net.InetAddress;
import java.util.ArrayList;

import com.niffy.AndEngineLockStepEngine.packet.ISendMessage;
import com.niffy.AndEngineLockStepEngine.window.IGenericWindowCrisisListener;

public interface ICommunicationThread extends IBaseCommunicationThread, IGenericWindowCrisisListener, ISendMessage {

    public void addClient(final InetAddress pAddress);

    public ArrayList<InetAddress> getClients();

    public void removeClient(final InetAddress pAddress);

}
