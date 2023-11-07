package edu.unh.cs.cs619.bulletzone.repository;

import org.junit.Assert;
import org.junit.Test;

import edu.unh.cs.cs619.bulletzone.model.BattleMap;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.entities.Wall;

public class GameBoardBuilderTest {

    @Test
    public void buildGameBoard_withBattleMap_returnsCorrectGame() {
        BattleMap map = new BattleMap();
        map.addEntity(0, new Wall());
        map.addEntity(1, new Wall());
        map.addEntity(2, new Wall(1000, 2));

        GameBoardBuilder builder = new GameBoardBuilder(16);
        Game game = builder.buildGameBoard(map);
        for (int i = 0; i < 16 * 16; i++) {
            FieldHolder holder = game.getHolderGrid().get(i);
            if (i == 0 || i == 1 || i == 2) {
                Assert.assertTrue(holder.getEntity() instanceof Wall); // Walls are walls
            } else {
                Assert.assertFalse(holder.isPresent()); // Empty fields are empty
            }
        }
    }
}
