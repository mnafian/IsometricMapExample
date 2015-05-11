package com.niffy.AndEngineLockStepEngine.packet;

import java.net.InetAddress;
import java.util.ArrayList;

import com.niffy.AndEngineLockStepEngine.window.IGenericWindowCrisisListener;

public interface IPacketHandler extends IGenericWindowCrisisListener, ISendMessage {
    public void reconstructData(final String pAddress, final byte[] pData);

    public void addClient(final InetAddress pAddress);

    public ArrayList<InetAddress> getClients();

    public void removeClient(final InetAddress pAddress);

    public void lockstepIncrement(final int pIncrement);
}
