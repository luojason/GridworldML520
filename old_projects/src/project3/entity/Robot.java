package project3.entity;

import project3.agents.DecisionAgent;
import project3.algorithms.SearchAlgo;
import project3.utility.Point;
import project3.utility.Tuple;

import java.util.List;

/**
 * Represents the actual agent that will move through the gridworld.
 */
public class Robot {
    private Point current;
    private Point destination; // the current target robot is trying to reach
    private DecisionAgent agent;
    private Grid kb; // knowledge base
    private SearchAlgo searchAlgo;
    private boolean verbose;
    private double keep; // what percentage of the samples to keep
    private static final double NANO_SECONDS = 1000000000d;

    // track some runtime statistics
    private int numStepsTaken;
    private int numBumps;
    private int numPlans;
    private int numExaminations;

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
    public Robot(Point start, DecisionAgent agent, Grid grid, SearchAlgo searchAlgo, boolean verbose, double keep) {
        this.current = start;
        this.agent = agent;
        this.kb = grid;
        this.searchAlgo = searchAlgo;
        this.verbose = verbose;
        this.keep = keep;
        
        // set default values
        this.numStepsTaken = 0;
        this.numBumps = 0;
        this.numPlans = 0;
        this.numExaminations = 0;

        // do some initial setup
        this.kb.renormalize();
        this.kb.getCell(start).setVisited(true); // is currently visiting the start cell
        this.destination = this.agent.getDestination(this.kb, start, start);
    }

    public Point getLocation() {
        return current;
    }

    public void move(Point pos) {
        current = pos;
    }

    public Grid getKnowledgeBase() {
        return kb;
    }

    public boolean visit(GridCell cell) {
        return true;
    }

    /**
     * Examines a cell. Assumes the robot knows the terrain type of the cell.
     * Updates the probabilities based on the results
     * 
     * @return Whether the examination revealed the target or not.
     */
    public boolean examineTerrain(GridCell cell) {
        double val = Math.random();
        if (cell.isGoal() && val >= cell.getTerrain().getFalseRate()) {
            return true;
        }
        cell.setProbGoal(cell.getTerrain().getFalseRate() * cell.getProbGoal()); // update probability
        return false;
    }

    /**
     * Attempts to follow a path, making inferences/learning along the way. Stops
     * prematurely if it detects or bumps into an obstacle.
     * 
     * @param path The path to follow
     * @return Did the robot find the target while running the path?
     */
    private boolean runPath(List<Point> path) {
        for (Point position : path) {
            // output data
            if(verbose && Math.random()*100 < keep) {
                System.out.print(getGridState());
                System.err.print(getDirectionCode(position));
            }

            GridCell nextCell = kb.getCell(position);
            
            // attempt to move into next space
            nextCell.setVisited(true);
            if (nextCell.isBlocked()) {
                this.numBumps++;
                nextCell.setProbGoal(0.0); // update probability
                kb.renormalize();
                return false;
            } else {
                this.numStepsTaken++;
                move(position);
            }

            // decide whether to examine the current cell
            if (current.equals(destination) || agent.doExamine(kb, current, destination)) {
                // output data
                if(verbose && Math.random()*100 < keep) {
                    System.out.print(getGridState());
                    System.err.print(4);
                    System.err.print(' ');
                }

                this.numExaminations++;
                boolean result = examineTerrain(nextCell);
                kb.renormalize();
                if (result) return true; // examination successful
            }
        }
        return false;
    }

    /**
     * Runs the entire agent workflow, and records runtime statistics.
     * Assumes the gridworld is solvable
     * 
     * @return A {@link project3.entity.GridWorldInfo} instance containing runtime statistics
     */
    public GridWorldInfo run() {
        GridWorldInfo gridWorldInfoGlobal = new GridWorldInfo();

        long start = System.nanoTime();
        boolean targetFound = false;
        while (!targetFound) { // loop while robot has not found the target
            // get next target to path towards
            destination = agent.getDestination(kb, current, destination);

            // if robot is already at destination, perform examination immediately
            if(current.equals(destination)) {
                this.numExaminations++;
                if(examineTerrain(kb.getCell(current))) {
                    targetFound = true;
                }
                continue;
            }

            // else, attempt to path to destination via Repeated search
            while(!current.equals(destination)) {
                this.numPlans++;
                Tuple<List<Point>, Integer> result =
                    searchAlgo.search(current, destination, kb, cell -> cell.isVisited() && cell.isBlocked());
                gridWorldInfoGlobal.numberOfCellsProcessed += result.f2;

                // no path to destination is possible -> invalidate this destination
                if(result.f1 == null) {
                    kb.getCell(destination).setProbGoal(0.0); // update probability
                    kb.renormalize();
                    break;
                }

                // run along path; if target found, break
                if(runPath(result.f1)) {
                    targetFound = true;
                    break;
                }
            }
        }
        long end = System.nanoTime();
        gridWorldInfoGlobal.runtime = (end - start) / NANO_SECONDS;
        
        // fill in remaining statistics
        gridWorldInfoGlobal.numStepsTaken = this.numStepsTaken;
        gridWorldInfoGlobal.numExaminations = this.numExaminations;
        gridWorldInfoGlobal.numBumps = this.numBumps;
        gridWorldInfoGlobal.numPlans = this.numPlans;

        return gridWorldInfoGlobal;
    }

    public String getGridState() {
        StringBuilder sb = new StringBuilder();
        for(GridCell cell : kb.getGrid()) { // output terrain grid
            int state = 2;
            if(cell.isVisited()) {
                switch (cell.getTerrain()) {
                    case Blocked:
                        state = 0;
                        break;
                    case Forest:
                        state = 1;
                        break;
                    case Hilly:
                        state = 3;
                        break;
                    case Flat:
                        state = 4;
                }
            }
            if(cell.getLocation().equals(current)) {
                state *= -1;
            }
            sb.append(state);
            sb.append(' ');
        }
        for(GridCell cell : kb.getGrid()) { // output probabilities
            sb.append(cell.getProbGoal());
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
