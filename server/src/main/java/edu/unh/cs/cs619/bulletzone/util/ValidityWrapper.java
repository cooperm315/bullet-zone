package edu.unh.cs.cs619.bulletzone.util;

public class ValidityWrapper {

    private boolean canMoveForward;
    private boolean canMoveBackward;
    private boolean build;
    private boolean dismantle;

    private boolean mine;

    private boolean powerUp;

    public ValidityWrapper() {}

    public ValidityWrapper(boolean canMoveForward, boolean canMoveBackward, boolean build, boolean dismantle, boolean mine, boolean powerUp) {
        this.canMoveForward = canMoveForward;
        this.canMoveBackward = canMoveBackward;
        this.build = build;
        this.dismantle = dismantle;
        this.mine = mine;
        this.powerUp = powerUp;
    }

    public boolean isCanMoveForward() {
        return canMoveForward;
    }

    public boolean isCanMoveBackward() {
        return canMoveBackward;
    }

    public boolean isBuild() {
        return build;
    }

    public boolean isDismantle() {
        return dismantle;
    }

    public boolean isMine() {return mine;}

    public boolean isPowerUp() {return powerUp;}
}
