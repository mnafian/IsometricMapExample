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

public class MessageError extends Message {
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    public static final Parcelable.Creator<MessageError> CREATOR = new Parcelable.Creator<MessageError>() {

        @Override
        public MessageError createFromParcel(Parcel source) {
            return new MessageError(source);
        }

        @Override
        public MessageError[] newArray(int size) {
            return new MessageError[size];
        }
    };
    // ===========================================================
    // Constants
    // ===========================================================
    @SuppressWarnings("unused")
    private final Logger log = LoggerFactory.getLogger(MessageError.class);
    // ===========================================================
    // Fields
    // ===========================================================
    protected int mErrorCode = -1;
    protected String mString = "Error:";
    protected int mStringSize;

    // ===========================================================
    // Constructors
    // ===========================================================
    protected byte[] mStringData;

    public MessageError() {
        super();
    }

    public MessageError(final int pIntended) {
        super(pIntended);
    }

    public MessageError(final int pIntended, final int pFlag, final int pSequenceNumber) {
        super(pIntended, pFlag, pSequenceNumber);
    }

    public MessageError(final int pFlag, final int pSequenceNumber) {
        super(pFlag, pSequenceNumber);
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    public MessageError(Parcel in) {
        super(in);
        this.mErrorCode = in.readInt();
        this.mString = in.readString();
        this.mStringSize = in.readInt();
        in.readByteArray(this.mStringData);
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
        this.mErrorCode = pDataInputStream.readInt();
        this.mStringSize = pDataInputStream.readInt();
        this.mStringData = new byte[this.mStringSize];
        pDataInputStream.read(this.mStringData, 0, this.mStringSize);
        this.mString = new String(this.mStringData, "utf-8");

    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
        pDataOutputStream.writeInt(this.mErrorCode);
        pDataOutputStream.writeInt(this.mStringSize);
        pDataOutputStream.write(this.mStringData);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.mErrorCode);
        dest.writeString(this.mString);
        dest.writeInt(this.mStringSize);
        dest.writeByteArray(this.mStringData);
    }

    public String getString() {
        return this.mString;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================
    public void setString(String pString) {
        this.mString = pString;
        try {
            this.mStringData = this.mString.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.mStringSize = this.mStringData.length;
    }

    /**
     * @param pErrorCode
     * @see {@link ErrorCodes}
     */
    public int getErrorCode() {
        return this.mErrorCode;
    }

    // ===========================================================
    // Methods
    // ===========================================================

    /**
     * @param pErrorCode
     * @see {@link ErrorCodes}
     */
    public void setErrorCode(final int pErrorCode) {
        this.mErrorCode = pErrorCode;
    }
}
