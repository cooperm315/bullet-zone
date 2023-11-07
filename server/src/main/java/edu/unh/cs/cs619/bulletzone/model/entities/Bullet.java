package edu.unh.cs.cs619.bulletzone.model.entities;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;

public class Bullet extends MovableEntity {

    private long tankId;
    private int bulletId;
    private BulletType bulletType;

    public Bullet(long tankId, Direction direction, BulletType bulletType) {
        this.setTankId(tankId);
        this.setDirection(direction);
        this.setBulletType(bulletType);
    }

    @Override
    public long getIntValue() {
        return 2000000 + 1000 * tankId + bulletType.bulletDamage * 10 + bulletId;
    }

    @Override
    public String toString() {
        return "B";
    }

    @Override
    public FieldEntity copy() {
        return new Bullet(tankId, direction, bulletType);
    }

    public long getTankId() {
        return tankId;
    }

    public void setTankId(long tankId) {
        this.tankId = tankId;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public BulletType getBulletType() {
        return bulletType;
    }

    public void setBulletType(BulletType bulletType) {
        this.bulletType = bulletType;
    }

    public int getDamage() {
        return bulletType.bulletDamage;
    }

//    public void setDamage(int damage) {
//        this.bulletType.bulletDamage = damage;
//    }

    public void setBulletId(int bulletId) {
        this.bulletId = bulletId;
    }

    public int getBulletId() {
        return bulletId;
    }

    @Override
    public boolean collide(FieldEntity other) {
        if (other instanceof VehicleEntity) {
            VehicleEntity vehicle = (VehicleEntity) other;
            vehicle.hit(this.getDamage());
            if (vehicle.health <= 0) {
                vehicle.kill();
            }
        } else if (other instanceof Wall) {
            Wall w = (Wall) other;

            w.hit(this.getDamage());
            if (w.health <= 0) {
                w.kill();
            }
        } else if (other instanceof Intractable) {
            game.items.remove(other);

            Intractable i = (Intractable) other;
            i.destroy();
        }
        return false; // cant move
    }

    @Override
    public boolean move(Direction direction) {
        FieldHolder currentField = this.getParent();
        FieldHolder nextField = currentField.getNeighbor(direction);

        boolean canMove = true;

        if (!nextField.getTerrainType().canMoveThrough(this)) // if terrain doesn't allow this entity to move through it, then return false
            canMove = false;


        if (canMove){
            if (this.isColliding(direction)) { // calls collide() if collision
                canMove = collide(this.getParent().getNeighbor(direction).getEntity());
            }
        }

        if (currentField.getEntity() == this) // remove from screen if there
            currentField.clearField();

        if (canMove) { // if can move, move
            nextField.setFieldEntity(this);
            this.setParent(nextField);
            return true;
        }
        return false;
    }
}
