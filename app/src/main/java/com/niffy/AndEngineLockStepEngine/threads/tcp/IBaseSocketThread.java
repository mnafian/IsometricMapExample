package com.niffy.AndEngineLockStepEngine.threads.tcp;

import com.niffy.AndEngineLockStepEngine.misc.IHandlerMessage;
import com.niffy.AndEngineLockStepEngine.misc.WeakThreadHandler;

public interface IBaseSocketThread {
    public WeakThreadHandler<IHandlerMessage> getHandler();
}
