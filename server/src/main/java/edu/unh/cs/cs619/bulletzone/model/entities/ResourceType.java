package edu.unh.cs.cs619.bulletzone.model.entities;

public enum ResourceType {
    CLAY(10000, 16, 1),
    ROCK(10001, 25, 1),
    IRON(10002, 78, 1),
    WOOD(10003, 1, 1);

    private final long id;
    private final int creditValue;
    private final int resourceValue;

    ResourceType(long id, int creditValue, int resourceValue) {
        this.id = id;
        this.creditValue = creditValue;
        this.resourceValue = resourceValue;
    }

    public long getID() {
        return id;
    }

    public int getCreditValue() {
        return creditValue;
    }

    public int getResourceValue() {
        return resourceValue;
    }

    public static ResourceType fromID(int id) {
        switch (id) {
            case 10000:
                return CLAY;
            case 10001:
                return ROCK;
            case 10002:
                return IRON;
            case 10003:
                return WOOD;
            default:
                return null;
        }
    }
}
