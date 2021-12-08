package project2;

import project2.agents.*;
import project2.algorithms.*;
import project2.entity.*;
import project2.utility.*;

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
        int x = Integer.parseInt(args[0]);
        int y = Integer.parseInt(args[1]);
        int iterations = args.length > 2 ? Integer.parseInt(args[2]): 100;
        runTests(x, y, iterations, 33);
    }

    /**
     * Runs all the tests for each agent, and writes the results to files
     * 
     * @param xDim    width of the generated grids
     * @param yDim    height of the generated grids
     * @param numIter number of iterations per density value
     * @param maxProb test densities from 0 to maxProb (inclusive)
     */
    public static void runTests(int xDim, int yDim, int numIter, int maxProb) {
        // create search algorithm (used by all agents)
        SearchAlgo algo = new AStarSearch(Heuristics::manhattanDistance);
        SearchAlgo algo2 = new AStarProbSearch(Heuristics::manhattanDistance);
        // create each agent
        InferenceAgent blindfolded = new BlindfoldedAgent();
        InferenceAgent fourNeighbour = new FourNeighbourAgent();
        InferenceAgent basicInference = new BasicInferenceAgent();
        InferenceAgent betterInference = new BetterInferenceAgent();
        InferenceAgent perfectInference = new PerfectInferenceAgent(2);

        // create lists to store results
        ArrayList<GridWorldInfo> blindfoldedResults= new ArrayList<>((maxProb + 1) * numIter);
        ArrayList<GridWorldInfo> fourNeighbourResults= new ArrayList<>((maxProb + 1) * numIter);
        ArrayList<GridWorldInfo> basicInferenceResults= new ArrayList<>((maxProb + 1) * numIter);
        ArrayList<GridWorldInfo> ratioInferenceResults= new ArrayList<>((maxProb + 1) * numIter);
        ArrayList<GridWorldInfo> betterInferenceResults= new ArrayList<>((maxProb + 1) * numIter);
        ArrayList<GridWorldInfo> perfectInferenceResults= new ArrayList<>((maxProb + 1) * numIter);

        // run tests
        for (int prob = 0; prob <= maxProb; prob++) {
            for (int iter = 0; iter < numIter; iter++) {
                Grid grid = getSolvableMaze(xDim, yDim, algo, prob);
                Point start = new Point(0, 0);
                Point goal = new Point(xDim-1, yDim-1);

                Robot blindfoldedRobot = new Robot(start, goal, blindfolded, new Grid(grid, true), algo);
                GridWorldInfo blindfoldedResult = blindfoldedRobot.run();
                blindfoldedResult.probability = prob;
                blindfoldedResults.add(blindfoldedResult);
                
                Robot fourNeighbourRobot = new Robot(start, goal, fourNeighbour, new Grid(grid, true), algo);
                GridWorldInfo fourNeighbourResult = fourNeighbourRobot.run();
                fourNeighbourResult.probability = prob;
                fourNeighbourResults.add(fourNeighbourResult);
                
                Robot basicInferenceRobot = new Robot(start, goal, basicInference, new Grid(grid, true), algo);
                GridWorldInfo basicResult = basicInferenceRobot.run();
                basicResult.probability = prob;
                basicInferenceResults.add(basicResult);

                Robot ratioInferenceRobot = new Robot(start, goal, basicInference, new Grid(grid, true), algo2);
                GridWorldInfo ratioResult = ratioInferenceRobot.run();
                basicResult.probability = prob;
                ratioInferenceResults.add(ratioResult);
                
                Robot betterInferenceRobot = new Robot(start, goal, betterInference, new Grid(grid, true), algo);
                GridWorldInfo betterResult = betterInferenceRobot.run();
                betterResult.probability = prob;
                betterInferenceResults.add(betterResult);

                Robot perfectInferenceRobot = new Robot(start, goal, perfectInference, grid, algo);
                GridWorldInfo perfectResult = perfectInferenceRobot.run();
                perfectResult.probability = prob;
                perfectInferenceResults.add(perfectResult);
            }
        }

        // output results
        printResultsToCsv("blindfolded.csv", blindfoldedResults);
        printResultsToCsv("fourNeighbour.csv", fourNeighbourResults);
        printResultsToCsv("basicInference.csv", basicInferenceResults);
        printResultsToCsv("betterInference.csv", betterInferenceResults);
        printResultsToCsv("perfectInference-d3.csv", perfectInferenceResults);
        printResultsToCsv("ratioInference.csv", ratioInferenceResults);
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
        Point end = new Point(xDim - 1, yDim - 1);
        GridWorldInfo completeResult;
        Grid grid;

        do {
            grid = new Grid(xDim, yDim, prob);
            completeResult = algo.search(start, end, grid, cell -> cell.isBlocked());
        } while (completeResult.path == null);

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
            sb.append("Probability");
            sb.append(',');
            sb.append("Solvable");
            sb.append(',');
            sb.append("Runtime");
            sb.append(',');
            sb.append("Path Length");
            sb.append(',');
            sb.append("Number of Cells Processed");
            sb.append(',');
            sb.append("Number of Bumps");
            sb.append(',');
            sb.append("Number of Planning Steps");
            sb.append(',');
            sb.append("Number of Cells Determined");
            sb.append('\n');
            writer.write(sb.toString());

            for (GridWorldInfo info : gridWorldInfo) {
                sb = new StringBuilder();
                sb.append(info.probability);
                sb.append(',');
                sb.append(!Double.isNaN(info.trajectoryLength));
                sb.append(',');
                sb.append(info.runtime);
                sb.append(',');
                sb.append(info.trajectoryLength);
                sb.append(',');
                sb.append(info.numberOfCellsProcessed);
                sb.append(',');
                sb.append(info.numBumps);
                sb.append(',');
                sb.append(info.numPlans);
                sb.append(',');
                sb.append(info.numCellsDetermined);
                sb.append('\n');
                writer.write(sb.toString());
            }

            System.out.println("done!");

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

}
