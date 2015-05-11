package com.niffy.AndEngineLockStepEngine.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Indicates that we cannot send anymore to this client as we are pending a
 * closure to shut down connection to the client.
 *
 * @author Paul Robinson
 * @since 16 May 2013 16:07:08
 */
public class ClientPendingClosure extends Exception {
    private static final long serialVersionUID = -133241486305463823L;
    // ===========================================================
    // Constants
    // ===========================================================
    @SuppressWarnings("unused")
    private final Logger log = LoggerFactory.getLogger(ClientPendingClosure.class);

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    public ClientPendingClosure(String pMessage) {
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
