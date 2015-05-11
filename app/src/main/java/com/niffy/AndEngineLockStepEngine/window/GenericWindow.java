package com.niffy.AndEngineLockStepEngine.window;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericWindow implements IGenericWindow {
    // ===========================================================
    // Fields
    // ===========================================================
    final protected InetAddress mAddress;
    // ===========================================================
    // Constants
    // ===========================================================
    @SuppressWarnings("unused")
    private final Logger log = LoggerFactory.getLogger(GenericWindow.class);
    /**
     * Arraylist(s) of Int's
     */
    protected List<ArrayList<Integer>> mInts;
    /**
     * What current window are we on.
     */
    protected int mCurrentWindow = 0;
    /**
     * How many steps before a crisis
     */
    protected int mStepsBeforeCrisis = 2;
    protected int mCurrentStep = -1;
    protected IGenericWindowCrisisListener mWindowListener;

    // ===========================================================
    // Constructors
    // ===========================================================

    public GenericWindow(final InetAddress pAddress, final int pStepsBeforeCrisis, final int pWindowCapacity) {
        this.mAddress = pAddress;
        this.mStepsBeforeCrisis = pStepsBeforeCrisis;
        this.mInts = new ArrayList<ArrayList<Integer>>(this.mStepsBeforeCrisis);
        for (int i = 0; i <= this.mStepsBeforeCrisis; i++) {
            this.mInts.add(new ArrayList<Integer>(pWindowCapacity));
        }
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    // ===========================================================
    // Methods for/from SuperClass/Interfaces IGenericWindow
    // ===========================================================
    @Override
    public void addInt(int pInt) {
        this.add(pInt);
    }

    @Override
    public boolean removeInt(int pInt) {
        return this.remove(pInt);
    }

    @Override
    public void removeInt(int[] pInts) {
        for (int i : pInts) {
            this.remove(pInts[i]);
        }
    }

    @Override
    public boolean currentWindowEmpty() {
        return this.empty();
    }

    @Override
    public void slide(int pStep) {
        this.mCurrentStep = pStep;
        this.slideWindow();
    }

    @Override
    public void setCrisisListener(IGenericWindowCrisisListener pWindowListener) {
        this.mWindowListener = pWindowListener;
    }

    @Override
    public int[] getIDsFromLastWindowAndClear() {
        return this.getLastWindowIntsAndClear();
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================
    protected boolean remove(final int pID) {
        for (int i = 0; i <= this.mStepsBeforeCrisis; i++) {
            boolean result = this.remove(pID, i);
            if (result) {
                return true;
            }
        }
        return false;
    }

    protected boolean remove(final int pID, final int pWindow) {
        return this.mInts.get(pWindow).remove((Integer) pID);
    }

    protected void add(final int pID) {
        this.mInts.get(this.mCurrentWindow).add(pID);
    }

    protected boolean empty() {
        if (this.mInts.get(this.mCurrentWindow).size() != 0) {
            return false;
        }
        return true;
    }

    protected void slideWindow() {
        int couldBe = this.mCurrentWindow;
        couldBe++;
        if (couldBe > this.mStepsBeforeCrisis) {
            this.mCurrentWindow = 0;
        } else {
            this.mCurrentWindow++;
        }
        if (this.mWindowListener != null) {
            if (!this.empty()) {
                this.mWindowListener.windowNotEmpty(this.mAddress);
            }
        }
    }

    protected int[] getLastWindowIntsAndClear() {
        int window = this.getLastWindow();
        int[] sequences = this.convertIntegers(this.mInts.get(window));
        this.mInts.get(window).clear();
        return sequences;
    }

    protected int getLastWindow() {
        int couldBe = this.mCurrentWindow;
        couldBe--;
        if (couldBe < 0) {
            return this.mStepsBeforeCrisis;
        } else {
            return couldBe;
        }
    }

    protected int[] convertIntegers(List<Integer> integers) {
        int[] ret = new int[integers.size()];
        Iterator<Integer> iterator = integers.iterator();
        for (int i = 0; i < ret.length; i++) {
            ret[i] = iterator.next().intValue();
        }
        return ret;
    }
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
