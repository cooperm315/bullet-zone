package edu.unh.cs.cs619.bulletzone.util;

public class IntArrayWrapper {
    private int[] collection;

    public IntArrayWrapper() {}

    public IntArrayWrapper(int[] input) {
        this.collection = input;
    }

    public int[] getResult() {
        return this.collection;
    }

    public void setResult(int[] set) {
        this.collection = set;
    }
}
