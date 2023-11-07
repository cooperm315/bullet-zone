package edu.unh.cs.cs619.bulletzone.model;

import edu.unh.cs.cs619.bulletzone.model.entities.ResourceType;

/**
 * This class represents the inventory of a player in the game
 */
public class Inventory {
    //Resources
    protected int clay;
    protected int rock;
    protected int iron;
    protected int wood;

    public Inventory() {
        clay = 0;
        rock = 0;
        iron = 0;
        wood = 0;
    }

    /**
     * Gets the amount of a resource
     * @param resourceType the resource to get
     * @return the amount of the resource
     */
    public int getResource(ResourceType resourceType) {
        switch (resourceType) {
            case CLAY:
                return clay;
            case ROCK:
                return rock;
            case IRON:
                return iron;
            case WOOD:
                return wood;
        }
        return 0;
    }

    /**
     * Adds resources to the inventory
     * @param resourceType the resource to add
     * @param amount the amount to add
     */
    public void addResource(ResourceType resourceType, int amount) {
        switch (resourceType) {
            case CLAY:
                clay += amount;
                break;
            case ROCK:
                rock += amount;
                break;
            case IRON:
                iron += amount;
                break;
            case WOOD:
                wood += amount;
                break;
        }
    }

    /**
     * Removes resources from the inventory
     * @param resourceType the resource to remove
     * @param amount the amount to remove
     */
    public void removeResource(ResourceType resourceType, int amount) {
        switch (resourceType) {
            case CLAY:
                clay -= amount;
                break;
            case ROCK:
                rock -= amount;
                break;
            case IRON:
                iron -= amount;
                break;
            case WOOD:
                wood -= amount;
                break;
        }
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "Clay=" + clay +
                ", Rock=" + rock +
                ", Iron=" + iron +
                ", Wood=" + wood +
                '}';
    }
}
