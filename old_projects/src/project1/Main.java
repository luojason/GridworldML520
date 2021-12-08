package project1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class Main {

    private static void runQuestion4(int xDim, int yDim, int numIter) {
        String fileName = "Q4-AStar-Manhattan-NoBumps-Results.csv";
        runRepeatedSearch(fileName, xDim, yDim, numIter, new AStarSearch(Heuristics::manhattanDistance), true, 100, false);
    }

    private static void runQuestion5(int xDim, int yDim, int numIter, int maxProb) {
        String fileName = "Q5-RepeatedAStar-Manhattan-NoBumps-Results.csv";
        runRepeatedSearch(fileName, xDim, yDim, numIter, new AStarSearch(Heuristics::manhattanDistance), true, maxProb, true);
        fileName = "Q5-RepeatedAStar-Euclidean-NoBumps-Results.csv";
        runRepeatedSearch(fileName, xDim, yDim, numIter, new AStarSearch(Heuristics::euclideanDistance), true, maxProb, true);
        fileName = "Q5-RepeatedAStar-Chebyshev-NoBumps-Results.csv";
        runRepeatedSearch(fileName, xDim, yDim, numIter, new AStarSearch(Heuristics::chebyshevDistance), true, maxProb, true);

        // next, run tests with regular A*, not repeated A*
        // initialize variables needed for searching
        Tuple<Integer, Integer> start = new Tuple<>(0, 0);
        Tuple<Integer, Integer> end = new Tuple<>(xDim-1, yDim-1);

        // initialize search objects and records for each type of heuristic
        ArrayList<SearchAlgo> algos = new ArrayList<>();
        algos.add(new AStarSearch(Heuristics::manhattanDistance));
        algos.add(new AStarSearch(Heuristics::euclideanDistance));
        algos.add(new AStarSearch(Heuristics::chebyshevDistance));
        ArrayList<ArrayList<GridWorldInfo>> results = new ArrayList<>();
        results.add(new ArrayList<>());
        results.add(new ArrayList<>());
        results.add(new ArrayList<>());

        for(int prob = 0; prob <= maxProb; ++prob) {
            for(int iter = 0; iter < numIter; ++iter) {
                // keep generating grids until a solvable one is found
                Grid grid = getSolvableMaze(xDim, yDim, algos.get(0), prob);

                // run A* with each heuristic
                for(int i = 0; i < algos.size(); ++i) {
                    long startTime = System.nanoTime();
                    GridWorldInfo result = algos.get(i).search(start, end, grid, cell -> cell.isBlocked());
                    long endTime = System.nanoTime();
                    result.setProbability(prob);
                    result.setRuntime(endTime - startTime);
                    results.get(i).add(result);
                }
            }
        }

        // print results
        printResultsToCsv("Q5-AStar-Manhattan-NoBumps-Results.csv", results.get(0));
        printResultsToCsv("Q5-AStar-Euclidean-NoBumps-Results.csv", results.get(1));
        printResultsToCsv("Q5-AStar-Chebyshev-NoBumps-Results.csv", results.get(2));
    }

    private static void runQuestion6(int xDim, int yDim, int numIter, int maxProb) {
        String fileName = "Q6-AStar-Manhattan-NoBumps-Results.csv";
        runThreeSearches(fileName, xDim, yDim, numIter, new AStarSearch(Heuristics::manhattanDistance), true, maxProb);

        fileName = "Q6-AStar-Euclidean-NoBumps-Results.csv";
        runThreeSearches(fileName, xDim, yDim, numIter, new AStarSearch(Heuristics::euclideanDistance), true, maxProb);

        fileName = "Q6-AStar-Chebyshev-NoBumps-Results.csv";
        runThreeSearches(fileName, xDim, yDim, numIter, new AStarSearch(Heuristics::chebyshevDistance), true, maxProb);

        fileName = "Q6-BFS-NoBumps-Results.csv";
        runThreeSearches(fileName, xDim, yDim, numIter, BFSObject::BFS, true, maxProb);
    }

    private static void runQuestion7(int xDim, int yDim, int numIter, int maxProb) {
        String fileName = "Q7-AStar-Manhattan-Bumps-Results.csv";
        runThreeSearches(fileName, xDim, yDim, numIter, new AStarSearch(Heuristics::manhattanDistance), false, maxProb);

        fileName = "Q7-AStar-Euclidean-Bumps-Results.csv";
        runThreeSearches(fileName, xDim, yDim, numIter, new AStarSearch(Heuristics::euclideanDistance), false, maxProb);

        fileName = "Q7-AStar-Chebyshev-Bumps-Results.csv";
        runThreeSearches(fileName, xDim, yDim, numIter, new AStarSearch(Heuristics::chebyshevDistance), false, maxProb);

        fileName = "Q7-BFS-Bumps-Results.csv";
        runThreeSearches(fileName, xDim, yDim, numIter, BFSObject::BFS, false, maxProb);
    }

    private static void runQuestion8(int xDim, int yDim, int numIter, int maxProb) {
        for(int i = 1; i < 55; i+=5) {
            String fileName = "Q8Dist=" + i + "-AStar-Manhattan-NoBumps-Results.csv";
            runRepeatedSearch(fileName, xDim, yDim, numIter, new AStarSearch(Heuristics::manhattanDistance), true, maxProb, true, i);
        }

    }

    private static void runQuestion9(int xDim, int yDim, int numIter, int prob, double maxWeight, double step) {
        String name1 = "Q9prob=" + prob + "-AStar-WeightedManhattan-NoBumps-Results.csv";
        String name2 = "Q9prob=" + prob + "-RepeatedAStar-WeightedManhattan-NoBumps-Results.csv";
        runWeightedSearch(name1, name2, xDim, yDim, numIter, Heuristics::manhattanDistance, prob, maxWeight, step);

        name1 = "Q9prob=" + prob + "-AStar-WeightedEuclidean-NoBumps-Results.csv";
        name2 = "Q9prob=" + prob + "-RepeatedAStar-WeightedEuclidean-NoBumps-Results.csv";
        runWeightedSearch(name1, name2, xDim, yDim, numIter, Heuristics::euclideanDistance, prob, maxWeight, step);

        name1 = "Q9prob=" + prob + "-AStar-WeightedChebyshev-NoBumps-Results.csv";
        name2 = "Q9prob=" + prob + "-RepeatedAStar-WeightedChebyshev-NoBumps-Results.csv";
        runWeightedSearch(name1, name2, xDim, yDim, numIter, Heuristics::chebyshevDistance, prob, maxWeight, step);

    }

    /**
     * Main Execution Method
     *
     * @param args Input arguments X - Gird X size, Y - Grid Y size, Iterations - Number of Iterations (defaults to 100)
     */
    // Input args: x, y, number of iterations
    public static void main(String[] args) {
        int x = Integer.parseInt(args[0]);
        int y = Integer.parseInt(args[1]);
        int iterations = args.length > 2 ? Integer.parseInt(args[2]): 100;
        runQuestion4(x,y,iterations);
        runQuestion5(x,y,iterations,30);
        runQuestion6(x, y, iterations, 33);
        runQuestion7(x, y, iterations, 33);
        runQuestion8(x,y,iterations,20);
        runQuestion9(x, y, iterations, 15, 10.0, 0.2);
        runQuestion9(x, y, iterations, 25, 10.0, 0.2);
    }

    /**
     * This method returns a solvable maze for given inputs. It uses a search algo to make sure there is a path to the
     * end.
     *
     * @param xDim Dimension of the Grid's X coord
     * @param yDim Dimension of the Grid's Y coord
     * @param algo Algorithm to use for searching
     * @param prob Probability of a space being blocked
     * @return returns a solvable maze
     */
    public static Grid getSolvableMaze(int xDim, int yDim, SearchAlgo algo, int prob){
        Tuple<Integer, Integer> start = new Tuple<>(0, 0);
        Tuple<Integer, Integer> end = new Tuple<>(xDim-1, yDim-1);
        GridWorldInfo completeResult;
        Grid grid;

        do{
            grid = new Grid(xDim, yDim, prob);
            completeResult = algo.search(start, end, grid, cell -> cell.isBlocked());
        }while (completeResult.getPath() == null);

        return grid;
    }

    /**
     * Overloaded method for running searching without backtracking
     *
     {@link Main#runRepeatedSearch(String, int, int, int, SearchAlgo, boolean, int, boolean, int)}.
     */
    public static void runRepeatedSearch(String fileName, int xDim, int yDim, int numIter, SearchAlgo algo, boolean canSeeSideways, int maxProb, boolean needsToBeSolvable) {
            runRepeatedSearch(fileName, xDim,yDim,numIter,algo,canSeeSideways,maxProb,needsToBeSolvable,1);
    }

    /**
     * This is used for questions 4,5,and 8 A*. It runs the searchs for a given number of iterations and puts there info
     * into a list of {@link GridWorldInfo} and prints to a CSV using {@link #printResultsToCsv(String, List)}  }
     *
     *
     * @param fileName Filename
     * @param xDim XDimension of grid
     * @param yDim YDimension of grid
     * @param numIter Number of iterations
     * @param algo Search Algo to use
     * @param backtrackDistance number of steps for backtracking (Question 8 only)
     * @param needsToBeSolvable Used to see if a grid needs to be solvable for data reporting purposes
     *
     */

    public static void runRepeatedSearch(String fileName, int xDim, int yDim, int numIter, SearchAlgo algo, boolean canSeeSideways, int maxProb, boolean needsToBeSolvable, int backtrackDistance){
        Tuple<Integer, Integer> start = new Tuple<>(0, 0);
        Tuple<Integer, Integer> end = new Tuple<>(xDim-1, yDim-1);

        ArrayList<GridWorldInfo> results = new ArrayList<>((maxProb+1)*numIter);
        for(int prob = 0; prob <= maxProb; ++prob) {
            for (int iter = 0; iter < numIter; ++iter) { // test each probability numIter times
                GridWorldInfo globalInfo = new GridWorldInfo(0, 0, new ArrayList<>()); // holds stats for this iteration
                Grid grid = needsToBeSolvable ? getSolvableMaze(xDim,yDim,algo,prob) : new Grid(xDim, yDim, prob);;

                Robot robot = new Robot(start, end, canSeeSideways, grid, algo);
                long startTime = System.nanoTime();
                GridWorldInfo repeatedResult = robot.run(backtrackDistance);
                long stopTime = System.nanoTime();
                globalInfo.setRuntime(stopTime-startTime);
                globalInfo.setTrajectoryLength(repeatedResult.getTrajectoryLength());
                globalInfo.setNumberOfCellsProcessed(repeatedResult.getNumberOfCellsProcessed());
                globalInfo.setPath(repeatedResult.getPath());
                globalInfo.setProbability(prob);
                globalInfo.setBacktrackSteps(backtrackDistance);
                results.add(globalInfo);
            }
        }
        printResultsToCsv(fileName, results);
    }

    /**
     * This is used for questions 6,7 with bfs and A*. It runs the searchs for a given number of iterations and puts there info
     * into a list of {@link GridWorldInfo} and prints to a CSV using {@link #printResultsToCsv(String, List)}  }
     *
     * @param fileName Filename
     * @param xDim XDimension of grid
     * @param yDim YDimension of grid
     * @param numIter Number of iterations
     * @param algo Search Algo to use
     * @param canSeeSideways Does this algo bump into obstacles
     * @param maxProb Maximum probability to go up to
     *
     */

    public static void runThreeSearches(String fileName, int xDim, int yDim, int numIter, SearchAlgo algo, boolean canSeeSideways, int maxProb) {
        // initialize variables needed for searching
        Tuple<Integer, Integer> start = new Tuple<>(0, 0);
        Tuple<Integer, Integer> end = new Tuple<>(xDim-1, yDim-1);

        ArrayList<GridWorldInfo> results = new ArrayList<>((maxProb+1)*numIter);
        for(int prob = 0; prob <= maxProb; ++prob) {
            for(int iter = 0; iter < numIter; ++iter) { // test each probability numIter times
                GridWorldInfo globalInfo = new GridWorldInfo(0, 0, new ArrayList<>()); // holds stats for this iteration
                Grid grid = getSolvableMaze(xDim,yDim,algo,prob);

                // run Repeated Search and record results
                Robot robot = new Robot(start, end, canSeeSideways, grid, algo);
                GridWorldInfo repeatedResult = robot.run();
                globalInfo.setTrajectoryLength(repeatedResult.getTrajectoryLength());
                globalInfo.setNumberOfCellsProcessed(repeatedResult.getNumberOfCellsProcessed());
                globalInfo.setPath(repeatedResult.getPath());

                // run search over discovered gridworld and record results
                Predicate<GridCell> discoveredAndFree = robot.getKnownFreeSpaces()::contains;
                Predicate<GridCell> undiscoveredOrBlocked = discoveredAndFree.negate();
                GridWorldInfo discoveredResult = algo.search(start, end, grid, undiscoveredOrBlocked);
                globalInfo.setTrajectoryLengthDiscovered(discoveredResult.getTrajectoryLength());

                // run search over complete gridworld and record results
                GridWorldInfo completeResult = algo.search(start, end, grid, cell -> cell.isBlocked());
                globalInfo.setTrajectoryLengthComplete(completeResult.getTrajectoryLength());

                globalInfo.setProbability(prob);
                results.add(globalInfo);
            }
        }

        printResultsToCsv(fileName, results);
    }

    /**
     * This is used for questions 9. It runs the searchs for a given number of iterations and puts there info
     * into a list of {@link GridWorldInfo} and prints to a CSV using {@link #printResultsToCsv(String, List)}  }
     * @param fileName1 First file name
     * @param fileName2 Second file name
     * @param xDim xDimension of Grid
     * @param yDim yDimension of Grid
     * @param numIter Number of Iterations
     * @param heuristic Hueristic to use
     * @param prob Probability of a space being blocked
     * @param maxWeight Maximum weight applied to heuristic
     * @param step Step size for weight increase
     */
    public static void runWeightedSearch(
        String fileName1, String fileName2,
        int xDim, int yDim, int numIter,
        BiFunction<Tuple<Integer, Integer>, Tuple<Integer, Integer>, Double> heuristic,
        int prob, double maxWeight, double step
    ) {
        // initialize variables needed for searching
        Tuple<Integer, Integer> start = new Tuple<>(0, 0);
        Tuple<Integer, Integer> end = new Tuple<>(xDim-1, yDim-1);
        SearchAlgo aStar = new AStarSearch(Heuristics::manhattanDistance); // used to test for solvability

        ArrayList<GridWorldInfo> results = new ArrayList<>(); // for regular A*
        ArrayList<GridWorldInfo> repeatedResults = new ArrayList<>(); // for repeated A*
        for(int iter = 0; iter < numIter; ++iter) {
            // keep generating grids until a solvable one is found
            Grid grid = getSolvableMaze(xDim, yDim, aStar, prob);

            for(int i = 0; 1.0 + i*step <= maxWeight; ++i) {
                // create search object using weighted heuristic
                double weight = 1.0 + i*step;
                SearchAlgo weightedAStar = new AStarSearch((coord1, coord2) -> weight*heuristic.apply(coord1, coord2));

                // run regular A* with the weighted heuristic and record the result
                long startTime = System.nanoTime();
                GridWorldInfo result = weightedAStar.search(start, end, grid, cell -> cell.isBlocked());
                long endTime = System.nanoTime();
                result.setProbability(prob);
                result.setWeight(weight);
                result.setRuntime(endTime - startTime);
                results.add(result);

                // run repeated A* with the weight heuristic snd record the result
                Robot robot = new Robot(start, end, true, grid, weightedAStar);
                startTime = System.nanoTime();
                GridWorldInfo repeatedResult = robot.run();
                endTime = System.nanoTime();
                repeatedResult.setProbability(prob);
                repeatedResult.setWeight(weight);
                repeatedResult.setRuntime(endTime - startTime);
                repeatedResults.add(repeatedResult);
            }
        }

        printResultsToCsv(fileName1, results);
        printResultsToCsv(fileName2, repeatedResults);
    }

    /**
     * Takes a list of GridWorldInfo: {@link GridWorldInfo} and prints it to a pre-designed csv template
     *
     * @param fileName name of the file
     * @param gridWorldInfo List of GridWorldInfos
     */
    public static void printResultsToCsv(String fileName, List<GridWorldInfo> gridWorldInfo){
        try (PrintWriter writer = new PrintWriter(new File(fileName))) {

            StringBuilder sb = new StringBuilder();
            sb.append("Probability");
            sb.append(',');
            sb.append("Solvable");
            sb.append(',');
            sb.append("Runtime");
            sb.append(',');
            sb.append("Path Length (Repeated A*");
            sb.append(',');
            sb.append("Path Length (Discovered)");
            sb.append(',');
            sb.append("Path Length (Complete)");
            sb.append(',');
            sb.append("Number of Cells Processed");
            sb.append(',');
            sb.append("Runtime Weight (EC)");
            sb.append(',');
            sb.append("Backtrack Steps (EC)");
            sb.append('\n');
            writer.write(sb.toString());

            for (GridWorldInfo info: gridWorldInfo) {
                sb = new StringBuilder();
                sb.append(info.getProbability());
                sb.append(',');
                sb.append(!Double.isNaN(info.getTrajectoryLength()));
                sb.append(',');
                sb.append(info.getRuntime());
                sb.append(',');
                sb.append(info.getTrajectoryLength());
                sb.append(',');
                sb.append(info.getTrajectoryLengthDiscovered());
                sb.append(',');
                sb.append(info.getTrajectoryLengthComplete());
                sb.append(',');
                sb.append(info.getNumberOfCellsProcessed());
                sb.append(',');
                sb.append(info.getWeight());
                sb.append(',');
                sb.append(info.getBacktrackSteps());
                sb.append('\n');
                writer.write(sb.toString());
            }

            System.out.println("done!");

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }


}
