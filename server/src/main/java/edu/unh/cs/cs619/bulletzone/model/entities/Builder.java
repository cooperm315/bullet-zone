package edu.unh.cs.cs619.bulletzone.model.entities;

import edu.unh.cs.cs619.bulletzone.datalayer.structure.StructureType;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.repository.ConstraintEnforcer;

public class Builder extends VehicleEntity {
    public Builder(long id, Direction direction, String username) {
        super(id, username, 6, 1000, 300, 1000, 1000); // controllable entity has id
        this.direction = direction;
        this.health = 80;
        this.rammingDamage = 0;
        this.rammingSelfDamage = 0;
    }

    public boolean build(long structureId) {
        if (ConstraintEnforcer.isValidBuild(this, structureId, game.getInventory(username))) {
            StructureType structure = StructureType.values()[(int) structureId - 1];
            FieldHolder nextCell = getParent().getNeighbor(Direction.getBack(direction));

            // sketchy wait logic
            long currTime = System.currentTimeMillis();
            long endTime = System.currentTimeMillis() + (1000L * getActionInterval(structure));
            while (currTime < endTime)
                currTime = System.currentTimeMillis();

            if (structureId > 0 && structureId < 5) {
                for (int i = 0; i < ResourceType.values().length; i++)
                    game.removeResource(username, ResourceType.values()[i], structure.resourceCost[i]);

                switch (structure) {
                    case WALL:
                        Wall wall = new Wall();
                        nextCell.setFieldEntity(wall);
                        wall.parent = nextCell;
                        break;
                    case INDESTRUCTIBLE_WALL:
                        IndestructibleWall iWall = new IndestructibleWall();
                        nextCell.setFieldEntity(iWall);
                        iWall.parent = nextCell;
                        break;
                    default:
                        break;
                }
                nextCell.setStructureType(structure);
                return true;
            }
        }
        return false;
    }

    public boolean dismantle() {
        FieldHolder behind = this.getParent().getNeighbor(Direction.getBack(direction));
        if (ConstraintEnforcer.isValidDismantle(this)) {
            if (behind.hasStructure())
                return dismantleStructure();
            else
                return dismantleEntity();
        }

        return false;
    }

    public boolean dismantleStructure() {
        StructureType structure = getParent().getNeighbor(Direction.getBack(direction)).getStructureType();
        FieldHolder behind = this.getParent().getNeighbor(Direction.getBack(direction));

        // sketchy wait logic
        long currTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis() + (1000L * getActionInterval(structure));
        while (currTime < endTime) {
            currTime = System.currentTimeMillis();
            if (!getParent().getNeighbor(Direction.getBack(direction)).hasStructure())
                return false;
        }

        for (int i = 0; i < ResourceType.values().length; i++)
            game.addResource(username, ResourceType.values()[i], structure.resourceCost[i]);
        if (behind.isPresent())
            behind.getEntity().kill();
        behind.setStructureType(null);
        return true;
    }

    public boolean dismantleEntity(){
        FieldEntity e = getParent().getNeighbor(Direction.getBack(direction)).getEntity();

        if (e instanceof Powerup) {
            Powerup p  = (Powerup) e;
            game.changeCoins(username, p.creditValue);
            p.kill();
            return true;
        } else if (e instanceof ResourceItem) {
            ResourceItem r = (ResourceItem) e;
            game.changeCoins(username, r.creditValue);
            r.kill();
            return true;
        }

        return false;
    }

    public int getActionInterval(StructureType structure) {
        int waitTime = 0;
        for (int i = 0; i < 4; i++)
            waitTime += structure.resourceCost[i];
        for (Powerup p : this.activePowerups) {
            waitTime *= p.moveMultiplier;
        }
        return waitTime;
    }

    @Override
    public boolean collide(FieldEntity other) {
        this.rammingDamage = (int)Math.ceil(this.health * .05);
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
    public FieldEntity copy() {
        return new Builder(id, direction, username);
    }

    @Override
    public long getIntValue() {
        return (int) (40000000 + 10000 * id + 10 * health + Direction
                .toByte(direction));
    }

    @Override
    public String toString() {
        return "T";
    }
}
