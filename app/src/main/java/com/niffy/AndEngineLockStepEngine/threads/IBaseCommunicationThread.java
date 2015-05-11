package com.niffy.AndEngineLockStepEngine.threads;

import java.net.InetAddress;

import com.niffy.AndEngineLockStepEngine.messages.MessageError;
import com.niffy.AndEngineLockStepEngine.misc.IHandlerMessage;
import com.niffy.AndEngineLockStepEngine.misc.WeakThreadHandler;

public interface IBaseCommunicationThread extends IHandlerMessage, IBaseThread {
    public WeakThreadHandler<IHandlerMessage> getParentHandler();

    public WeakThreadHandler<IHandlerMessage> getHandler();

    public void handleErrorMessage(final InetAddress pAddress, final MessageError pMessage);
}
