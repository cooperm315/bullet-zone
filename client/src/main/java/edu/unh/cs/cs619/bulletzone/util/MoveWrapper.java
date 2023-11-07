package edu.unh.cs.cs619.bulletzone.util;

/**
 * Created by aidan on 5/4/23.
 */
public class MoveWrapper {

    private boolean result;
    private boolean forwardOpen;
    private boolean backwardOpen;
    private boolean build;
    private boolean dismantle;

    public MoveWrapper() {}

    public MoveWrapper(boolean result, boolean isForwardOpen, boolean isBackwardOpen, boolean build, boolean dismantle) {
        this.result = result;
        this.forwardOpen = isForwardOpen;
        this.backwardOpen = isBackwardOpen;
        this.build = build;
        this.dismantle = dismantle;

    }

    public boolean isResult() {
        return result;
    }

    public boolean isForwardOpen() {
        return forwardOpen;
    }

    public boolean isBackwardOpen() {
        return backwardOpen;
    }

    public boolean isBuild() {
        return build;
    }

    public boolean isDismantle() {
        return dismantle;
    }


}