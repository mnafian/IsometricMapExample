package com.niffy.AndEngineLockStepEngine.window;

public interface IGenericWindowQuery {
    /**
     * Is the current window empty. This will help determine if we're missing
     * any acks.
     *
     * @return {@link Boolean} <code>true</code> for it is empty,
     * <code>false</code> for not empty
     */
    public boolean currentWindowEmpty();

    /**
     * Slide the window
     *
     * @param pStep {@link Integer} of current step;
     */
    public void slide(final int pStep);

    /**
     * Set the listener to be called in case of crisis when calling
     * {@link #slide()}
     *
     * @param pAckWindowListener {@link IGenericWindowCrisisListener} to call on crisis
     */
    public void setCrisisListener(final IGenericWindowCrisisListener pWindowListener);

    /**
     * Get the ack's sent from the last window. this will also clear the window
     *
     * @return {@link Integer} array of Int's from last window.
     */
    public int[] getIDsFromLastWindowAndClear();
}
