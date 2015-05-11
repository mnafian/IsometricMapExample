package com.niffy.AndEngineLockStepEngine.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Parcel;
import android.os.Parcelable;

public class MessageAckMulti extends Message {
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    public static final Parcelable.Creator<MessageAckMulti> CREATOR = new Parcelable.Creator<MessageAckMulti>() {

        @Override
        public MessageAckMulti createFromParcel(Parcel source) {
            return new MessageAckMulti(source);
        }

        @Override
        public MessageAckMulti[] newArray(int size) {
            return new MessageAckMulti[size];
        }
    };
    // ===========================================================
    // Constants
    // ===========================================================
    @SuppressWarnings("unused")
    private final Logger log = LoggerFactory.getLogger(MessageAckMulti.class);

    // ===========================================================
    // Constructors
    // ===========================================================
    // ===========================================================
    // Fields
    // ===========================================================
    protected int[] mAckFor;

    public MessageAckMulti() {
        super();
    }

    public MessageAckMulti(final int pIntended) {
        super(pIntended);
    }

    public MessageAckMulti(final int pIntended, final int pFlag, final int pSequenceNumber) {
        super(pIntended, pFlag, pSequenceNumber);
    }

    public MessageAckMulti(final int pFlag, final int pSequenceNumber) {
        super(pFlag, pSequenceNumber);
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    public MessageAckMulti(Parcel in) {
        super(in);
        in.readIntArray(this.mAckFor);
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
        int size = pDataInputStream.readInt();
        this.mAckFor = new int[size];
        for (int i = 0; i <= size; i++) {
            this.mAckFor[i] = pDataInputStream.readInt();
        }
    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
        pDataOutputStream.writeInt(this.mAckFor.length);
        for (int item : this.mAckFor) {
            pDataOutputStream.writeInt(item);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeIntArray(this.mAckFor);
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================
    public void addSequences(final int[] pSequences) {
        this.mAckFor = pSequences;
    }

    // ===========================================================
    // Methods
    // ===========================================================

    public int[] getSequences() {
        return this.mAckFor;
    }
}
