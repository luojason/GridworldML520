package project2.algorithms;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import project2.entity.Grid;
import project2.entity.GridCell;
import project2.entity.GridWorldInfo;
import project2.utility.Point;
import project2.utility.Sentiment;

public class AStarProbSearch extends AStarSearch {
    private BiFunction<Point, Point, Double> heuristic;

    /**
     * Constructs the search object with the specified heuristic function.
     *
     * @param heuristic The heuristic to use when searching
     */
    public AStarProbSearch(BiFunction<Point, Point, Double> heuristic) {
        super(heuristic);
        this.heuristic = heuristic;
    }

    @Override
    public GridWorldInfo search(Point start, Point end, Grid grid, Predicate<GridCell> isBlocked) {
        GridCell startCell = grid.getCell(start);
        GridCell endCell = grid.getCell(end);
        if (startCell == endCell || startCell == null || endCell == null) // checks invalid input
            throw new IllegalArgumentException("start/end cells coincide or are out of bounds");

        // create fringe and process start cell
        HashMap<GridCell, HeuristicDataProb> dataMap = new HashMap<>();
        PriorityQueue<HeuristicDataProb> fringe = new PriorityQueue<>(new GridCellComparatorProb());
        HeuristicDataProb startData = new HeuristicDataProb(startCell, end, null, 0, grid);
        dataMap.put(startCell, startData);
        fringe.add(startData);

        // begin processing cells
        HeuristicDataProb currentNode;
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
                return new GridWorldInfo(previousCost, numberOfCellsProcessed, path);
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
                    HeuristicDataProb data = new HeuristicDataProb(child, end, currentNode, previousCost + 1, grid);
                    fringe.add(data);
                    dataMap.put(child, data);
                } else {
                    HeuristicDataProb childData = dataMap.get(child);

                    // already in queue -> check if priority needs to be updated
                    if (previousCost + 1 < childData.getG()) {
                        fringe.remove(childData);
                        childData.updateProbBlocked();
                        childData.setG(previousCost + 1);
                        childData.setPrev(currentNode);
                        fringe.add(childData);
                    }
                }
            }
        }

        // path not found
        return new GridWorldInfo(Double.NaN, numberOfCellsProcessed, null);
    }

    class GridCellComparatorProb implements Comparator<HeuristicDataProb> { // Custom Comparator for Priority Queue
        @Override
        public int compare(HeuristicDataProb o1, HeuristicDataProb o2) {
            if (Double.compare(o1.getCost(), o2.getCost()) == 0) {
                return Double.compare(o1.getH(), o2.getH()); // prefer higher f-cost over higher h-cost
            } else {
                return Double.compare(o1.getCost(), o2.getCost());
            }
        }
    }

    class HeuristicDataProb {
        private final GridCell cell;
        private final Point location;
        private HeuristicDataProb prev;
        private double f;
        private double g;
        private double h;
        private double probBlocked;
        private double costScale = 1;

        public HeuristicDataProb(GridCell cell, Point end, HeuristicDataProb prev, double g, Grid grid) {
            this.cell = cell;
            this.location = cell.getLocation();
            this.prev = prev;
            this.g = g;
            this.h = heuristic.apply(this.location, end);
            this.probBlocked = 0;
            costScale = grid.getXSize() / 10;
            updateProbBlocked();
            this.f = Math.max(0, this.g + this.h - (probBlocked * costScale));


        }

        private void update() {
            this.f = Math.max(0,this.f + this.g - (this.probBlocked * costScale)); // also update f-cost
        }

        public double getProbBlocked() {
            return cell.getProbBlocked();
        }

        public void updateProbBlocked() {
            if (cell.getBlockSentiment().equals(Sentiment.Blocked)) { // We increase the cost to the cell because we know its blocked
                probBlocked = 0;
                return;
            }
            if (cell.getBlockSentiment().equals(Sentiment.Free)) { // If a cell isn't blocked we don't touch the cost
                probBlocked = 1;
                return;
            }
            probBlocked = cell.getProbBlocked();
            update();

        }

        public Point getLocation() {
            return location;
        }

        public HeuristicDataProb getPrev() {
            return prev;
        }

        public void setPrev(HeuristicDataProb prev) {
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