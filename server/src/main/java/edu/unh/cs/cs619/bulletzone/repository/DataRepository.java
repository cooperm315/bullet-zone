package edu.unh.cs.cs619.bulletzone.repository;

import org.springframework.stereotype.Component;

import edu.unh.cs.cs619.bulletzone.datalayer.BulletZoneData;
import edu.unh.cs.cs619.bulletzone.datalayer.account.BankAccount;
import edu.unh.cs.cs619.bulletzone.datalayer.item.GameItemContainer;
import edu.unh.cs.cs619.bulletzone.datalayer.user.GameUser;
import edu.unh.cs.cs619.bulletzone.datalayer.user.GameUserRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class provides tailored access to objects that are needed by the REST API/Controller
 * classes. The idea is that it will interface with a BulletZoneData instance as well as
 * any other objects it needs to answer requests having to do with users, items, accounts,
 * permissions, and other things that are related to what is stored in the database.
 *
 * The convention is that actual objects will be returned by the DataRepository so that internal
 * objects can make effective use of the results as well as the Controllers. This means that
 * all API/Controller classes will need to translate these objects into the strings they need
 * to communicate information back to the caller.
 */
//Note that the @Component annotation below causes an instance of a DataRepository to be
//created and used for the Controller classes in the "web" package.
@Component
public class DataRepository {
    private BulletZoneData bzdata;

    public DataRepository() {
        // to run as jar, compile and run the command `java -jar server.jar --server.address=10.21.185.171 --server.port=61901`
        String url = "jdbc:mysql://stman1.cs.unh.edu:3306/cs61901";
        String username = "antares";
        String password = "pT55[sStrt";

        bzdata = new BulletZoneData(url, username, password);
    }

    DataRepository(Boolean test) {
        bzdata = new BulletZoneData();
    }

    /**
     * Creates user if it does not exist
     * @param username Username for the user to create or validate
     * @param password Password for the user
     * @return GameUser corresponding to the username/password if successful. Null otherwise.
     */
    public GameUser createUser(String username, String password){
        GameUserRepository repo = bzdata.users;
        GameUser user = repo.getUser(username);
        GameItemContainer bay = bzdata.items.createContainer(bzdata.types.GarageBay);
        // user owns a miner and tank upon login
        GameItemContainer tank = bzdata.items.createContainer(bzdata.types.TankFrame);
        GameItemContainer miner = bzdata.items.createContainer(bzdata.types.TruckFrame);

        BankAccount account = bzdata.accounts.create();

        if (user != null) {
            return null;
        }

        //user does not exist, create user
        user = repo.createUser(username, username, password);

        // set user permissions with tanks/account
        bzdata.permissions.setOwner(account, user);
        bzdata.accounts.modifyBalance(account, 1000);

        bzdata.permissions.setOwner(tank, user);
        bzdata.items.addItemToContainer(tank, bay);

        bzdata.permissions.setOwner(miner, user);
        bzdata.items.addItemToContainer(miner, bay);
        return user;
    }

    /**
     * Validates user login
     * @param username Username for the user to validate
     * @param password Password for the user
     * @return GameUser corresponding to the username/password if successful, null otherwise
     */
    public GameUser validateUser(String username, String password) {
        GameUserRepository repo = bzdata.users;
        GameUser user = repo.validateLogin(username, password);
        return user;
    }

    public double getBalance(GameUser user) {
        BankAccount acc = (BankAccount) user.getOwnedAccounts().toArray()[0];
        System.out.println("Acc: " + acc);

        return acc.getBalance();
    }
    // returns the BulletZoneData for testing purposes
    public BulletZoneData getBZD() {return bzdata;}
}
