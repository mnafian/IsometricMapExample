package com.niffy.AndEngineLockStepEngine;

import org.andengine.engine.lockstep.ILockstep;

public interface ILockstepEngine extends ILockstep {

    /**
     * Start the ticking!
     */
    public void start();

    /**
     * Stop the ticking!
     */
    public void stop();

    /**
     * Pause the ticking, broadcast this to all!
     */
    public void pause();

    /**
     * Get the current tick length in use
     *
     * @return {@link Long} of current step time in milliseconds.
     */
    public long getCurrentTickLength();

    /**
     * Get the standard tick length set as default/
     *
     * @return {@link Long} of step time in milliseconds.
     */
    public long getStandardTicklength();

    /**
     * Get current game step.
     *
     * @return {@link Integer} of game step
     */
    public int getCurrentStep();

    /**
     * Set the standard game step duration
     *
     * @param pStandardGameTickLength {@link Long} of duration in milliseconds
     */
    public void setStandardGameTickLength(final long pStandardGameTickLength);

    /**
     * Set the change over step for the tick length.
     *
     * @param pGameStep   {@link Integer} of step when the new length should start
     * @param pTickLength {@link Long} of tick length in milliseconds.
     */
    public void setChangeOver(final int pGameStep, final long pTickLength);

    /**
     * Countdown to start the game steps.
     *
     * @param pCountdown {@link Long} of countdown time in milliseconds
     */
    public void setCountDownToStart(final long pCountdown);

    /**
     * Subscribe for when the game step changes.
     *
     * @param pLockstepListener {@link ILockstepStepChangeListener} to call
     */
    public void subscribeStepChangeListener(final ILockstepStepChangeListener pLockstepListener);

    /**
     * Unsubscribe from game step changes.
     *
     * @param pLockstepListener {@link ILockstepStepChangeListener} which is unsubscribing.
     */
    public void unsubscribeStepChangeListener(final ILockstepStepChangeListener pLockstepListener);

    public ILockstepNetwork getLockstepNetwork();

    public ILockstepClientListener getLockstepClientListener();
}
