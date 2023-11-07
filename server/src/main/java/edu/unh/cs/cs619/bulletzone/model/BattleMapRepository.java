package edu.unh.cs.cs619.bulletzone.model;

import edu.unh.cs.cs619.bulletzone.model.entities.Deck;
import edu.unh.cs.cs619.bulletzone.model.entities.IndestructibleWall;
import edu.unh.cs.cs619.bulletzone.model.entities.Road;
import edu.unh.cs.cs619.bulletzone.model.entities.Wall;

public class BattleMapRepository {
    static public BattleMap DEFAULT_MAP = new BattleMap() {{
        this.addEntity(1, new IndestructibleWall(1));
        this.addEntity(2, new IndestructibleWall(2));
        this.addEntity(3, new IndestructibleWall(3));

        this.addEntity(17, new IndestructibleWall());
        this.addEntity(33, new Wall(100, 33));
        this.addEntity(49, new Wall(100, 49));
        this.addEntity(65, new Wall(100, 65));

        this.addEntity(34, new IndestructibleWall(34));
        this.addEntity(66, new Wall(100, 66));

        this.addEntity(35, new IndestructibleWall(35));
        this.addEntity(51, new IndestructibleWall(51));
        this.addEntity(67, new Wall(100, 67));

        this.addEntity(5, new IndestructibleWall(5));
        this.addEntity(21, new IndestructibleWall(21));
        this.addEntity(37, new IndestructibleWall(37));
        this.addEntity(53, new IndestructibleWall(53));
        this.addEntity(69, new Wall(100, 69));

        this.addEntity(7, new IndestructibleWall(7));
        this.addEntity(23, new IndestructibleWall(23));
        this.addEntity(39, new IndestructibleWall(39));
        this.addEntity(71, new Wall(100, 71));

        this.addEntity(8, new IndestructibleWall(8));
        this.addEntity(40, new IndestructibleWall(40));
        this.addEntity(72, new Wall(100, 72));

        this.addEntity(9, new IndestructibleWall(9));
        this.addEntity(25, new IndestructibleWall(25));
        this.addEntity(41, new IndestructibleWall(41));
        this.addEntity(57, new IndestructibleWall(57));
        this.addEntity(73, new IndestructibleWall(73));
    }};

//    static public BattleMap NEW_MAP = BattleMap.getBattleMapFromFile("../resources/main/maps/new_map.csv");
//    static public BattleMap QUAD_ZONE = BattleMap.getBattleMapFromFile("../resources/main/maps/quad_zone.csv");
}
