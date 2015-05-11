package com.niffy.AndEngineLockStepEngine.threads.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import com.niffy.AndEngineLockStepEngine.exceptions.ClientDoesNotExist;
import com.niffy.AndEngineLockStepEngine.exceptions.ClientPendingClosure;
import com.niffy.AndEngineLockStepEngine.exceptions.NotConnectedToClient;

public interface IClientSelector extends ISelectorThread {
    /**
     * @param pAddress
     * @param pData
     * @throws NotConnectedToClient If client is added but channel is not connected
     * @throws ClientDoesNotExist   If client has never been added to the thread.
     * @throws ClientPendingClosure If the connection is pending closure, will not add any more
     *                              messages to send.
     * @throws IOException
     */
    public void send(final InetAddress pAddress, final byte[] pData) throws NotConnectedToClient, ClientDoesNotExist,
            ClientPendingClosure, IOException;

    /**
     * Connect to a client-server
     *
     * @param pAddress {@link InetSocketAddress} to connect to.
     * @throws IOException
     */
    public void connectTo(final InetSocketAddress pAddress) throws IOException;
}
