package edu.unh.cs.cs619.bulletzone.model.entities;

import java.text.Format;

import edu.unh.cs.cs619.bulletzone.datalayer.structure.StructureType;

public abstract class StructureEntity extends StaticEntity {
    protected int pos;
    protected StructureType type;

    public abstract StructureType getType();
}
