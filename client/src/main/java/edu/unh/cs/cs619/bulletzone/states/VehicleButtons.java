package edu.unh.cs.cs619.bulletzone.states;

public abstract class VehicleButtons {

    public boolean canMoveUp = true;
    public boolean canMoveDown = true;
    public boolean canTurnLeft = true;
    public boolean canTurnRight = true;
    public boolean canFire = true;
    public boolean canEject = false;

    public boolean canMine = false;
    public boolean canBuild = false;
    public boolean canDismantle = false;

    public boolean canSelectTank;
    public boolean canSelectMiner;
    public boolean canSelectBuilder;

    public void setDead() {
        this.setCanMoveUp(false);
        this.setCanMoveDown(false);
        this.setCanTurnLeft(false);
        this.setCanTurnRight(false);
        this.setCanFire(false);
        this.setCanEject(false);
        this.setCanMine(false);
        this.setCanBuild(false);
        this.setCanDismantle(false);
    }

    public void setCanMoveUp(boolean canMoveUp) {
        this.canMoveUp = canMoveUp;
    }

    public void setCanMoveDown(boolean canMoveDown) {
        this.canMoveDown = canMoveDown;
    }

    public void setCanTurnLeft(boolean canTurnLeft) {
        this.canTurnLeft = canTurnLeft;
    }

    public void setCanTurnRight(boolean canTurnRight) {
        this.canTurnRight = canTurnRight;
    }

    public void setCanFire(boolean canFire) {
        this.canFire = canFire;
    }

    public void setCanEject(boolean canEject) {
        this.canEject = canEject;
    }

    public void setCanMine(boolean canMine) {
        this.canMine = canMine;
    }

    public void setCanBuild(boolean canBuild) {
        this.canBuild = canBuild;
    }

    public void setCanDismantle(boolean canDismantle) {
        this.canDismantle = canDismantle;
    }
}
