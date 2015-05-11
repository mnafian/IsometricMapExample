package com.niffy.AndEngineLockStepEngine.packet.ack;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niffy.AndEngineLockStepEngine.flags.IntendedFlag;
import com.niffy.AndEngineLockStepEngine.flags.MessageFlag;
import com.niffy.AndEngineLockStepEngine.messages.MessageAckMulti;
import com.niffy.AndEngineLockStepEngine.packet.IPacketHandler;
import com.niffy.AndEngineLockStepEngine.window.IGenericWindowCrisisListener;

public class AckWindowManager implements IAckWindowManager {
    // ===========================================================
    // Fields
    // ===========================================================
    final protected IPacketHandler mParent;
    // ===========================================================
    // Constants
    // ===========================================================
    private final Logger log = LoggerFactory.getLogger(AckWindowManager.class);
    protected int mStepsBeforeCrisis = 0;
    protected int mWindowSize = 0;
    protected IGenericWindowCrisisListener mCrisisListener;
    protected HashMap<InetAddress, IAckWindowClient> mClients;

    // ===========================================================
    // Constructors
    // ===========================================================

    public AckWindowManager(final IPacketHandler pParent, final int pStepsBeforeCrisis, final int pWindowCapacity) {
        this(pParent, pStepsBeforeCrisis, pWindowCapacity, null);
    }

    public AckWindowManager(final IPacketHandler pParent, final int pStepsBeforeCrisis, final int pWindowCapacity,
                            final IGenericWindowCrisisListener pCrisisListener) {
        this.mParent = pParent;
        this.mStepsBeforeCrisis = pStepsBeforeCrisis;
        this.mWindowSize = pWindowCapacity;
        this.mCrisisListener = pCrisisListener;
        this.mClients = new HashMap<InetAddress, IAckWindowClient>();
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public boolean currentWindowEmpty() {
        boolean windowsEmpty = true;
        Iterator<Entry<InetAddress, IAckWindowClient>> entries = this.mClients.entrySet().iterator();
        while (entries.hasNext()) {
            Entry<InetAddress, IAckWindowClient> entry = entries.next();
            if (entry.getValue().currentWindowEmpty()) {
                /* All is ok */
            } else {
                windowsEmpty = false;
                log.warn("Client: {} has not acknowledged our packets", entry.getKey());
            }
        }
        return windowsEmpty;
    }

    @Override
    public void slide(int pStep) {
        Iterator<Entry<InetAddress, IAckWindowClient>> entries = this.mClients.entrySet().iterator();
        while (entries.hasNext()) {
            Entry<InetAddress, IAckWindowClient> entry = entries.next();
            entry.getValue().slide(pStep);
            this.sendMultiAck(entry.getKey(), entry.getValue().getIDsFromLastWindowAndClear());
        }
    }

    @Override
    public void setCrisisListener(IGenericWindowCrisisListener pWindowListener) {
        this.mCrisisListener = pWindowListener;
    }

    /**
     * This does not do anything, instead access its children. {@link #mClients}
     *
     * @see com.niffy.AndEngineLockStepEngine.window.IGenericWindowQuery#getIDsFromLastWindowAndClear()
     */
    @Override
    public int[] getIDsFromLastWindowAndClear() {
        return null;
    }

    @Override
    public void windowNotEmpty(InetAddress pAddress) {
        this.mParent.windowNotEmpty(pAddress);
    }

    @Override
    public void addClient(InetAddress pAddress) {
        if (this.mClients.containsKey(pAddress)) {
            log.warn("Went to create ack window manager for: {} but is already added.", pAddress);
        } else {
            this.mClients.put(pAddress, new AckWindowClientManager(this, pAddress, this.mStepsBeforeCrisis,
                    this.mWindowSize, this));
        }
    }

    @Override
    public void removeClient(InetAddress pAddress) {
        if (this.mClients.containsKey(pAddress)) {
            this.mClients.remove(pAddress);
        } else {
            log.warn("Went to remove client: {} from ack window, but client didn't have one!");
        }
    }

    @Override
    public void processReceivedAck(InetAddress pAddress, int pReceivedAckForSequence) {
        if (this.mClients.containsKey(pAddress)) {
            this.mClients.get(pAddress).removeAwaitingAck(pReceivedAckForSequence);
        }
    }

    @Override
    public void processReceivedAck(InetAddress pAddress, int[] pReceivedAckForSequence) {
        if (this.mClients.containsKey(pAddress)) {
            this.mClients.get(pAddress).removeAwaitingAck(pReceivedAckForSequence);
        }

    }

    @Override
    public void addAwaitingAck(InetAddress pAddress, int pSequenceNumber) {
        if (this.mClients.containsKey(pAddress)) {
            this.mClients.get(pAddress).addAwaitingAck(pSequenceNumber);
        }
    }

    @Override
    public void addSentAck(InetAddress pAddress, int pSequenceNumber) {
        if (this.mClients.containsKey(pAddress)) {
            this.mClients.get(pAddress).addSentAck(pSequenceNumber);
        }
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================
    // ===========================================================
    // Methods
    // ===========================================================
    protected void sendMultiAck(final InetAddress pAddress, final int[] pAcksSent) {
        MessageAckMulti ack = (MessageAckMulti) this.mParent.obtainMessage(MessageFlag.ACK_MULTI);
        ack.setRequireAck(false);
        ack.setIntended(IntendedFlag.NETWORK);
        ack.addSequences(pAcksSent);
        this.mParent.sendMessage(pAddress, ack, false);
        this.mParent.recycleMessage(ack);
    }
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
