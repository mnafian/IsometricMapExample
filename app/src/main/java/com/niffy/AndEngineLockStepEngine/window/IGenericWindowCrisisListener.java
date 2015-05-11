package com.niffy.AndEngineLockStepEngine.window;

import java.net.InetAddress;

public interface IGenericWindowCrisisListener {
    /**
     * This is to be called when {@link IGenericWindow#slide()} is executed and the
     * new window is not empty of pending Ints
     */
    public void windowNotEmpty(final InetAddress pAddress);
}
