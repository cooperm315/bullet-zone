package edu.unh.cs.cs619.bulletzone.repository;

import edu.unh.cs.cs619.bulletzone.datalayer.structure.StructureType;
import static com.google.common.base.Preconditions.checkNotNull;

import edu.unh.cs.cs619.bulletzone.datalayer.structure.StructureType;
import edu.unh.cs.cs619.bulletzone.datalayer.terrain.TerrainType;
import edu.unh.cs.cs619.bulletzone.model.entities.Builder;
import edu.unh.cs.cs619.bulletzone.model.entities.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.entities.IndestructibleWall;
import edu.unh.cs.cs619.bulletzone.model.entities.Item;
import edu.unh.cs.cs619.bulletzone.model.entities.Miner;
import edu.unh.cs.cs619.bulletzone.model.entities.Powerup;
import edu.unh.cs.cs619.bulletzone.model.entities.StaticEntity;
import edu.unh.cs.cs619.bulletzone.model.entities.StructureEntity;
import edu.unh.cs.cs619.bulletzone.model.entities.VehicleEntity;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.entities.Wall;

/**
 * Checks if the tank meets the constraints for moving, turning, and firing
 */
public class ConstraintEnforcer {
    /**
     * Checks if the tank meets the all the constraints for moving
     * * Time constraints
     * * Direction constraints
     * * Field constraints
     *
     * @param c the controllable object to check
     * @param direction the direction requested to move
     * @param nextField the field the tank is trying to move to
     * @return true if the tank is allowed to move, false otherwise
     */
    public static boolean isValidMove(VehicleEntity c, Direction direction, FieldHolder nextField) {
        // check if the tank is allowed to move (direction constraints)
        if (!isAllowedToMove(c, direction)) {
            return false;
        }

        if (!meetsMovementTimeConstraints(c)) { // if the tank is not allowed to turn (time constraints, return false
            return false;
        }

        return true;
    }

    /**
     * Checks if the tank meets the time constraints for turning (moving)
     * @param c the tank to check
     * @return true if the tank is allowed to move, false otherwise
     */
    public static boolean isValidTurn(VehicleEntity c, Direction direction) {
        if (!meetsTurnTimeConstraints(c)) { // if the tank does not meet the time constraints, return false
            return false;
        }

        Direction currentDirection = c.getDirection();

        if (currentDirection == direction) { // if the tank is already facing the direction, return false
            return false;
        }

        if (currentDirection == Direction.getBack(direction)) // if the tank is facing the opposite direction, return false
            return false;

        return true;
    }

    /**
     * Checks if the tank meets the time constraints and bullet constraints for firing
     * @param v the vehicle entity to check
     * @return true if the tank is allowed to fire, false otherwise
     */
    public static boolean isValidFire(VehicleEntity v) {
        if(v.getNumberOfBullets() >= v.getAllowedBullets()) // too many bullets, return false
            return false;
        if(!meetsFireTimeConstraints(v))
            return false;

        return true;
    }

    /**
     *
     * @param terrainId the return of getResource() at a certain cell
     * @return true if resource present, false otherwise
     */
    public static boolean isValidMine(Miner m, long terrainId) {
        if (m.getParent().hasStructure())
            return false;
        // if terrain id not in TerrainType.values(), return false
        boolean isValidMine = false;
        for (TerrainType t : TerrainType.values()) {
            if (t.ordinal() == terrainId) {
                isValidMine = true;
                break;
            }
        }

        if(isValidMine == false)
            return false;

        long millis = System.currentTimeMillis();
        // if current time is less than the time to next mine, return false
        if (millis < m.getNextAllowedActionTime())
            return false;

        return true;
    }

    public static boolean isValidBuild(Builder builder, long structureId, int[] inventory) {
        // the cell directly in front the builder
        FieldHolder nextCell = builder.getParent().getNeighbor(Direction.getBack(builder.getDirection()));

        // can build on water if it is a deck
        if (structureId == 1 && nextCell.getTerrainType() == TerrainType.WATER)
            return true;

        // can't build deck on land
        if (structureId == 1 && nextCell.getTerrainType() != TerrainType.WATER)
            return false;

        // cant place on water
        if (nextCell.getTerrainType() == TerrainType.WATER)
            return false;

        // can't build if already has structure
        if (nextCell.hasStructure())
            return false;

        // can't build if occupied by another vehicle, unless it is a road
        if (nextCell.isPresent() && structureId != 2) {
            return false;
        }

        // can build if the player has the resources needed
        if (playerHasResource(structureId, inventory))
            return true;

        return false;
    }

    public static boolean isValidDismantle(Builder builder) {
        // the cell directly behind the builder
        FieldHolder nextCell = builder.getParent().getNeighbor(Direction.getBack(builder.getDirection()));
        if (nextCell.hasStructure() || nextCell.isPresent())
            return true;

        if (!nextCell.isPresent())
            return false;

        if (nextCell.getEntity() instanceof StructureEntity ||
                nextCell.getEntity() instanceof Powerup)
            return true;

        return false;
    }

    /**
     * Checks if the tank is allowed to move in the given direction
     * Used for the tank's direction constraints (like only moving up or down when facing up or down)
     * @param tank the tank to check
     * @param direction the direction requested to move
     * @return true if the tank is allowed to move in the given direction, false otherwise
     */
    private static boolean isAllowedToMove(VehicleEntity tank, Direction direction) {
        Direction currentDirection = tank.getDirection();

        FieldHolder nextField = tank.getParent().getNeighbor(direction);
        TerrainType terrainType = nextField.getTerrainType();

        // only happens if water is present (checking to place deck done in isValidBuild)
        if (nextField.getStructureType() == StructureType.DECK)
            return true;

        // If the tank is facing the same direction or the opposite direction, return true
        if (currentDirection == direction || currentDirection == Direction.getBack(direction))
            return true;

        return false;
    }

    /**
     * Checks if the tank meets the time constraints for firing
     * @param c the controllable entity to check
     * @return true if the tank meets the time constraints, false otherwise
     */
    private static boolean meetsFireTimeConstraints(VehicleEntity c) {
        long millis = System.currentTimeMillis();
        // if current time is less than the time allowed for the next move, return false
        if (millis < c.getNextAllowedFireTime())
            return false;

        return true;
    }

    /**
     * Checks if the tank meets the time constraints for moving
     * @param c the tank to check
     * @return true if the tank meets the time constraints, false otherwise
     */
    private static boolean meetsMovementTimeConstraints(VehicleEntity c) {
        long millis = System.currentTimeMillis();
        // if current time is less than the time to next move, return false
        if (millis < c.getNextAllowedMoveTime())
            return false;

        return true;
    }

    private static boolean meetsTurnTimeConstraints(VehicleEntity c) {
        long millis = System.currentTimeMillis();
        if (millis < c.getNextAllowedTurnTime())
            return false;

        return true;
    }

    private static boolean playerHasResource(long structureId, int[] inv) {
        switch ((int) structureId) {
            case 1:
                if (inv[2] >= 1 && inv[3] >= 5) return true;
                break;
            case 2:
                if (inv[0] >= 3) return true;
                break;
            case 3:
                if (inv[0] >= 1 && inv[1] >= 2) return true;
                break;
            case 4:
                if (inv[0] >= 3 && inv[1] >= 3 && inv[2] >= 3) return true;
                break;
            default:
                break;
        }

        return false;
    }
}