package project2.utility;

public class Heuristics {
    /**
     * This program implements the euclidean distance calculation for heuristic cost
     *
     * @param currentCoordinate Current Coordinate
     * @param goalCoordinate Goal Coordinate
     * @return Returns a double value
     */
    public static double euclideanDistance(Point currentCoordinate, Point goalCoordinate) {
        double tempX = Math.pow(currentCoordinate.f1 - goalCoordinate.f1, 2);
        double tempY = Math.pow(currentCoordinate.f2 - goalCoordinate.f2, 2);

        return Math.sqrt(tempX + tempY);
    }

    /**
     * This program implements the manhattan distance calculation for heuristic cost
     *
     * @param currentCoordinate Current Coordinate
     * @param goalCoordinate Goal Coordinate
     * @return Returns a double value
     */
    public static double manhattanDistance(Point currentCoordinate, Point goalCoordinate) {
        double tempX = Math.abs(currentCoordinate.f1 - goalCoordinate.f1);
        double tempY = Math.abs(currentCoordinate.f2 - goalCoordinate.f2);

        return tempX + tempY;
    }

    /**
     * This program implements the chebyshev distance calculation for heuristic cost
     *
     * @param currentCoordinate Current Coordinate
     * @param goalCoordinate Goal Coordinate
     * @return Returns a double value
     */
    public static double chebyshevDistance(Point currentCoordinate, Point goalCoordinate) {
        double tempX = Math.abs(currentCoordinate.f1 - goalCoordinate.f1);
        double tempY = Math.abs(currentCoordinate.f2 - goalCoordinate.f2);

        return Math.max(tempX, tempY);
    }

}
