package project1;

public class GridCell {
    private int x;
    private int y;
    private boolean isBlocked;
    private double cost;
    private double heuristicCost;
    private GridCell prev;

    public GridCell(int x, int y, boolean isBlocked, int i) {
        this.x = x;
        this.y = y;
        this.isBlocked = isBlocked;
        this.cost = 0;
        this.prev = null;
    }

    public double getHeuristicCost() {
        return heuristicCost;
    }

    public void setHeuristicCost(double heuristicCost) {
        this.heuristicCost = heuristicCost;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Tuple<Integer, Integer> getLocation(){
        return new Tuple<>(x,y);
    }

    public GridCell getPrev() {
        return prev;
    }

    public void setPrev(GridCell prev) {
        this.prev = prev;
    }

    @Override
    public String toString() {
        return "GridCell{" +
                "x=" + x +
                ", y=" + y +
                ", isBlocked=" + isBlocked +
                ", cost=" + cost +
                '}';
    }


}
