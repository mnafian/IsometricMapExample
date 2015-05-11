package com.niffy.AndEngineLockStepEngine.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import android.os.Parcel;

import com.niffy.AndEngineLockStepEngine.flags.IntendedFlag;

/**
 * (c) 2010 Nicolas Gramlich (c) 2011 Zynga Inc. (c) 2013 Paul Robinson
 * <p/>
 * The contents of a basic message is at least 16.125 bytes (129 bits / 8)
 *
 * @author Nicolas Gramlich
 * @see IMessage
 * @since 15:27:13 - 18.09.2009
 */
public abstract class Message implements IMessage {
    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================
    protected int mSequenceNumber = -1;
    protected int mIntended = IntendedFlag.CLIENT;
    protected int mFlag = -1;
    protected boolean mRequireACK = true;
    protected int mVersion = -1;

    // ===========================================================
    // Constructors
    // ===========================================================
    public Message() {

    }

    public Message(final int pIntended) {
        this.mIntended = pIntended;
    }

    public Message(final int pIntended, final int pFlag, final int pSequenceNumber) {
        this.mIntended = pIntended;
        this.mFlag = pFlag;
        this.mSequenceNumber = pSequenceNumber;
    }

    public Message(final int pFlag, final int pSequenceNumber) {
        this.mFlag = pFlag;
        this.mSequenceNumber = pSequenceNumber;
    }

    public Message(Parcel in) {
        this.mVersion = in.readInt();
        this.mSequenceNumber = in.readInt();
        this.mRequireACK = in.readByte() == 1;
        this.mIntended = in.readInt();
        this.mFlag = in.readInt();
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    protected abstract void onReadTransmissionData(final DataInputStream pDataInputStream) throws IOException;

    protected abstract void onWriteTransmissionData(final DataOutputStream pDataOutputStream) throws IOException;

    /**
     * For debugging purposes, append all data of this {@link Message} to the
     * {@link StringBuilder}.
     *
     * @param pStringBuilder
     */
    protected void onAppendTransmissionDataForToString(final StringBuilder pStringBuilder) {
        /* Nothing by default. */
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append(this.getClass().getSimpleName()).append("[getFlag()=").append(this.getMessageFlag());

        this.onAppendTransmissionDataForToString(sb);

        sb.append("]");

        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }

        final Message other = (Message) obj;

        return this.getMessageFlag() == other.getMessageFlag();
    }

    @Override
    public int getVersion() {
        return this.mVersion;
    }

    @Override
    public void setVersion(int pVersion) {
        this.mVersion = pVersion;
    }

    @Override
    public int getMessageFlag() {
        return this.mFlag;
    }

    @Override
    public void setMessageFlag(int pFlag) {
        this.mFlag = pFlag;
    }

    @Override
    public void write(final DataOutputStream pDataOutputStream) throws IOException {
        pDataOutputStream.writeInt(this.mVersion);
        pDataOutputStream.writeInt(this.mSequenceNumber);
        pDataOutputStream.writeBoolean(this.mRequireACK);
        pDataOutputStream.writeInt(this.mIntended);
        pDataOutputStream.writeInt(this.mFlag);
        this.onWriteTransmissionData(pDataOutputStream);
        pDataOutputStream.flush();
    }

    @Override
    public void read(final DataInputStream pDataInputStream) throws IOException {
        this.onReadTransmissionData(pDataInputStream);
    }

    @Override
    public int getSequence() {
        return this.mSequenceNumber;
    }

    @Override
    public void setSequence(int pSequence) {
        this.mSequenceNumber = pSequence;
    }

    @Override
    public int getIntended() {
        return this.mIntended;
    }

    @Override
    public void setIntended(int pIntended) {
        this.mIntended = pIntended;
    }

    @Override
    public boolean getRequireAck() {
        return this.mRequireACK;
    }

    @Override
    public void setRequireAck(boolean pRequireAck) {
        this.mRequireACK = pRequireAck;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mVersion);
        dest.writeInt(this.mSequenceNumber);
        dest.writeByte((byte) (this.mRequireACK ? 1 : 0));
        dest.writeInt(this.mIntended);
        dest.writeInt(this.mFlag);
    }

    @Override
    public int describeContents() {

        return 0;
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
