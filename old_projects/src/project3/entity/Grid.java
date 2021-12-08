package project3.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

import project3.utility.Point;
import project3.utility.Terrain;

/**
 * Collection of {@link project3.entity.GridCell} instances representing the knowledge
 * base.
 */
public class Grid {
    private GridCell[] grid; // represent as flat array to make deep-copying easier
    private Point goal; // cache the location of the goal
    private int xSize;
    private int ySize;

    /**
     * Constructs the grid with the specified parameters.
     * 
     * @param xSize Width of grid
     * @param ySize Height of grid
     */
    public Grid(int xSize, int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.grid = generateGrid(xSize, ySize);
    }

    /**
     * Copy constructor.
     * 
     * @param other Grid from which this grid is initialized.
     * @param deep  Whether the copy should be deep or shallow.
     */
    public Grid(Grid other, boolean deep) {
        this.xSize = other.getXSize();
        this.ySize = other.getYSize();
        this.grid = other.getGrid().clone();
        this.goal = other.getGoal();

        if (deep) {
            for (int i = 0; i < grid.length; i++) {
                this.grid[i] = (GridCell) other.grid[i].clone();
            }
        }
    }

    private boolean generateIsBlocked() {
        return Math.random() * 100 < 30;
    }

    private GridCell[] setGoal(GridCell[] grid, ArrayList<Integer> freeCellIndexes) {
        int location = (int) (Math.random() * freeCellIndexes.size());
        int index = freeCellIndexes.get(location);
        grid[index].setGoal(true);
        this.goal = grid[index].getLocation();
        return grid;
    }

    private GridCell[] generateGrid(int dimX, int dimY) {
        int size = dimX * dimY;
        GridCell[] grid = new GridCell[size];
        ArrayList<Integer> freeListIndexes = new ArrayList<>();
        for (int y = 0; y < dimY; y++) {
            for (int x = 0; x < dimX; x++) {
                int index = y * dimX + x;

                // determine if cell is blocked
                boolean isBlocked = (index == 0) ? false : generateIsBlocked();
                Terrain terrain = Terrain.Blocked;
                if (!isBlocked) {
                    double terrainType = Math.random() * 90;
                    if (terrainType <= 90) {
                        terrain = Terrain.Forest;
                    }
                    if (terrainType <= 60) {
                        terrain = Terrain.Hilly;
                    }
                    if (terrainType <= 30) {
                        terrain = Terrain.Flat;
                    }
                    freeListIndexes.add(index);
                }
                GridCell gc = new GridCell(x, y, terrain, this);
                grid[index] = gc;
            }
        }
        return setGoal(grid, freeListIndexes);
    }

    public Point getGoal() {
        return goal;
    }

    /**
     * Retrieves the GridCell at (x, y) <strong>FOR READ-ONLY USAGE</strong>.
     * 
     * @param x x-coord of GridCell
     * @param y y-coord of GridCell
     * @return The GridCell instance
     */
    public GridCell getCell(int x, int y) {
        if (x >= 0 && y >= 0 && x < xSize && y < ySize) { // check if in bounds
            return grid[y * getXSize() + x];
        }
        return null;
    }

    /**
     * Retrieves the GridCell at coord <strong>FOR READ-ONLY USAGE</strong>.
     * 
     * @param coord The coordinate of the GridCell
     * @return The GridCell instance
     */
    public GridCell getCell(Point coord) {
        return getCell(coord.f1, coord.f2);
    }

    /**
     * Retrieves the GridCell at coord <strong>FOR READING OR WRITING</strong>.
     * Checks if the GridCell actually belongs to this grid and if not, creates a
     * copy to preserve the original instance.
     * 
     * @param coord The coordinate of the GridCell
     * @return The GridCell instance
     */
    public GridCell setCell(Point coord) {
        GridCell cell = getCell(coord);
        if (cell != null && cell.getOwner() != this) { // the cell belongs to a different grid
            GridCell copy = cell.clone(); // clone it to avoid mangling the other grid
            copy.setOwner(this); // make this grid the new owner
            grid[coord.f2 * getXSize() + coord.f1] = copy;
        }
        return getCell(coord);
    }

    public GridCell[] getGrid() {
        return grid;
    }

    public int getXSize() {
        return xSize;
    }

    public int getYSize() {
        return ySize;
    }

    public void forEachNeighbour(Point coord, Consumer<GridCell> action) {
        Arrays.stream(coord.get8Neighbours()).forEach(point -> {
            GridCell cell = setCell(point);
            if (cell != null)
                action.accept(cell);
        });
    }

    /**
     * Renormalizes the goal probabilities so that they add to one.
     */
    public void renormalize() {
        double sum = Arrays.stream(getGrid())
                           .mapToDouble(cell -> cell.getProbGoal())
                           .sum();
        Arrays.stream(getGrid())
              .forEach(cell -> cell.setProbGoal(cell.getProbGoal()/sum));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Entity.Grid{");
        for (int x = 0; x < xSize; x++) {
            builder.append("\n");
            for (int y = 0; y < ySize; y++) {
                if (x == 0 && y == 0) {
                    builder.append("S");
                } else if (x == xSize - 1 && y == ySize - 1) {
                    builder.append("G");
                } else {
                    if (this.getCell(x, y).isBlocked()) {
                        builder.append("X");
                    } else {
                        builder.append(" ");
                    }
                }
                builder.append(",");
            }
        }
        builder.append("\n}");
        return builder.toString();
    }
}
