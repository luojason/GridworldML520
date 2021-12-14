package project3.utility;

public enum Terrain {
    Blocked (-1, 0), // Blocked Terrain
    Flat    (0, .2), // Flat Terrain
    Hilly   (1, .5), // Hilly Terrain
    Forest  (2, .8); // Forest Terrain

    private final int terrain;
    private final double falseRate;

    Terrain(int terrainCode, double falseRate) {
        this.falseRate = falseRate;
        this.terrain = terrainCode;
    }

    public int getTerrain() {
        return this.terrain;
    }

    public double getFalseRate() {
        return falseRate;
    }
}
