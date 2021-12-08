package project1;

import java.util.function.Predicate;

@FunctionalInterface
public interface SearchAlgo {
    GridWorldInfo search(Tuple<Integer, Integer> start, Tuple<Integer, Integer> end, Grid grid, Predicate<GridCell> isBlocked);
}
