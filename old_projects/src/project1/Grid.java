package project1;

import java.util.ArrayList;

public class Grid {

    private ArrayList<ArrayList<GridCell>> grid;
    private int xSize;
    private int ySize;

    private boolean generateIsBlocked(int probabilityOfBlocked){
        return Math.random()*100 < probabilityOfBlocked;
    }

    private ArrayList<ArrayList<GridCell>> generateGrid(int dimensionX, int dimensionY, int probabilityOfBlocked){
        ArrayList<ArrayList<GridCell>> grid = new ArrayList<>();

        for (int y = 0; y < dimensionY; y++){
            ArrayList<GridCell> tempList = new ArrayList<>();
            for(int x = 0; x < dimensionX; x++){
                if((x == 0 && y == 0) || (x == dimensionX-1 && y == dimensionY-1)){
                    tempList.add(new GridCell(x,y,generateIsBlocked(0),0));
                }
                else {
                    tempList.add(new GridCell(x, y, generateIsBlocked(probabilityOfBlocked), 0));
                }

            }
            grid.add(tempList);
        }

        return grid;
    }

    public Grid(int xSize, int ySize, int probability) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.grid = generateGrid(xSize,ySize, probability);
    }

    public GridCell getCell(Tuple<Integer, Integer> coordinate){
        if( coordinate.f1 >=0 && coordinate.f2 >=0 && coordinate.f1 < xSize && coordinate.f2 < ySize){
            return grid.get(coordinate.f2).get(coordinate.f1);
        }
        return null;
    }


    public ArrayList<ArrayList<GridCell>> getGrid() {
        return grid;
    }

    public int getXSize() {
        return xSize;
    }

    public int getYSize() {
        return ySize;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Grid{");
        for (int x = 0; x < xSize; x++){
            builder.append("\n");
            for(int y = 0; y< ySize; y++) {

                if (x == 0 && y == 0) {
                    builder.append("S");
                } else if (x == xSize - 1 && y == ySize - 1) {
                    builder.append("G");
                } else {
                    if (this.getCell(new Tuple<>(x,y)).isBlocked()) {
                        builder.append("X");
                    } else {
                        builder.append(" ");
                    }
                }
                builder.append(",");
            }
        }
        builder.append("\n}");
        return builder.toString();
    }
}
