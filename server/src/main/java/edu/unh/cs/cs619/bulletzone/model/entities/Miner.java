package edu.unh.cs.cs619.bulletzone.model.entities;

import edu.unh.cs.cs619.bulletzone.datalayer.terrain.TerrainType;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.repository.ConstraintEnforcer;

public class Miner extends VehicleEntity {

    private static final String TAG = "Miner";

    public Miner(long id, Direction direction, String username) {
        super(id, username, 4, 800, 800, 1200, 1000); // controllable entity has id
        this.direction = direction;
        this.health = 300;
    }

    @Override
    public FieldEntity copy() {
        return new Miner(id, direction, username);
    }

    @Override
    public boolean collide(FieldEntity other) {
        this.rammingDamage = (int)Math.ceil(this.health * .1);
        if (other instanceof Wall){
            if(other.health > 0){
                this.rammingSelfDamage = (int)Math.floor((other.health) * .05); // dumb walls have 1000 health meaning 0 which stuffs up the math
            }
        } else {
            this.rammingSelfDamage = (int)Math.floor(other.health * .05);
        }
        return super.collide(other);
    }

    public boolean mine(long terrainId) throws InterruptedException {
        if (ConstraintEnforcer.isValidMine(this, terrainId)) {
            int rId = TerrainType.values()[(int)terrainId - 1].resourceTypeID;
            game.addResource(username, ResourceType.fromID(rId), 1);
            this.nextAllowedActionTime = System.currentTimeMillis() + this.getActionInterval();
            return true;
        }
        return false;
    }

    @Override
    public long getIntValue() {
        return (30000000 + 10000 * id + 10 * health + Direction
                .toByte(direction));
    }

    @Override
    public String toString() {
        return "M";
    }
}
