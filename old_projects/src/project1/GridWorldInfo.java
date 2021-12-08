package project1;

import java.util.List;

public class GridWorldInfo {
    double probability;
    double trajectoryLength;
    double trajectoryLengthDiscovered; // running A* on the discovered gridworld
    double trajectoryLengthComplete; // running A* on the complete gridworld
    int numberOfCellsProcessed;
    long runtime;
    double weight; // weight of the heuristic used (EC)
    double backtrackSteps; // how many steps backwards are taken (EC)

    List<Tuple<Integer, Integer>> path; // path does not include start cell

    public GridWorldInfo(double trajectoryLength, int numberOfCellsProcessed, List<Tuple<Integer, Integer>> path) {
        this.trajectoryLength = trajectoryLength;
        this.numberOfCellsProcessed = numberOfCellsProcessed;
        this.path = path;

        // set default values for other data
        this.trajectoryLengthDiscovered = -1;
        this.trajectoryLengthComplete = -1;
        this.runtime = -1;
        this.weight = 1;
        this.backtrackSteps = 0;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public double getTrajectoryLength() {
        return trajectoryLength;
    }

    public void setTrajectoryLength(double trajectoryLength) {
        this.trajectoryLength = trajectoryLength;
    }

    public double getTrajectoryLengthDiscovered() {
        return trajectoryLengthDiscovered;
    }

    public void setTrajectoryLengthDiscovered(double trajectoryLengthDiscovered) {
        this.trajectoryLengthDiscovered = trajectoryLengthDiscovered;
    }

    public double getTrajectoryLengthComplete() {
        return trajectoryLengthComplete;
    }

    public void setTrajectoryLengthComplete(double trajectoryLengthComplete) {
        this.trajectoryLengthComplete = trajectoryLengthComplete;
    }

    public int getNumberOfCellsProcessed() {
        return numberOfCellsProcessed;
    }

    public void setNumberOfCellsProcessed(int numberOfCellsProcessed) {
        this.numberOfCellsProcessed = numberOfCellsProcessed;
    }

    public long getRuntime() {
        return runtime;
    }

    public void setRuntime(long runtime) {
        this.runtime = runtime;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getBacktrackSteps() {
        return backtrackSteps;
    }

    public void setBacktrackSteps(double backtrackSteps) {
        this.backtrackSteps = backtrackSteps;
    }

    public List<Tuple<Integer, Integer>> getPath() {
        return path;
    }

    public void setPath(List<Tuple<Integer, Integer>> path) {
        this.path = path;
    }

    public void addCellsProcessed(int additionalCells) {
        this.numberOfCellsProcessed += additionalCells;
    }

    public void addTrajectoryLength(double additionTrajectory) {
        this.trajectoryLength += additionTrajectory;
    }
}
