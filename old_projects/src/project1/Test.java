package project1;

import java.util.*;
import java.util.function.Predicate;

// just tests the search algos to make sure they work
// usage: java Test xSize ySize blockedProbability%
public class Test {
    public static void printResults(GridWorldInfo result, Grid world, Robot robot, Predicate<GridCell> isBlocked) {
        System.out.println("num cells expanded: " + result.getNumberOfCellsProcessed());
        System.out.println("trajectory length: " + result.getTrajectoryLength());

        HashSet<GridCell> trajectory = new HashSet<>();
        trajectory.add(world.getCell(new Tuple<Integer, Integer>(0, 0)));
        if(result.getPath() == null) {
            System.out.println("Path not found.");
        } else {
            for(Tuple<Integer, Integer> coord : result.getPath()) {
                trajectory.add(world.getCell(coord));
            }
        }
        for(int j = 0; j < world.getYSize(); ++j) {
            for(int i = 0; i < world.getXSize(); ++i) {
                GridCell cell = world.getCell(new Tuple<>(i, j));
                String symbol = "o"; // default symbol
                if(trajectory.contains(cell)) symbol = "\u001B[36m-\u001B[0m";
                else if(robot.getKnownObstacles().contains(cell)) symbol = "\u001B[33mx\u001B[0m";
                else if(isBlocked.test(cell)) symbol = "\u001B[31mx\u001B[0m";
                System.out.print(symbol);
            }
            System.out.print('\n');
        }
    }

    public static void main(String[] args) {
        int x = Integer.parseInt(args[0]);
        int y = Integer.parseInt(args[1]);
        int prob = Integer.parseInt(args[2]);
        Grid world = new Grid(x, y, prob);

        // test repeated A* search
        System.out.println("Testing Repeated A*...");
        SearchAlgo aso = new AStarSearch(Heuristics::manhattanDistance);
        Tuple<Integer, Integer> start = new Tuple<>(0, 0);
        Tuple<Integer, Integer> end = new Tuple<>(x-1, y-1);
        Robot robot = new Robot(start, end, true, world, aso, false);
        GridWorldInfo result = robot.run();
        printResults(result, world, robot, cell -> cell.isBlocked());
        System.out.println();

        // test regular A* search
        System.out.println("Running A* on discovered gridworld...");
        Predicate<GridCell> discoveredAndFree = robot.getKnownFreeSpaces()::contains;
        Predicate<GridCell> undiscoveredOrBlocked = discoveredAndFree.negate();
        result = aso.search(start, end, world, undiscoveredOrBlocked);
        printResults(result, world, robot, undiscoveredOrBlocked);


    }
}
