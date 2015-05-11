package com.niffy.AndEngineLockStepEngine.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import android.os.Parcelable;

import com.niffy.AndEngineLockStepEngine.flags.IntendedFlag;
import com.niffy.AndEngineLockStepEngine.flags.MessageFlag;

/**
 * (c) 2010 Nicolas Gramlich (c) 2011 Zynga Inc. (c) 2013 Paul Robinson
 *
 * @author Nicolas Gramlich
 * @since 18:24:50 - 19.09.2009
 */
public interface IMessage extends Parcelable {
    // ===========================================================
    // Methods
    // ===========================================================

    /**
     * Get the version number of network library code.
     *
     * @return
     */
    public int getVersion();

    /**
     * Set the version number of network library code.
     *
     * @param pVersion
     */
    public void setVersion(final int pVersion);

    /**
     * {@link MessageFlag} of what the message is
     *
     * @return
     */
    public int getMessageFlag();

    /**
     * {@link MessageFlag} of what the message is
     *
     * @param pFlag
     */
    public void setMessageFlag(final int pFlag);

    /**
     * This message is intended for {@link IntendedFlag#CLIENT} or
     * {@link IntendedFlag#LOCKSTEP}
     *
     * @return
     */
    public int getIntended();

    /**
     * This message is intended for {@link IntendedFlag#CLIENT} or
     * {@link IntendedFlag#LOCKSTEP}
     *
     * @param pIntended
     */
    public void setIntended(final int pIntended);

    /**
     * Set the message sequence
     *
     * @return
     */
    public int getSequence();

    /**
     * Set the message sequence
     *
     * @param pSequence
     */
    public void setSequence(final int pSequence);

    /**
     * Does the message require an acknowledgement
     */
    public boolean getRequireAck();

    /**
     * Does the message require an acknowledgement
     *
     * @param pRequireAck
     */
    public void setRequireAck(final boolean pRequireAck);

    /**
     * Before reading in a message, you should do read it in the follow way. *
     * <ol>
     * 1. {@link DataInputStream#readInt()} read in the network library version.
     * </ol>
     * <ol>
     * 2. {@link DataInputStream#readInt()} read in the message sequence.
     * </ol>
     * <ol>
     * 3. {@link DataInputStream#readBoolean()} read in if the message requires
     * an ack.
     * </ol>
     * <ol>
     * 4. {@link DataInputStream#readInt()} to get the intended flag.
     * </ol>
     * <ol>
     * 5. {@link DataInputStream#readInt()} to get the message flag.
     * </ol>
     * Once the steps have been completed you can then call this to read the
     * message into the correct object.
     *
     * @param pDataInputStream
     * @throws IOException
     */
    public void read(final DataInputStream pDataInputStream) throws IOException;

    /**
     * This will write in the following order *
     * <ol>
     * 1. {@link DataOutputStream#writeInt(int)} for the network library version
     * </ol>
     * <ol>
     * 2. {@link DataOutputStream#writeInt(int)} for the sequence number. flag.
     * </ol>
     * <ol>
     * 3. {@link DataOutputStream#writeBoolean(boolean)} if the message requires
     * an ack.
     * </ol>
     * <ol>
     * 4. {@link DataOutputStream#writeInt(int)} for writing the intended flag.
     * </ol>
     * <ol>
     * 5. {@link DataOutputStream#writeInt(int)} for writing the message flag.
     * </ol>
     * Writing will then be passed to
     * {@link #onWriteTransmissionData(DataOutputStream)}
     *
     * @param pDataOutputStream
     * @throws IOException
     */
    public void write(final DataOutputStream pDataOutputStream) throws IOException;

}
