package edu.unh.cs.cs619.bulletzone.model.entities;

public class CoinItem extends Item {
    /**
     * Returns a random integer between min and max, inclusive.
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     */
    public static int getRandomInt(int min, int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }

    public CoinItem(long id, int creditValue) {
        super(11000, creditValue);
    }

    public static CoinItem getRandomCoinItem() {
        int id = (int) (Math.random() * 3);
        if (id == 0) {
            return new CoinItem(11000, getRandomInt(25, 100));
        } else if (id == 1) {
            return new CoinItem(11001, getRandomInt(200, 400));
        } else {
            return new CoinItem(11002, getRandomInt(500, 1000));
        }
    }
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    @Override
    public FieldEntity copy() {
        return new CoinItem(id, creditValue);
    }

    @Override
    public void pickup(VehicleEntity vehicleEntity) {
        vehicleEntity.getGame().changeCoins(vehicleEntity.getUsername(), creditValue);
        super.destroy();
    }
}
