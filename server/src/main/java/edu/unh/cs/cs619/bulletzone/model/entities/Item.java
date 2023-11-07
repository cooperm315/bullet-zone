package edu.unh.cs.cs619.bulletzone.model.entities;

public abstract class Item extends StaticEntity implements Intractable {
    long id;
    int creditValue; // amount of credits

    public Item(long id, int creditValue) {
        this.id = id;
        this.creditValue = creditValue;
    }

    public static Item getRandomItem() {
        int id = (int) (Math.random() * 2);
        // 0 = resource, 1 = coin
        if (id == 0) {
            return ResourceItem.getRandomResource();
        } else {
            return CoinItem.getRandomCoinItem();
        }
    }

    @Override
    public long getIntValue() {
        return id;
    }

    @Override
    public void destroy() {
        super.kill();
    }
}
