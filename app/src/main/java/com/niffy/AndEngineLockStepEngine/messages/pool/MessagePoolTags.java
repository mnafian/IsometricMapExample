package com.niffy.AndEngineLockStepEngine.messages.pool;

/**
 * Flags to indicate message pool sizes
 *
 * @author Paul Robinson
 * @since 28 Mar 2013 21:05:51
 */
public final class MessagePoolTags {
    // ===========================================================
    // Constants
    // ===========================================================
    public final static String PING_INITIAL_STRING = "PING_INITIAL_STRING";
    public final static String PING_GROWTH_STRING = "PING_GROWTH_STRING";
    public final static int PING_INITIAL_INT = 10;
    public final static int PING_GROWTHL_INT = 10;

    public final static String ACK_INITIAL_STRING = "ACK_INITIAL_STRING";
    public final static String ACK_GROWTH_STRING = "ACK_GROWTH_STRING";
    public final static int ACK_INITIAL_INT = 50;
    public final static int ACK_GROWTH_INT = 50;

    public final static String STEPCHANGE_INITIAL_STRING = "STEPCHANGE_INITIAL_STRING";
    public final static String STEPCHANGE_GROWTH_STRING = "STEPCHANGE_GROWTH_STRING";
    public final static int STEPCHANGE_INITIAL_INT = 10;
    public final static int STEPCHANGE_GROWTH_INT = 10;

    public final static String COUNTDOWN_INITIAL_STRING = "COUNTDOWN_INITIAL_STRING";
    public final static String COUNTDOWN_GROWTH_STRING = "COUNTDOWN_GROWTH_STRING";
    public final static int COUNTDOWN_INITIAL_INT = 10;
    public final static int COUNTDOWN_GROWTH_INT = 10;

    public final static String ERROR_INITIAL_STRING = "ERROR_INITIAL_STRING";
    public final static String ERROR_GROWTH_STRING = "ERROR_GROWTH_STRING";
    public final static int ERROR_INITIAL_INT = 10;
    public final static int ERROR_GROWTH_INT = 10;

    public final static String MIGRATE_INITIAL_STRING = "MIGRATE_INITIAL_STRING";
    public final static String MIGRATE_GROWTH_STRING = "MIGRATE_GROWTH_STRING";
    public final static int MIGRATE_INITIAL_INT = 10;
    public final static int MIGRATE_GROWTH_INT = 10;

    public final static String CLIENT_JOIN_INITIAL_STRING = "CLIENT_JOIN_INITIAL_STRING";
    public final static String CLIENT_JOIN_GROWTH_STRING = "CLIENT_JOIN_GROWTH_STRING";
    public final static int CLIENT_JOIN_INITIAL_INT = 10;
    public final static int CLIENT_JOIN_GROWTH_INT = 10;

    public final static String CLIENT_DISCONNECTED_INITIAL_STRING = "CLIENT_DISCONNECTED_INITIAL_STRING";
    public final static String CLIENT_DISCONNECTED_GROWTH_STRING = "CLIENT_DISCONNECTED_GROWTH_STRING";
    public final static int CLIENT_DISCONNECTED_INITIAL_INT = 10;
    public final static int CLIENT_DISCONNECTED_GROWTH_INT = 10;

    public final static String ENCAPSULATED_INITIAL_STRING = "ENCAPSULATED_INITIAL_STRING";
    public final static String ENCAPSULATED_GROWTH_STRING = "ENCAPSULATED_GROWTH_STRING";
    public final static int ENCAPSULATED_INITIAL_INT = 50;
    public final static int ENCAPSULATED_GROWTH_INT = 50;

    public final static String CLIENT_OUT_OF_SYNC_INITIAL_STRING = "CLIENT_OUT_OF_SYNC_INITIAL_STRING";
    public final static String CLIENT_OUT_OF_SYNC_GROWTH_STRING = "CLIENT_OUT_OF_SYNC_GROWTH_STRING";
    public final static int CLIENT_OUT_OF_SYNC_INITIAL_INT = 10;
    public final static int CLIENT_OUT_OF_SYNC_GROWTH_INT = 10;
}
