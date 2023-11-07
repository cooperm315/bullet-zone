package edu.unh.cs.cs619.bulletzone.repository;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.entities.VehicleEntity;

public interface GameRepository {

    /**
     * Joins an IP into a game as a tank
     * Also starts the game (sets status to RUNNING)
     * @param username username to join
     * @return Tank object
     */
    VehicleEntity[] join(String username);

    long[][] getGrid();

    boolean turn(long tankId, Direction direction)
            throws TankDoesNotExistException, IllegalTransitionException, LimitExceededException;

    boolean move(long tankId, Direction direction)
            throws TankDoesNotExistException, IllegalTransitionException, LimitExceededException;

    boolean fire(long tankId, int strength)
            throws TankDoesNotExistException, LimitExceededException;

    boolean mine (long minerId, long resourceId) throws TankDoesNotExistException, InterruptedException;

    boolean build(long builderId, long structureId) throws TankDoesNotExistException, InterruptedException;

    boolean dismantle(long builderId) throws TankDoesNotExistException, InterruptedException;

    /**
     * Leaves a tank from the game
     * Also stops the game if it's the last tank to leave (sets status to STOPPED)
     * @param username username of tank to leave
     * @throws TankDoesNotExistException if tank does not exist
     */
    void leave(String username)
            throws TankDoesNotExistException;

    void devAddResources(String username);

    int[] getInventory(String username);

    boolean eject(long vehicleId);

    boolean canBuild(long vehicleId);

    boolean canDismantle(long vehicleId);

//    long getVehicleHealth(long vehicleId)
//            throws TankDoesNotExistException;
    boolean getFrontMoveValidity(long tankId);

    boolean getBackMoveValidity(long tankId);

    boolean getCanBuild(long tankId);

    boolean getCanMine(long currId);

    boolean getCanDismantle(long tankId);


    boolean getPoweredUp(long tankId);

}
