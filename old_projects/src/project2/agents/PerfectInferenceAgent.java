package project2.agents;

import java.util.Queue;

import project2.entity.*;
import project2.utility.*;

import java.util.ArrayDeque;

/**
 * An inference agent that can infer everything possible from the KB (used for comparison purposes).
 */
public class PerfectInferenceAgent implements InferenceAgent {
    private BasicInferenceAgent deterministicAgent;
    private int depth;
    private static final Sentiment[] list = { Sentiment.Blocked, Sentiment.Free };

    /**
     * Default constructor: initializes agent to run SAT-checker to infinite depth.
     */
    public PerfectInferenceAgent() {
        this(-1);
    }

    /**
     * Initializes agent to run SAT-checker up to 'depth' levels deep (to improve
     * runtime).
     * 
     * @param depth The max recursion depth of the SAT-checker used.
     */
    public PerfectInferenceAgent(int depth) {
        this.deterministicAgent = new BasicInferenceAgent();
        this.depth = depth;
    }

    /**
     * Learns about surroundings by applying contradiction testing using a
     * SAT-checker.
     */
    @Override
    public void learn(Grid kb, Point location) {
        // first make all the deterministic inferences possible
        deterministicAgent.learn(kb, location);

        // do satisfiability testing to find further inferences
        Queue<GridCell> openCells = new ArrayDeque<>(); // queue to hold relevant cells
        boolean done = false;
        while (!done) {
            done = true;

            // get collection of relevant cells to test
            for (int y = 0; y < kb.getYSize(); y++) {
                for (int x = 0; x < kb.getXSize(); x++) {
                    GridCell cell = kb.getCell(x, y);
                    if (cell.isVisited() && cell.getNumAdjHidden() > 0)
                        openCells.add(cell);
                }
            }

            // perform contradiction testing on neighbours of each open cell
            while (!openCells.isEmpty()) {
                GridCell cell = openCells.poll();
                for (Point adj : cell.getLocation().get8Neighbours()) {
                    GridCell nbr = kb.getCell(adj);
                    if (nbr == null || nbr.getBlockSentiment() != Sentiment.Unsure) {
                        continue;
                    }

                    // test if setting nbr to each status yields a contradiction
                    for (int i = 0; i < list.length; i++) {
                        Grid copy = new Grid(kb, false); // make shallow copy so original grid isn't mangled up
                        copy.setSentiment(adj, list[i]);
                        boolean status = propagateInferences(copy, adj) && totalSolve(copy, depth); // use short-circuiting
                        if (status == false) { // contradiction found -> nbr is the other option
                            done = false;
                            kb.setSentiment(adj, list[(i + 1) % 2]);
                            propagateInferences(kb, adj);
                            break;
                        }
                    }

                    // either nbr's status has been set, or the test is inconclusive
                }
            }
        }

    }

    /**
     * A satisfiability checker for the gridworld. Attempts to find an assignment
     * satisfying the constraints. Modifies the original board with cell statuses
     * that are inferred/assigned along the way;
     * <strong>do not rely on the state of kb after calling this function.</strong>
     * 
     * @param kb    The current state of the knowledge base
     * @param depth How many levels deep to run the checker
     * @return If the current KB is satisfiable or not
     */
    public boolean totalSolve(Grid kb, int depth) {
        // check if max allowed depth has been reached
        if (depth == 0) {
            return true;
        }

        // find a relevant cell to test
        GridCell openCell = null;
        for (int y = 0; y < kb.getYSize(); y++) {
            for (int x = 0; x < kb.getXSize(); x++) {
                GridCell cell = kb.getCell(x, y);
                if (cell.isVisited() && cell.getNumAdjHidden() > 0) {
                    openCell = cell;
                    break;
                }
            }
        }

        if (openCell == null) {
            return true; // no open cells -> partial assignment already found
        }

        GridCell nbr = null;
        for (Point adj : openCell.getLocation().get8Neighbours()) {
            nbr = kb.getCell(adj);
            if (nbr != null && nbr.getBlockSentiment() == Sentiment.Unsure) {
                break;
            }
        }
        Point adj = nbr.getLocation();

        // attempt to solve setting nbr -> blocked
        Grid copy = new Grid(kb, false);
        copy.setSentiment(adj, Sentiment.Blocked);
        boolean status = propagateInferences(copy, adj) && totalSolve(copy, depth - 1); // use short-circuiting
        if (status == true) { // found a valid partial assignment
            return true;
        }

        // attempt to solve setting nbr -> free
        kb.setSentiment(adj, Sentiment.Free);
        status = propagateInferences(kb, adj) && totalSolve(kb, depth - 1); // use short-circuiting
        if (status == true) { // found a valid partial assignment
            return true;
        }

        return false; // kb is not satisfiable
    }
}
