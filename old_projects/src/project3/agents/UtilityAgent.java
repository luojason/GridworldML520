package project3.agents;

import java.util.Arrays;

import project3.entity.Grid;
import project3.entity.GridCell;
import project3.utility.Heuristics;
import project3.utility.Point;

/**
 * A decision agent template that uses a utility function to decide where to go next
 */
public abstract class UtilityAgent implements DecisionAgent {
    /**
     * Use the current state of the robot and the knowledge base to compute some
     * utility for a cell.
     * 
     * @param kb
     * @param current
     * @param cell
     * @return
     */
    public abstract double utility(Grid kb, Point current, GridCell cell);

    /**
     * Gets the cell with the maximum utility as the next destination.
     * Breaks ties by using manhattan distance to the current position.
     */
    @Override
    public Point getDestination(Grid kb, Point current, Point oldDestination) {
        GridCell bestChoice =
        Arrays.stream(kb.getGrid())
              .reduce((cell1, cell2) -> {
                  double u1 = utility(kb, current, cell1);
                  double u2 = utility(kb, current, cell2);
                  if(u2 == u1) { // tie-break by distance
                      return (Heuristics.manhattanDistance(current, cell2.getLocation())  <
                              Heuristics.manhattanDistance(current, cell1.getLocation())) ?
                              cell2 : cell1;
                  }
                  return (u2 > u1) ? cell2 : cell1;
              })
              .get();
        return bestChoice.getLocation();
    }
}
