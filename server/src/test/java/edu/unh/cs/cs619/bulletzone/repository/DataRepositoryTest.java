package edu.unh.cs.cs619.bulletzone.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;

import edu.unh.cs.cs619.bulletzone.datalayer.BulletZoneData;
import edu.unh.cs.cs619.bulletzone.datalayer.account.BankAccount;
import edu.unh.cs.cs619.bulletzone.datalayer.user.GameUser;
import edu.unh.cs.cs619.bulletzone.datalayer.user.GameUserRepository;

// Note: There are loads of garbage accounts in the database that were created by the tests
// and many of them are without owners.
// This breaks a lot.

// Running into many concurrent modification exceptions when running tests.
public class DataRepositoryTest {

    static final String user1 = "testing1";
    static final String pass1 = "pass1";
    static final String user2 = "testing2";
    static final String pass2 = "pass2";
    static final String user3 = "testing3";
    static final String pass3 = "pass3";

    // Deletes accounts
//    public static void main(String[] args) {
//        BulletZoneData bzd = new DataRepository().getBZD();
//        bzd.rebuildData();
//    }

    /**
     * Deletes all accounts that are created in the tests
     * to assure that the tests are independent of each other and no users are left over
     * (was messing up tests before)
     */

    @After
    public void after_deleteAccounts() {
        // goes through all accounts and if they are created by the tests, delete them
        DataRepository dataRepo = new DataRepository(true);
        BulletZoneData bzd = dataRepo.getBZD();
        bzd.rebuildData();
    }

    @Test
    public void createAccountNotExist() {
        DataRepository dataRepo = new DataRepository(true);
        BulletZoneData bzd = dataRepo.getBZD();
        GameUser gameUser1 = dataRepo.createUser(user1, pass1);
        GameUser gameUser2 = dataRepo.createUser(user2, pass2);
        GameUser gameUser3 = dataRepo.createUser(user3, pass3);

        // assert created user is found in the collection of users in BulletZoneData
        assert (bzd.users.getUsers().contains(gameUser1));
        assert (bzd.users.getUser(user1).equals(gameUser1));
        assert (bzd.users.getUsers().contains(gameUser2));
        assert (bzd.users.getUser(user2).equals(gameUser2));
        assert (bzd.users.getUsers().contains(gameUser3));
        assert (bzd.users.getUser(user3).equals(gameUser3));
    }

    // tries to create account that already exists
    @Test
    public void createAccountExist() {
        DataRepository dataRepo = new DataRepository(true);
        dataRepo.createUser(user1, pass1);
        dataRepo.createUser(user2, pass2);

        // assert that the user already exists
        try {
            dataRepo.createUser(user1, pass1);
        } catch (IllegalArgumentException e) {
            assert (e.getMessage().equals("User already exists"));
        }

        // assert that the user already exists
        try {
            dataRepo.createUser(user2, pass2);
        } catch (IllegalArgumentException e) {
            assert (e.getMessage().equals("User already exists"));
        }
    }

    // tries to log into non existent account (wrong info)
    @Test
    public void loginAccountNotExist() {
        DataRepository dataRepo = new DataRepository(true);
        dataRepo.createUser(user1, pass1);
        dataRepo.createUser(user2, pass2);

        // assert that the user does not exist
        try {
            dataRepo.validateUser(user3, pass3);
        } catch (IllegalArgumentException e) {
            assert (e.getMessage().equals("User does not exist"));
        }
    }

    // logs into account that exists with correct info
    @Test
    public void loginAccountExist() {
        DataRepository dataRepo = new DataRepository(true);

        GameUser gameUser1 = dataRepo.createUser(user1, pass1);
        GameUser gameUser2 = dataRepo.createUser(user2, pass2);

        // assert that the user exists
        assert (dataRepo.validateUser(user1, pass1).equals(gameUser1));
        assert (dataRepo.validateUser(user2, pass2).equals(gameUser2));
    }

    // assures bank account balance of account is 1000 credits upon creation
    @Test
    public void accountBalanceCorrect() {
        DataRepository dataRepo = new DataRepository(true);

        GameUser user = dataRepo.createUser(user1, pass1);

        // assert that the account balance is 1000
        assert (dataRepo.getBalance(user) == 1000);
    }
}
