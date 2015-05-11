package com.niffy.AndEngineLockStepEngine.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niffy.AndEngineLockStepEngine.flags.ErrorCodes;

import android.os.Parcel;
import android.os.Parcelable;

public class MessageOutOfSyncWith extends Message {
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    public static final Parcelable.Creator<MessageOutOfSyncWith> CREATOR = new Parcelable.Creator<MessageOutOfSyncWith>() {

        @Override
        public MessageOutOfSyncWith createFromParcel(Parcel source) {
            return new MessageOutOfSyncWith(source);
        }

        @Override
        public MessageOutOfSyncWith[] newArray(int size) {
            return new MessageOutOfSyncWith[size];
        }
    };
    // ===========================================================
    // Constants
    // ===========================================================
    private final Logger log = LoggerFactory.getLogger(MessageOutOfSyncWith.class);
    // ===========================================================
    // Fields
    // ===========================================================
    protected String mWhoIsOutOfSync = "Error:";
    protected int mWhoIsOutOfSyncSize;
    protected byte[] mWhoIsOutOfSyncData;
    protected String mSender = "Error:";
    protected int mSenderSize;

    // ===========================================================
    // Constructors
    // ===========================================================
    protected byte[] mSenderData;

    public MessageOutOfSyncWith() {
        super();
    }

    public MessageOutOfSyncWith(final int pIntended) {
        super(pIntended);
    }

    public MessageOutOfSyncWith(final int pIntended, final int pFlag, final int pSequenceNumber) {
        super(pIntended, pFlag, pSequenceNumber);
    }

    public MessageOutOfSyncWith(final int pFlag, final int pSequenceNumber) {
        super(pFlag, pSequenceNumber);
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    public MessageOutOfSyncWith(Parcel in) {
        super(in);
        this.mWhoIsOutOfSync = in.readString();
        this.mWhoIsOutOfSyncSize = in.readInt();
        in.readByteArray(this.mWhoIsOutOfSyncData);
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
        this.mWhoIsOutOfSyncSize = pDataInputStream.readInt();
        this.mWhoIsOutOfSyncData = new byte[this.mWhoIsOutOfSyncSize];
        pDataInputStream.read(this.mWhoIsOutOfSyncData, 0, this.mWhoIsOutOfSyncSize);
        this.mWhoIsOutOfSync = new String(this.mWhoIsOutOfSyncData, "utf-8");
        this.mSenderSize = pDataInputStream.readInt();
        this.mSenderData = new byte[this.mSenderSize];
        pDataInputStream.read(this.mSenderData, 0, this.mSenderSize);
        this.mSender = new String(this.mSenderData, "utf-8");
    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
        pDataOutputStream.writeInt(this.mWhoIsOutOfSyncSize);
        pDataOutputStream.write(this.mWhoIsOutOfSyncData);
        pDataOutputStream.writeInt(this.mSenderSize);
        pDataOutputStream.write(this.mSenderData);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.mWhoIsOutOfSync);
        dest.writeInt(this.mWhoIsOutOfSyncSize);
        dest.writeByteArray(this.mWhoIsOutOfSyncData);
        dest.writeString(this.mSender);
        dest.writeInt(this.mSenderSize);
        dest.writeByteArray(this.mSenderData);
    }

    public String getWhoIsOutOfSync() {
        return this.mWhoIsOutOfSync;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================
    public void setWhoIsOutOfSync(String pString) {
        this.mWhoIsOutOfSync = pString;
        try {
            this.mWhoIsOutOfSyncData = this.mWhoIsOutOfSync.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            log.error("Could not read in who is out of sync.", e);
        }
        this.mWhoIsOutOfSyncSize = this.mWhoIsOutOfSyncData.length;
    }

    public String getSender() {
        return this.mSender;
    }

    // ===========================================================
    // Methods
    // ===========================================================

    public void setSender(final String pString) {
        this.mSender = pString;
        try {
            this.mSenderData = this.mSender.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            log.error("Could not read in sender.", e);
        }
        this.mSenderSize = this.mSenderData.length;
    }
}
