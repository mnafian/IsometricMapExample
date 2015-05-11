package com.niffy.AndEngineLockStepEngine.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Parcel;
import android.os.Parcelable;

public class MessageEncapsulated extends Message {
    // ===========================================================
    // Methods
    // ===========================================================
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    public static final Parcelable.Creator<MessageEncapsulated> CREATOR = new Parcelable.Creator<MessageEncapsulated>() {

        @Override
        public MessageEncapsulated createFromParcel(Parcel source) {
            return new MessageEncapsulated(source);
        }

        @Override
        public MessageEncapsulated[] newArray(int size) {
            return new MessageEncapsulated[size];
        }
    };
    // ===========================================================
    // Constants
    // ===========================================================
    @SuppressWarnings("unused")
    private final Logger log = LoggerFactory.getLogger(MessageEncapsulated.class);
    // ===========================================================
    // Fields
    // ===========================================================
    protected byte[] mData;

    // ===========================================================
    // Constructors
    // ===========================================================
    protected int mDataSize = -1;

    public MessageEncapsulated() {
        super();
    }

    public MessageEncapsulated(final int pIntended) {
        super(pIntended);
    }

    public MessageEncapsulated(final int pIntended, final int pFlag, final int pSequenceNumber) {
        super(pIntended, pFlag, pSequenceNumber);
    }

    public MessageEncapsulated(final int pFlag, final int pSequenceNumber) {
        super(pFlag, pSequenceNumber);
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    public MessageEncapsulated(Parcel in) {
        super(in);
        this.mDataSize = in.readInt();
        in.readByteArray(this.mData);
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
        this.mDataSize = pDataInputStream.readInt();
        this.mData = new byte[this.mDataSize];
        pDataInputStream.read(this.mData, 0, this.mDataSize);
    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
        pDataOutputStream.writeInt(this.mDataSize);
        pDataOutputStream.write(this.mData);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.mDataSize);
        dest.writeByteArray(this.mData);
    }

    public byte[] getData() {
        return this.mData;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================
    public void setData(final byte[] pData) {
        this.mData = pData;
        this.mDataSize = this.mData.length;
    }
}
