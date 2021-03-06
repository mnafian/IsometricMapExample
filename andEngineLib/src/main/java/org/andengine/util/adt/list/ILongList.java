package org.andengine.util.adt.list;

/**
 * (c) 2012 Zynga Inc.
 *
 * @author Nicolas Gramlich <ngramlich@zynga.com>
 * @since 19:36:57 - 03.05.2012
 */
public interface ILongList {
    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    public boolean isEmpty();

    public long get(final int pIndex) throws ArrayIndexOutOfBoundsException;

    public void add(final long pItem);

    public void add(final int pIndex, final long pItem) throws ArrayIndexOutOfBoundsException;

    public long remove(final int pIndex) throws ArrayIndexOutOfBoundsException;

    public int size();

    public void clear();

    public long[] toArray();
}