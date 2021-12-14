package project3.agents;

import project3.entity.Grid;
import project3.entity.GridCell;
import project3.utility.Heuristics;
import project3.utility.Point;

public class Agent8 extends UtilityAgent {
    /**
     * Computes a utility function factoring both success likelihood and distance
     * from current position.
     */
    @Override
    public double utility(Grid kb, Point current, GridCell cell) {
        double dist = Heuristics.manhattanDistance(current, cell.getLocation()) + 1;
        return cell.getProbSuccess() / dist;
    }

    @Override
    public boolean doExamine(Grid kb, Point current, Point destination) {
        return kb.getCell(current).getProbSuccess() >= kb.getCell(destination).getProbSuccess();
    }
}
