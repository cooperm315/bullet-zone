package edu.unh.cs.cs619.bulletzone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import edu.unh.cs.cs619.bulletzone.datalayer.BulletZoneData;
import edu.unh.cs.cs619.bulletzone.datalayer.account.BankAccount;
import edu.unh.cs.cs619.bulletzone.datalayer.user.GameUser;
import edu.unh.cs.cs619.bulletzone.model.entities.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.entities.Intractable;
import edu.unh.cs.cs619.bulletzone.model.entities.ResourceType;
import edu.unh.cs.cs619.bulletzone.model.entities.VehicleEntity;
import edu.unh.cs.cs619.bulletzone.repository.DataRepository;

public final class Game {
    /**
     * Field dimensions
     */
    private static final int FIELD_DIM = 16;
    private final long id;
    private final ArrayList<FieldHolder> holderGrid = new ArrayList<>();

    private final ConcurrentMap<Long, VehicleEntity> vehicles = new ConcurrentHashMap<>(); // tankid, tank object
    private final ConcurrentMap<String, Long> playersUsernames = new ConcurrentHashMap<>(); // username, tankId
    private final ConcurrentMap<String, Inventory> playerInventories = new ConcurrentHashMap<>(); //username, resources

    private final HashSet<String> users = new HashSet<>();

    public final ArrayList<Intractable> items = new ArrayList<>();

    private final Object monitor = new Object();
    private BulletZoneData bzd = new DataRepository().getBZD();

    public Game() {
        this.id = 0;
    }

    @JsonIgnore
    public long getId() {
        return id;
    }

    @JsonIgnore
    public ArrayList<FieldHolder> getHolderGrid() {
        return holderGrid;
    }

    public void addUser(String username){
        users.add(username);
        playerInventories.put(username, new Inventory());
    }

    public HashSet<String> getUsers(){
        return users;
    }

    public void removeUser(String username){
        users.remove(username);
    }

    public int[] getInventory(String username){
        int[] inventory = new int[5];
        int balance = getCoins(username);
        Inventory inv = playerInventories.get(username);
        inventory[0] = inv.getResource(ResourceType.CLAY);
        inventory[1] = inv.getResource(ResourceType.ROCK);
        inventory[2] = inv.getResource(ResourceType.IRON);
        inventory[3] = inv.getResource(ResourceType.WOOD);
        inventory[4] = balance;

        return inventory;
    }

    public Inventory getUserInventory(String username) {return playerInventories.get(username);}

    public void changeCoins(String username, int amount) {
        try{
            GameUser gu = bzd.users.getUser(username);
            // get owned accounts
            Collection<BankAccount> accounts = gu.getOwnedAccounts();
            // get first account
            BankAccount ba = accounts.iterator().next();
            // get balance
            ba.changeBalance(amount);

        }
        catch (Exception e){
            System.out.println("Error changing coins");
        }
    }

    public int getCoins(String username){
        try{
            GameUser gu = bzd.users.getUser(username);
            // get owned accounts
            Collection<BankAccount> accounts = gu.getOwnedAccounts();
            // get first account
            BankAccount ba = accounts.iterator().next();
            // get balance
            double balance = ba.getBalance();
            return (int)balance;
        }
        catch (NullPointerException e){
            System.out.println("Error getting coins, user not found");
        }
        return 0;
    }

    public void addVehicle(String username, VehicleEntity vehicle) {
        synchronized (vehicles) {
            vehicles.put(vehicle.getId(), vehicle);
            playersUsernames.put(username, vehicle.getId());
        }
    }

    public VehicleEntity getVehicle(long vehicleId) {
        return vehicles.get(vehicleId);
    }

    public ConcurrentMap<Long, VehicleEntity> getVehicles() {
        return vehicles;
    }

    public List<Optional<FieldEntity>> getGrid() {
        synchronized (holderGrid) {
            List<Optional<FieldEntity>> entities = new ArrayList<>();

            FieldEntity entity;
            for (FieldHolder holder : holderGrid) {
                if (holder.isPresent()) {
                    entity = holder.getEntity();
                    entity = entity.copy();

                    entities.add(Optional.<FieldEntity>of(entity));
                } else {
                    entities.add(Optional.<FieldEntity>empty());
                }
            }
            return entities;
        }
    }

    public VehicleEntity getVehicle(String username){
        if (playersUsernames.containsKey(username)){
            return vehicles.get(playersUsernames.get(username));
        }
        return null;
    }

    // sorta works?
    public ArrayList<VehicleEntity> getVehicles(String username){
        // get every vehicle associated with the username
        ArrayList<Long> ids = new ArrayList<>();
        ArrayList<VehicleEntity> vehicles = new ArrayList<>();

        for (String key : playersUsernames.keySet()){
            if (key.equals(username)){
                ids.add(playersUsernames.get(key));
            }
        }

        for (Long id : ids){
            vehicles.add(getVehicle(id));
        }

        return vehicles;
    }

    public void removeVehicle(long vehicleId){
        synchronized (vehicles) {
            VehicleEntity t = vehicles.remove(vehicleId);
            if (t != null) {
                playersUsernames.remove(t.getUsername());
                playerInventories.remove(t.getUsername());
            }
        }
    }

    public void killVehicle(long vehicleId) {
        synchronized (vehicles) {
            VehicleEntity t = vehicles.get(vehicleId);
            if (t != null)
                vehicles.remove(vehicleId);
        }
    }

    public long[][] getGrid2D() {
        long[][] grid = new long[FIELD_DIM][FIELD_DIM];

        synchronized (holderGrid) {
            FieldHolder holder;
            for (int i = 0; i < FIELD_DIM; i++) {
                for (int j = 0; j < FIELD_DIM; j++) {
                    holder = holderGrid.get(i * FIELD_DIM + j);
                    grid[i][j] = holder.getValue();
                }
            }
        }

        return grid;
    }

    public String getUsername(long vehicleId){
        return vehicles.get(vehicleId).getUsername();
    }

    public ConcurrentMap<String, Inventory> getPlayerInventories() {
        return playerInventories;
    }

    public int getPlayerCount(){
        return playersUsernames.size();
    }

    public void addResource(String username, ResourceType resource, int amount){
        playerInventories.get(username).addResource(resource, amount);
    }

    public void removeResource(String username, ResourceType resource, int amount) {
        playerInventories.get(username).removeResource(resource, amount);
    }

    public FieldEntity getStructure(long structureId) {

        return null;
    }
}
