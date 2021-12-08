package project2.agents;

import project2.entity.Grid;
import project2.entity.GridCell;
import project2.utility.Point;
import project2.utility.Sentiment;

/**
 * The example inference agent (Agent 3).
 */
public class BasicInferenceAgent implements InferenceAgent {
    /**
     * Learns about surroundings by applying the example inference agent rules as
     * described in the project document.
     */
    @Override
    public void learn(Grid kb, Point location) {
        // the possible cells with updated sentiments are the current location and its
        // nbrs; need to run propagateInferences on all of them
        propagateInferences(kb, location);
        kb.forEachNeighbour(location, nbr -> propagateInferences(kb, nbr.getLocation()));
    }

    /**
     * Backup of inefficient implementation of the example inference agent.
     * Used to verify that behaviour is the same.
     * 
     * @param kb       The knowledge base (will be updated with what was learned)
     * @param location The agent's current location
     */
    public static void naiveLearn(Grid kb, Point location) {
        boolean done = false;
        while (!done) { // keep iterating until no more updates are made
            done = true;
            for (int y = 0; y < kb.getYSize(); y++) {
                for (int x = 0; x < kb.getXSize(); x++) {
                    GridCell cell = kb.getCell(x, y);
                    if (cell.getNumAdjHidden() == 0 || !cell.isVisited()) {
                        continue; // nothing can be inferred from this cell
                    }

                    // check each condition
                    if (cell.getNumAdjBlocked() == cell.getNumSensedBlocked()) { // C_x = B_x
                        done = false;
                        kb.forEachNeighbour(cell.getLocation(), nbr -> {
                            if (nbr.getBlockSentiment() == Sentiment.Unsure) {
                                kb.setSentiment(nbr.getLocation(), Sentiment.Free);
                            }
                        });
                    } else if (cell.getNumAdjEmpty() == cell.getNumSensedEmpty()) { // N_x - C_x = E_x
                        done = false;
                        kb.forEachNeighbour(cell.getLocation(), nbr -> {
                            if (nbr.getBlockSentiment() == Sentiment.Unsure) {
                                kb.setSentiment(nbr.getLocation(), Sentiment.Blocked);
                            }
                        });
                    }
                }
            }
        }
    }
}
