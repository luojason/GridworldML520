package project3;

import project3.agents.*;
import project3.algorithms.*;
import project3.entity.*;
import project3.utility.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
        int xDim = Integer.parseInt(args[0]);
        int yDim = Integer.parseInt(args[1]);
        int numGrid = Integer.parseInt(args[2]); // how many grids to generate
        int iterPerGrid = Integer.parseInt(args[3]); // how many times to solve each grid
        String prefix = args[4];

        runTests(xDim, yDim, numGrid, iterPerGrid, prefix, 30);
    }

    /**
     * 
     * @param xDim
     * @param yDim
     * @param numGrid     How many grids to generate
     * @param iterPerGrid How many times to solve each grid
     * @param prefix      Prefix for generated csv files
     * @param prob
     */
    public static void runTests(int xDim, int yDim, int numGrid, int iterPerGrid, String prefix, int prob) {
        // parameters common to each agent
        SearchAlgo algo = new AStarSearch(Heuristics::manhattanDistance);
        Point start = new Point(0, 0);

        // initialize agents
        DecisionAgent agent6 = new Agent6();
        DecisionAgent agent7 = new Agent7();
        DecisionAgent agent8 = new Agent8();

        ArrayList<GridWorldInfo> result6 = new ArrayList<>(numGrid * iterPerGrid);
        ArrayList<GridWorldInfo> result7 = new ArrayList<>(numGrid * iterPerGrid);
        ArrayList<GridWorldInfo> result8 = new ArrayList<>(numGrid * iterPerGrid);

        for (int i = 0; i < numGrid; ++i) {
            Grid grid = getSolvableMaze(xDim, yDim, algo, 30);
            for (int j = 0; j < iterPerGrid; ++j) {
                Robot robot6 = new Robot(start, agent6, new Grid(grid, true), algo);
                Robot robot7 = new Robot(start, agent7, new Grid(grid, true), algo);
                Robot robot8 = new Robot(start, agent8, new Grid(grid, true), algo);

                result6.add(robot6.run());
                result7.add(robot7.run());
                result8.add(robot8.run());
            }
        }

        printResultsToCsv(prefix + "agent6-" + xDim + "x" + yDim + "-" + numGrid + "x" + iterPerGrid + ".csv", result6);
        printResultsToCsv(prefix + "agent7-" + xDim + "x" + yDim + "-" + numGrid + "x" + iterPerGrid + ".csv", result7);
        printResultsToCsv(prefix + "agent8-" + xDim + "x" + yDim + "-" + numGrid + "x" + iterPerGrid + ".csv", result8);
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

    /**
     * Takes a list of Entity.GridWorldInfo: {@link GridWorldInfo} and prints it to
     * a pre-designed csv template
     *
     * @param fileName      name of the file
     * @param gridWorldInfo List of GridWorldInfos
     */
    public static void printResultsToCsv(String fileName, List<GridWorldInfo> gridWorldInfo) {
        try (PrintWriter writer = new PrintWriter(new File(fileName))) {

            StringBuilder sb = new StringBuilder();
            sb.append("probability");
            sb.append(',');
            sb.append("num_steps_taken");
            sb.append(',');
            sb.append("num_examinations");
            sb.append(',');
            sb.append("num_cells_processed");
            sb.append(',');
            sb.append("num_bumps");
            sb.append(',');
            sb.append("num_plans");
            sb.append(',');
            sb.append("runtime");
            sb.append('\n');
            writer.write(sb.toString());

            for (GridWorldInfo info : gridWorldInfo) {
                sb = new StringBuilder();
                sb.append(info.probability);
                sb.append(',');
                sb.append(info.numStepsTaken);
                sb.append(',');
                sb.append(info.numExaminations);
                sb.append(',');
                sb.append(info.numberOfCellsProcessed);
                sb.append(',');
                sb.append(info.numBumps);
                sb.append(',');
                sb.append(info.numPlans);
                sb.append(',');
                sb.append(info.runtime);
                sb.append('\n');
                writer.write(sb.toString());
            }

            System.out.println("done!");

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

}
