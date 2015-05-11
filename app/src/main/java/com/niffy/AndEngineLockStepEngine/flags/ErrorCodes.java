package com.niffy.AndEngineLockStepEngine.flags;

import java.net.InetAddress;

/**
 * ErrorCodes
 *
 * @author Paul Robinson
 * @since 1 Apr 2013 14:39:04
 */
public final class ErrorCodes {
    // ===========================================================
    // Constants
    // ===========================================================
    /**
     * A client window is not empty. This indicates that a given client has not
     * acknowledge some messages. Therefore not everyone will have the same game
     * world.
     */
    public final static int CLIENT_WINDOW_NOT_EMPTY = 0;
    /**
     * Could not cast a {@link String} to an {@link InetAddress};
     */
    public final static int COULD_NOT_CAST_INETADDRESS = 1;
    /**
     * Could send from thread to client.
     */
    public final static int COULD_NOT_SEND = 2;
    /**
     * Could not receive message
     */
    public final static int COULD_NOT_RECEIVE = 3;

}
