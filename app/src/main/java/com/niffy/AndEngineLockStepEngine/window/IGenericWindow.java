package com.niffy.AndEngineLockStepEngine.window;

public interface IGenericWindow extends IGenericWindowQuery {
    /**
     * Add an int to the current window.
     *
     * @param pInt {@link Integer} of int
     */
    public void addInt(final int pInt);

    /**
     * Remove an int.
     *
     * @param pInt {@link Integer} of int
     * @return {@link Boolean} <code>true</code> if removed, <code>false</code>
     * if not removed
     */
    public boolean removeInt(final int pInt);

    /**
     * Remove a list of int's.
     *
     * @param pInts {@link Integer} array of int's
     */
    public void removeInt(final int[] pInts);

}
