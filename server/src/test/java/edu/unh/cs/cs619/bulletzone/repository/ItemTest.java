package edu.unh.cs.cs619.bulletzone.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.ConcurrentMap;

import edu.unh.cs.cs619.bulletzone.model.BattleMap;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.Inventory;
import edu.unh.cs.cs619.bulletzone.model.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.entities.CoinItem;
import edu.unh.cs.cs619.bulletzone.model.entities.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.entities.ResourceItem;
import edu.unh.cs.cs619.bulletzone.model.entities.ResourceType;
import edu.unh.cs.cs619.bulletzone.model.entities.VehicleEntity;

@RunWith(MockitoJUnitRunner.StrictStubs.class) // needed for repo to not be null ig
public class ItemTest {
    @InjectMocks
    InMemoryGameRepository repo;

    DataRepository dataRepo;

    @Before
    public void setUp() throws Exception {
        repo.setCurrentBattleMap(new BattleMap()); // map with nothing in it
        repo.terrainSeed = 0;
        dataRepo = new DataRepository();
    }

    @Test
    public void vehicleCollide_pickUpResource_increasesProperResourceCount() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        VehicleEntity vehicle = repo.join("")[0];
        //get location
        FieldHolder holder = vehicle.getParent();
        Assert.assertNotNull(vehicle);
        Assert.assertTrue(vehicle.getId() >= 0);

        FieldEntity fieldEntity = new ResourceItem(ResourceType.CLAY);
        FieldHolder itemHolder = holder.getNeighbor(Direction.Up);
        itemHolder.setFieldEntity(fieldEntity);
        fieldEntity.setParent(itemHolder);

        Assert.assertTrue(repo.move(vehicle.getId(), Direction.Up));

        //check that the resource count is increased
        ConcurrentMap<String, Inventory> inventories = vehicle.getGame().getPlayerInventories();
        Inventory inventory = inventories.get("");
        Assert.assertTrue(inventory.getResource(ResourceType.CLAY) == 1);
        Assert.assertTrue(inventory.getResource(ResourceType.ROCK) == 0);
        Assert.assertTrue(inventory.getResource(ResourceType.IRON) == 0);
        Assert.assertTrue(inventory.getResource(ResourceType.WOOD) == 0);
    }

    @Test
    public void vehicleCollide_pickUpCoin_increasesMoney() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        dataRepo.createUser("test_this_is_bad_practice", "test_this_is_bad_practice");

        VehicleEntity vehicle = repo.join("test_this_is_bad_practice")[0];

        //get location
        FieldHolder holder = vehicle.getParent();
        Assert.assertNotNull(vehicle);
        Assert.assertTrue(vehicle.getId() >= 0);

        FieldEntity fieldEntity = CoinItem.getRandomCoinItem();
        FieldHolder itemHolder = holder.getNeighbor(Direction.Up);
        itemHolder.setFieldEntity(fieldEntity);
        fieldEntity.setParent(itemHolder);

        int balanceBefore = vehicle.getGame().getCoins(vehicle.getUsername());

        Assert.assertTrue(repo.move(vehicle.getId(), Direction.Up));

        //sleep
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int balanceAfter = vehicle.getGame().getCoins(vehicle.getUsername());

        System.out.println("Balance before: " + balanceBefore);
        System.out.println("Balance after: " + balanceAfter);


        // TODO: check that the money count is increased
        // should increase by >= 25 <= 1000
        Assert.assertTrue(balanceAfter > balanceBefore);
        vehicle.getGame().changeCoins(vehicle.getUsername(), balanceBefore - balanceAfter);
    }


    @Test
    public void bulletCollide_hitsItem_removesItem() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        VehicleEntity vehicle = repo.join("")[0];
        //get location
        FieldHolder holder = vehicle.getParent();
        Assert.assertNotNull(vehicle);
        Assert.assertTrue(vehicle.getId() >= 0);

        FieldEntity fieldEntity = new ResourceItem(ResourceType.CLAY);
        FieldHolder itemHolder = holder.getNeighbor(Direction.Up);
        itemHolder.setFieldEntity(fieldEntity);
        fieldEntity.setParent(itemHolder);

        Assert.assertTrue(repo.fire(vehicle.getId(), 1));

        //wait for bullet to move
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertTrue(repo.move(vehicle.getId(), Direction.Up));

        //check that the resource count is increased
        ConcurrentMap<String, Inventory> inventories = vehicle.getGame().getPlayerInventories();
        Inventory inventory = inventories.get("");
        Assert.assertTrue(inventory.getResource(ResourceType.CLAY) == 0);
        Assert.assertTrue(inventory.getResource(ResourceType.ROCK) == 0);
        Assert.assertTrue(inventory.getResource(ResourceType.IRON) == 0);
        Assert.assertTrue(inventory.getResource(ResourceType.WOOD) == 0);

    }
}
