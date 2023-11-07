package edu.unh.cs.cs619.bulletzone.model.entities;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;

public abstract class Powerup extends StaticEntity
        implements Intractable {
    long id;
    int creditValue;

    double moveMultiplier;
    double fireMultiplier;
    double actionMultiplier;
    double bulletMultiplier;

    public Powerup(long id, int creditValue, double moveMultiplier, double fireMultiplier, double actionMultiplier, double bulletMultiplier) {
        this.id = id;
        this.creditValue = creditValue;

        this.moveMultiplier = moveMultiplier;
        this.fireMultiplier = fireMultiplier;
        this.actionMultiplier = actionMultiplier;
        this.bulletMultiplier = bulletMultiplier;
    }

    /**
     * Pickup the powerup
     * @param vehicleEntity The vehicle that is picking up the powerup
     */
    public void pickup(VehicleEntity vehicleEntity){
        vehicleEntity.activePowerups.add(this);
        destroy();
    }

    /**
     * Eject the powerup and places it next to the vehicle
     * @param vehicleEntity The vehicle that is ejecting the powerup
     */
    public void eject(VehicleEntity vehicleEntity){
        vehicleEntity.activePowerups.remove(this);

        // drop it on the ground
        FieldHolder fieldHolder = vehicleEntity.getParent();

        for (int i = 0; i <= 6; i+=2) {
            if (!fieldHolder.getNeighbor(Direction.values()[i]).isPresent()) {
                FieldEntity fieldEntity = copy();
                FieldHolder next = fieldHolder.getNeighbor(Direction.values()[i]);
                next.setFieldEntity(fieldEntity);
                fieldEntity.setParent(next);
                break;
            }
        }
    }

    @Override
    public void destroy() {
        super.kill();
    }

    @Override
    public long getIntValue() {
        return id;
    }

    public static Powerup getRandomPowerup() {
        int random = (int)(Math.random() * 2);
        if (random == 0) {
            return new GravAssist();
        } else {
            return new FusionGenerator();
        }
    }
}
