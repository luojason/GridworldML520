package project2;

import project2.agents.*;
import project2.algorithms.*;
import project2.entity.*;
import project2.utility.*;

import java.text.DecimalFormat;

// just tests the search algos to make sure they work
// usage: java Test xSize ySize blockedProbability%
public class Test {
    public static void printResults(GridWorldInfo result) {
        System.out.println("num cells expanded: " + result.numberOfCellsProcessed);
        System.out.println("trajectory length: " + result.trajectoryLength);
        System.out.println("number of bumps: " + result.numBumps);
        System.out.println("number of (re)-plans: " + result.numPlans);
        System.out.println("number of cells determined: " + result.numCellsDetermined);
        System.out.println("runtime: " + result.runtime + "s");
    }

    public static void printWorld(Grid world) {
        for (int j = 0; j < world.getYSize(); ++j) {
            for (int i = 0; i < world.getXSize(); ++i) {
                GridCell cell = world.getCell(i, j);
                String symbol = "o"; // default symbol
                if (cell.isVisited())
                    symbol = ColorConstant.ANSI_CYAN + "-" + ColorConstant.ANSI_RESET;
                else if (cell.getBlockSentiment() == Sentiment.Free)
                    symbol = ColorConstant.ANSI_GREEN + "o" + ColorConstant.ANSI_RESET;
                else if (cell.getBlockSentiment() == Sentiment.Blocked)
                    symbol = ColorConstant.ANSI_YELLOW + "x" + ColorConstant.ANSI_RESET;
                else if (cell.isBlocked())
                    symbol = ColorConstant.ANSI_RED + "x" + ColorConstant.ANSI_RESET;
                System.out.print(symbol);
            }
            System.out.print('\n');
        }
    }
    public static String generateColorGradient(double prob){
        prob*=100;
        if(prob <= 25)
            return   ColorConstant.ANSI_GREEN + "%" + ColorConstant.ANSI_RESET;
        if(prob <= 50)
            return   ColorConstant.ANSI_YELLOW + "%" + ColorConstant.ANSI_RESET;
        if(prob <= 75)
            return   ColorConstant.ANSI_BLUE + "%" + ColorConstant.ANSI_RESET;
        if(prob <= 100)
            return  ColorConstant.ANSI_RED + "%" + ColorConstant.ANSI_RESET;
        return "";
    }
    public static void printWorld2(Grid world) {
        DecimalFormat df = new DecimalFormat("#.##");
        for (int j = 0; j < world.getYSize(); ++j) {
            for (int i = 0; i < world.getXSize(); ++i) {
                GridCell cell = world.getCell(i, j);
                System.out.print("("+ df.format(cell.getProbBlocked())+")");
            }
            System.out.print('\n');
        }
    }
    public static void printWorld3(Grid world) {
        for (int j = 0; j < world.getYSize(); ++j) {
            for (int i = 0; i < world.getXSize(); ++i) {
                GridCell cell = world.getCell(i, j);
                System.out.print(generateColorGradient(cell.getProbBlocked()));
            }
            System.out.print('\n');
        }
    }

    public static void printMineSweeper(Grid world) {
        for (int j = 0; j < world.getYSize(); j++) {
            for (int i = 0; i < world.getXSize(); i++) {
                GridCell cell = world.getCell(i, j);
                System.out.print(cell.isBlocked() ? ColorConstant.ANSI_RED : ColorConstant.ANSI_RESET); // set color
                System.out.print(cell.getNumSensedBlocked());
            }
            System.out.print('\n');
        }
        System.out.print(ColorConstant.ANSI_RESET);
    }

    public static void main(String[] args) {
        int x = Integer.parseInt(args[0]);
        int y = Integer.parseInt(args[1]);
        int prob = Integer.parseInt(args[2]);
        Grid world = new Grid(x, y, prob);
        Grid world2 = new Grid(world, true);
        Grid world3 = new Grid(world, true);
        Point start = new Point(0, 0);
        Point goal = new Point(x - 1, y - 1);

        SearchAlgo algo = new AStarSearch(Heuristics::manhattanDistance);
        SearchAlgo algo2 = new AStarProbSearch(Heuristics::manhattanDistance);
        InferenceAgent agent = new BasicInferenceAgent();
        InferenceAgent betterAgent = new BetterInferenceAgent();
        InferenceAgent perfectAgent = new PerfectInferenceAgent();

        System.out.println("Prob version:");
        Robot robotp = new Robot(start, goal, agent, world, algo2, false);
        GridWorldInfo resultp = robotp.run();
        printResults(resultp);
        printWorld(world);
        printWorld3(world);
        System.out.println();

        System.out.println("Example version:");
        Robot robot = new Robot(start, goal, agent, world, algo, false);
        GridWorldInfo result = robot.run();
        printResults(result);
        printWorld(world);
       // printWorld2(world);
        //printMineSweeper(world);
        System.out.println();

        System.out.println("'Better' version:");
        Robot robot2 = new Robot(start, goal, betterAgent, world2, algo, false);
        GridWorldInfo result2 = robot2.run();
        printResults(result2);
        printWorld(world2);
        System.out.println();

        System.out.println("'Perfect' version:");
        Robot robot3 = new Robot(start, goal, perfectAgent, world3, algo, false);
        GridWorldInfo result3 = robot3.run();
        printResults(result3);
        printWorld(world3);

    }
}
