package com.niffy.AndEngineLockStepEngine;

import com.niffy.AndEngineLockStepEngine.threads.ICommunicationThread;

public interface ILockstepHelper {
    public ICommunicationThread createTCPThread();

    public ICommunicationThread createUDPThread();
}
