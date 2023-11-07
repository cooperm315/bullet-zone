package edu.unh.cs.cs619.bulletzone.model.entities;

import edu.unh.cs.cs619.bulletzone.datalayer.structure.StructureType;

public class Road extends StructureEntity {
    public Road() {}

    public Road(int pos) {
        this.pos = pos;
    }

    @Override
    public long getIntValue() {return 2000000000;}

    @Override
    public FieldEntity copy() {return new Road();}

    @Override
    public StructureType getType() {
        return StructureType.ROAD;
    }
}
