package project3;

import project3.agents.*;
import project3.algorithms.*;
import project3.entity.*;
import project3.utility.*;

import java.util.*;
// import java.util.function.BiFunction;
// import java.util.function.Predicate;

public class Main {
    /**
     * Main Execution Method
     *
     * @param args Input arguments X - Grid X size, Y - Entity.Grid Y size,
     *             Iterations - Number of Iterations (defaults to 100)
     */
    public static void main(String[] args) {
        int x = Integer.parseInt(args[0]);
        int y = Integer.parseInt(args[1]);
        int iterations = Integer.parseInt(args[2]); // how many grids to generate

        SearchAlgo algo = new AStarSearch(Heuristics::manhattanDistance);
        DecisionAgent agent = new Agent7();
        Point start = new Point(0, 0);
        // Point end = new Point(x-1, y-1);

        for(int i = 0; i < iterations; i++) {
            Grid world = getSolvableMaze(x, y, algo, 30);
            Robot rob = new Robot(start, agent, world, algo, true, 0.2);
            rob.run();
        }
    }

    

    /**
     * This method returns a solvable maze for given inputs. It uses a search algo
     * to make sure there is a path to the end.
     *
     * @param xDim Dimension of the Entity.Grid's X coord
     * @param yDim Dimension of the Entity.Grid's Y coord
     * @param algo Algorithm to use for searching
     * @param prob Probability of a space being blocked
     * @return returns a solvable maze
     */
    public static Grid getSolvableMaze(int xDim, int yDim, SearchAlgo algo, int prob) {
        Point start = new Point(0, 0);
        Tuple<List<Point>, Integer> completeResult;
        Grid grid;

        do {
            grid = new Grid(xDim, yDim);
            completeResult = algo.search(start, grid.getGoal(), grid, cell -> cell.isBlocked());
        } while (completeResult.f1 == null);

        return grid;
    }
}
