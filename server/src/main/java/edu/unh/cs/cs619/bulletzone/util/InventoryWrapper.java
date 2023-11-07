package edu.unh.cs.cs619.bulletzone.util;

public class InventoryWrapper {
    private int[] collection;

    private boolean poweredUp;

    public InventoryWrapper() {}

    public InventoryWrapper(int[] input, boolean poweredUp) {
        this.collection = input;
        this.poweredUp = poweredUp;
    }

    public int[] getResult() {
        return this.collection;
    }

    public void setResult(int[] set) {
        this.collection = set;
    }

    public boolean isPoweredUp() {
        return poweredUp;
    }

    public void setPoweredUp(boolean poweredUp) {
        this.poweredUp = poweredUp;
    }
}
