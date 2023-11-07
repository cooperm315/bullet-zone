package edu.unh.cs.cs619.bulletzone.repository;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicLong;

import edu.unh.cs.cs619.bulletzone.datalayer.structure.StructureType;
import edu.unh.cs.cs619.bulletzone.model.BattleMap;
import edu.unh.cs.cs619.bulletzone.model.BattleMapRepository;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Inventory;
import edu.unh.cs.cs619.bulletzone.model.entities.Builder;
import edu.unh.cs.cs619.bulletzone.model.entities.BulletType;
import edu.unh.cs.cs619.bulletzone.model.entities.Miner;
import edu.unh.cs.cs619.bulletzone.model.entities.ResourceType;
import edu.unh.cs.cs619.bulletzone.model.entities.VehicleEntity;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.entities.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;

import static com.google.common.base.Preconditions.checkNotNull;

@Component
public class InMemoryGameRepository implements GameRepository {

    /**
     * Field dimensions
     */
    private static final int FIELD_DIM = 16;

    /**
     * Bullet step time in milliseconds
     */
    private static final int BULLET_PERIOD = 200;

    /**
     * Bullet's impact effect [life]
     */
    private static final int BULLET_DAMAGE = 1;

    /**
     * Tank's default life [life]
     */
    private static final int TANK_LIFE = 100;
    private static final int MINER_LIFE = 300;
    private static final int BUILDER_LIFE = 80;
    private final Timer timer = new Timer();
    private final AtomicLong idGenerator = new AtomicLong();
    private final Object monitor = new Object();
    private Game game = null;
    private BattleMap currentBattleMap = BattleMapRepository.DEFAULT_MAP; // BattleMap for the current game

    public int terrainSeed = 12345;

    private ItemSpawner interactableItemHandler;

    private GameStatus gameStatus = GameStatus.STOPPED; // Only getter, no setter | only this class can change the status

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    /**
     * Gets a random unused location on the grid
     *
     * @return a random unused location on the grid
     * @throws RuntimeException if there are no possible locations
     */
    public FieldHolder getRandomEmptyFieldHolder() throws RuntimeException {
        Random random = new Random();

        ArrayList<FieldHolder> possibleLocations = new ArrayList<>();

        // check every field on the grid to see if its full or not
        for (int i = 0; i < FIELD_DIM * FIELD_DIM; i++) {
            FieldHolder fieldElement = game.getHolderGrid().get(i);
            if (!fieldElement.isPresent()) {// add the location to the list of possible locations
                if (fieldElement.getTerrainType().isSpawnable) {
                    possibleLocations.add(fieldElement);
                }
            }
        }

        if (possibleLocations.size() == 0) {
            throw new RuntimeException("No possible locations");
        }

        // pick a random location from the list of possible locations
        int randomIndex = random.nextInt(possibleLocations.size());

        return possibleLocations.get(randomIndex);
    }

    @Override
    public VehicleEntity[] join(String username) {
        synchronized (this.monitor) {
            VehicleEntity tank;
            VehicleEntity miner;
            VehicleEntity builder;
            if (game == null) {
                this.create();
            }

            game.addUser(username);

            // already joined
            if ((tank = game.getVehicle(username)) != null && (miner = game.getVehicle(username)) != null && (builder = game.getVehicle(username)) != null) {
                VehicleEntity[] vehicles = new VehicleEntity[3];
                vehicles[0] = tank;
                vehicles[1] = miner;
                vehicles[2] = builder;
                return vehicles;
            }

            VehicleEntity[] arr = new VehicleEntity[3];

            Long tankId = this.idGenerator.getAndIncrement();
            Long minerId = this.idGenerator.getAndIncrement();
            Long builderId = this.idGenerator.getAndIncrement();

            tank = new Tank(tankId, Direction.Up, username);
            tank.setHealth(TANK_LIFE);
            tank.setGame(game);

            miner = new Miner(minerId, Direction.Up, username);
            miner.setHealth(MINER_LIFE);
            miner.setGame(game);

            builder = new Builder(builderId, Direction.Up, username);
            builder.setHealth(BUILDER_LIFE);
            builder.setGame(game);

            // This may run for forever.. If there is no free space. XXX
            for (; ; ) {
                try {
                    FieldHolder tankHolder = getRandomEmptyFieldHolder();
                    tankHolder.setFieldEntity(tank);
                    tank.setParent(tankHolder);

                    FieldHolder minerHolder = getRandomEmptyFieldHolder();
                    minerHolder.setFieldEntity(miner);
                    miner.setParent(minerHolder);

                    FieldHolder builderHolder = getRandomEmptyFieldHolder();
                    builderHolder.setFieldEntity(builder);
                    builder.setParent(builderHolder);

                    break;
                } catch (RuntimeException e) {
                    //Log.i(TAG, "No possible locations");
                }
            }

            game.addVehicle(username, tank);
            game.addVehicle(username, miner);
            game.addVehicle(username, builder);

            if (gameStatus == GameStatus.STOPPED) {
                onStart();
            }
            arr[0] = tank;
            arr[1] = miner;
            arr[2] = builder;
            return arr;
        }
    }

    public int[] getInventory(String username) {
        return game.getInventory(username);
    }

    @Override
    public boolean eject(long vehicleId) {
        // if vehicle not in game, return false
        VehicleEntity vehicle = game.getVehicles().get(vehicleId);
        if (vehicle == null) {
            return false;
        }

        // eject powerup
        return vehicle.ejectPowerup();
    }

    @Override
    public boolean canBuild(long vehicleId) {
        VehicleEntity vehicle = game.getVehicle(vehicleId);
        if (!(vehicle instanceof Builder)) {
            return false;
        }
        FieldHolder back = vehicle.getParent().getNeighbor(Direction.getBack(vehicle.getDirection()));
        if (back.hasStructure()) {
            return false;
        }
        if (back.isPresent()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean canDismantle(long vehicleId) {
        VehicleEntity vehicle = game.getVehicle(vehicleId);
        if (!(vehicle instanceof Builder)) {
            return false;
        }
        return ConstraintEnforcer.isValidDismantle((Builder) vehicle);
    }

    public Inventory getUserInventory(String username) {return game.getUserInventory(username);}

    @Override
    public long[][] getGrid() {
        synchronized (this.monitor) {
            if (game == null) {
                this.create();
            }
        }
        return game.getGrid2D();
    }

    public void setCurrentBattleMap(BattleMap battleMap) {
        this.currentBattleMap = battleMap;
    }

    public BattleMap getCurrentBattleMap() {
        return this.currentBattleMap;
    }

    @Override
    public boolean turn(long controllableID, Direction direction)
            throws TankDoesNotExistException, IllegalTransitionException, LimitExceededException {
        synchronized (this.monitor) {
            checkNotNull(direction);

            // Find user
            VehicleEntity vehicle = game.getVehicles().get(controllableID);
            if (vehicle == null) {
                //Log.i(TAG, "Cannot find user with id: " + tankId);
                throw new TankDoesNotExistException(controllableID);
            }

            return vehicle.turn(direction);
        }
    }

    @Override
    public boolean move(long controllableID, Direction direction)
            throws TankDoesNotExistException, IllegalTransitionException, LimitExceededException {
        synchronized (this.monitor) {

            // Find tank
            VehicleEntity vehicle = game.getVehicles().get(controllableID);
            if (vehicle == null) {
                //Log.i(TAG, "Cannot find user with id: " + tankId);
                //return false;
                throw new TankDoesNotExistException(controllableID);
            }

            return vehicle.move(direction);
        }
    }

    @Override
    public boolean fire(long vehicleId, int intBulletType)
            throws TankDoesNotExistException, LimitExceededException {
        synchronized (this.monitor) {

            // Find tank
            VehicleEntity vehicle = game.getVehicles().get(vehicleId);
            if (vehicle == null) {
                //Log.i(TAG, "Cannot find user with id: " + tankId);
                //return false;
                throw new TankDoesNotExistException(vehicleId);
            }

            // get the bullet type (int bulletType)
            BulletType bulletType = BulletType.values()[intBulletType]; // tank fires a mid bullet [1]

            return vehicle.fire(bulletType);
        }
    }

    public boolean mine(long minerId, long resourceId)
            throws TankDoesNotExistException, InterruptedException {
        VehicleEntity vehicle = game.getVehicles().get(minerId);
        if (vehicle == null)
            throw new TankDoesNotExistException(minerId);

        if (vehicle instanceof Miner) {
            Miner miner = (Miner) vehicle;
            return miner.mine(resourceId);
        }

        return false;
    }

    public boolean build(long builderId, long structureId) throws TankDoesNotExistException {
        VehicleEntity vehicle = game.getVehicles().get(builderId);
        if (vehicle == null)
            throw new TankDoesNotExistException(builderId);

        if (vehicle instanceof Builder) {
            Builder builder = (Builder) vehicle;
            return builder.build(structureId);
        }

        return false;
    }

    public boolean dismantle(long builderId) throws TankDoesNotExistException {
        VehicleEntity vehicle = game.getVehicles().get(builderId);
        if (vehicle == null)
            throw new TankDoesNotExistException(builderId);

        if (vehicle instanceof Builder) {
            Builder builder = (Builder) vehicle;
            return builder.dismantle();
        }

        return false;
    }

    @Override
    public void leave(String username)
            throws TankDoesNotExistException {
        synchronized (this.monitor) {
            // Too lazy to fix this throw rn
//            if (!this.game.getVehicles().containsKey(username)) {
//                throw new TankDoesNotExistException();
//            }

            int[] inventory = game.getInventory(username);

            /*
             * Clay (16)
             * Rock (25)
             * Iron (78)
             * Wood (7)
             */
            int points = inventory[0] * 16 + inventory[1] * 25 + inventory[2] * 78 + inventory[3] *7;
            game.changeCoins(username, points);

            ArrayList<VehicleEntity> vehicles = game.getVehicles(username);
            for (VehicleEntity vehicle : vehicles) {
                vehicle.onLeave();
            }

            game.removeUser(username);

            // if last tank leaves, stop the game
            if (game.getVehicles().isEmpty()) {
                onStop();
            }
        }
    }

    public void devAddResources(String username) {
        synchronized (this.monitor) {
            game.addResource(username, ResourceType.CLAY, 10);
            game.addResource(username, ResourceType.ROCK, 10);
            game.addResource(username, ResourceType.IRON, 10);
            game.addResource(username, ResourceType.WOOD, 10);
        }
    }

    @Override
    public boolean getCanBuild(long tankId) {
        VehicleEntity vehicle = game.getVehicle(tankId);
        if (!(vehicle instanceof  Builder)) {
            return false;
        }
        // the cell directly behind the builder
        FieldHolder nextCell = vehicle.getParent().getNeighbor(Direction.getBack(vehicle.getDirection()));

        // can't build if already has structure
        if (nextCell.hasStructure())
            return false;

        return true;
    }

    @Override
    public boolean getCanMine(long currId) {
        VehicleEntity v = game.getVehicle(currId);
        if (v instanceof  Miner) {
            Miner m = (Miner) v;
            return ConstraintEnforcer.isValidMine(m, m.getParent().getTerrainType().resourceTypeID % 10000);
        }

        return false;
    }

    @Override
    public boolean getCanDismantle(long tankId) {
        VehicleEntity vehicle = game.getVehicle(tankId);
        if (!(vehicle instanceof  Builder)) {
            return false;
        }
        ConstraintEnforcer.isValidDismantle((Builder) vehicle);
        return true;
    }

    @Override
    public boolean getPoweredUp(long tankId) {
        return game.getVehicle(tankId).activePowerups.size() > 0;
    }

    @Override
    public boolean getFrontMoveValidity(long tankId) {
        VehicleEntity vehicle = game.getVehicle(tankId);
        FieldHolder front = vehicle.getParent().getNeighbor(vehicle.getDirection());
        if (front.isPresent()) {
            return true;
        }
        if (front.hasStructure() && front.getStructureType() == StructureType.INDESTRUCTIBLE_WALL) {
            return false;
        }
        if (front.hasStructure() && front.getStructureType() == StructureType.DECK) {
            return true;
        }
        return front.getTerrainType().canMoveThrough(vehicle);
    }

    @Override
    public boolean getBackMoveValidity(long tankId) {
        VehicleEntity vehicle = game.getVehicle(tankId);
        FieldHolder back = vehicle.getParent().getNeighbor(Direction.getBack(vehicle.getDirection()));
        if (back.isPresent()) {
            return true;
        }
        if (back.hasStructure() && back.getStructureType() == StructureType.INDESTRUCTIBLE_WALL) {
            return false;
        }
        if (back.hasStructure() && back.getStructureType() == StructureType.DECK) {
            return true;
        }
        return back.getTerrainType().canMoveThrough(vehicle);
    }


    /**
     * Creates a new game board. If the game board is already created, this
     * method does nothing.
     *
     * @throws IllegalStateException if the battle map is not set
     */
    public void create() throws IllegalStateException {
        if (game != null) {
            return;
        }

        if (currentBattleMap == null) {
            throw new IllegalStateException("Battle map is not set");
        }

        synchronized (this.monitor) {
            GameBoardBuilder builder = new GameBoardBuilder(FIELD_DIM, terrainSeed);
            this.game = builder.buildGameBoard(currentBattleMap);
        }
    }

    public void onStart() {
        gameStatus = GameStatus.RUNNING;
        interactableItemHandler = new ItemSpawner(this);
        interactableItemHandler.game = this.game;
        interactableItemHandler.spawnInitialResources(3);
        interactableItemHandler.spawnRandomResources(1); // Starts spawning resources on a different thread
    }

    public void onStop() {
        gameStatus = GameStatus.STOPPED;
    }
}
