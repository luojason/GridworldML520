package project2.entity;

import java.util.Arrays;
import java.util.function.Consumer;

import project2.utility.Point;
import project2.utility.Sentiment;

/**
 * Collection of {@link project2.entity.GridCell} instances representing the knowledge
 * base.
 */
public class Grid {
    private GridCell[] grid; // represent as flat array to make deep-copying easier
    private int xSize;
    private int ySize;

    /**
     * Constructs the grid with the specified parameters.
     * 
     * @param xSize       Width of grid
     * @param ySize       Height of grid
     * @param probability Density of obstacles
     */
    public Grid(int xSize, int ySize, int probability) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.grid = generateGrid(xSize, ySize, probability);
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

        if (deep) {
            for (int i = 0; i < grid.length; i++) {
                this.grid[i] = (GridCell) other.grid[i].clone();
            }
        }
    }

    private boolean generateIsBlocked(int probabilityOfBlocked) {
        return Math.random() * 100 < probabilityOfBlocked;
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < xSize && y < ySize;
    }

    private GridCell[] generateGrid(int dimX, int dimY, int probabilityOfBlocked) {
        GridCell[] grid = new GridCell[dimX * dimY];

        for (int y = 0; y < dimY; y++) {
            for (int x = 0; x < dimX; x++) {
                int index = y * dimX + x;

                int numAdj = 0; // determine how many neighbours the new cell has
                for (Point neighbour : new Point(x, y).get8Neighbours()) {
                    if (inBounds(neighbour.f1, neighbour.f2))
                        numAdj++;
                }

                // determine if cell is blocked
                boolean isBlocked = (index == 0 || index == dimX * dimY - 1) ? false
                        : generateIsBlocked(probabilityOfBlocked);

                grid[index] = new GridCell(x, y, numAdj, isBlocked, this);
            }
        }

        // iterate through grid one more time to get C_x for each cell
        for (int y = 0; y < dimY; y++) {
            for (int x = 0; x < dimX; x++) {
                int index = y * dimX + x;
                if (grid[index].isBlocked()) {
                    for (Point adj : grid[index].getLocation().get8Neighbours()) {
                        if (inBounds(adj.f1, adj.f2)) {
                            grid[adj.f2 * dimX + adj.f1].addNumSensedBlocked(1);
                        }
                    }
                }
            }
        }

        return grid;
    }

    /**
     * Retrieves the GridCell at (x, y) <strong>FOR READ-ONLY USAGE</strong>.
     * 
     * @param x x-coord of GridCell
     * @param y y-coord of GridCell
     * @return The GridCell instance
     */
    public GridCell getCell(int x, int y) {
        if (inBounds(x, y)) {
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

    /**
     * Confirms an unknown cell as blocked/empty and updates KB accordingly.
     * 
     * @param coord The location of the cell
     * @param sent  The confirmed status of that cell
     */
    public void setSentiment(Point coord, Sentiment sent) {
        if (getCell(coord).getBlockSentiment() == sent)
            return; // shortcut check
        GridCell cell = setCell(coord);

        // first reset current cell's sentiment if already set
        switch (cell.getBlockSentiment()) {
            case Free:
                forEachNeighbour(coord, nbr -> nbr.addNumAdjEmpty(-1));
                break;
            case Blocked:
                forEachNeighbour(coord, nbr -> nbr.addNumAdjBlocked(-1));
                break;
            case Unsure:
                break;
        }

        // set cell to new sentiment and update neighbours
        cell.setBlockSentiment(sent);
        switch (sent) {
            case Free:
                forEachNeighbour(coord, nbr -> nbr.addNumAdjEmpty(1));
                break;
            case Blocked:
                forEachNeighbour(coord, nbr -> nbr.addNumAdjBlocked(1));
                break;
            case Unsure:
                break;
        }
    }

    public void forEachNeighbour(Point coord, Consumer<GridCell> action) {
        Arrays.stream(coord.get8Neighbours()).forEach(point -> {
            GridCell cell = setCell(point);
            if (cell != null)
                action.accept(cell);
        });
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
