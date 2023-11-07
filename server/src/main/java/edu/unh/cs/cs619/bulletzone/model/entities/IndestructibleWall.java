package edu.unh.cs.cs619.bulletzone.model.entities;

import edu.unh.cs.cs619.bulletzone.datalayer.structure.StructureType;

public class IndestructibleWall extends StructureEntity{
    public IndestructibleWall() {}

    public IndestructibleWall(int pos) {this.pos = pos;}

    @Override
    public long getIntValue() {
        return 1000;
    }

    @Override
    public FieldEntity copy() {
        return new IndestructibleWall();
    }

    @Override
    public StructureType getType() {
        return StructureType.INDESTRUCTIBLE_WALL;
    }
}
