package com.niffy.AndEngineLockStepEngine.packet.ack;

import java.net.InetAddress;

import com.niffy.AndEngineLockStepEngine.window.IGenericWindowCrisisListener;
import com.niffy.AndEngineLockStepEngine.window.IGenericWindowQuery;

public interface IAckWindowManager extends IGenericWindowQuery, IGenericWindowCrisisListener {
    public void addClient(final InetAddress pAddress);

    public void removeClient(final InetAddress pAddress);

    /**
     * Process an ack
     *
     * @param pAddress                {@link InetAddress} who the ack is from
     * @param pReceivedAckForSequence {@link Integer} sequence which was acknowledged.
     */
    public void processReceivedAck(final InetAddress pAddress, final int pReceivedAckForSequence);

    /**
     * Process a collection of acks.
     *
     * @param pAddress                {@link InetAddress} who the ack's is from
     * @param pReceivedAckForSequence {@link Integer} sequences which was acknowledged.
     * @see #processReceivedAck(InetAddress, int)
     */
    public void processReceivedAck(final InetAddress pAddress, final int[] pReceivedAckForSequence);

    /**
     * Add sequence number which is requiring an ack.
     *
     * @param pAddress        {@link InetAddress} of who was send the sequence.
     * @param pSequenceNumber {@link Integer} of sequence number awaiting ack for.
     */
    public void addAwaitingAck(final InetAddress pAddress, final int pSequenceNumber);

    /**
     * Ack sent to whom and for what sequence number.
     *
     * @param pAddress        {@link InetAddress} of whom sent to.
     * @param pSequenceNumber {@link Integer} of sequence acknowledged.
     */
    public void addSentAck(final InetAddress pAddress, final int pSequenceNumber);
}
