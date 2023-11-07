package edu.unh.cs.cs619.bulletzone.util;

public class BuildWrapper {

    private boolean result;
    private boolean build;
    private boolean dismantle;

    public BuildWrapper() {}

    public BuildWrapper(boolean result, boolean build, boolean dismantle) {
        this.result = result;
        this.build = build;
        this.dismantle = dismantle;
    }

    public boolean isResult() {
        return result;
    }

    public boolean isBuild() {
        return build;
    }

    public boolean isDismantle() {
        return dismantle;
    }

}
