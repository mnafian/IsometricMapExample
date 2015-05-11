package com.niffy.AndEngineLockStepEngine;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Message;

import com.niffy.AndEngineLockStepEngine.misc.IHandlerMessage;
import com.niffy.AndEngineLockStepEngine.options.IBaseOptions;

public class Lockstep implements ILockstepEngine, IHandlerMessage {
    // ===========================================================
    // Fields
    // ===========================================================
    public final long NANOSECOND = 1000000;
    final ILockstepNetwork mLockstepNetwork;
    // ===========================================================
    // Constants
    // ===========================================================
    private final Logger log = LoggerFactory.getLogger(Lockstep.class);
    /**
     * Accumulator to count ticks.
     */
    protected float mSecondsElapsedAccumulator = 0;
    /**
     * Listeners to update when the game step changes.
     */
    protected ArrayList<ILockstepStepChangeListener> mStepChangeListeners;
    /**
     * Current game step
     */
    protected int mCurrentGameStep = 0;
    /**
     * Standard TickLength in milliseconds
     */
    protected long mStandardTickLength = 50;
    /**
     * Current tick length. {@link #mStandardTickLength} +
     * {@link #mNewTickLength}
     */
    protected long mCurrentTickLength = 0;
    /**
     * {@link #mCurrentTickLength} but in nanoseconds
     */
    protected long mCurrentTickLengthNanoSeconds = 0;
    /**
     * At what step the tick length will change.
     */
    protected int mGameStepChangeOver = 0;
    /**
     * New tick length to use. Excluding {@link #mStandardTickLength}
     */
    protected long mNewTickLength = 0;
    /**
     * Countdown to start the game.
     */
    protected long mCountdownTime = 0;
    /**
     * Started the UDP lockstep? If false probably doing TCP stages.
     */
    protected boolean mStarted = false;
    protected ILockstepClientListener mLockstepClientListener;
    protected IBaseOptions mBaseOptions;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     *
     */
    public Lockstep(final ILockstepClientListener pLockstepClientListener, IBaseOptions pBaseOptions) {
        this.mStepChangeListeners = new ArrayList<ILockstepStepChangeListener>();
        this.mLockstepNetwork = new LockstepNetwork(this, pBaseOptions);
        this.mLockstepClientListener = pLockstepClientListener;
        this.mBaseOptions = pBaseOptions;
        this.mStandardTickLength = this.mBaseOptions.getStandardTickLength();
        /* TODO create Ping RTT Client*/
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces IHandlerMessage
    // ===========================================================
    @Override
    public void handlePassedMessage(Message pMessage) {
        this.mLockstepNetwork.handlePassedMessage(pMessage);
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces ILockstepEngine
    // ===========================================================
    @Override
    public void onUpdate(float pNanosecondsElapsed) {
        if (this.mStarted) {
            this.mSecondsElapsedAccumulator += pNanosecondsElapsed;
            while (this.mSecondsElapsedAccumulator >= this.mCurrentTickLengthNanoSeconds) {
                this.incrementGameStep();
                if (this.mGameStepChangeOver == this.mCurrentGameStep) {
                    this.tickChangeOver();
                }
                this.mSecondsElapsedAccumulator -= this.mCurrentTickLengthNanoSeconds;
            }
        }
    }

    @Override
    public void start() {
        this.mStarted = true;
    }

    @Override
    public void stop() {
        this.mStarted = false;
    }

    @Override
    public void pause() {
        /*
         * TODO implement pause
		 */
    }

    @Override
    public long getCurrentTickLength() {
        return this.mCurrentTickLength;
    }

    @Override
    public long getStandardTicklength() {
        return this.mStandardTickLength;
    }

    @Override
    public int getCurrentStep() {
        return this.mCurrentGameStep;
    }

    @Override
    public void setStandardGameTickLength(long pStandardGameStepTime) {
        this.mStandardTickLength = pStandardGameStepTime;
    }

    @Override
    public void setChangeOver(int pGameStep, long pStepLength) {
        this.mGameStepChangeOver = pGameStep;
        this.mNewTickLength = pStepLength;
    }

    @Override
    public void setCountDownToStart(long pCountdown) {
        this.mCountdownTime = pCountdown;
    }

    @Override
    public void subscribeStepChangeListener(ILockstepStepChangeListener pLockstepListener) {
        if (!this.mStepChangeListeners.contains(pLockstepListener)) {
            this.mStepChangeListeners.add(pLockstepListener);
        }
    }

    @Override
    public void unsubscribeStepChangeListener(ILockstepStepChangeListener pLockstepListener) {
        if (this.mStepChangeListeners.contains(pLockstepListener)) {
            this.mStepChangeListeners.remove(pLockstepListener);
        }
    }

    @Override
    public ILockstepNetwork getLockstepNetwork() {
        return this.mLockstepNetwork;
    }

    @Override
    public ILockstepClientListener getLockstepClientListener() {
        return this.mLockstepClientListener;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================
    private void incrementGameStep() {
        this.mCurrentGameStep++;
        this.informGameStepChange();
    }

    private void informGameStepChange() {
        final int count = this.mStepChangeListeners.size();
        for (int i = 0; i < count; i++) {
            this.mStepChangeListeners.get(i).lockstepStepChange(this.mCurrentGameStep);
        }
    }

    private void tickChangeOver() {
        this.mCurrentTickLength = this.mNewTickLength;
        this.mCurrentTickLengthNanoSeconds = this.mCurrentTickLength * this.NANOSECOND;
    }
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
