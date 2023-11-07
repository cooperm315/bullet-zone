package edu.unh.cs.cs619.bulletzone.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import edu.unh.cs.cs619.bulletzone.model.entities.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.entities.Wall;

/**
 * BattleMap contains a hashmap of index, and field entity
 * This is used to load maps into the game using the GameBoardBuilder
 */
public class BattleMap {
    private HashMap<Integer, FieldEntity> map;

    public BattleMap() {
        map = new HashMap<>();
    }

    public BattleMap(HashMap<Integer, FieldEntity> map) {
        this.map = map;
    }

    public void addEntity(int index, FieldEntity field) {
        map.put(index, field);
    }

    public FieldEntity getEntity(int index) {
        return map.get(index);
    }

    /**
     * This method will take in a file name and return a BattleMap
     * @param fileName the name of the file to be read
     * @return a BattleMap
     */
    public static BattleMap getBattleMapFromFile(String fileName) {
        HashMap<Integer, FieldEntity> map = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine();
            //convert the line to an array of chars separated by commas
            String[] lineArray = line.split(",");
            // if the char is a 2, then add a wall to the map
            for (int i = 0; i < lineArray.length; i++) {
                if (lineArray[i].equals("2")) {
                    map.put(i, new Wall());
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + fileName);
            e.printStackTrace();
        }

        return new BattleMap(map);
    }
}
