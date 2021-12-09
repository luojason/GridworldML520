package project3;

import project3.agents.*;
import project3.algorithms.*;
import project3.entity.*;
import project3.utility.*;

// import java.text.DecimalFormat;

// just tests the search algos to make sure they work
// usage: java Test xSize ySize blockedProbability%
public class Test {
    public static void printResults(GridWorldInfo result) {
        System.out.println("probability: " + result.probability);
        System.out.println("num_steps_taken: " + result.numStepsTaken);
        System.out.println("num_examinations: " + result.numExaminations);
        System.out.println("num_cells_processed: " + result.numberOfCellsProcessed);
        System.out.println("num_bumps: " + result.numBumps);
        System.out.println("num_plans: " + result.numPlans);
        System.out.println("runtime: " + result.runtime + "s");
    }

    public static void printWorld(Grid world) {
        for (int j = 0; j < world.getYSize(); ++j) {
            for (int i = 0; i < world.getXSize(); ++i) {
                GridCell cell = world.getCell(i, j);

                if (cell.isGoal()) {
                    System.out.print(ColorConstant.ANSI_CYAN);
                    switch(cell.getTerrain()) {
                        case Blocked:
                            throw new AssertionError("this code should never be entered");
                        case Flat:
                            System.out.print("F");
                            break;
                        case Hilly:
                            System.out.print("H");
                            break;
                        case Forest:
                            System.out.print("T");
                            break;
                    }
                } else {
                    if(cell.isVisited()) {
                        switch(cell.getTerrain()) {
                            case Blocked:
                                System.out.print(ColorConstant.ANSI_RED);
                                break;
                            case Flat:
                                System.out.print(ColorConstant.ANSI_YELLOW);
                                break;
                            case Hilly:
                                System.out.print(ColorConstant.ANSI_GREEN);
                                break;
                            case Forest:
                                System.out.print(ColorConstant.ANSI_PURPLE);
                                break;
                        }
                    }
                    System.out.print((cell.isBlocked()) ? "x" : "-");
                }
                System.out.print(ColorConstant.ANSI_RESET);
            }
            System.out.print('\n');
        }
    }


    public static void main(String[] args) {
        int xDim = Integer.parseInt(args[0]);
        int yDim = Integer.parseInt(args[1]);
        SearchAlgo algo = new AStarSearch(Heuristics::manhattanDistance);
        int prob = 30;
        Grid world = Main.getSolvableMaze(xDim, yDim, algo, prob);

        Point start = new Point(0, 0);
        
        Robot bot1 = new Robot(start, new Agent6(), new Grid(world, true), algo, false);
        GridWorldInfo info1 = bot1.run();

        Robot bot2 = new Robot(start, new Agent7(), new Grid(world, true), algo, false);
        GridWorldInfo info2 = bot2.run();

        Robot bot3 = new Robot(start, new Agent8(), new Grid(world, true), algo, false);
        GridWorldInfo info3 = bot3.run();

        printResults(info1);
        System.out.println();
        printResults(info2);
        System.out.println();
        printResults(info3);
        // printWorld(world);
    }
}
