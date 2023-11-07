package edu.unh.cs.cs619.bulletzone;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import edu.unh.cs.cs619.bulletzone.model.BattleMap;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.entities.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.entities.Tank;
import edu.unh.cs.cs619.bulletzone.model.entities.VehicleEntity;
import edu.unh.cs.cs619.bulletzone.model.entities.Wall;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;

@RunWith(MockitoJUnitRunner.StrictStubs.class) // needed for repo to not be null ig
public class CollisionTest {
    @InjectMocks
    InMemoryGameRepository repo;

    @Before
    public void setUp() {
        repo.setCurrentBattleMap(new BattleMap()); // map with nothing in it
        repo.terrainSeed = 0;
    }

    @Test
    public void vehicleCollide_collidesWithWall_tankTakesDamage() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        VehicleEntity vehicle = repo.join("")[0];
        //get location
        FieldHolder holder = vehicle.getParent();
        Assert.assertNotNull(vehicle);
        Assert.assertTrue(vehicle.getId() >= 0);

        FieldEntity fieldEntity = new Wall(50, 55); // pos doesn't matter
        FieldHolder itemHolder = holder.getNeighbor(Direction.Up);
        itemHolder.setFieldEntity(fieldEntity);
        fieldEntity.setParent(itemHolder);
        Assert.assertTrue(holder.getNeighbor(Direction.Up).getEntity() instanceof Wall);

        Assert.assertFalse(repo.move(vehicle.getId(), Direction.Up)); // assert false because it should not be able to move

        //assert that the tank took damage
        Assert.assertTrue(fieldEntity.getHealth() < 50);
        Assert.assertTrue(vehicle.getHealth() < 100);

        //assert locations remain the same
        Assert.assertTrue(holder.getNeighbor(Direction.Up).getEntity() instanceof Wall);
        Assert.assertTrue(holder.getEntity() instanceof Tank);
    }

    @Test
    public void vehicleCollide_collidesWithWallAndDestroys_tankMoves() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        VehicleEntity vehicle = repo.join("")[0];
        //get location
        FieldHolder holder = vehicle.getParent();
        Assert.assertNotNull(vehicle);
        Assert.assertTrue(vehicle.getId() >= 0);

        FieldEntity fieldEntity = new Wall(1, 55); // pos doesn't matter
        FieldHolder itemHolder = holder.getNeighbor(Direction.Up);
        itemHolder.setFieldEntity(fieldEntity);
        fieldEntity.setParent(itemHolder);
        Assert.assertTrue(holder.getNeighbor(Direction.Up).getEntity() instanceof Wall);

        Assert.assertTrue(repo.move(vehicle.getId(), Direction.Up)); // assert false because it should not be able to move

        //assert that the tank took damage
        Assert.assertTrue(fieldEntity.getHealth() < 100);

        //assert locations remain the same
        Assert.assertTrue(holder.getNeighbor(Direction.Up).getEntity() instanceof Tank);
    }

    @Test
    public void vehicleCollide_collidesWithTank_tankTakesDamage() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        VehicleEntity vehicle = repo.join("")[0];
        //get location
        FieldHolder holder = vehicle.getParent();
        Assert.assertNotNull(vehicle);
        Assert.assertTrue(vehicle.getId() >= 0);

        VehicleEntity fieldEntity = repo.join("1")[0];
        FieldHolder itemHolder = holder.getNeighbor(Direction.Up);
        itemHolder.setFieldEntity(fieldEntity);
        fieldEntity.setParent(itemHolder);
        Assert.assertTrue(holder.getNeighbor(Direction.Up).getEntity() instanceof Tank);

        Assert.assertFalse(repo.move(vehicle.getId(), Direction.Up)); // assert false because it should not be able to move

        //assert that the tank took damage
        Assert.assertTrue(fieldEntity.getHealth() < 100);
        Assert.assertTrue(vehicle.getHealth() < 100);

        //assert locations remain the same
        Assert.assertTrue(holder.getNeighbor(Direction.Up).getEntity() instanceof Tank);
        Assert.assertTrue(holder.getEntity() instanceof Tank);
    }

    @Test
    public void vehicleCollide_collidesWithTankAndDestroys_tankMoves() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        VehicleEntity vehicle = repo.join("")[0];        //get location
        FieldHolder holder = vehicle.getParent();
        Assert.assertNotNull(vehicle);
        Assert.assertTrue(vehicle.getId() >= 0);

        FieldEntity fieldEntity = repo.join("1")[0];
        Tank tank = (Tank) fieldEntity;
        tank.setHealth(5);
        FieldHolder itemHolder = holder.getNeighbor(Direction.Up);
        itemHolder.setFieldEntity(fieldEntity);
        fieldEntity.setParent(itemHolder);
        Assert.assertTrue(holder.getNeighbor(Direction.Up).getEntity() instanceof Tank);

        Assert.assertTrue(repo.move(vehicle.getId(), Direction.Up)); // assert false because it should not be able to move

        //assert that the tank took damage
        Assert.assertTrue(fieldEntity.getHealth() < 100);

        //assert locations remain the same
        Assert.assertTrue(holder.getNeighbor(Direction.Up).getEntity() == vehicle);
    }
}
