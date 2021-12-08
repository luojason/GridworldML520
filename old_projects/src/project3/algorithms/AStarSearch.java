package project3.algorithms;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import project3.entity.Grid;
import project3.entity.GridCell;
import project3.utility.Point;
import project3.utility.Tuple;

public class AStarSearch implements SearchAlgo {
    private BiFunction<Point, Point, Double> heuristic;

    /**
     * Constructs the search object with the specified heuristic function.
     * 
     * @param heuristic The heuristic to use when searching
     */
    public AStarSearch(BiFunction<Point, Point, Double> heuristic) {
        this.heuristic = heuristic;
    }

    /**
     * This runs the A* Search algorithm.
     */
    @Override
    public Tuple<List<Point>, Integer> search(Point start, Point end, Grid grid, Predicate<GridCell> isBlocked) {
        GridCell startCell = grid.getCell(start);
        GridCell endCell = grid.getCell(end);

        // checks invalid input
        if (startCell == null || endCell == null) {
            throw new IllegalArgumentException("start/end cells coincide or are out of bounds");
        }

        // shortcut check
        if(startCell == endCell) {
            return new Tuple<List<Point>, Integer>(new LinkedList<>(), 0);
        }

        // create fringe and process start cell
        HashMap<GridCell, HeuristicData> dataMap = new HashMap<>();
        PriorityQueue<HeuristicData> fringe = new PriorityQueue<>(new GridCellComparator());
        HeuristicData startData = new HeuristicData(startCell, end, null, 0);
        dataMap.put(startCell, startData);
        fringe.add(startData);

        // begin processing cells
        HeuristicData currentNode;
        double previousCost;
        int numberOfCellsProcessed = 0;
        while (!fringe.isEmpty()) {
            numberOfCellsProcessed++; // Cell Processed Counter
            currentNode = fringe.poll();// Get First in Queue
            previousCost = currentNode.getG(); // Previous g-cost of the cell

            if (currentNode.getLocation().equals(end)) { // check if end then start return obj creation
                // goal found, reconstruct path
                LinkedList<Point> path = new LinkedList<>();
                while (currentNode.getPrev() != null) { // while we have not reached the start cell...
                    path.push(currentNode.getLocation());
                    currentNode = currentNode.getPrev();
                }
                return new Tuple<List<Point>, Integer>(path, numberOfCellsProcessed);
            }

            // else, generate the children of currentNode
            Point[] directions = currentNode.getLocation().get4Neighbours();

            // process each child
            for (Point direction : directions) {
                GridCell child = grid.getCell(direction);
                if (child == null || isBlocked.test(child))
                    continue; // check that cell is valid
                if (!dataMap.containsKey(child)) {
                    // first time processing this cell -> initialize and insert into fringe
                    HeuristicData data = new HeuristicData(child, end, currentNode, previousCost + 1);
                    fringe.add(data);
                    dataMap.put(child, data);
                } else {
                    HeuristicData childData = dataMap.get(child);

                    // already in queue -> check if priority needs to be updated
                    if (previousCost + 1 < childData.getG()) {
                        fringe.remove(childData);
                        childData.setG(previousCost + 1);
                        childData.setPrev(currentNode);
                        fringe.add(childData);
                    }
                }
            }
        }

        // path not found; update probabilities by marking unprocessed cells as unreachable
        Arrays.stream(grid.getGrid())
              .filter(cell -> !dataMap.containsKey(cell))
              .forEach(cell -> cell.setProbGoal(0.0));
        return new Tuple<List<Point>, Integer>(null, numberOfCellsProcessed);
    }

    class GridCellComparator implements Comparator<HeuristicData> { // Custom Comparator for Priority Queue
        @Override
        public int compare(HeuristicData o1, HeuristicData o2) {
            if (Double.compare(o1.getCost(), o2.getCost()) == 0) {
                return Double.compare(o1.getH(), o2.getH()); // prefer higher f-cost over higher h-cost
            } else {
                return Double.compare(o1.getCost(), o2.getCost());
            }
        }
    }

    class HeuristicData {
        private final Point location;
        private HeuristicData prev;
        private double f;
        private double g;
        private double h;

        public HeuristicData(GridCell cell, Point end, HeuristicData prev, double g) {
            this.location = cell.getLocation();
            this.prev = prev;
            this.g = g;
            this.h = heuristic.apply(this.location, end);
            this.f = this.g + this.h;
        }
        private void update(){
            this.f = this.f + this.g; // also update f-cost
        }
        public Point getLocation() {
            return location;
        }

        public HeuristicData getPrev() {
            return prev;
        }

        public void setPrev(HeuristicData prev) {
            this.prev = prev;
        }

        public double getCost() { // returns the total f-cost (NOT the g-cost)
            return f;
        }

        public double getG() {
            return g;
        }

        public void setG(double g) {
            this.g = g;
            update();
        }

        public double getH() {
            return h;
        }

        public void setH(double h) {
            this.h = h;
            update();
        }
    }
}
