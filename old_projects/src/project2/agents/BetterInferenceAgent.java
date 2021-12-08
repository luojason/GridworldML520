package project2.agents;

import java.util.ArrayDeque;
import java.util.Queue;

import project2.entity.Grid;
import project2.entity.GridCell;
import project2.utility.Point;
import project2.utility.Sentiment;

/**
 * The improved inference agent (Agent 4).
 */
public class BetterInferenceAgent implements InferenceAgent {
    BasicInferenceAgent deterministicAgent;
    private static final Sentiment[] list = { Sentiment.Blocked, Sentiment.Free };

    public BetterInferenceAgent() {
        deterministicAgent = new BasicInferenceAgent();
    }

    /**
     * Learns about surroundings by applying contradiction testing to unrconfirmed cells.
     */
    @Override
    public void learn(Grid kb, Point location) {
        // first make all the deterministic inferences possible
        deterministicAgent.learn(kb, location);

        // do contradicting testing to find further inferences
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
                        boolean status = propagateInferences(copy, adj);
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
}