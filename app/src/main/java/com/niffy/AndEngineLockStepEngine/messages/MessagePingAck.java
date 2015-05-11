package com.niffy.AndEngineLockStepEngine.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Parcel;
import android.os.Parcelable;

public class MessagePingAck extends Message {
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    public static final Parcelable.Creator<MessagePingAck> CREATOR = new Parcelable.Creator<MessagePingAck>() {

        @Override
        public MessagePingAck createFromParcel(Parcel source) {
            return new MessagePingAck(source);
        }

        @Override
        public MessagePingAck[] newArray(int size) {
            return new MessagePingAck[size];
        }
    };

    // ===========================================================
    // Fields
    // ===========================================================
    // ===========================================================
    // Constructors
    // ===========================================================
    // ===========================================================
    // Constants
    // ===========================================================
    @SuppressWarnings("unused")
    private final Logger log = LoggerFactory.getLogger(MessagePingAck.class);

    public MessagePingAck() {
        super();
    }

    public MessagePingAck(final int pIntended) {
        super(pIntended);
    }

    public MessagePingAck(final int pIntended, final int pFlag, final int pSequenceNumber) {
        super(pIntended, pFlag, pSequenceNumber);
    }

    public MessagePingAck(final int pFlag, final int pSequenceNumber) {
        super(pFlag, pSequenceNumber);
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    public MessagePingAck(Parcel in) {
        super(in);
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================
    // ===========================================================
    // Methods
    // ===========================================================

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }
}
