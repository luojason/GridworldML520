package project1;

import java.util.*;
import java.util.function.Predicate;

public class BFSObject {

    /**
     * Runs BFS on a Grid and returns a {@link GridWorldInfo} containing usage data.
     *
     * @param start Start Position
     * @param end End Position
     * @param grid Grid to Search
     * @param isBlocked Predicate whether a cell is blocked
     * @return GridWorldInfo containing the info for grid world
     */
    public static GridWorldInfo BFS(Tuple<Integer, Integer> start, Tuple<Integer, Integer> end, Grid grid, Predicate<GridCell> isBlocked) {
        GridCell startCell = grid.getCell(start);
        GridCell endCell = grid.getCell(end);
        if (startCell == endCell || startCell == null || endCell == null) return null;

        // create fringe and process start cell
        Queue<GridCell> fringe = new LinkedList<>();
        startCell.setCost(0);
        startCell.setPrev(null);
        fringe.add(startCell);

        // begin processing cells
        GridCell currentCell;
        double previousCost;
        HashSet<GridCell> discoveredCells = new HashSet<>();
        discoveredCells.add(startCell);
        int numberOfCellsProcessed = 0;
        while (!fringe.isEmpty()) {
            numberOfCellsProcessed++;
            currentCell = fringe.poll();
            previousCost = currentCell.getCost();
            if (currentCell.equals(endCell)) {
                // goal found, reconstruct path
                LinkedList<Tuple<Integer, Integer>> path = new LinkedList<>();
                while(currentCell.getPrev() != null) { // while we have not reached the start cell...
                    path.push(currentCell.getLocation());
                    currentCell = currentCell.getPrev();
                }
                return new GridWorldInfo(previousCost, numberOfCellsProcessed, path);
            }

            // else, generate the children of currentCell
            ArrayList<Tuple<Integer, Integer>> directions = new ArrayList<>(4);
            directions.add(new Tuple<>(currentCell.getX() + 1, currentCell.getY())); // right
            directions.add(new Tuple<>(currentCell.getX() - 1, currentCell.getY())); // left
            directions.add(new Tuple<>(currentCell.getX(), currentCell.getY() - 1)); // up
            directions.add(new Tuple<>(currentCell.getX(), currentCell.getY() + 1)); // down

            // process each child
            for(Tuple<Integer, Integer> direction : directions) {
                GridCell child = grid.getCell(direction);
                if(child == null || isBlocked.test(child) || discoveredCells.contains(child)) continue; // check that cell is valid and undiscovered
                child.setCost(previousCost + 1);
                child.setPrev(currentCell);
                fringe.add(child);
                discoveredCells.add(child);
            }
        }

        // path not found
        return new GridWorldInfo(Double.NaN, numberOfCellsProcessed, null);
    }
}
