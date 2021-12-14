package project3.entity;

import project3.utility.Point;
import project3.utility.Terrain;

/**
 * Consolidates all the information associated with a cell in the gridworld.
 */
public class GridCell implements Cloneable {
    private final int x;
    private final int y;
    private Terrain terrain;
    private boolean isGoal;
    private boolean isVisited; // tracks if the robot knows the terrain of this cell
    private Grid owner; // used for lazy copying
    private double probGoal = 0; // Probability this cell has the goal

    /**
     * Used by Grid to construct a GridCell. Automatically assigns GridCells as
     * not containing the target and equally likely to contain the goal.
     * 
     * @param x
     * @param y
     * @param owner
     */
    public GridCell(int x, int y, Terrain terrain, Grid owner) {
        this.x = x;
        this.y = y;
        this.terrain = terrain;
        this.owner = owner;

        // set some default values
        this.isGoal = false;
        this.isVisited = false;
        this.probGoal = 1d;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

    public boolean isGoal() {
        return isGoal;
    }

    public void setGoal(boolean goal) {
        isGoal = goal;
    }

    /**
     * Returns probability of a a successful examination.
     * Used for Agent 7.
     * 
     * @return Probability that an examination of this cell succeeds.
     */
    public double getProbSuccess() {
        if (!isVisited()) {
            return 0.35*getProbGoal(); // special case when cell is not visited
        } else {
            return (1 - getTerrain().getFalseRate())*getProbGoal();
        }
    }

    public double getProbGoal() {
        return probGoal;
    }

    public void setProbGoal(double probGoal) {
        this.probGoal = probGoal;
    }

    public Point getLocation() {
        return new Point(x, y);
    }

    public boolean isBlocked() {
        return terrain.equals(Terrain.Blocked);
    }

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
    }

    protected Grid getOwner() {
        return owner;
    }

    protected void setOwner(Grid owner) {
        this.owner = owner;
    }

    @Override
    public GridCell clone() {
        GridCell copy = null;
        try {
            copy = (GridCell) super.clone();
        } catch (CloneNotSupportedException e) {
            // this should never be entered
            System.err.println(e.toString());
        }
        return copy;
    }
}
