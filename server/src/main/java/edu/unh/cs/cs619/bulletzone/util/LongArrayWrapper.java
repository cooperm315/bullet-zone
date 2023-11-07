package edu.unh.cs.cs619.bulletzone.util;

public class LongArrayWrapper {
    private long[] collection;

    public LongArrayWrapper() {

    }

    public LongArrayWrapper(long[] input) {
        this.collection = input;
    }

    public long[] getResult() {
        return this.collection;
    }

    public void setResult(long[] set) {
        this.collection = set;
    }
}
