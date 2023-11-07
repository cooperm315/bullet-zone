package edu.unh.cs.cs619.bulletzone.model.entities;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;

public class FusionGenerator extends Powerup{

    public FusionGenerator() {
        super(12001, 400, 1.25, 0.5, 0.5, 2.0);
    }
    @Override
    public FieldEntity copy() {
        return new FusionGenerator();
    }

    @Override
    public long getIntValue() {return id;}
}
