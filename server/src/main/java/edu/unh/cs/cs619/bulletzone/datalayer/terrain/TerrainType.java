package edu.unh.cs.cs619.bulletzone.datalayer.terrain;

import java.util.ArrayList;
import java.util.Arrays;

import edu.unh.cs.cs619.bulletzone.model.entities.Bullet;
import edu.unh.cs.cs619.bulletzone.model.entities.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.entities.Miner;
import edu.unh.cs.cs619.bulletzone.model.entities.MovableEntity;

/**
 * TerrainType is an enum that contains all the possible terrain types
 */
public enum TerrainType {
    WATER(10000, false, 4, new ArrayList<>(Arrays.asList(Bullet.class))), // not traversable
    SAND(10000, true, 1.2, new ArrayList<>(Arrays.asList(MovableEntity.class))),
    MEADOW(10000, true, 1, new ArrayList<>(Arrays.asList(MovableEntity.class))),
    FOREST(10003, false, 2, new ArrayList<>(Arrays.asList(Miner.class))), // restricted only to miner
    ROCKY(10001, true, 2, new ArrayList<>(Arrays.asList(MovableEntity.class))), // 2x slow
    HILLY(10002, false, 1.5, new ArrayList<>(Arrays.asList(MovableEntity.class))), // 50% slow
    DRYLAND(10001, true, 1.2, new ArrayList<>(Arrays.asList(MovableEntity.class)));

    // The resourceTypeID is the ID of the resource that is associated with this terrain type
    public final int resourceTypeID;

    public final boolean isSpawnable;

    // The speed modifier for this terrain type, Larger is slower (because it's a multiplier for the time till next move)
    public final double speedModifier;
    public final ArrayList<Class<?>> allowedEntities; // what classes are allowed to enter this terrain

    TerrainType(int resourceTypeID, boolean isSpawnable, double speedModifier, ArrayList<Class<?>> allowedEntities) {
        this.resourceTypeID = resourceTypeID;
        this.isSpawnable = isSpawnable;
        this.speedModifier = speedModifier;
        this.allowedEntities = allowedEntities;
    }

    public boolean canMoveThrough(FieldEntity entity) {
        for (Class<?> clazz : allowedEntities) {
            if (clazz.isAssignableFrom(entity.getClass())) {
                return true;
            }
        }
        return false;
    }
}
