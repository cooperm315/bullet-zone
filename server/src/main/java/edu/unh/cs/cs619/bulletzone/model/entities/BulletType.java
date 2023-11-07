package edu.unh.cs.cs619.bulletzone.model.entities;

public enum BulletType {
    MINER_BULLET(5, 200, 200), // for miner
    TANK_BULLET(30, 500, 200), // for tank
    BUILDER_BULLET(10, 1000, 200); // for builder

    final int bulletDamage;
    final int bulletDelay;
    final int bulletSpeed;
    BulletType(int damage, int delay, int speed){
        this.bulletDamage = damage;
        this.bulletDelay = delay;
        this.bulletSpeed = speed;
    }
}
