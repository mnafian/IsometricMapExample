package com.niffy.AndEngineLockStepEngine.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Parcel;
import android.os.Parcelable;

public class MessageClientDisconnect extends Message {
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    public static final Parcelable.Creator<MessageClientDisconnect> CREATOR = new Parcelable.Creator<MessageClientDisconnect>() {

        @Override
        public MessageClientDisconnect createFromParcel(Parcel source) {
            return new MessageClientDisconnect(source);
        }

        @Override
        public MessageClientDisconnect[] newArray(int size) {
            return new MessageClientDisconnect[size];
        }
    };
    // ===========================================================
    // Constants
    // ===========================================================
    @SuppressWarnings("unused")
    private final Logger log = LoggerFactory.getLogger(MessageClientDisconnect.class);
    // ===========================================================
    // Fields
    // ===========================================================
    protected String mInetAddressString = "0.0.0.0";
    protected int mInetAddressSize;
    protected byte[] mInetAddressData;
    protected String mMessageString = "Default Message: Error";
    protected int mMessageStringSize;

    // ===========================================================
    // Constructors
    // ===========================================================
    protected byte[] mMessageStringData;

    public MessageClientDisconnect() {
        super();
    }

    public MessageClientDisconnect(final int pIntended) {
        super(pIntended);
    }

    public MessageClientDisconnect(final int pIntended, final int pFlag, final int pSequenceNumber) {
        super(pIntended, pFlag, pSequenceNumber);
    }

    public MessageClientDisconnect(final int pFlag, final int pSequenceNumber) {
        super(pFlag, pSequenceNumber);
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    public MessageClientDisconnect(Parcel in) {
        super(in);
        this.mInetAddressString = in.readString();
        this.mInetAddressSize = in.readInt();
        in.readByteArray(this.mInetAddressData);
        this.mMessageString = in.readString();
        this.mMessageStringSize = in.readInt();
        in.readByteArray(this.mMessageStringData);
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
        this.mInetAddressSize = pDataInputStream.readInt();
        this.mInetAddressData = new byte[this.mInetAddressSize];
        pDataInputStream.read(this.mInetAddressData, 0, this.mInetAddressSize);
        this.mInetAddressString = new String(this.mInetAddressData, "utf-8");
        this.mMessageStringSize = pDataInputStream.readInt();
        this.mMessageStringData = new byte[this.mMessageStringSize];
        pDataInputStream.read(this.mMessageStringData, 0, this.mMessageStringSize);
        this.mMessageString = new String(this.mMessageStringData, "utf-8");

    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
        pDataOutputStream.writeInt(this.mInetAddressSize);
        pDataOutputStream.write(this.mInetAddressData);
        pDataOutputStream.writeInt(this.mMessageStringSize);
        pDataOutputStream.write(this.mMessageStringData);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.mInetAddressString);
        dest.writeInt(this.mInetAddressSize);
        dest.writeByteArray(this.mInetAddressData);
        dest.writeString(this.mMessageString);
        dest.writeInt(this.mMessageStringSize);
        dest.writeByteArray(this.mMessageStringData);
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

    public String getMessage() {
        return this.mMessageString;
    }

    // ===========================================================
    // Methods
    // ===========================================================

    public void setMessage(final String pMessage) {
        this.mMessageString = pMessage;
        try {
            this.mMessageStringData = this.mMessageString.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.mMessageStringSize = this.mMessageStringData.length;
    }
}
