package edu.unh.cs.cs619.bulletzone.util;

/**
 * Created by simon on 10/1/14.
 */
public class GridWrapper {
    private long[][] grid;

    private long timeStamp;

    public GridWrapper() {
    }

    public GridWrapper(long[][] grid) {
        this.grid = grid;
    }

    public long[][] getGrid() {
        return this.grid;
    }

    public void setGrid(long[][] grid) {
        this.grid = grid;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
