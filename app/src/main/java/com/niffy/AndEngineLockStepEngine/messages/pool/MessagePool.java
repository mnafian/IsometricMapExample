package com.niffy.AndEngineLockStepEngine.messages.pool;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;

import org.andengine.util.adt.pool.GenericPool;
import org.andengine.util.adt.pool.MultiPool;
import org.andengine.util.debug.Debug;

import com.niffy.AndEngineLockStepEngine.messages.IMessage;

/**
 * (c) 2010 Nicolas Gramlich (c) 2011 Zynga Inc.
 *
 * @author Nicolas Gramlich
 * @since 11:33:23 - 02.03.2011
 */
public class MessagePool<M extends IMessage> {
    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private final MultiPool<M> mMessageMultiPool = new MultiPool<M>();

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    public void registerMessage(final int pFlag, final Class<? extends M> pMessageClass) {
        this.mMessageMultiPool.registerPool(pFlag, new GenericPool<M>() {
            @Override
            protected M onAllocatePoolItem() {
                try {
                    return pMessageClass.newInstance();
                } catch (final Throwable t) {
                    Debug.e(t);
                    return null;
                }
            }
        });
    }

    public void registerMessage(final int pFlag, final Class<? extends M> pMessageClass, final int pInitialSize,
                                final int pGrowth) {
        GenericPool<M> pool = new GenericPool<M>(pInitialSize, pGrowth) {
            @Override
            protected M onAllocatePoolItem() {
                try {
                    return pMessageClass.newInstance();
                } catch (final Throwable t) {
                    Debug.e(t);
                    return null;
                }
            }
        };
        this.mMessageMultiPool.registerPool(pFlag, pool);
    }

    public M obtainMessage(final int pFlag) {
        M msg = this.mMessageMultiPool.obtainPoolItem(pFlag);
        msg.setMessageFlag(pFlag);
        return msg;
    }

    public M obtainMessage(final int pFlag, final DataInputStream pDataInputStream) throws IOException {
        final M message = this.mMessageMultiPool.obtainPoolItem(pFlag);
        if (message != null) {
            message.setMessageFlag(pFlag);
            message.read(pDataInputStream);
            return message;
        } else {
            throw new IllegalArgumentException("No message found for pFlag='" + pFlag + "'.");
        }
    }

    public void recycleMessage(final M pMessage) {
        this.mMessageMultiPool.recyclePoolItem(pMessage.getMessageFlag(), pMessage);
    }

    public void recycleMessages(final List<? extends M> pMessages) {
        final MultiPool<M> messageMultiPool = this.mMessageMultiPool;
        for (int i = pMessages.size() - 1; i >= 0; i--) {
            final M message = pMessages.get(i);
            messageMultiPool.recyclePoolItem(message.getMessageFlag(), message);
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
