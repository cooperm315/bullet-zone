package edu.unh.cs.cs619.bulletzone.model;

import java.util.Optional;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

import edu.unh.cs.cs619.bulletzone.datalayer.structure.StructureType;
import edu.unh.cs.cs619.bulletzone.datalayer.terrain.TerrainType;
import edu.unh.cs.cs619.bulletzone.model.entities.FieldEntity;

public class FieldHolder {

    private final Map<Direction, FieldHolder> neighbors = new HashMap<Direction, FieldHolder>();
    private Optional<FieldEntity> entityHolder = Optional.empty();
    private TerrainType terrainType;
    private StructureType structureType = null;

    public void addNeighbor(Direction direction, FieldHolder fieldHolder) {
        neighbors.put(checkNotNull(direction), checkNotNull(fieldHolder));
    }

    public long getValue() {
        long value = 0;
        // ads a structure if there is one present
        if (structureType != null)
            value += (structureType.ordinal()+1) * 1000000000L;
        // adds terrain to the 100,000,000s place (starting at 1
        value += (terrainType.ordinal()+1) * 100000000;
        if (entityHolder.isPresent()) {
            value += entityHolder.get().getIntValue();
        }
        return value;
    }

    public FieldHolder getNeighbor(Direction direction) {
        return neighbors.get(checkNotNull(direction,
                "Direction cannot be null."));
    }

    public void setTerrainType(TerrainType terrainType) {
        this.terrainType = terrainType;
    }

    public TerrainType getTerrainType() {
        return terrainType;
    }

    public void setStructureType(StructureType structureType) {this.structureType = structureType;}

    public StructureType getStructureType() {return structureType;}

    public boolean hasStructure() {return structureType != null;}

    public boolean isPresent() {
        return entityHolder.isPresent();
    }

    public FieldEntity getEntity() {
        return entityHolder.get();
    }

    public void setFieldEntity(FieldEntity entity) {
        entityHolder = Optional.of(checkNotNull(entity,
                "FieldEntity cannot be null."));
    }

    public void clearField() {
        if (entityHolder.isPresent()) {
            entityHolder = Optional.empty();
        }
    }

}
