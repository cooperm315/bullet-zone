package edu.unh.cs.cs619.bulletzone;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import edu.unh.cs.cs619.bulletzone.datalayer.terrain.TerrainType;
import edu.unh.cs.cs619.bulletzone.model.BattleMap;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.entities.Tank;
import edu.unh.cs.cs619.bulletzone.model.entities.VehicleEntity;
import edu.unh.cs.cs619.bulletzone.repository.DataRepository;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;

@RunWith(MockitoJUnitRunner.StrictStubs.class) // needed for repo to not be null ig
public class TerrainMovementTest {
    @InjectMocks
    InMemoryGameRepository repo;

    @Before
    public void setUp() {
        repo.setCurrentBattleMap(new BattleMap()); // map with nothing in it
    }

    @Test
    public void vehicleMovesInMeadow_tankSpeedIsStandard() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        VehicleEntity[] vehicleEntities = repo.join("");
        for (int i = 0; i < vehicleEntities.length; i++) {
            VehicleEntity vehicle = vehicleEntities[i];
            // Get location
            FieldHolder holder = vehicle.getParent();
            Assert.assertNotNull(vehicle);
            Assert.assertTrue(vehicle.getId() >= 0);

            holder.setTerrainType(TerrainType.MEADOW);
            FieldHolder nextHolder = holder.getNeighbor(Direction.Up);
            nextHolder.setTerrainType(TerrainType.MEADOW);

            Assert.assertTrue(repo.move(vehicle.getId(), Direction.Up)); // Make sure it's able to move into the neighbor
            Thread.sleep(50 + Math.round(vehicle.getBaseMoveInterval() * TerrainType.MEADOW.speedModifier)); // Sleep to make sure it is allowed to move at the right speed
            Assert.assertTrue(repo.move(vehicle.getId(), Direction.Down)); // Move it twice to make it wait

            // Make sure the tank ended up at the right spot
            Assert.assertTrue(holder.getEntity() instanceof VehicleEntity);
        }

    }

    @Test
    public void vehicleMovesInHilly_tankSpeedIsStandard() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        VehicleEntity[] vehicleEntities = repo.join("");
        for (int i = 0; i < vehicleEntities.length; i++) {
            VehicleEntity vehicle = vehicleEntities[i];
            // Get location
            FieldHolder holder = vehicle.getParent();
            Assert.assertNotNull(vehicle);
            Assert.assertTrue(vehicle.getId() >= 0);

            holder.setTerrainType(TerrainType.HILLY);
            FieldHolder nextHolder = holder.getNeighbor(Direction.Up);
            nextHolder.setTerrainType(TerrainType.HILLY);

            Assert.assertTrue(repo.move(vehicle.getId(), Direction.Up)); // Make sure it's able to move into the neighbor
            Thread.sleep(50 + Math.round(vehicle.getBaseMoveInterval() * TerrainType.HILLY.speedModifier)); // Sleep to make sure it is allowed to move at the right speed
            Assert.assertTrue(repo.move(vehicle.getId(), Direction.Down)); // Move it twice to make it wait

            // Make sure the tank ended up at the right spot
            Assert.assertTrue(holder.getEntity() instanceof VehicleEntity);
        }
    }

    @Test
    public void vehicleMovesInRocky_tankSpeedIsStandard() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        VehicleEntity[] vehicleEntities = repo.join("");
        for (int i = 0; i < vehicleEntities.length; i++) {
            VehicleEntity vehicle = vehicleEntities[i];
            // Get location
            FieldHolder holder = vehicle.getParent();
            Assert.assertNotNull(vehicle);
            Assert.assertTrue(vehicle.getId() >= 0);

            holder.setTerrainType(TerrainType.ROCKY);
            FieldHolder nextHolder = holder.getNeighbor(Direction.Up);
            nextHolder.setTerrainType(TerrainType.ROCKY);

            Assert.assertTrue(repo.move(vehicle.getId(), Direction.Up)); // Make sure it's able to move into the neighbor
            Thread.sleep(200 + Math.round(vehicle.getBaseMoveInterval() * TerrainType.ROCKY.speedModifier)); // Sleep to make sure it is allowed to move at the right speed
            Assert.assertTrue(repo.move(vehicle.getId(), Direction.Down)); // Move it twice to make it wait

            // Make sure the tank ended up at the right spot
            Assert.assertTrue(holder.getEntity() instanceof VehicleEntity);
        }
    }

    @Test
    public void vehicleMovesInSand_tankSpeedIsStandard() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        VehicleEntity[] vehicleEntities = repo.join("");
        for (int i = 0; i < vehicleEntities.length; i++) {
            VehicleEntity vehicle = vehicleEntities[i];
            // Get location
            FieldHolder holder = vehicle.getParent();
            Assert.assertNotNull(vehicle);
            Assert.assertTrue(vehicle.getId() >= 0);

            holder.setTerrainType(TerrainType.SAND);
            FieldHolder nextHolder = holder.getNeighbor(Direction.Up);
            nextHolder.setTerrainType(TerrainType.SAND);

            Assert.assertTrue(repo.move(vehicle.getId(), Direction.Up)); // Make sure it's able to move into the neighbor
            Thread.sleep(50 + Math.round(vehicle.getBaseMoveInterval() * TerrainType.SAND.speedModifier)); // Sleep to make sure it is allowed to move at the right speed
            Assert.assertTrue(repo.move(vehicle.getId(), Direction.Down)); // Move it twice to make it wait

            // Make sure the tank ended up at the right spot
            Assert.assertTrue(holder.getEntity() instanceof VehicleEntity);
        }
    }

    @Test
    public void vehicleMovesInDryland_tankSpeedIsStandard() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        VehicleEntity[] vehicleEntities = repo.join("");
        for (int i = 0; i < vehicleEntities.length; i++) {
            VehicleEntity vehicle = vehicleEntities[i];
            // Get location
            FieldHolder holder = vehicle.getParent();
            Assert.assertNotNull(vehicle);
            Assert.assertTrue(vehicle.getId() >= 0);

            holder.setTerrainType(TerrainType.DRYLAND);
            FieldHolder nextHolder = holder.getNeighbor(Direction.Up);
            nextHolder.setTerrainType(TerrainType.DRYLAND);

            Assert.assertTrue(repo.move(vehicle.getId(), Direction.Up)); // Make sure it's able to move into the neighbor
            Thread.sleep(50 + Math.round(vehicle.getBaseMoveInterval() * TerrainType.DRYLAND.speedModifier)); // Sleep to make sure it is allowed to move at the right speed
            Assert.assertTrue(repo.move(vehicle.getId(), Direction.Down)); // Move it twice to make it wait

            // Make sure the tank ended up at the right spot
            Assert.assertTrue(holder.getEntity() instanceof VehicleEntity);
        }
    }

    @Test
    public void vehicleMovesToWater_moveRejected() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        VehicleEntity vehicle = repo.join("")[0];
        // Get location
        FieldHolder holder = vehicle.getParent();
        Assert.assertNotNull(vehicle);
        Assert.assertTrue(vehicle.getId() >= 0);

        holder.setTerrainType(TerrainType.MEADOW);
        FieldHolder nextHolder = holder.getNeighbor(Direction.Up);
        nextHolder.setTerrainType(TerrainType.WATER);

        // Make sure move is rejected
        Assert.assertFalse(repo.move(vehicle.getId(), Direction.Up));
        Assert.assertTrue(holder.getEntity() instanceof Tank);
    }
}
