package project3.entity;

/**
 * POD class for holding statistics from running tests.
 */
public class GridWorldInfo {
    public double probability;
    public int numStepsTaken;
    public int numExaminations;
    public int numberOfCellsProcessed;
    public int numBumps;
    public int numPlans;
    public double runtime;

    public GridWorldInfo() {
        // set default values for other data
        this.probability = 0.30;
    }
}
