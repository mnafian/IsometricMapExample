package com.niffy.AndEngineLockStepEngine.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The address is not known to the selector thread, as it has never received or
 * set up a connection.
 *
 * @author Paul Robinson
 * @since 16 May 2013 16:07:51
 */
public class ClientDoesNotExist extends Exception {

    private static final long serialVersionUID = 1263605861139102739L;
    // ===========================================================
    // Constants
    // ===========================================================
    @SuppressWarnings("unused")
    private final Logger log = LoggerFactory.getLogger(ClientDoesNotExist.class);

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    public ClientDoesNotExist(String pMessage) {
        super(pMessage);
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
