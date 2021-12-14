package project2.entity;

import project2.agents.InferenceAgent;
import project2.algorithms.SearchAlgo;
import project2.utility.Point;
import project2.utility.Sentiment;
import project2.utility.Tuple;

import java.util.List;

/**
 * Represents the actual agent that will move through the gridworld.
 */
public class Robot {
    private Point current;
    private Point goal;
    private InferenceAgent agent;
    private Grid cleanKB; // copy of knowledge without robot's inferences
    private Grid kb; // knowledge base
    private SearchAlgo searchAlgo;
    private boolean verbose;
    private static double NANO_SECONDS = 1000000000d;

    /**
     * Constructs the agent with the specified parameters.
     * 
     * @param start      Where the agent starts
     * @param goal       Where the agent is trying to reach
     * @param agent      The algorithm used to make inferences/learn about the
     *                   surroundings
     * @param grid       The initial knowledge base
     * @param searchAlgo The algorithm used to path-plan
     */
    public Robot(Point start, Point goal, InferenceAgent agent, Grid grid, SearchAlgo searchAlgo, boolean verbose) {
        this.current = start;
        this.goal = goal;
        this.agent = agent;
        this.kb = grid;
        this.cleanKB = new Grid(grid, true);
        this.searchAlgo = searchAlgo;
        this.verbose = verbose;

        cleanKB.setSentiment(start, Sentiment.Free);
        kb.setSentiment(start, Sentiment.Free); // the agent starts off knowing that the start is unblocked
        kb.setCell(start).setVisited(true);
        this.agent.learn(this.kb, this.current);
    }

    public Point getLocation() {
        return current;
    }

    public Point getGoal() {
        return goal;
    }

    public void move(Point pos) {
        current = pos;
    }

    public Grid getKnowledgeBase() {
        return kb;
    }

    public SearchAlgo getSearchAlgo() {
        return searchAlgo;
    }

    /**
     * Attempts to follow a path, making inferences/learning along the way. Stops
     * prematurely if it detects or bumps into an obstacle.
     * 
     * @param path The path to follow
     * @return The number of steps taken before stopping, and whether the agent
     *         bumped into an obstacle
     */
    private Tuple<Integer, Boolean> runPath(List<Point> path) {
        int numStepsTaken = 0;
        boolean bumped = false;
        boolean obstructed = false;
        while (!path.isEmpty() && !obstructed) {
            Point position = path.get(0);
            GridCell nextCell = kb.getCell(position);

            // output data
            if(verbose) {
                System.out.print(getGridState());
                System.err.print(getDirectionCode(position));
            }

            // attempt to move into next space,
            // and update KB accordingly based on if cell was blocked or empty
            if (nextCell.isBlocked()) {
                cleanKB.setSentiment(position, Sentiment.Blocked);
                kb.setSentiment(position, Sentiment.Blocked);
                bumped = true;
            } else {
                cleanKB.setSentiment(position, Sentiment.Free);
                kb.setSentiment(position, Sentiment.Free);

                move(position);

                cleanKB.getCell(position).setVisited(true);
                nextCell.setVisited(true); // mark cell as visited (implies we've sensed the info in that cell)
                numStepsTaken++;
                path.remove(0);
            }
            agent.learn(kb, current); // learn anything possible by running the inference algorithm

            // check if any steps on remaining path are confirmed to be blocked
            for (Point p : path) {
                if (kb.getCell(p).getBlockSentiment() == Sentiment.Blocked) {
                    obstructed = true;
                    break;
                }
            }
        }

        return new Tuple<>(numStepsTaken, bumped);
    }

    /**
     * Runs the entire agent workflow, and records runtime statistics.
     * 
     * @return A {@link project2.entity.GridWorldInfo} instance containing runtime statistics
     */
    public GridWorldInfo run() {
        GridWorldInfo gridWorldInfoGlobal = new GridWorldInfo(0, 0, null);

        long start = System.nanoTime();
        while (!current.equals(goal)) { // loop while robot has not reached the destination
            // find path
            gridWorldInfoGlobal.numPlans++;
            GridWorldInfo result = searchAlgo.search(current, goal, kb,
                    cell -> cell.getBlockSentiment() == Sentiment.Blocked);

            // if no path found, exit with failure
            if (result == null || result.path == null) {
                gridWorldInfoGlobal.trajectoryLength = Double.NaN;
                return gridWorldInfoGlobal;
            }

            // attempt to travel down returned path, and update statistics
            Tuple<Integer, Boolean> pair = runPath(result.path);
            gridWorldInfoGlobal.trajectoryLength += pair.f1;
            gridWorldInfoGlobal.numberOfCellsProcessed += result.numberOfCellsProcessed;
            if (pair.f2)
                gridWorldInfoGlobal.numBumps++;
        }
        long end = System.nanoTime();
        gridWorldInfoGlobal.runtime = (end - start) / NANO_SECONDS;

        // count number of cells determined
        for (int y = 0; y < kb.getYSize(); y++) {
            for (int x = 0; x < kb.getXSize(); x++) {
                if (kb.getCell(x, y).getBlockSentiment() != Sentiment.Unsure) {
                    gridWorldInfoGlobal.numCellsDetermined++;
                }
            }
        }

        return gridWorldInfoGlobal;
    }

    /**
     * Formats grid state as a string
     * 
     * @return string representation of (clean) knowledge base
     */
    public String getGridState() {
        StringBuilder sb = new StringBuilder();
        for(GridCell cell : cleanKB.getGrid()) { // first layer - occupancy info
            Point p = cell.getLocation();
            if(p.equals(current)) {
                sb.append(2);
            } else if(p.equals(goal)) {
                sb.append(3);
            } else {
                sb.append(cell.getBlockSentiment().getSentiment());
            }
            sb.append(' ');
        }
        for(GridCell cell : cleanKB.getGrid()) { // second layer - sensory info
            sb.append(cell.isVisited() ? cell.getNumSensedBlocked() : 0);
            sb.append(' ');
        }
        return sb.toString();
    }

    /**
     * Returns code associated with robot decision.
     * 
     * @param pt New point the robot is moving to
     * @return Direction code associated with the motion
     * @throws IllegalArgumentException If pt is not reachable from the current location in one step
     */
    public String getDirectionCode(Point pt) throws IllegalArgumentException {
        int dx = pt.f1 - current.f1;
        int dy = pt.f2 - current.f2;
        if(Math.abs(dx) + Math.abs(dy) != 1) {
            throw new IllegalArgumentException("pt must be adjacent to current location");
        }
        String result = null;
        if(dy == -1) {
            result = "0 ";
        } else if(dy == 1) {
            result = "2 ";
        }
        if(dx == -1) {
            result = "3 ";
        } else if(dx == 1) {
            result = "1 ";
        }
        return result;
    }
}
