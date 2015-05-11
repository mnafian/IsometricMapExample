package com.niffy.AndEngineLockStepEngine.packet.ack;

import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niffy.AndEngineLockStepEngine.window.GenericWindow;
import com.niffy.AndEngineLockStepEngine.window.IGenericWindow;
import com.niffy.AndEngineLockStepEngine.window.IGenericWindowCrisisListener;

public class AckWindowClientManager implements IAckWindowClient {
    // ===========================================================
    // Fields
    // ===========================================================
    final protected InetAddress mAddress;
    final protected IAckWindowManager mWindowManager;
    final protected IGenericWindow mSentAcks;
    final protected IGenericWindow mAwaitingAcks;
    // ===========================================================
    // Constants
    // ===========================================================
    @SuppressWarnings("unused")
    private final Logger log = LoggerFactory.getLogger(AckWindowClientManager.class);
    protected IGenericWindowCrisisListener mCrisisListener;

    // ===========================================================
    // Constructors
    // ===========================================================

    public AckWindowClientManager(final IAckWindowManager pWindowManager, final InetAddress pAddress,
                                  final int pStepsBeforeCrisis, final int pWindowCapacity) {
        this(pWindowManager, pAddress, pStepsBeforeCrisis, pWindowCapacity, null);
    }

    public AckWindowClientManager(final IAckWindowManager pWindowManager, final InetAddress pAddress,
                                  final int pStepsBeforeCrisis, final int pWindowCapacity, final IGenericWindowCrisisListener pCrisisListener) {
        this.mWindowManager = pWindowManager;
        this.mAddress = pAddress;
        this.mSentAcks = new GenericWindow(this.mAddress, pStepsBeforeCrisis, pWindowCapacity);
        this.mAwaitingAcks = new GenericWindow(this.mAddress, pStepsBeforeCrisis, pWindowCapacity);
        this.mCrisisListener = pCrisisListener;
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    @Override
    public boolean currentWindowEmpty() {
        return this.mAwaitingAcks.currentWindowEmpty();
    }

    @Override
    public void slide(int pStep) {
        this.mSentAcks.slide(pStep);
        this.mAwaitingAcks.slide(pStep);
    }

    @Override
    public void setCrisisListener(IGenericWindowCrisisListener pWindowListener) {
        this.mCrisisListener = pWindowListener;
    }

    @Override
    public int[] getIDsFromLastWindowAndClear() {
        return this.mSentAcks.getIDsFromLastWindowAndClear();
    }

    @Override
    public void addSentAck(int pAck) {
        this.mSentAcks.addInt(pAck);
    }

    @Override
    public void addAwaitingAck(int pAck) {
        this.mAwaitingAcks.addInt(pAck);
    }

    @Override
    public void removeAwaitingAck(int pAck) {
        this.mAwaitingAcks.removeInt(pAck);
    }

    @Override
    public void removeAwaitingAck(int[] pAck) {
        this.mAwaitingAcks.removeInt(pAck);
    }

    @Override
    public void windowNotEmpty(InetAddress pAddress) {
        this.mWindowManager.windowNotEmpty(pAddress);
    }

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
