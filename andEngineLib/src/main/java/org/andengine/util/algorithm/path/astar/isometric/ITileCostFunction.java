package org.andengine.util.algorithm.path.astar.isometric;

import org.andengine.util.algorithm.path.ICostFunction;


/**
 * Derived from {@link ICostFunction}
 *
 * @param <T>
 * @author Paul Robinson
 * @since 6 Sep 2012 15:41:57
 */
public interface ITileCostFunction<T> {
    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    public float getCost(final ITilePathFinderMap<T> pTilePathFinderMap, final int pFromRow, final int pFromCol, final int pToRow, final int pToCol, final T pEntity);
}
