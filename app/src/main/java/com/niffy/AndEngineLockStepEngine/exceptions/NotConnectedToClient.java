package com.niffy.AndEngineLockStepEngine.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Indicates the channel exists but is not connected.
 *
 * @author Paul Robinson
 * @since 16 May 2013 16:07:08
 */
public class NotConnectedToClient extends Exception {
    private static final long serialVersionUID = 372377532544075462L;
    // ===========================================================
    // Constants
    // ===========================================================
    @SuppressWarnings("unused")
    private final Logger log = LoggerFactory.getLogger(NotConnectedToClient.class);

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    public NotConnectedToClient(String pMessage) {
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
