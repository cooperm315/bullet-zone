package edu.unh.cs.cs619.bulletzone.repository;

import edu.unh.cs.cs619.bulletzone.datalayer.terrain.TerrainType;
import edu.unh.cs.cs619.bulletzone.model.BattleMap;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Game;

/**
 * GameBoardBuilder is the builder class for the game board
 * Unfortunately, it has to directly edit the game object as opposed to returning a new one
 */
public class GameBoardBuilder {
    private final int FIELD_DIM;
    private final int TERRAIN_SEED;


    /**
     * Constructor
     *
     * @param FIELD_DIM Field dimensions
     */
    public GameBoardBuilder(int FIELD_DIM) {
        this.FIELD_DIM = FIELD_DIM;
        this.TERRAIN_SEED = (int)(Math.random()*1000);
    }
    public GameBoardBuilder(int FIELD_DIM, int terrainSeed) {
        this.FIELD_DIM = FIELD_DIM;
        this.TERRAIN_SEED = terrainSeed;
    }



    /**
     * Build the game board
     *
     * @param battleMap BattleMap object to load into the game
     */
    public Game buildGameBoard(BattleMap battleMap) {
        Game game = new Game();

        createFieldHolderGrid(game);

        // Loading every field entity stored in the battlemap into the game
        for (int i = 0; i < FIELD_DIM * FIELD_DIM; i++) {
            if (battleMap.getEntity(i) != null) {
                game.getHolderGrid().get(i).setFieldEntity(battleMap.getEntity(i));
                //set parent
                battleMap.getEntity(i).setParent(game.getHolderGrid().get(i));
            }
            //else it fills with Optional.empty()
        }
        return game;
    }

    /**
     * Create the field holder grid
     *
     * @param game Game object to load the grid into
     */
    private void createFieldHolderGrid(Game game) {
        game.getHolderGrid().clear();
        TerrainType[][] terrain = generateTerrain();
        for (int i = 0; i < FIELD_DIM * FIELD_DIM; i++) {
            FieldHolder fieldHolder = new FieldHolder();
            fieldHolder.setTerrainType(terrain[i / FIELD_DIM][i % FIELD_DIM]);
            game.getHolderGrid().add(fieldHolder); // Creates FieldHolder based on calculated terrain
        }

        FieldHolder targetHolder;
        FieldHolder rightHolder;
        FieldHolder downHolder;

        // Build connections
        for (int i = 0; i < FIELD_DIM; i++) {
            for (int j = 0; j < FIELD_DIM; j++) {
                targetHolder = game.getHolderGrid().get(i * FIELD_DIM + j);
                rightHolder = game.getHolderGrid().get(i * FIELD_DIM
                        + ((j + 1) % FIELD_DIM));
                downHolder = game.getHolderGrid().get(((i + 1) % FIELD_DIM)
                        * FIELD_DIM + j);

                targetHolder.addNeighbor(Direction.Right, rightHolder);
                rightHolder.addNeighbor(Direction.Left, targetHolder);

                targetHolder.addNeighbor(Direction.Down, downHolder);
                downHolder.addNeighbor(Direction.Up, targetHolder);
            }
        }
    }

    /***
     * Generates a noise map of the terrain and returns the terrain type [][]
     * @return terrain - TerrainType[][]
     */
    private TerrainType[][] generateTerrain() {
        TerrainType[][] terrain = new TerrainType[FIELD_DIM][FIELD_DIM];
        if (TERRAIN_SEED == 0) { // 0 is the meadow only seed
            for (int i = 0; i < FIELD_DIM; i++) {
                for (int j = 0; j < FIELD_DIM; j++) {
                    terrain[i][j] = TerrainType.MEADOW;
                }
            }
            return terrain;
        }

        // good seed is 12345
        Noise noise = new Noise(FIELD_DIM, FIELD_DIM, 0, 6, TERRAIN_SEED);
        noise.generate();
        noise.smooth(1);
        noise.fluctuate(3);
        noise.smooth(1);
        noise.changeHeight(-1);
        double[][] data = noise.data;
        for (int i = 0; i < FIELD_DIM; i++) {
            for (int j = 0; j < FIELD_DIM; j++) {
                // round it and then cast it to an int
                // then equate it to the value of the enum
                terrain[i][j] = TerrainType.values()[(int) Math.round(data[i][j])];
            }
        }
        return terrain;
    }
}
