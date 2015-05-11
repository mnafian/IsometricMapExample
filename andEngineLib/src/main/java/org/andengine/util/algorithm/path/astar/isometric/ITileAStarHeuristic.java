package org.andengine.util.algorithm.path.astar.isometric;

import org.andengine.util.algorithm.path.astar.IAStarHeuristic;


/**
 * Derived from {@link IAStarHeuristic}
 *
 * @param <T>
 * @author Paul Robinson
 * @since 6 Sep 2012 15:45:30
 */
public interface ITileAStarHeuristic<T> {
    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    public float getExpectedRestCost(final ITilePathFinderMap<T> pPathFinderMap, final T pEntity, final int pFromRow, final int pFromCol, final int pToRow, final int pToCol);
}
