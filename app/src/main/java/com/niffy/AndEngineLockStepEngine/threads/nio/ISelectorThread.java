package com.niffy.AndEngineLockStepEngine.threads.nio;

import java.net.InetAddress;

import com.niffy.AndEngineLockStepEngine.threads.IBaseThread;

/**
 * <b>Extends: </b> {@link IBaseThread}
 *
 * @author Paul Robinson
 * @since 16 May 2013 15:35:36
 */
public interface ISelectorThread extends IBaseThread {
    public void removeClient(final InetAddress pAddress);

    public boolean containsClient(final InetAddress pAddress);
}
