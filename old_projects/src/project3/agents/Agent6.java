package project3.agents;

import project3.entity.Grid;
import project3.entity.GridCell;
import project3.utility.Point;

public class Agent6 extends UtilityAgent {
    /**
     * Defines utility by the probability of a cell containing the target.
     */
    @Override
    public double utility(Grid kb, Point current, GridCell cell) {
        return cell.getProbGoal();
    }
}
