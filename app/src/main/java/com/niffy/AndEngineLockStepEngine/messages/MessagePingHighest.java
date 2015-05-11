package com.niffy.AndEngineLockStepEngine.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Parcel;
import android.os.Parcelable;

public class MessagePingHighest extends Message {
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    public static final Parcelable.Creator<MessagePingHighest> CREATOR = new Parcelable.Creator<MessagePingHighest>() {

        @Override
        public MessagePingHighest createFromParcel(Parcel source) {
            return new MessagePingHighest(source);
        }

        @Override
        public MessagePingHighest[] newArray(int size) {
            return new MessagePingHighest[size];
        }
    };
    // ===========================================================
    // Constants
    // ===========================================================
    @SuppressWarnings("unused")
    private final Logger log = LoggerFactory.getLogger(MessagePingHighest.class);
    // ===========================================================
    // Fields
    // ===========================================================
    protected String mInetAddressString = "0.0.0.0";
    protected int mInetAddressSize;
    protected byte[] mInetAddressData;
    protected long mHighestPingTime = -1;

    // ===========================================================
    // Constructors
    // ===========================================================
    protected long mTickLength = -1;

    public MessagePingHighest() {
        super();
    }

    public MessagePingHighest(final int pIntended) {
        super(pIntended);
    }

    public MessagePingHighest(final int pIntended, final int pFlag, final int pSequenceNumber) {
        super(pIntended, pFlag, pSequenceNumber);
    }

    public MessagePingHighest(final int pFlag, final int pSequenceNumber) {
        super(pFlag, pSequenceNumber);
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    public MessagePingHighest(Parcel in) {
        super(in);
        this.mInetAddressString = in.readString();
        this.mInetAddressSize = in.readInt();
        in.readByteArray(this.mInetAddressData);
        this.mHighestPingTime = in.readLong();
        this.mTickLength = in.readLong();
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
        this.mInetAddressSize = pDataInputStream.readInt();
        this.mInetAddressData = new byte[this.mInetAddressSize];
        pDataInputStream.read(this.mInetAddressData, 0, this.mInetAddressSize);
        this.mInetAddressString = new String(this.mInetAddressData, "utf-8");
        this.mHighestPingTime = pDataInputStream.readLong();
        this.mTickLength = pDataInputStream.readLong();

    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
        pDataOutputStream.writeInt(this.mInetAddressSize);
        pDataOutputStream.write(this.mInetAddressData);
        pDataOutputStream.writeLong(this.mHighestPingTime);
        pDataOutputStream.writeLong(this.mTickLength);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.mInetAddressString);
        dest.writeInt(this.mInetAddressSize);
        dest.writeByteArray(this.mInetAddressData);
        dest.writeLong(this.mHighestPingTime);
        dest.writeLong(this.mTickLength);
    }

    public long getHighestPingTime() {
        return this.mHighestPingTime;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================
    public void setHighestPingTime(final long pHighestPingTime) {
        this.mHighestPingTime = pHighestPingTime;
    }

    public long getTickLength() {
        return this.mTickLength;
    }
    // ===========================================================
    // Methods
    // ===========================================================

    public void setTickLength(final long pTickLength) {
        this.mTickLength = pTickLength;
    }
}
