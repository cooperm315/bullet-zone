package edu.unh.cs.cs619.bulletzone.model.entities;

import static com.google.common.base.Preconditions.checkNotNull;

import edu.unh.cs.cs619.bulletzone.model.Direction;

public class Tank extends VehicleEntity {

    private static final String TAG = "Tank"; // why is this here? - Chris
    private static final int allowedMoveTime = 500;

    public Tank(long id, Direction direction, String username) {
        super(id, username, 2, 500, 500, 1500, 0); // controllable entity has id
        this.direction = direction;
        this.health = 100;
    }

    @Override
    public FieldEntity copy() {
        return new Tank(id, direction, username);
    }

    @Override
    public boolean collide(FieldEntity other) {
        this.rammingDamage = (int)Math.ceil(this.health * .1);
        if (other instanceof Wall){
            if(other.health > 0){
                this.rammingSelfDamage = (int)Math.floor((other.health) * .1); // dumb walls have 1000 health meaning 0 which stuffs up the math
            }
        } else {
            this.rammingSelfDamage = (int)Math.floor(other.health * .1);
        }
        return super.collide(other);
    }

    @Override
    public long getIntValue() {
        return 10000000 + 10000 * id + 10 * health + Direction
                .toByte(direction);
    }

    @Override
    public String toString() {
        return "T";
    }


}
