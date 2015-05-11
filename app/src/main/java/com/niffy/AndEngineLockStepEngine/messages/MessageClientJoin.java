package com.niffy.AndEngineLockStepEngine.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Parcel;
import android.os.Parcelable;

public class MessageClientJoin extends Message {
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    public static final Parcelable.Creator<MessageClientJoin> CREATOR = new Parcelable.Creator<MessageClientJoin>() {

        @Override
        public MessageClientJoin createFromParcel(Parcel source) {
            return new MessageClientJoin(source);
        }

        @Override
        public MessageClientJoin[] newArray(int size) {
            return new MessageClientJoin[size];
        }
    };
    // ===========================================================
    // Constants
    // ===========================================================
    @SuppressWarnings("unused")
    private final Logger log = LoggerFactory.getLogger(MessageClientJoin.class);
    // ===========================================================
    // Fields
    // ===========================================================
    protected String mInetAddressString = "0.0.0.0";
    protected int mInetAddressSize;
    protected byte[] mInetAddressData;
    protected String mClientNameString = "DefaultName: Error";
    protected int mClientNameSize;

    // ===========================================================
    // Constructors
    // ===========================================================
    protected byte[] mClientNameData;

    public MessageClientJoin() {
        super();
    }

    public MessageClientJoin(final int pIntended) {
        super(pIntended);
    }

    public MessageClientJoin(final int pIntended, final int pFlag, final int pSequenceNumber) {
        super(pIntended, pFlag, pSequenceNumber);
    }

    public MessageClientJoin(final int pFlag, final int pSequenceNumber) {
        super(pFlag, pSequenceNumber);
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    public MessageClientJoin(Parcel in) {
        super(in);
        this.mInetAddressString = in.readString();
        this.mInetAddressSize = in.readInt();
        in.readByteArray(this.mInetAddressData);
        this.mClientNameString = in.readString();
        this.mClientNameSize = in.readInt();
        in.readByteArray(this.mClientNameData);
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
        this.mInetAddressSize = pDataInputStream.readInt();
        this.mInetAddressData = new byte[this.mInetAddressSize];
        pDataInputStream.read(this.mInetAddressData, 0, this.mInetAddressSize);
        this.mInetAddressString = new String(this.mInetAddressData, "utf-8");
        this.mClientNameSize = pDataInputStream.readInt();
        this.mClientNameData = new byte[this.mClientNameSize];
        pDataInputStream.read(this.mClientNameData, 0, this.mClientNameSize);
        this.mClientNameString = new String(this.mClientNameData, "utf-8");

    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
        pDataOutputStream.writeInt(this.mInetAddressSize);
        pDataOutputStream.write(this.mInetAddressData);
        pDataOutputStream.writeInt(this.mClientNameSize);
        pDataOutputStream.write(this.mClientNameData);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.mInetAddressString);
        dest.writeInt(this.mInetAddressSize);
        dest.writeByteArray(this.mInetAddressData);
        dest.writeString(this.mClientNameString);
        dest.writeInt(this.mClientNameSize);
        dest.writeByteArray(this.mClientNameData);
    }

    public String getClientInetAddress() {
        return this.mInetAddressString;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================
    public void setClientInetAddress(final String pAddress) {
        this.mInetAddressString = pAddress;
        try {
            this.mInetAddressData = this.mInetAddressString.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.mInetAddressSize = this.mInetAddressData.length;
    }

    public String getClientName() {
        return this.mClientNameString;
    }

    // ===========================================================
    // Methods
    // ===========================================================

    public void setClientName(final String pName) {
        this.mClientNameString = pName;
        try {
            this.mClientNameData = this.mClientNameString.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.mClientNameSize = this.mClientNameData.length;
    }
}
