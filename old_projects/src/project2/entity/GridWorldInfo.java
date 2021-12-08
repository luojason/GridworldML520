package project2.entity;

import java.util.List;

import project2.utility.Point;

/**
 * POD class for holding statistics from running tests.
 */
public class GridWorldInfo {
    public double probability;
    public double trajectoryLength;
    public int numberOfCellsProcessed;
    public int numBumps;
    public int numPlans;
    public int numCellsDetermined;
    public double runtime;

    public List<Point> path; // path does not include start cell

    public GridWorldInfo(double trajectoryLength, int numberOfCellsProcessed, List<Point> path) {
        this.trajectoryLength = trajectoryLength;
        this.numberOfCellsProcessed = numberOfCellsProcessed;
        this.path = path;

        // set default values for other data
        this.probability = 0;
        this.numBumps = 0;
        this.numPlans = 0;
        this.numCellsDetermined = 0;
        this.runtime = 0;
    }


}
