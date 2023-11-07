package edu.unh.cs.cs619.bulletzone.model.entities;

import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedDeque;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.repository.ConstraintEnforcer;

public abstract class VehicleEntity extends MovableEntity {
    protected final long id;
    protected final String username;

    protected int baseAllowedBullets;
    protected int allowedBullets;

    protected final int baseMoveInterval;
    protected int allowedMoveInterval;
    protected long nextAllowedMoveTime;

    private long nextAllowedTurnTime;
    private int baseTurnInterval;



    protected int baseFireInterval;
    protected int allowedFireInterval;
    protected long nextAllowedFireTime;

    protected int baseActionInterval; // for mining and building
    protected int allowedActionInterval;
    protected long nextAllowedActionTime;

    protected int rammingDamage;
    protected int rammingSelfDamage;

    private ArrayList<Bullet> activeBullets = new ArrayList<>(); // allows for any amount of bullets

    // Basically a thread safe stack?
    public ConcurrentLinkedDeque<Powerup> activePowerups = new ConcurrentLinkedDeque<>(); // allows for any amount of powerups

    protected VehicleEntity(long id, String username, int baseAllowedBullets, int initialMoveInterval, int initialTurnInterval, int initialFireInterval, int initialActionInterval) {
        this.id = id;
        this.username = username;

        this.baseAllowedBullets = baseAllowedBullets;
        this.allowedBullets = baseAllowedBullets;

        this.baseTurnInterval =  initialTurnInterval;
        this.baseMoveInterval = initialMoveInterval;
        this.baseFireInterval = initialFireInterval;
        this.baseActionInterval = initialActionInterval;


        this.allowedMoveInterval = initialMoveInterval;
        this.allowedFireInterval = initialFireInterval;
        this.allowedActionInterval = initialActionInterval;

        this.nextAllowedTurnTime = 0;
        this.nextAllowedMoveTime = 0;
        this.nextAllowedFireTime = 0;
        this.nextAllowedActionTime = 0;
    }


    @JsonIgnore
    public long getId() {
        return id;
    }

    @Override
    public boolean collide(FieldEntity other) {
        boolean canMove = true;
        if (other instanceof Intractable) {
            Intractable i = (Intractable) other;
            i.pickup(this);
        } else if (other instanceof VehicleEntity) {
            VehicleEntity c = (VehicleEntity) other;
            c.health -= rammingDamage;
            this.health -= rammingSelfDamage;
            if (c.health <= 0) {
                c.kill();
            } else
                canMove = false;

        } else if (other instanceof Wall) {
            Wall w = (Wall) other;
            // if the wall is not indestructible, hit it
            this.health -= rammingSelfDamage; // take damage from hitting wall
            w.hit(this.rammingDamage);
            if (w.health <= 0) {
                w.kill();
            } else
                canMove = false;
        } else if (other instanceof IndestructibleWall) {
            canMove = false;
        }
        if (this.health <= 0) {
            this.kill();
        }
        return canMove;
    }

    @Override
    public boolean move(Direction direction) {
        FieldHolder parent = this.getParent();

        FieldHolder nextField = parent.getNeighbor(direction);
        checkNotNull(parent.getNeighbor(direction), "Neighbor is not available");

        boolean canMove = false;

        if (ConstraintEnforcer.isValidMove(this, direction, nextField)) {
            if (!nextField.hasStructure()) {
                // check valid terrain
                // if terrain doesn't allow this entity to move through it, then return false
                if (!nextField.getTerrainType().canMoveThrough(this))
                    return false;
            }

            // calls collide() if collision is detected
            if (this.isColliding(direction)) {
                canMove = this.collide(nextField.getEntity());
            } else
                canMove = true;

            //setting next allowed move time
            long millis = System.currentTimeMillis();
            this.nextAllowedMoveTime = millis + getMoveInterval();
            this.nextAllowedTurnTime = millis + getTurnInterval();

            if (!canMove) {
                return false;
            }

            //moving tank
            parent.clearField();
            nextField.setFieldEntity(this);
            this.setParent(nextField);
            canMove = true;
        }

        return canMove;
    }

    @Override
    public boolean turn(Direction direction) {
        if (ConstraintEnforcer.isValidTurn(this, direction)) {
            long millis = System.currentTimeMillis();
            this.nextAllowedMoveTime = millis + this.getMoveInterval();
            this.nextAllowedTurnTime = millis + this.getTurnInterval();
            this.setDirection(direction);
            return true;
        }
        return false;
    }

    public boolean fire(BulletType bulletType) {
        if (!ConstraintEnforcer.isValidFire(this)) {
            return false;
        }

        Direction direction = this.getDirection();
        FieldHolder parent = this.getParent();

        // commented out because bullet type is now an enum
        // might need to check if bullet type is valid in IMGR
//        if (!(bulletType >= 1 && bulletType <= 3)) {
//            System.out.println("Bullet type must be 1, 2 or 3, set to 1 by default.");
//            bulletType = 1;
//        }

        // setting next allowed fire time based on bullet type
        long millis = System.currentTimeMillis();
        this.nextAllowedFireTime = millis + bulletType.bulletDelay;

        // Create a new bullet to fire
        final Bullet bullet = new Bullet(this.id, direction, bulletType);
        // Set the same parent for the bullet.
        // This should be only a one way reference.
        bullet.setParent(parent);

        activeBullets.add(bullet); // add new bullet to list
        bullet.setBulletId(activeBullets.indexOf(bullet)); // set bullet id to index of bullet in list
        bullet.setGame(game);

        // For use in thread because "this" is not accessible in thread
        VehicleEntity entity = this;
        Timer timer = new Timer();
        timer.schedule(
                new TimerTask() {

                    @Override
                    public void run() {
                        // This might be important for thread safety, but we'll see how it is without it
                        // Having the tanks manage the bullets moving is probably not the best idea but it works for now
                        //synchronized (monitor) {
                        System.out.println("Active Bullet: " + entity.getNumberOfBullets() + "---- Bullet ID: " + bullet.getIntValue());

                        if (!bullet.move(bullet.getDirection())) { //if collision, bullet will be removed
                            activeBullets.remove(bullet);
                            cancel();
                        }
                        //}
                    }
                }, 0, bulletType.bulletSpeed);

        return true;
    }

    @Override
    public void kill() {
        super.kill();
        game.killVehicle(this.getId());
    }

    public void onLeave() {
        this.getParent().clearField();
        this.setParent(null);
        game.removeVehicle(this.getId());
    }

    public boolean ejectPowerup() {
        if (!this.activePowerups.isEmpty()) {
            Powerup p = this.activePowerups.pop();
            p.eject(this);
            return true;
        }
        return false;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getNumberOfBullets() {
        return activeBullets.size();
    }

    public long getNextAllowedActionTime() {
        return nextAllowedActionTime;
    }

    public long getNextAllowedFireTime() {
        return nextAllowedFireTime;
    }

    public long getNextAllowedMoveTime() {
        return nextAllowedMoveTime;
    }

    public long getNextAllowedTurnTime() {return nextAllowedTurnTime;}

    // Getting initials


    public int getBaseActionInterval() {
        return baseActionInterval;
    }

    public int getBaseAllowedBullets() {
        return baseAllowedBullets;
    }

    public int getBaseFireInterval() {
        return baseFireInterval;
    }

    public int getBaseMoveInterval() {
        return baseMoveInterval;
    }

//    public int getActionInterval() {
//        int modifiedActionInterval = this.baseActionInterval;
//        for (Powerup p : this.activePowerups) {
//            modifiedActionInterval *= p.actionMultiplier;
//        }
//        return modifiedActionInterval;
//    }

    public int getFireInterval() {
        int modifiedFireInterval = this.baseFireInterval;
        for (Powerup p : this.activePowerups) {
            modifiedFireInterval *= p.fireMultiplier;
        }
        return modifiedFireInterval;
    }

    public int getTurnInterval() {
        int modifiedTurnInterval = this.baseTurnInterval;
        if (getParent().hasStructure())
            modifiedTurnInterval *= getParent().getStructureType().speedModifier;
        else
            modifiedTurnInterval *= getParent().getTerrainType().speedModifier;
        for (Powerup p : this.activePowerups) {
            modifiedTurnInterval *= p.moveMultiplier;
        }
        return modifiedTurnInterval;
    }

    public int getActionInterval() {
        int modifiedActionInterval = this.baseActionInterval;
        for (Powerup p : this.activePowerups) {
            modifiedActionInterval *= p.moveMultiplier;
        }
        return modifiedActionInterval;
    }

    public int getMoveInterval() {
        int modifiedMoveInterval = this.baseMoveInterval;
        if (getParent().hasStructure())
            modifiedMoveInterval *= getParent().getStructureType().speedModifier;
        else
            modifiedMoveInterval *= getParent().getTerrainType().speedModifier;
        for (Powerup p : this.activePowerups) {
            modifiedMoveInterval *= p.moveMultiplier;
        }
        return modifiedMoveInterval;
    }

    public int getAllowedBullets() {
        int modifiedAllowedBullets = this.baseAllowedBullets;
        for (Powerup p : this.activePowerups) {
            modifiedAllowedBullets *= p.bulletMultiplier;
        }
        return modifiedAllowedBullets;
    }

    public String getUsername() {
        return this.username;
    }
}
