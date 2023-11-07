package edu.unh.cs.cs619.bulletzone.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.ConcurrentLinkedDeque;

import edu.unh.cs.cs619.bulletzone.model.BattleMap;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.entities.Builder;
import edu.unh.cs.cs619.bulletzone.model.entities.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.entities.FusionGenerator;
import edu.unh.cs.cs619.bulletzone.model.entities.GravAssist;
import edu.unh.cs.cs619.bulletzone.model.entities.Powerup;
import edu.unh.cs.cs619.bulletzone.model.entities.VehicleEntity;

@RunWith(MockitoJUnitRunner.StrictStubs.class) // needed for repo to not be null ig
public class PowerupTest {
    @InjectMocks
    InMemoryGameRepository repo;

    DataRepository dataRepo;

    @Before
    public void setUp() throws Exception {
        repo.setCurrentBattleMap(new BattleMap()); // map with nothing in it
        dataRepo = new DataRepository();
        repo.terrainSeed = 0;
    }

    @AfterEach
    public void waitABit() throws InterruptedException {
        Thread.sleep(2000); // waiting so that not invalid move
    }

    @Test
    public void vehicleCollide_pickUpGravAssist_addsToActivePowerups() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        VehicleEntity vehicle = repo.join("")[0];
        //get location
        FieldHolder holder = vehicle.getParent();
        Assert.assertNotNull(vehicle);
        Assert.assertTrue(vehicle.getId() >= 0);

        FieldEntity fieldEntity = new GravAssist();
        FieldHolder itemHolder = holder.getNeighbor(Direction.Up);
        itemHolder.setFieldEntity(fieldEntity);
        fieldEntity.setParent(itemHolder);

        Assert.assertTrue(repo.move(vehicle.getId(), Direction.Up));

        //check that the vehicle picked up the powerup
        ConcurrentLinkedDeque<Powerup> powerups = vehicle.activePowerups;
        for (Powerup p : powerups) {
            Assert.assertTrue(p instanceof GravAssist);
        }
    }

    @Test
    public void vehicleCollide_pickUpGravAssist_increasesWhatItShould() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        VehicleEntity vehicle = repo.join("")[1]; // testing on miner because tank has 0 action interval
        //get location
        FieldHolder holder = vehicle.getParent();
        Assert.assertNotNull(vehicle);
        Assert.assertTrue(vehicle.getId() >= 0);

        FieldEntity fieldEntity = new GravAssist();
        FieldHolder itemHolder = holder.getNeighbor(Direction.Up);
        itemHolder.setFieldEntity(fieldEntity);
        fieldEntity.setParent(itemHolder);

        Assert.assertTrue(repo.move(vehicle.getId(), Direction.Up));

        //check that the vehicle picked up the powerup
        ConcurrentLinkedDeque<Powerup> powerups = vehicle.activePowerups;

        // get vehicle speed and check that it is increased
        int speed = vehicle.getMoveInterval();
        int fire = vehicle.getFireInterval();
//        int action = vehicle.getActionInterval();
        int bullets = vehicle.getAllowedBullets();

        Assert.assertTrue(speed < vehicle.getBaseMoveInterval());
        Assert.assertTrue(fire > vehicle.getBaseFireInterval());
//        Assert.assertTrue(action == vehicle.getBaseActionInterval());
        Assert.assertTrue(bullets == vehicle.getBaseAllowedBullets());
    }

    @Test
    public void vehicleCollide_pickUpFusionGenerator_addsToActivePowerups() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        VehicleEntity vehicle = repo.join("")[0];
        //get location
        FieldHolder holder = vehicle.getParent();
        Assert.assertNotNull(vehicle);
        Assert.assertTrue(vehicle.getId() >= 0);

        FieldEntity fieldEntity = new FusionGenerator();
        FieldHolder itemHolder = holder.getNeighbor(Direction.Up);
        itemHolder.setFieldEntity(fieldEntity);
        fieldEntity.setParent(itemHolder);

        Assert.assertTrue(repo.move(vehicle.getId(), Direction.Up));

        //check that the vehicle picked up the powerup
        ConcurrentLinkedDeque<Powerup> powerups = vehicle.activePowerups;
        for (Powerup p : powerups) {
            Assert.assertTrue(p instanceof FusionGenerator);
        }
    }

    @Test
    public void vehicleCollide_pickUpFusionGenerator_increasesWhatItShould() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        VehicleEntity vehicle = repo.join("")[1]; // testing on miner because tank has 0 action interval
        //get location
        FieldHolder holder = vehicle.getParent();
        Assert.assertNotNull(vehicle);
        Assert.assertTrue(vehicle.getId() >= 0);

        FieldEntity fieldEntity = new FusionGenerator();
        FieldHolder itemHolder = holder.getNeighbor(Direction.Up);
        itemHolder.setFieldEntity(fieldEntity);
        fieldEntity.setParent(itemHolder);

        Assert.assertTrue(repo.move(vehicle.getId(), Direction.Up));

        //check that the vehicle picked up the powerup
        ConcurrentLinkedDeque<Powerup> powerups = vehicle.activePowerups;

        // get vehicle speed and check that it is increased
        int speed = vehicle.getMoveInterval();
        int fire = vehicle.getFireInterval();
//        int action = vehicle.getActionInterval();
        int bullets = vehicle.getAllowedBullets();

        Assert.assertTrue(speed > vehicle.getBaseMoveInterval());
        Assert.assertTrue(fire < vehicle.getBaseFireInterval());
//        Assert.assertTrue(action < vehicle.getBaseActionInterval());
        Assert.assertTrue(bullets > vehicle.getBaseAllowedBullets());
    }

    @Test
    public void vehicle_ejectPowerup_placesPowerupNearby() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        VehicleEntity vehicle = repo.join("")[0]; // testing on miner because tank has 0 action interval
        //get location
        FieldHolder holder = vehicle.getParent();
        Assert.assertNotNull(vehicle);
        Assert.assertTrue(vehicle.getId() >= 0);

        FieldEntity fieldEntity = new FusionGenerator();
        FieldHolder itemHolder = holder.getNeighbor(Direction.Up);
        itemHolder.setFieldEntity(fieldEntity);
        fieldEntity.setParent(itemHolder);

        Assert.assertTrue(repo.move(vehicle.getId(), Direction.Up));

        vehicle.ejectPowerup();
        boolean isNearby = false;
        for (int i = 0; i <= 6; i += 2) {
            FieldHolder neighbor = vehicle.getParent().getNeighbor(Direction.values()[i]);
            if(neighbor.isPresent()){
                if (neighbor.getEntity() instanceof Powerup) {
                    isNearby = true;
                }
            }
        }
        Assert.assertTrue(isNearby);
    }

//    @Test
//    public void builder_dismantlePowerup_increasesCoins() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
//        Builder builder = (Builder)repo.join("test_this_is_bad_practice")[2]; // testing on miner because tank has 0 action interval
//        //get location
//        FieldHolder holder = builder.getParent();
//        Assert.assertNotNull(builder);
//        Assert.assertTrue(builder.getId() >= 0);
//
//        FieldEntity fieldEntity = new FusionGenerator();
//        FieldHolder itemHolder = holder.getNeighbor(Direction.Up);
//        itemHolder.setFieldEntity(fieldEntity);
//        fieldEntity.setParent(itemHolder);
//
//        Assert.assertTrue(repo.move(builder.getId(), Direction.Up));
//        int balanceBefore = builder.getGame().getCoins(builder.getUsername());
//        builder.dismantleEntity();
//        int balanceAfter = builder.getGame().getCoins(builder.getUsername());
//
//        Assert.assertTrue(balanceAfter > balanceBefore);
//        builder.getGame().changeCoins(builder.getUsername(), balanceBefore - balanceAfter);
//    }
}
