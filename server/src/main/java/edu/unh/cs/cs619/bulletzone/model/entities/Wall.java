package edu.unh.cs.cs619.bulletzone.model.entities;

import edu.unh.cs.cs619.bulletzone.datalayer.structure.StructureType;

public class Wall extends StructureEntity {

    public Wall(){
        this.health = 100;
    }

    public Wall(int health, int pos){
        this.health = health;
        this.pos = pos;
    }

    @Override
    public FieldEntity copy() {
        return new Wall();
    }

    @Override
    public long getIntValue() {
        return 1000 + health;
    }

    @Override
    public String toString() {
        return "W";
    }

    public int getPos(){
        return pos;
    }

    @Override
    public StructureType getType() {
        return StructureType.WALL;
    }
}
