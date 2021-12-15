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

public class Simulator {
    /**
     * Main Execution Method
     *
     * @param args Input arguments X - Grid X size, Y - Entity.Grid Y size,
     *             Iterations - Number of Iterations (defaults to 100)
     */
    public static void main(String[] args) {
        int xDim = Integer.parseInt(args[0]);
        int yDim = Integer.parseInt(args[1]);
        int iterations = Integer.parseInt(args[2]);
        generateData(xDim, yDim, iterations, 30);
    }

    public static void generateData(int xDim, int yDim, int iterations, int prob) {
        SearchAlgo algo = new AStarSearch(Heuristics::manhattanDistance);
        Point start = new Point(0, 0);
        DecisionAgent agent = new Agent7();

        ArrayList<GridWorldInfo> results = new ArrayList<>();
        for(int i = 0; i < iterations; i++) {
            Grid world = getSolvableMaze(xDim, yDim, algo, prob);
            printMaze(world);
            for(int trial = 0; trial < 3; trial++) {
                Robot rob = new Robot(start, agent, new Grid(world, true), algo, false, 0);
                results.add(rob.run());
            }
        }

        printResultsToCsv("p3-realagent.csv", results);
    }

    public static void printMaze(Grid world) {
        for(int y = 0; y < world.getYSize(); y++) {
            for(int x = 0; x < world.getXSize(); x++) {
                Point pt = new Point(x, y);
                GridCell cell = world.getCell(pt);
                switch (cell.getTerrain()) {
                    case Blocked:
                        System.out.print("0 ");
                        break;
                    case Forest:
                        System.out.print("1 ");
                        break;
                    case Hilly:
                        System.out.print("3 ");
                        break;
                    case Flat:
                        System.out.print("4 ");
                        break;
                }
            }
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

        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

}
