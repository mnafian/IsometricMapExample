package com.niffy.AndEngineLockStepEngine.misc;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class WeakThreadHandler<T extends IHandlerMessage> extends Handler {
    private final WeakReference<T> mThread;

    public WeakThreadHandler(final T pThread, Looper myLooper) {
        super(myLooper);
        this.mThread = new WeakReference<T>(pThread);
    }

    public WeakThreadHandler(final T pThread) {
        super();
        this.mThread = new WeakReference<T>(pThread);

    }

    @Override
    public void handleMessage(Message msg) {
        IHandlerMessage thread = mThread.get();
        if (thread != null) {
            thread.handlePassedMessage(msg);
        }
    }

}
