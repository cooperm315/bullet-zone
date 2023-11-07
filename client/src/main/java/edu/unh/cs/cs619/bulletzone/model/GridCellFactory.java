package edu.unh.cs.cs619.bulletzone.model;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import edu.unh.cs.cs619.bulletzone.R;

public class GridCellFactory {
    private static GridCellFactory factory;
    GameUser user;
    Context context;
    private GridCellFactory(Context context) {
        this.context = context;
        user = GameUser.getInstance();
    }

    /**
     *
     * @return an instance of itself (allows singleton)
     */
    public static synchronized GridCellFactory getInstance(Context context) {
        if (factory == null)
            factory = new GridCellFactory(context);

        return factory;
    }

    /**
     *
     * @param val the value passed in from the server (determines what is in the cell)
     * @return a new grid cell that is its respective type
     */
    public GridCell getCell(long val, long[] adjacent){ //adjacent is an array of 4 ints, 0 is north, 1 is east, 2 is south, 3 is west
        GridCell cell = new GridCell();

        //setting cell terrain
        long terrainVal = (long) (val / 100000000.0);
        if (val >= 1000000000)
            terrainVal = terrainVal % 10; // fun casting stuff to get far right digit
        if (terrainVal == 1)
            cell.setBackground(ContextCompat.getDrawable(context, R.drawable.water));
        else if (terrainVal == 2)
            cell.setBackground(ContextCompat.getDrawable(context, R.drawable.sand));
        else if (terrainVal == 3)
            cell.setBackground(ContextCompat.getDrawable(context, R.drawable.meadow));
        else if (terrainVal == 4)
            cell.setBackground(ContextCompat.getDrawable(context, R.drawable.forest));
        else if (terrainVal == 5)
            cell.setBackground(ContextCompat.getDrawable(context, R.drawable.rocky));
        else if (terrainVal == 6)
            cell.setBackground(ContextCompat.getDrawable(context, R.drawable.mountainous));
        else if (terrainVal == 7)
            cell.setBackground(ContextCompat.getDrawable(context, R.drawable.dryland));
        else{
            cell.setBackground(ContextCompat.getDrawable(context, R.drawable.meadow)); // default
        }
        // setting cell structure
        if (val >= 1000000000) {
            long structureVal = val / 1000000000;
            if (structureVal == 1)
                cell.setMiddleGround(ContextCompat.getDrawable(context, R.drawable.pontoon));
            else if (structureVal == 2)
                cell.setMiddleGround(ContextCompat.getDrawable(context, getDynamicRoad(adjacent)));
            else if (structureVal == 3)
                cell.setMiddleGround(ContextCompat.getDrawable(context, R.drawable.wood_wall));
            else if (structureVal == 4)
                cell.setMiddleGround(ContextCompat.getDrawable(context, R.drawable.brick_wall));
            else
                cell.setMiddleGround(cell.getBackground());
        } else
            cell.setMiddleGround(cell.getBackground());

        //setting cell entity
        // get rid of far left digit
        long entityVal = val % 100000000;
        if (entityVal == 0)
            cell.setForeground(null); // hope it don't break
        else if (entityVal == 1000)
            cell.setForeground(ContextCompat.getDrawable(context, R.drawable.brick_wall));
        else if (entityVal < 2000)
            cell.setForeground(ContextCompat.getDrawable(context, R.drawable.wood_wall));
        else if (entityVal == 10000)
            cell.setForeground(ContextCompat.getDrawable(context, R.drawable.clay));
        else if (entityVal == 10001)
            cell.setForeground(ContextCompat.getDrawable(context, R.drawable.rock));
        else if (entityVal == 10002)
            cell.setForeground(ContextCompat.getDrawable(context, R.drawable.iron));
        else if (entityVal == 10003)
            cell.setForeground(ContextCompat.getDrawable(context, R.drawable.wood));
        else if (entityVal == 11000)
            cell.setForeground(ContextCompat.getDrawable(context, R.drawable.coin));
        else if (entityVal == 11001)
            cell.setForeground(ContextCompat.getDrawable(context, R.drawable.coinpurse));
        else if (entityVal == 11002)
            cell.setForeground(ContextCompat.getDrawable(context, R.drawable.treasurechest));
        else if (entityVal == 12000)
            cell.setForeground(ContextCompat.getDrawable(context, R.drawable.grav_assist));
        else if (entityVal == 12001)
            cell.setForeground(ContextCompat.getDrawable(context, R.drawable.fusion_generator));
        else if (entityVal < 2000000)
            cell.setForeground(ContextCompat.getDrawable(context, R.drawable.meadow));
        else if (entityVal < 3000000) {
            if (user.getActiveUnit().equals("BUILDER"))
                cell.setForeground(ContextCompat.getDrawable(context, R.drawable.cluster_bullet));
            else
                cell.setForeground(ContextCompat.getDrawable(context, R.drawable.bullet));
        }

        else if (entityVal < 20000000) {
            long tankID = (entityVal % 10000000) / 10000;
            Drawable dir;
            if (tankID == user.getTankId()) {
                if (user.getTankDirection() == 0)
                    dir = ContextCompat.getDrawable(context, R.drawable.blue_tank_up);
                else if (user.getTankDirection() == 2)
                    dir = ContextCompat.getDrawable(context, R.drawable.blue_tank_right);
                else if (user.getTankDirection() == 4)
                    dir = ContextCompat.getDrawable(context, R.drawable.blue_tank_down);
                else
                    dir = ContextCompat.getDrawable(context, R.drawable.blue_tank_left);
            }
            else {
                if (entityVal % 10 == 0)
                    dir = ContextCompat.getDrawable(context, R.drawable.red_tank_up);
                else if (entityVal % 10 == 2)
                    dir = ContextCompat.getDrawable(context, R.drawable.red_tank_right);
                else if (entityVal % 10 == 4)
                    dir = ContextCompat.getDrawable(context, R.drawable.red_tank_down);
                else
                    dir = ContextCompat.getDrawable(context, R.drawable.red_tank_left);
            }
            cell.setForeground(dir);

        } else if (entityVal < 40000000) {
            long minerID = (entityVal % 30000000) / 10000;
            Drawable dir;
            if (minerID == user.getMinerId()) {
                if (user.getMinerDirection() == 0)
                    dir = ContextCompat.getDrawable(context, R.drawable.blue_miner_up);
                else if (user.getMinerDirection() == 2)
                    dir = ContextCompat.getDrawable(context, R.drawable.blue_miner_right);
                else if (user.getMinerDirection() == 4)
                    dir = ContextCompat.getDrawable(context, R.drawable.blue_miner_down);
                else
                    dir = ContextCompat.getDrawable(context, R.drawable.blue_miner_left);
                cell.setForeground(dir);
                user.setMinerTerrain(terrainVal);
            } else {
                if (entityVal % 10 == 0)
                    dir = ContextCompat.getDrawable(context, R.drawable.red_miner_up);
                else if (entityVal % 10 == 2)
                    dir = ContextCompat.getDrawable(context, R.drawable.red_miner_right);
                else if (entityVal % 10 == 4)
                    dir = ContextCompat.getDrawable(context, R.drawable.red_miner_down);
                else
                    dir = ContextCompat.getDrawable(context, R.drawable.red_miner_left);
                cell.setForeground(dir);
            }
        }
        else if (entityVal < 50000000) {
            long builderId = (entityVal % 40000000) / 10000;
            Drawable dir;
            if (builderId == user.getBuilderId()) {
                if (user.getBuilderDirection() == 0)
                    dir = ContextCompat.getDrawable(context, R.drawable.player_builder0);
                else if (user.getBuilderDirection() == 2)
                    dir = ContextCompat.getDrawable(context, R.drawable.player_builder2);
                else if (user.getBuilderDirection() == 4)
                    dir = ContextCompat.getDrawable(context, R.drawable.player_builder4);
                else
                    dir = ContextCompat.getDrawable(context, R.drawable.player_builder6);
                cell.setForeground(dir);
            } else {
                if (entityVal % 10 == 0)
                    dir = ContextCompat.getDrawable(context, R.drawable.enemy_builder0);
                else if (entityVal % 10 == 2)
                    dir = ContextCompat.getDrawable(context, R.drawable.enemy_builder2);
                else if (entityVal % 10 == 4)
                    dir = ContextCompat.getDrawable(context, R.drawable.enemy_builder4);
                else
                    dir = ContextCompat.getDrawable(context, R.drawable.enemy_builder6);
                cell.setForeground(dir);
            }
        }
        cell.setCellType("We should delete this");
        cell.setVal(val);
        return cell;
    }

    private int getDynamicRoad(long[] adjacent) {
        boolean[] adj = new boolean[4]; // if they are of type road
        int count = 0;
        for (int i = 0; i < 4; i++) {
            if (adjacent[i] >= 1000000000) {
                long structureVal = adjacent[i] / 1000000000;
                if (structureVal == 2) {
                    adj[i] = true;
                    count++;
                }
            }
        }

        if(count > 2){
            return R.drawable.road_no_lines;
        }


        //bunch of if statements
        if(adj[0] && adj[2]) // vert up down
            return R.drawable.road_vert;
        else if(adj[1] && adj[3]) // horiz left right
            return R.drawable.road_horiz;
        else if(adj[0] && adj[1]) // down right
            return R.drawable.road_down_turn_right;
        else if(adj[1] && adj[2]) // up right
            return R.drawable.road_turn_right;
        else if(adj[2] && adj[3]) // up left
            return R.drawable.road_turn_left;
        else if(adj[0] && adj[3]) // down left
            return R.drawable.road_down_turn_left;
        else if(adj[0]) // down right up
            return R.drawable.road_vert;
        else if(adj[1]) // right down left
            return R.drawable.road_horiz;
        else if(adj[2]) // up left down
            return R.drawable.road_vert;
        else if(adj[3]) // left up right
            return R.drawable.road_horiz;
        else
            return R.drawable.road_no_lines;
    }
}
