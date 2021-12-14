package project2.agents;

import project2.entity.Grid;
import project2.entity.GridCell;
import project2.utility.Point;
import project2.utility.Sentiment;

/**
 * A general interface for describing different inference/learning agents.
 */
@FunctionalInterface
public interface InferenceAgent {
    /**
     * Attempt to learn about the environment based on one's existing knowledge and
     * current location.
     *
     * @param kb       The knowledge base (will be updated with what was learned)
     * @param location The agent's current location
     */
    void learn(Grid kb, Point location);

    /**
     * Propagates inferences using rules of the Example Inference Agent, while
     * checking for contradictions.
     *
     * @param kb       The knowledge base (will be mutated by this function)
     * @param location The location where a sentiment was changed
     * @return Returns false if an inconsistency was found, true otherwise
     */
    default boolean propagateInferences(Grid kb, Point location) {
        // each neighbour's information has updated, check those
        for (Point adj : location.get8Neighbours()) {
            GridCell cell = kb.getCell(adj);

            // check to make sure robot has visited cell (and hence sensed its surroundings)
            if (cell != null && cell.isVisited()) {
                // first check for contradictions
                if (cell.getNumAdjBlocked() > cell.getNumSensedBlocked()
                        || cell.getNumAdjEmpty() > cell.getNumSensedEmpty()) {
                    return false;
                }
                // shortcut check to see if nothing can be inferred
                if (cell.getNumAdjHidden() == 0)
                    continue;
                // check each condition
                if (cell.getNumAdjBlocked() == cell.getNumSensedBlocked()) { // C_x = B_x
                    if (!updateNeighborsAsFree(adj, kb))
                        return false;
                } else if (cell.getNumAdjEmpty() == cell.getNumSensedEmpty()) { // N_x - C_x = E_x
                    if (!updateNeighborsAsBlocked(adj, kb))
                        return false;
                }
            }
        }
        return true; // no inconsistencies detected
    }

    /**
     * Mark all undecided neighbours of a cell as free/unblocked, and continue
     * propagating the changes. Checks for contradictions along the way.
     * 
     * @param adj The location of the cell whose neighbours will be marked free
     * @param kb  The knowledge base (will be mutated by this function)
     * @return Returns false if an inconsistency was found, true otherwise
     */
    default boolean updateNeighborsAsFree(Point adj, Grid kb) {
        for (Point p : adj.get8Neighbours()) {
            GridCell nbr = kb.getCell(p);
            if (nbr != null && nbr.getBlockSentiment() == Sentiment.Unsure) {
                kb.setSentiment(p, Sentiment.Free);
                boolean status = propagateInferences(kb, p); // continuing propagating changes
                if (status == false)
                    return false; // contradiction from deeper level detected
            }
        }
        return true;
    }

    /**
     * Mark all undecided neighbours of a cell as blocked, and continue
     * propagating the changes. Checks for contradictions along the way.
     * 
     * @param adj The location of the cell whose neighbours will be marked free
     * @param kb  The knowledge base (will be mutated by this function)
     * @return Returns false if an inconsistency was found, true otherwise
     */
    default boolean updateNeighborsAsBlocked(Point adj, Grid kb) {
        for (Point p : adj.get8Neighbours()) {
            GridCell nbr = kb.getCell(p);
            if (nbr != null && nbr.getBlockSentiment() == Sentiment.Unsure) {
                kb.setSentiment(p, Sentiment.Blocked);
                boolean status = propagateInferences(kb, p); // continuing propagating changes
                if (status == false)
                    return false; // contradiction from deeper level detected
            }
        }
        return true;
    }
}
