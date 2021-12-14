package project1;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class AStarSearch implements SearchAlgo{

    private BiFunction<Tuple<Integer, Integer>, Tuple<Integer, Integer>, Double> heuristic;


    public AStarSearch(BiFunction<Tuple<Integer, Integer>, Tuple<Integer, Integer>, Double> heuristic) {
        this.heuristic = heuristic;
    }

    /**
     * This runs the A* Search algorithm. Predicate is used to pass in a boolean evaluation function to check what spaces we have been to/are blocked or spaces that are free.
     * Returns a GridWorldInfo Object {@link GridWorldInfo}.
     * @param start Start Location
     * @param end End Location
     * @param grid Grid to Search
     * @param isBlocked Function to check whether cells are blocked
     * @return
     */

    @Override
    public GridWorldInfo search(Tuple<Integer, Integer> start, Tuple<Integer, Integer> end, Grid grid, Predicate<GridCell> isBlocked) {
        GridCell startCell = grid.getCell(start);
        GridCell endCell = grid.getCell(end);
        if (startCell == endCell || startCell == null || endCell == null) return null; // Checks invalid cells

        // create fringe and process start cell
        PriorityQueue<GridCell> fringe = new PriorityQueue<>(new GridCellComparator()); // Priority Queue for reference
        startCell.setCost(0);
        startCell.setHeuristicCost(heuristic.apply(start, end));
        startCell.setPrev(null);
        fringe.add(startCell);

        // begin processing cells
        GridCell currentCell;
        double previousCost;
        HashSet<GridCell> discoveredCells = new HashSet<>();
        discoveredCells.add(startCell);
        int numberOfCellsProcessed = 0;
        while (!fringe.isEmpty()) {
            numberOfCellsProcessed++; //Cell Processed Counter
            currentCell = fringe.poll();// Get First in Queue
            previousCost = currentCell.getCost(); //Previous cost of the cell
            if (currentCell.equals(endCell)) { //check if end then start return obj creation
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
                if(child == null || isBlocked.test(child)) continue; // check that cell is valid
                if(!discoveredCells.contains(child)) { // first time processing this cell -> initialize and insert into fringe
                    child.setHeuristicCost(heuristic.apply(child.getLocation(), end));
                    child.setCost(previousCost + 1);
                    child.setPrev(currentCell);
                    fringe.add(child);
                    discoveredCells.add(child);
                } else if(previousCost + 1 < child.getCost()) { // already in queue -> check if priority needs to be updated
                    fringe.remove(child);
                    // if(check == false) System.out.println("heuristic consistency violated");
                    child.setCost(previousCost + 1);
                    child.setPrev(currentCell);
                    fringe.add(child);
                }
            }
        }

        // path not found
        return new GridWorldInfo(Double.NaN, numberOfCellsProcessed, null);
    }

    class GridCellComparator implements Comparator<GridCell> { //Custom Comparator for Priority Queue

        @Override
        public int compare(GridCell o1, GridCell o2) {
            Double cost1 = o1.getCost() + o1.getHeuristicCost();
            Double cost2 = o2.getCost() + o2.getHeuristicCost();
            if(Double.compare(cost1, cost2) == 0) { // prefer higher g-cost over higher h-cost
                return Double.compare(o1.getHeuristicCost(), o2.getHeuristicCost());
            } else {
                return Double.compare(cost1, cost2);
            }
        }
    }
}
