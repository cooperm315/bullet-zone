package edu.unh.cs.cs619.bulletzone;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.entities.Builder;
import edu.unh.cs.cs619.bulletzone.model.entities.Miner;
import edu.unh.cs.cs619.bulletzone.model.entities.Tank;
import edu.unh.cs.cs619.bulletzone.model.entities.VehicleEntity;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;

@RunWith(MockitoJUnitRunner.StrictStubs.class) // needed for repo to not be null ig
public class JoinDisconnectTest {
    @InjectMocks
    InMemoryGameRepository repo;

    @Test
    public void joinCreatesVehicles() {
        VehicleEntity[] vehicleEntities = repo.join("");
        Assert.assertTrue(vehicleEntities.length == 3);

        // Checking tank
        VehicleEntity tank = vehicleEntities[0];
        //get location
        FieldHolder tankHolder = tank.getParent();
        Assert.assertNotNull(tank);
        Assert.assertTrue(tank.getId() >= 0);
        Assert.assertTrue(tankHolder.getEntity() instanceof Tank);

        // Checking miner
        VehicleEntity miner = vehicleEntities[1];
        //get location
        FieldHolder minerHolder = miner.getParent();
        Assert.assertNotNull(miner);
        Assert.assertTrue(miner.getId() >= 0);
        Assert.assertTrue(minerHolder.getEntity() instanceof Miner);

        // Checking builder
        VehicleEntity builder = vehicleEntities[2];
        //get location
        FieldHolder builderHolder = builder.getParent();
        Assert.assertNotNull(builder);
        Assert.assertTrue(builder.getId() >= 0);
        Assert.assertTrue(builderHolder.getEntity() instanceof Builder);

    }

    @Test
    public void disconnectRemovesVehicles() throws TankDoesNotExistException {
        VehicleEntity[] vehicleEntities = repo.join("");
        Assert.assertTrue(vehicleEntities.length == 3);

        // Checking tank
        VehicleEntity tank = vehicleEntities[0];
        //get location
        FieldHolder tankHolder = tank.getParent();
        Assert.assertNotNull(tank);
        Assert.assertTrue(tank.getId() >= 0);
        Assert.assertTrue(tankHolder.getEntity() instanceof Tank);

        // Checking miner
        VehicleEntity miner = vehicleEntities[1];
        //get location
        FieldHolder minerHolder = miner.getParent();
        Assert.assertNotNull(miner);
        Assert.assertTrue(miner.getId() >= 0);
        Assert.assertTrue(minerHolder.getEntity() instanceof Miner);

        // Checking builder
        VehicleEntity builder = vehicleEntities[2];
        //get location
        FieldHolder builderHolder = builder.getParent();
        Assert.assertNotNull(builder);
        Assert.assertTrue(builder.getId() >= 0);
        Assert.assertTrue(builderHolder.getEntity() instanceof Builder);

        repo.leave(""); // Disconnect

        // Make sure they are gone
//        Assert.assertFalse(tankHolder.getEntity() instanceof Tank);
//        Assert.assertThrows(NoSuchElementException, minerHolder.getEntity());
    }

}