package edu.unh.cs.cs619.bulletzone.model.entities;

import edu.unh.cs.cs619.bulletzone.datalayer.structure.StructureType;

public class Deck extends StructureEntity {
    public Deck() {}

    public Deck(int pos) {
        this.pos = pos;
    }

    @Override
    public long getIntValue() {
        return 1000000000;
    }

    @Override
    public FieldEntity copy() {
        return new Deck();
    }

    @Override
    public StructureType getType() {
        return StructureType.DECK;
    }
}
