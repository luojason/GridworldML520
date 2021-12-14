package project3.agents;

import project3.entity.Grid;
import project3.entity.GridCell;
import project3.utility.Point;

public class Agent7 extends UtilityAgent {
    /**
     * Defines utility by the likelihood of discovering the target by examining this
     * cell.
     */
    @Override
    public double utility(Grid kb, Point current, GridCell cell) {
        return cell.getProbSuccess();
    }

    @Override
    public boolean doExamine(Grid kb, Point current, Point destination) {
        return kb.getCell(current).getProbSuccess() >= kb.getCell(destination).getProbSuccess();
    }
}
