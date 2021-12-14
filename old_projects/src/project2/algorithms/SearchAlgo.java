package project2.algorithms;

import java.util.function.Predicate;

import project2.entity.Grid;
import project2.entity.GridCell;
import project2.entity.GridWorldInfo;
import project2.utility.Point;

/**
 * A general interface for describing search algorithm.
 */
@FunctionalInterface
public interface SearchAlgo {
    /**
     * Attempts to find a path through the gridworld.
     * 
     * @param start     Start Location
     * @param end       End Location
     * @param grid      Grid to Search
     * @param isBlocked Function to check whether cells are blocked
     * @return The path found (if any) as well as some runtime statistics
     *         ({@link project2.entity.GridWorldInfo}).
     */
    GridWorldInfo search(Point start, Point end, Grid grid, Predicate<GridCell> isBlocked);
}
