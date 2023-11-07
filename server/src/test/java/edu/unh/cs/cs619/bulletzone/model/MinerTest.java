package edu.unh.cs.cs619.bulletzone.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import edu.unh.cs.cs619.bulletzone.model.entities.Miner;
import edu.unh.cs.cs619.bulletzone.model.entities.Tank;
import edu.unh.cs.cs619.bulletzone.model.entities.VehicleEntity;
import edu.unh.cs.cs619.bulletzone.model.entities.Wall;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;

/**
 * Thread.sleep is for the constraint enforcements comment out constraint in Miner
 * Movement tests are in InMemoryGameRepositoryTest with the tank movement tests
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class MinerTest {
    @InjectMocks
    InMemoryGameRepository repo;

    @Before
    public void setUp() {
        repo.setCurrentBattleMap(new BattleMap());
    }

//    @Test
//    public void minerRemoveResourceTest() throws InterruptedException, TankDoesNotExistException {
//        VehicleEntity[] vehicles = repo.join("");
//        Miner miner = (Miner) vehicles[1];
//        Inventory inv = repo.getUserInventory(miner.getUsername());
//
//        // adding clay
//        for (int i = 0; i < 3; i++) {
//            miner.mine(3);
//            Thread.sleep(1050);
//        }
//        Assert.assertEquals(3, inv.minedClay);
//
//        // adding rock
//        for (int i = 0; i < 5; i++) {
//            miner.mine(4);
//            Thread.sleep(1050);
//        }
//        Assert.assertEquals(5, inv.minedRock);
//
//        for (int i = 0; i < 8; i++) {
//            miner.mine(5);
//            Thread.sleep(1050);
//        }
//        Assert.assertEquals(8,inv.minedIron);
//
//        inv.removeResource(3, 3);
//        inv.removeResource(4, 5);
//        inv.removeResource(5, 8);
//
//        Assert.assertEquals(0, inv.getResource(3));
//        Assert.assertEquals(0, inv.getResource(4));
//        Assert.assertEquals(0, inv.getResource(5));
//
//        repo.leave(miner.getUsername());
//    }
//
//    @Test
//    public void minerAddsResourceTest() throws InterruptedException, TankDoesNotExistException {
//        VehicleEntity[] vehicles = repo.join("");
//        Miner miner = (Miner) vehicles[1];
//        Inventory inv = repo.getUserInventory(miner.getUsername());
//        // adding clay
//        for (int i = 0; i < 8; i++) {
//            miner.mine(3);
//            Thread.sleep(1050);
//        }
//        Assert.assertEquals(8, inv.minedClay);
//
//        // adding rock
//        for (int i = 0; i < 3; i++) {
//            miner.mine(4);
//            Thread.sleep(1050);
//        }
//
//        Assert.assertEquals(3, inv.minedRock);
//
//        for (int i = 0; i < 6; i++) {
//            miner.mine(5);
//            Thread.sleep(1050);
//        }
//
//        Assert.assertEquals(6, inv.minedIron);
//
//        inv.removeResource(3, 8);
//        inv.removeResource(4, 3);
//        inv.removeResource(5, 6);
//
//        repo.leave(miner.getUsername());
//    }

//    @Test
//    public void leaveRemovesMiner() throws TankDoesNotExistException {
//        VehicleEntity[] vehicles = repo.join("");
//        Miner miner = (Miner) vehicles[1];
//        repo.leave(miner.getUsername());
//        assert(miner.getParent() == null);
//    }

//    @Test
//    public void constraintEnforcementTest() throws TankDoesNotExistException, InterruptedException {
//        VehicleEntity[] vehicles = repo.join("");
//        Miner miner = (Miner) vehicles[1];
//        Inventory inv = repo.getUserInventory(miner.getUsername());
//        for (int i = 0; i < 10; i++)
//            miner.mine(3);
//        Assert.assertEquals(1, inv.minedClay);
//
//        // allows for miner to mine next material
//        Thread.sleep(1050);
//
//        for (int i = 0; i < 100; i++)
//            miner.mine(4);
//        Assert.assertEquals(1, inv.minedRock);
//
//        // constraints
//        Thread.sleep(1050);
//
//        for (int i = 0; i < 40; i++)
//            miner.mine(5);
//        Assert.assertEquals(1, inv.minedIron);
//
//        repo.leave(miner.getUsername());
//    }

    @Test
    public void collisionDamageTest() throws TankDoesNotExistException {
        VehicleEntity[] vehicles = repo.join("");
        Tank tank = (Tank) vehicles[0];
        Miner miner = (Miner) vehicles[1];
        Assert.assertEquals(300, miner.getHealth());
        Assert.assertEquals(100, tank.getHealth());

        // miner hits tank
        miner.collide(tank);
        Assert.assertEquals(295, miner.getHealth());
        Assert.assertEquals(70, tank.getHealth());

        // tank hits miner
        tank.collide(miner);
        Assert.assertEquals(288, miner.getHealth());
        Assert.assertEquals(41, tank.getHealth());

        repo.leave(miner.getUsername());
    }
}
