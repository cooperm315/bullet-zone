package edu.unh.cs.cs619.bulletzone.datalayer.structure;

public enum StructureType {
    // Turned types are because the asymmetry of image
    // miner will determine direction on build
    DECK(false, false, new int[]{0, 0, 1, 5}, 1),
    ROAD(false, false, new int[]{3, 0, 0, 0}, .5),
    WALL(true, true, new int[]{1, 2, 0, 0}, 1),
    INDESTRUCTIBLE_WALL(false, true, new int[]{3, 3, 3, 0}, 1);

    public boolean destructible;
    public boolean hittable;
    public int[] resourceCost;   // {CLAY, ROCK, IRON, WOOD}

    public double speedModifier;

    StructureType(boolean destructible, boolean hittable, int[] resourceCost, double speedModifier) {
        this.destructible = destructible;
        this.hittable = hittable;
        this.resourceCost = resourceCost;
        this.speedModifier = speedModifier;
    }
}
