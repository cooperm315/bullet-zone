package edu.unh.cs.cs619.bulletzone.model;

import edu.unh.cs.cs619.bulletzone.states.MinerButtons;
import edu.unh.cs.cs619.bulletzone.states.TankButtons;
import edu.unh.cs.cs619.bulletzone.states.VehicleButtonStates;

public class GameUser {
    private static GameUser user;

    private int[] inventory = new int[5]; // index 0 = clay, index 1 = rock, index 2 = iron, index 3 = wood, index 4 = credits

    private long tankId;
    private long minerId;
    private long currId;
    private long builderId;

    private long minerTerrain;

    private byte tankDirection;
    private byte minerDirection;
    private byte builderDirection;
    public boolean isMining;
    public boolean isBuilding;
    public boolean isDismantling;

    private String username;
    private int activeHealth; // health of active unit
    private boolean isAlive = true; // is the active unit alive

    VehicleButtonStates vehicleButtonStates;


    private GameUser() {
        tankId = -1;
        minerId = -1;
        builderId = -1;
        username = null;
        isMining = false;
        isBuilding = false;
        isDismantling = false;
        for (int i = 0; i < 5; i++)
            inventory[i] = 0;
        vehicleButtonStates = VehicleButtonStates.getInstance();
    }

    public static synchronized GameUser getInstance() {
        if (user == null)
            user = new GameUser();

        return user;
    }

    /**
     * Returns the ID of the active unit (just the ID, not the whole object)
     *
     * @return ID of active unit
     */
    public long getTankId() {return tankId;}

    public void setTankId(long tankId) {this.tankId = tankId;}

    public long getMinerId() {return minerId;}

    public void setMinerId(long minerId) {this.minerId = minerId;}

    public long getBuilderId() {return this.builderId;}

    public void setBuilderId(long builderId) {this.builderId = builderId;}

    public long getCurrId() {return currId;}

    public void setCurrId(long currId) {this.currId = currId;}

    public String getUsername() {return username;}

    public String getActiveUnit() {
        if (currId == tankId) {
            return "TANK";
        }
        if (currId == minerId) {
            return "MINER";
        }
        if (currId == builderId) {
            return "BUILDER";
        }
        return "NO UNIT FOUND";
    }

    public long getActiveHealth() {
        return activeHealth;
    }

    public void setActiveHealth(int activeHealth) {
        this.activeHealth = activeHealth;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        if (alive == false) {
            activeHealth = 0;
            vehicleButtonStates.getActiveButtons().setDead();
        }
        isAlive = alive;
    }
    public void setUsername(String username) {this.username = username;}

    public byte getTankDirection() {return tankDirection;}

    public byte getMinerDirection() {return minerDirection;}

    public byte getBuilderDirection() {return builderDirection;}

    public void setTankDirection(byte tankDirection) {this.tankDirection = tankDirection;}

    public void setMinerDirection(byte minerDirection) {this.minerDirection = minerDirection;}

    public long getMinerTerrain() {return this.minerTerrain;}

    public void setBuilderDirection(byte builderDirection) {this.builderDirection = builderDirection;}

    public void setMinerTerrain(long terrain) {this.minerTerrain = terrain;}

    public void toggleMining() {isMining = !isMining;}

    public int[] getInventory() {return inventory;}

    public void setInventory(int[] inventory) {
        this.inventory = inventory;
    }
}
