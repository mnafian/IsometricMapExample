package com.niffy.AndEngineLockStepEngine.packet.ack;

import com.niffy.AndEngineLockStepEngine.window.IGenericWindowCrisisListener;
import com.niffy.AndEngineLockStepEngine.window.IGenericWindowQuery;

public interface IAckWindowClient extends IGenericWindowQuery, IGenericWindowCrisisListener {
    public void addSentAck(final int pAck);

    public void addAwaitingAck(final int pAck);

    public void removeAwaitingAck(final int pAck);

    public void removeAwaitingAck(final int[] pAck);
}
