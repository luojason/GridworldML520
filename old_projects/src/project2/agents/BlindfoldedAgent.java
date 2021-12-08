package project2.agents;

import project2.entity.Grid;
import project2.utility.Point;

/**
 * This is the blindfolded agent from Project 1 (Agent 1).
 */
public class BlindfoldedAgent implements InferenceAgent {
    /**
     * Learns nothing from its surroundings.
     */
    @Override
    public void learn(Grid kb, Point location) {
        // do nothing
    }
}