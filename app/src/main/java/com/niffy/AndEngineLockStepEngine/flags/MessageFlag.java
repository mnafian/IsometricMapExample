package com.niffy.AndEngineLockStepEngine.flags;

/**
 * Flags to indicate packet type
 *
 * @author Paul Robinson
 * @since 28 Mar 2013 21:05:51
 */
public final class MessageFlag {
    // ===========================================================
    // Constants
    // ===========================================================
    public final static int ENCAPSULATED = 9;
    public final static int ERROR = 10;
    public final static int ACK = 11;
    public final static int ACK_MULTI = 12;
    public final static int PING = 13;
    public final static int PING_ACK = 14;
    public final static int PING_HIGHEST = 15;

    public final static int MIGRATE = 20;
    public final static int STEPCHANGE = 21;
    public final static int COUNTDOWN = 22; /* TODO do we need this? just work it out after PING_HIGHEST */

    public final static int CLIENT_JOIN = 30;
    public final static int CLIENT_DISCONNECTED = 31;
    public final static int CLIENT_OUT_OF_SYNC = 32;
}
