package edu.unh.cs.cs619.bulletzone.repository;

import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.entities.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.entities.Intractable;
import edu.unh.cs.cs619.bulletzone.model.entities.Item;
import edu.unh.cs.cs619.bulletzone.model.entities.Powerup;
import edu.unh.cs.cs619.bulletzone.model.entities.ResourceItem;
import edu.unh.cs.cs619.bulletzone.model.entities.ResourceType;

public class ItemSpawner {
    InMemoryGameRepository gameRepository;
    Game game;

    private long cooldownSeconds = 15;

    public ItemSpawner(InMemoryGameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    /**
     * Spawns resources randomly on the grid, but only when the game is running.
     * @param cooldownSeconds cooldown between spawns in seconds (min 1)
     */
    public void spawnRandomResources(long cooldownSeconds) {
        setCooldownSeconds(cooldownSeconds);
        new Thread(() -> {
            while (true) {// Always running in the background
                while (gameRepository.getGameStatus() == GameStatus.RUNNING) { // while game is running

                    // spawn if can
                    int playerCount = game.getPlayerCount();
                    int itemCount = game.items.size();

                    //The probability of showing a new item should be calculated as 25% * P/(N + 1), where P is the number of Players currently in the game and N is the number of items already on the board (N is decremented whenever an item is picked up).
                    if (!(Math.random() > 0.25 * playerCount / (itemCount + 1.0))) {
                        // Get random empty location and spawn a random item
                        FieldHolder holder = gameRepository.getRandomEmptyFieldHolder();
                        Intractable item;

                        //rand int 0-1
                        int rand = (int) (Math.random() * 2);
                        if (rand == 0) {
                            item = Item.getRandomItem();
                        } else {
                            item = Powerup.getRandomPowerup();
                        }
                        FieldEntity entity = (FieldEntity) item;
                        holder.setFieldEntity(entity);
                        entity.setParent(holder);
                        game.items.add(item);
                        System.out.println("Spawned " + item + " of value " + item.getIntValue());
                    }

                    try {
                        Thread.sleep(cooldownSeconds * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // Game is not running

            }
        }).start();
    }

    /**
     * Spawns initial resources on the grid
     * @param numResources number of resources to spawn (min 3)
     */
    public void spawnInitialResources(int numResources) {
        if (numResources < 3) {
            throw new IllegalArgumentException("numResources must be at least 3");
        }

        ResourceItem[] items = new ResourceItem[3];
        items[0] = new ResourceItem(ResourceType.CLAY);
        items[1] = new ResourceItem(ResourceType.ROCK);
        items[2] = new ResourceItem(ResourceType.IRON);
        for (int i = 0; i < numResources; i++) {
            FieldHolder holder = gameRepository.getRandomEmptyFieldHolder();
            holder.setFieldEntity(items[i % 3]);
            items[i % 3].setParent(holder);
        }
    }

    /**
     * Sets the cooldown between spawns in seconds
     * @param cooldownSeconds cooldown in seconds (min 1)
     */
    public void setCooldownSeconds(long cooldownSeconds) {
        if (cooldownSeconds < 1) {
            throw new IllegalArgumentException("cooldownSeconds must be at least 1");
        }
        this.cooldownSeconds = cooldownSeconds;
    }

    public long getCooldownSeconds() {
        return cooldownSeconds;
    }
}
