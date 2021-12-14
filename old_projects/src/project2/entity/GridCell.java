package project2.entity;

import java.util.concurrent.atomic.AtomicInteger;

import project2.utility.Point;
import project2.utility.Sentiment;

/**
 * Consolidates all the information associated with a cell in the gridworld.
 */
public class GridCell implements Cloneable {
    private final int x;
    private final int y;
    private Sentiment blockSentiment;
    private int numAdj; // N_x
    private int numSensedBlocked; // C_x
    private int numAdjBlocked; // B_x (confirmed blocked)
    private int numAdjEmpty; // E_x (confirmed empty)
    private int numAdjHidden; // H_x -> has no setter since it's determined by other fields
    private boolean isBlocked;
    private boolean isVisited;
    private Grid owner; // used for lazy copying
    private double probBlocked = 0;

    public GridCell(int x, int y, int numAdj, boolean isBlocked, Grid owner) {
        this.x = x;
        this.y = y;
        this.numAdj = numAdj;
        this.numAdjHidden = numAdj; // all cells start off hidden
        this.isBlocked = isBlocked;
        this.owner = owner;

        // set default values
        this.blockSentiment = Sentiment.Unsure; // all cells start off undetermined
        this.numSensedBlocked = 0;
        this.numAdjBlocked = 0;
        this.numAdjEmpty = 0;
        this.isVisited = false;
    }



    public double getProbBlocked() {
        AtomicInteger numberSensedBlocked = new AtomicInteger();
        owner.forEachNeighbour(getLocation(), nbr -> {
            if(nbr.isVisited)
             numberSensedBlocked.addAndGet(nbr.getNumSensedBlocked());
        });
        this.setProbBlocked(Math.min(25,numberSensedBlocked.get()) / 25d);
        return 1 - probBlocked;
    }

    public void setProbBlocked(double probBlocked) {
        this.probBlocked = probBlocked;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point getLocation() {
        return new Point(x, y);
    }

    public Sentiment getBlockSentiment() {
        return blockSentiment;
    }

    public void setBlockSentiment(Sentiment blockSentiment) {
        this.blockSentiment = blockSentiment;
    }

    public int getNumAdj() {
        return numAdj;
    }

    public int getNumSensedBlocked() {
        return numSensedBlocked;
    }

    public int getNumSensedEmpty() {
        return numAdj - numSensedBlocked;
    }

    public void addNumSensedBlocked(int numSensedBlocked) {
        this.numSensedBlocked += numSensedBlocked;
    }

    public int getNumAdjBlocked() {
        return numAdjBlocked;
    }

    public void addNumAdjBlocked(int numAdjBlocked) {
        this.numAdjBlocked += numAdjBlocked;
        this.numAdjHidden -= numAdjBlocked;
    }

    public int getNumAdjEmpty() {
        return numAdjEmpty;
    }

    public void addNumAdjEmpty(int numAdjEmpty) {
        this.numAdjEmpty += numAdjEmpty;
        this.numAdjHidden -= numAdjEmpty;
    }

    public int getNumAdjHidden() {
        return numAdjHidden;
    }

    public boolean isBlocked() {
        return isBlocked || blockSentiment == Sentiment.Blocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
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
