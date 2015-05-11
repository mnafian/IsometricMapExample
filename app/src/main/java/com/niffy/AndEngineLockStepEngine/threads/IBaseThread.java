package com.niffy.AndEngineLockStepEngine.threads;

public interface IBaseThread {
    public boolean isRunning();

    public boolean isTerminated();

    public void terminate();
}
