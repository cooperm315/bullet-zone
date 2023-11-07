package edu.unh.cs.cs619.bulletzone.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import edu.unh.cs.cs619.bulletzone.datalayer.structure.StructureType;
import edu.unh.cs.cs619.bulletzone.datalayer.terrain.TerrainType;
import edu.unh.cs.cs619.bulletzone.model.entities.VehicleEntity;
import edu.unh.cs.cs619.bulletzone.model.entities.Wall;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class StructureTest {
    @InjectMocks
    InMemoryGameRepository repo;

    @Before
    public void setUp() {
        repo.setCurrentBattleMap(new BattleMap());
    }

    @Test
    public void vehicleMovesOnDeckOnWaterTest() {
        VehicleEntity[] vehicles = repo.join("");
        for (VehicleEntity v : vehicles) {
            FieldHolder deckHolder = v.getParent().getNeighbor(Direction.Up);
            deckHolder.setTerrainType(TerrainType.WATER);
            deckHolder.setStructureType(StructureType.DECK);

            Assert.assertTrue(v.move(v.getDirection()));
            Assert.assertTrue(v.getParent().hasStructure());
            Assert.assertSame(v.getParent().getStructureType(), StructureType.DECK);
            Assert.assertSame(v.getParent().getTerrainType(), TerrainType.WATER);
        }
    }

    @Test
    public void vehicleMovesAtNormalSpeedOnDeckTest() throws InterruptedException {
        VehicleEntity[] vehicles = repo.join("");
        for (VehicleEntity v : vehicles) {
            // creates a 5 unit long deck to move across
            FieldHolder deckHolder = v.getParent().getNeighbor(Direction.Up);
            for (int i = 0; i < 5; i++) {
                deckHolder.setTerrainType(TerrainType.WATER);
                deckHolder.setStructureType(StructureType.DECK);
                Assert.assertTrue(deckHolder.hasStructure());
                deckHolder = deckHolder.getNeighbor(Direction.Up);
            }
            // moves down deck at the specified speed
            for (int i = 0; i < 4; i++) {
                Assert.assertTrue(v.getParent().getNeighbor(v.getDirection()).hasStructure());
                Assert.assertTrue(v.move(v.getDirection()));
                waitForCoolDown(v);
            }
        }
    }

    @Test
    public void vehicleMovesFasterOnRoad() throws InterruptedException {
        VehicleEntity[] vehicles = repo.join("");
        for (VehicleEntity v : vehicles) {
            // creates a 5 unit long road to move across
            FieldHolder roadHolder = v.getParent().getNeighbor(Direction.Up);
            for (int i = 0; i < 5; i++) {
                roadHolder.setTerrainType(TerrainType.ROCKY);
                roadHolder.setStructureType(StructureType.ROAD);
                Assert.assertTrue(roadHolder.hasStructure());
                roadHolder = roadHolder.getNeighbor(Direction.Up);
            }

            // moves down the road waiting half of its typical wait time
            for (int i = 0; i < 4; i++) {
                Assert.assertTrue(v.getParent().getNeighbor(v.getDirection()).hasStructure());
                Assert.assertTrue(v.move(v.getDirection()));
                waitForCoolDown(v);
            }
        }
    }

    private void waitForCoolDown(VehicleEntity vehicle) throws InterruptedException {
        Thread.sleep(vehicle.getMoveInterval()*4);
    }
}
