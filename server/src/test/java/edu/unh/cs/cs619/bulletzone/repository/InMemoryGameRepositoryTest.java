package edu.unh.cs.cs619.bulletzone.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import edu.unh.cs.cs619.bulletzone.model.BattleMap;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.entities.Builder;
import edu.unh.cs.cs619.bulletzone.model.entities.Miner;
import edu.unh.cs.cs619.bulletzone.model.entities.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.entities.VehicleEntity;
import edu.unh.cs.cs619.bulletzone.model.entities.Wall;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class InMemoryGameRepositoryTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @InjectMocks
    InMemoryGameRepository repo;

    @Before
    public void setUp() throws Exception {
        repo.setCurrentBattleMap(new BattleMap()); // map with nothing in it
        repo.terrainSeed = 0;
    }

    @Test
    public void testJoin() throws Exception {
        VehicleEntity[] vehicles = repo.join("");
        for (VehicleEntity vehicle : vehicles)
            Assert.assertNotNull(vehicle);
        for (VehicleEntity vehicle : vehicles)
            Assert.assertTrue(vehicle.getId() >= 0);
        for (VehicleEntity vehicle : vehicles)
            Assert.assertNotNull(vehicle.getDirection());
        for (VehicleEntity vehicle : vehicles)
            Assert.assertTrue(vehicle.getDirection() == Direction.Up);
        for (VehicleEntity vehicle : vehicles)
            Assert.assertNotNull(vehicle.getParent());
    }

    @Test
    public void testTurn_functionality() throws Exception {
        VehicleEntity[] vehicles = repo.join("");
        for (VehicleEntity vehicle : vehicles)
            Assert.assertNotNull(vehicle);
        for (VehicleEntity vehicle : vehicles)
            Assert.assertTrue(vehicle.getId() >= 0);
        for (VehicleEntity vehicle : vehicles)
            Assert.assertNotNull(vehicle.getDirection());
        for (VehicleEntity vehicle : vehicles)
            Assert.assertTrue(vehicle.getDirection() == Direction.Up);
        for (VehicleEntity vehicle : vehicles)
            Assert.assertNotNull(vehicle.getParent());
        for (VehicleEntity vehicle : vehicles)
            Assert.assertTrue(repo.turn(vehicle.getId(), Direction.Right));
        for (VehicleEntity vehicle : vehicles)
            Assert.assertTrue(vehicle.getDirection() == Direction.Right);

        thrown.expect(TankDoesNotExistException.class);
        thrown.expectMessage("Tank '1000' does not exist");
        repo.turn(1000, Direction.Right);
    }

    @Test
    public void testTurn_directionalRestriction() throws Exception {
        VehicleEntity[] vehicles = repo.join("");
        Tank tank = (Tank) vehicles[0];
        Miner miner = (Miner) vehicles[1];
        for (VehicleEntity vehicle : vehicles) {
            Assert.assertNotNull(vehicle);
            Assert.assertTrue(vehicle.getId() >= 0);
            Assert.assertNotNull(vehicle.getDirection());
            Assert.assertTrue(vehicle.getDirection() == Direction.Up);
            Assert.assertNotNull(vehicle.getParent());

            Assert.assertTrue(repo.turn(vehicle.getId(), Direction.Right));
            waitForCooldown(vehicle);

            Assert.assertFalse(repo.turn(vehicle.getId(), Direction.Left)); // invalid 180 degree turn
        }

    }
    @Test
    public void testTurn_cooldownRestriction() throws Exception {
        VehicleEntity[] vehicles = repo.join("");
        for (VehicleEntity vehicle : vehicles) {
        Assert.assertNotNull(vehicle);
        Assert.assertTrue(vehicle.getId() >= 0);
        Assert.assertTrue(repo.turn(vehicle.getId(), Direction.Right));

            Assert.assertFalse(repo.turn(vehicle.getId(), Direction.Down));
            waitForCooldown(vehicle);

            Assert.assertTrue(repo.turn(vehicle.getId(), Direction.Down));
            waitForCooldown(vehicle);

            Assert.assertTrue(repo.turn(vehicle.getId(), Direction.Left));
        }
    }

    @Test
    public void testMove_functionality() throws Exception {
        VehicleEntity[] vehicles = repo.join("");

        for (VehicleEntity vehicle : vehicles) {
            //get location
            FieldHolder holder = vehicle.getParent();
            Assert.assertNotNull(holder);
            Assert.assertTrue(holder.isPresent());
            Assert.assertTrue(holder.getEntity() instanceof Tank ||
                    holder.getEntity() instanceof Miner || holder.getEntity() instanceof Builder);

            Assert.assertNotNull(vehicle);
            Assert.assertTrue(vehicle.getId() >= 0);
            Assert.assertNotNull(vehicle.getDirection());
            Assert.assertTrue(vehicle.getDirection() == Direction.Up);

            Assert.assertTrue(repo.move(vehicle.getId(), Direction.Up));
            Assert.assertTrue(holder.getNeighbor(Direction.Up).getEntity() instanceof Tank ||
                    holder.getNeighbor(Direction.Up).getEntity() instanceof Miner ||
                    holder.getNeighbor(Direction.Up).getEntity() instanceof Builder);
            Assert.assertTrue(holder.getNeighbor(Direction.Up).isPresent());
            Assert.assertFalse(holder.isPresent());
            waitForCooldown(vehicle);
        }
    }

    @Test
    public void testMove_intoObject_ReturnsFalse() throws Exception {
        VehicleEntity[] vehicles = repo.join("");
        for (VehicleEntity vehicle : vehicles) {
            //get location
            FieldHolder holder = vehicle.getParent();
            Assert.assertNotNull(holder);
            Assert.assertTrue(holder.isPresent());
            Assert.assertTrue(holder.getEntity() instanceof Tank ||
                    holder.getEntity() instanceof Miner || holder.getEntity() instanceof Builder);

            // set wall above tank
            FieldHolder above = holder.getNeighbor(Direction.Up);
            above.setFieldEntity(new Wall());
            Assert.assertTrue(holder.getNeighbor(Direction.Up).getEntity() instanceof Wall);
            Assert.assertTrue(holder.getNeighbor(Direction.Up).isPresent());

            Assert.assertNotNull(vehicle);
            Assert.assertTrue(vehicle.getId() >= 0);
            Assert.assertNotNull(vehicle.getDirection());
            Assert.assertTrue(vehicle.getDirection() == Direction.Up);

            Assert.assertFalse(repo.move(vehicle.getId(), Direction.Up));
        }
    }

    @Test
    public void testMove_directionalRestriction() throws Exception {
        VehicleEntity[] vehicles = repo.join("");
        for (VehicleEntity vehicle : vehicles) {
            //get location
            FieldHolder holder = vehicle.getParent();
            Assert.assertNotNull(holder);
            Assert.assertTrue(holder.isPresent());
            Assert.assertTrue(holder.getEntity() instanceof Tank ||
                    holder.getEntity() instanceof Miner || holder.getEntity() instanceof Builder);

            Assert.assertNotNull(vehicle);
            Assert.assertTrue(vehicle.getId() >= 0);
            Assert.assertNotNull(vehicle.getDirection());
            vehicle.setDirection(Direction.Right);
            Assert.assertTrue(vehicle.getDirection() == Direction.Right);

            Assert.assertFalse(repo.move(vehicle.getId(), Direction.Up)); // Can't move up when not facing up or down
            waitForCooldown(vehicle);

            Assert.assertFalse(repo.move(vehicle.getId(), Direction.Down)); // Can't move down when not facing up or down
            waitForCooldown(vehicle);

            Assert.assertTrue(repo.move(vehicle.getId(), Direction.Left)); // Can move left when facing right or left
            waitForCooldown(vehicle);

            Assert.assertTrue(repo.move(vehicle.getId(), Direction.Right)); // Can move right when facing right or left
        }

    }

    @Test
    public void testMove_cooldownRestriction() throws Exception{
        VehicleEntity[] vehicles = repo.join("");
        for (VehicleEntity vehicle : vehicles) {
            //get location
            FieldHolder holder = vehicle.getParent();
            Assert.assertNotNull(holder);
            Assert.assertTrue(holder.isPresent());
            Assert.assertTrue(holder.getEntity() instanceof Tank ||
                    holder.getEntity() instanceof Miner || holder.getEntity() instanceof Builder);

            Assert.assertNotNull(vehicle);
            Assert.assertTrue(vehicle.getId() >= 0);
            Assert.assertNotNull(vehicle.getDirection());
            vehicle.setDirection(Direction.Up);
            Assert.assertTrue(vehicle.getDirection() == Direction.Up);

            Assert.assertTrue(repo.move(vehicle.getId(), Direction.Up));
            Assert.assertFalse(repo.move(vehicle.getId(), Direction.Up)); // not obeying cooldown
            waitForCooldown(vehicle);

            Assert.assertTrue(repo.move(vehicle.getId(), Direction.Up));
        }
    }

    @Test
    public void testFire_cooldownRestriction() throws Exception {
        VehicleEntity[] vehicles = repo.join("");
        //get location
        for (VehicleEntity vehicle : vehicles) {
            FieldHolder holder = vehicle.getParent();
            Assert.assertNotNull(holder);
            Assert.assertTrue(holder.isPresent());
            Assert.assertTrue(holder.getEntity() instanceof Tank ||
                    holder.getEntity() instanceof Miner || holder.getEntity() instanceof Builder);

            Assert.assertNotNull(vehicle);
            Assert.assertTrue(vehicle.getId() >= 0);
            Assert.assertNotNull(vehicle.getDirection());
            vehicle.setDirection(Direction.Up);
            Assert.assertTrue(vehicle.getDirection() == Direction.Up);

            Assert.assertTrue(repo.fire(vehicle.getId(), 1));
            Assert.assertFalse(repo.fire(vehicle.getId(), 1)); // not obeying cooldown
            waitForCooldown(vehicle);

            Assert.assertTrue(repo.fire(vehicle.getId(), 1));
            Assert.assertFalse(repo.fire(vehicle.getId(), 1)); // too many bullets on field
        }

    }

    @Test
    public void testFire_bulletCountRestriction() throws Exception {
        VehicleEntity[] vehicles = repo.join("");
        for (VehicleEntity vehicle : vehicles) {
            //get location
            FieldHolder holder = vehicle.getParent();
            Assert.assertNotNull(holder);
            Assert.assertTrue(holder.isPresent());
            Assert.assertTrue(holder.getEntity() instanceof Tank ||
                    holder.getEntity() instanceof Miner || holder.getEntity() instanceof Builder);

            Assert.assertNotNull(vehicle);
            Assert.assertTrue(vehicle.getId() >= 0);
            Assert.assertNotNull(vehicle.getDirection());
            vehicle.setDirection(Direction.Up);
            Assert.assertTrue(vehicle.getDirection() == Direction.Up);

            Assert.assertTrue(repo.fire(vehicle.getId(), 1));
            waitForCooldown(vehicle);

            Assert.assertTrue(repo.fire(vehicle.getId(), 1));
            Assert.assertFalse(repo.fire(vehicle.getId(), 1)); // too many bullets on field
        }

    }

    @Test
    public void testTurn_moveCoolDownAfterTurn() throws InterruptedException {
        VehicleEntity[] vehicles = repo.join("");
        for (VehicleEntity v : vehicles) {
            Assert.assertTrue(v.move(v.getDirection()));
            Assert.assertFalse(v.turn(Direction.getLeft(v.getDirection())));
            waitForCooldown(v);
            Assert.assertTrue(v.move(v.getDirection()));
            waitForCooldown(v);
            Assert.assertTrue(v.turn(Direction.getLeft(v.getDirection())));
        }
    }

    @Test
    public void testLeave() throws Exception {

    }

    private void waitForCooldown(VehicleEntity vehicle) throws InterruptedException {
        Thread.sleep(vehicle.getMoveInterval()*4);
    }
}