package edu.unh.cs.cs619.bulletzone.model.entities;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;

public class GravAssist extends Powerup{

    public GravAssist() {
        super(12000, 300, 0.5, 1.25, 1.0, 1.0);
    }

    @Override
    public FieldEntity copy() {
        return new GravAssist();
    }

    @Override
    public long getIntValue() {return id;}
}
