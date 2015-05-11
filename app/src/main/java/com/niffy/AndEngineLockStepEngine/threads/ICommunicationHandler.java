package com.niffy.AndEngineLockStepEngine.threads;

import com.niffy.AndEngineLockStepEngine.threads.nio.IClientSelector;
import com.niffy.AndEngineLockStepEngine.threads.nio.IServerSelector;

public interface ICommunicationHandler extends ICommunicationThread {

    public void setUDPSelectorThread(final IClientSelector pSelectorThread);

    public void setTCPClientSelectorThread(final IClientSelector pSelectorThread);

    public void setTCPServerSelectorThread(final IServerSelector pSelectorThread);
}
