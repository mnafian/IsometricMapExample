package com.niffy.AndEngineLockStepEngine.flags;

import android.os.Bundle;
import android.os.Message;

import com.niffy.AndEngineLockStepEngine.Lockstep;
import com.niffy.AndEngineLockStepEngine.LockstepNetwork;
import com.niffy.AndEngineLockStepEngine.packet.PacketHandler;
import com.niffy.AndEngineLockStepEngine.threads.CommunicationHandler;
import com.niffy.AndEngineLockStepEngine.threads.CommunicationThread;
import com.niffy.AndEngineLockStepEngine.threads.nio.ClientSelector;
import com.niffy.AndEngineLockStepEngine.threads.nio.SelectorFlag;
import com.niffy.AndEngineLockStepEngine.threads.nio.ServerSelector;
import com.niffy.AndEngineLockStepEngine.threads.nio.UDPSelector;

/**
 * Flags to indicate packet type
 *
 * @author Paul Robinson
 * @since 28 Mar 2013 21:05:51
 */
public final class ITCFlags {
    // ===========================================================
    // Constants
    // ===========================================================
    public final static int TCP_SERVER_SELECTOR_START = -1000000;
    public final static int TCP_CLIENT_SELECTOR_START = -1000001;
    public final static int UDP_CLIENT_SELECTOR_START = -1000002;
    public final static int MAIN_COMMUNICATION_START = -1000003;

    /**
     * This will be sent from {@link LockstepNetwork} to
     * {@link CommunicationThread} to connect to a host. Bundle will have a
     * string key <code>ip</code> with the ip to connect to.
     */
    public final static int CONNECT_TO = -10;
    /**
     * This will be send from {@link CommunicationHandler} to
     * {@link LockstepNetwork} to indicate that a connection has been made. <br>
     * <b>The main activity should not act upon this flag, other then pass it on
     * to {@link LockstepNetwork}</b> TODO not used
     */
    public final static int CONNECTED_TO_HOST = -11;
    /**
     * This will be send from TCPCommunicationThread to
     * {@link LockstepNetwork} to indicate that a connection has not been made. <br>
     * <b>The main activity should not act upon this flag, other then pass it on
     * to {@link LockstepNetwork}</b> TODO not used
     */
    public final static int CONNECT_TO_ERROR = -12;
    /**
     * This will send from {@link CommunicationHandler} to the main activity.
     * This will need to be passed onto {@link LockstepNetwork}<br>
     * {@link Message#getData()} {@link Bundle} will contain a {@link String}
     * with the key <code>ip</code> This will contain the IP address of the
     * client. <br>
     * <b>The main activity should not act upon this flag, other then pass it on
     * to {@link LockstepNetwork}</b>
     */
    public final static int CLIENT_CONNECTED = 0;
    /**
     * This will send from {@link CommunicationHandler} to the main activity.
     * This will need to be passed onto {@link LockstepNetwork}<br>
     * {@link Message#getData()} {@link Bundle} will contain a {@link String}
     * with the key <code>ip</code> This will contain the IP address of the
     * client. <br>
     * <b>The main activity should not act upon this flag, other then pass it on
     * to {@link LockstepNetwork}</b>
     */
    public final static int CLIENT_DISCONNECTED = 1;
    /**
     * This will send from {@link CommunicationHandler} to the main activity.
     * This will need to be passed onto {@link LockstepNetwork} <br>
     * <b>The main activity should not act upon this flag, other then pass it on
     * to {@link LockstepNetwork}</b> {@link Message#getData()} {@link Bundle}
     * will contain a {@link String} with the key <code>ip</code> This will
     * contain the IP address of the client. <br>
     */
    public final static int CLIENT_ERROR = 2;
    /**
     * This will be passed from {@link ServerSelector} to
     * {@link CommunicationThread} thread. <br>
     * {@link Message#getData()} {@link Bundle} will contain a {@link String}
     * with the key <code>ip</code> This will contain the IP address of the
     * client. <br>
     * The data is stored as a byte array with the key of <code>data</code> This
     * data will be an encapsulated packet.
     */
    public final static int TCP_CLIENT_INCOMMING = 10;
    public final static int TCP_CLIENT_OUTGOING = 20;
    /**
     * This will be passed from {@link UDPSelector} to
     * {@link CommunicationThread} thread. {@link Message#getData()}
     * {@link Bundle} will contain a {@link String} with the key <code>ip</code>
     * This will contain the IP address of the client. <br>
     * The data is stored as a byte array with the key of <code>data</code> This
     * data will be an encapsulated packet.
     */
    public final static int UDP_INCOMMING = 1;
    /**
     * This will be passed from the {@link LockstepNetwork} to subclasses of
     * {@link CommunicationThread}, which has to handle sending the message in
     * its own way. <br>
     * {@link Message#getData()} {@link Bundle} will contain a {@link String}
     * with the key <code>ip</code> This will contain the IP address to who to
     * send the message to. <br>
     * An {@link Integer} with the key <code>intended</code> from
     * {@link IntendedFlag}, for who it is intended for. <br>
     * {@link Byte} Array with the key <code>data</code>, the encapsulated
     * message. <br>
     * {@link Boolean} with the key <code>method</code> <code>true</code> means
     * TCP <code>false</code> means UDP
     */
    public final static int SEND_MESSAGE = 20;
    /**
     * This will be passed from {@link PacketHandler} to the main activity for
     * it to process itself, as its a packet intended for the client. <br>
     * {@link Message#getData()} {@link Bundle} will contain a {@link String}
     * with the key <code>ip</code> This will contain the sender IP address. <br>
     * The data is stored as a byte array with the key of <code>data</code> This
     * data should not be encapsulated.
     */
    public final static int RECIEVE_MESSAGE_CLIENT = 21;
    /**
     * This will be passed from {@link PacketHandler} to the main activity for
     * it to pass on to {@link LockstepNetwork} <br>
     * {@link Message#getData()} {@link Bundle} will contain a {@link String}
     * with the key <code>ip</code> This will contain the sender IP address. <br>
     * The data is stored as a byte array with the key of <code>data</code> This
     * data should not be encapsulated. <br>
     * There will also be an {@link Integer} flag indicated the packet type.
     */
    public final static int RECIEVE_MESSAGE_LOCKSTEP = 22;
    /**
     * This will be passed from the {@link Lockstep} to
     * {@link CommunicationThread} to inform of a step increment.<br>
     * {@link Message#getData()} {@link Bundle} will contain a {@link Integer}
     * with the key <code>step</code> This will contain the new step number.
     */
    public final static int LOCKSTEP_INCREMENT = 50;
    /**
     * This will passed from subclass of {@link CommunicationThread} to main
     * activity to pass onto {@link LockstepNetwork}. This will only happen if
     * the network gets {@link ErrorCodes#CLIENT_WINDOW_NOT_EMPTY} <br>
     * {@link Message#getData()} {@link Bundle} will contain a {@link String}
     * with the key <code>ip</code> This will contain the IP address of the
     * client. <br>
     * <b>The main activity should not act upon this flag, other then pass it on
     * to {@link LockstepNetwork}</b>
     */
    public final static int CLIENT_WINDOW_NOT_EMPTY = 100;
    /**
     * This will be passed from the subclasses of {@link CommunicationThread} to
     * the main activity for {@link LockstepNetwork}. <br>
     * {@link Message#getData()} {@link Bundle} will contain a {@link String}
     * with the key <code>ip</code> This will contain the IP address of the
     * client. <br>
     * and the a {@link Integer} with the key <code>error</code> containing a
     * {@link ErrorCodes}
     */
    public final static int NETWORK_ERROR = 900;
    /**
     * This will be passed from {@link CommunicationThread} to main activity if
     * it could not send a message to a client. {@link Message#getData()}
     * {@link Bundle} will contain a {@link String} with the key <code>ip</code>
     * This will contain the IP address of the client. <br>
     * It will also contain a byte array of key <code>data</code> of the
     * message, this data may be incomplete! <br>
     * An {@link Integer} with the key <code>error</code> will contain the error
     * code.
     */
    public final static int NETWORK_SEND_MESSAGE_FAILURE = 901;
    /**
     * int key <code>selector</code> of vale from {@link SelectorFlag}
     */
    public final static int NETWORK_SELECTER_DEFAULT = -100;
    /**
     * To be passed from {@link ServerSelector} or {@link ClientSelector} to
     * {@link CommunicationHandler}. Should inform {@link ClientSelector} of
     * this to create connection back. Inform UDP selector to allow and accept
     * connections from this IP. <br>
     * {@link Message#getData()} {@link Bundle} will contain a {@link String}
     * with the key <code>ip</code> This will contain the IP address of the
     * client. <br>
     */
    public final static int NEW_CLIENT_CONNECTED = -200;

    /**
     * All flags that need to be passed to the lockstep engine
     */
    public final static int[] LOCKSTEP_FLAGS = {CONNECTED_TO_HOST, CONNECT_TO_ERROR, CONNECT_TO_ERROR,
            CLIENT_CONNECTED, CLIENT_DISCONNECTED, CLIENT_ERROR, RECIEVE_MESSAGE_LOCKSTEP, LOCKSTEP_INCREMENT,
            CLIENT_WINDOW_NOT_EMPTY, NETWORK_ERROR, NETWORK_SEND_MESSAGE_FAILURE};
    public final static int NETWORK_TCP_SHUTDOWN_SOCKET = 10;
    public final static int NETWORK_TCP_EXCEPTION = 200;
    public final static int TCP_LISTENER_THREAD_START = 300;
    public static final int TCP_THREAD_START = 100;
    public static final int UDP_THREAD_START = 210;

    public static final int NETWORK_RECIEVE_FAILURE = 200;
}
