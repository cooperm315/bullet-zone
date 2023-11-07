package edu.unh.cs.cs619.bulletzone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.rest.spring.annotations.RestService;
import org.androidannotations.api.BackgroundExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.model.GameUser;
import edu.unh.cs.cs619.bulletzone.rest.BZRestErrorhandler;
import edu.unh.cs.cs619.bulletzone.rest.BulletZoneRestClient;
import edu.unh.cs.cs619.bulletzone.rest.GridPollerTask;
import edu.unh.cs.cs619.bulletzone.rest.GridUpdateEvent;
import edu.unh.cs.cs619.bulletzone.states.VehicleButtonStates;
import edu.unh.cs.cs619.bulletzone.states.VehicleButtons;
import edu.unh.cs.cs619.bulletzone.ui.GridAdapter;
import edu.unh.cs.cs619.bulletzone.util.BooleanWrapper;
import edu.unh.cs.cs619.bulletzone.util.BuildWrapper;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;
import edu.unh.cs.cs619.bulletzone.util.InventoryWrapper;
import edu.unh.cs.cs619.bulletzone.util.MoveWrapper;
import edu.unh.cs.cs619.bulletzone.util.SoundPlayer;
import edu.unh.cs.cs619.bulletzone.util.ValidityWrapper;

@EActivity(R.layout.activity_client)
public class ClientActivity extends AppCompatActivity {

    private static final String TAG = "ClientActivity";

    private GameUser user;

    private VehicleButtonStates vehicleButtonStates;

    @Bean
    protected GridAdapter mGridAdapter;

    @ViewById(R.id.gridView)
    protected GridView gridView;

    @Bean
    BusProvider busProvider;

    @NonConfigurationInstance
    @Bean
    GridPollerTask gridPollTask;

    @RestService
    BulletZoneRestClient restClient;

    @Bean
    BZRestErrorhandler bzRestErrorhandler;

    byte direction = 0;
    SoundPlayer soundPlayer = new SoundPlayer(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shakeDetector();
        vehicleButtonStates = VehicleButtonStates.getInstance();
        soundPlayer = new SoundPlayer(this);
        soundPlayer.playSound("music" ,R.raw.game_music, true);

        // Handle back button
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                leaveGame();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @Override
    protected void onStart() {
        super.onStart();
        try{
            soundPlayer.resumeAll();
        }
        catch (Exception e){
            Log.e(TAG, "onStart: ", e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        soundPlayer.pauseAll();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPlayer.stopAll();
    }

    /**
     * Otto has a limitation (as per design) that it will only find
     * methods on the immediate class type. As a result, if at runtime this instance
     * actually points to a subclass implementation, the methods registered in this class will
     * not be found. This immediately becomes a problem when using the AndroidAnnotations
     * framework as it always produces a subclass of annotated classes.
     * <p>
     * To get around the class hierarchy limitation, one can use a separate anonymous class to
     * handle the events.
     */
    private Object gridEventHandler = new Object() {
        @Subscribe
        public void onUpdateGrid(GridUpdateEvent event) {
            updateGrid(event.gw);
            updateButtons();
        }
    };


    @AfterViews
    protected void afterViewInjection() {
        joinAsync();
        SystemClock.sleep(500);
        mGridAdapter.setContext(this);
        gridView.setAdapter(mGridAdapter);
    }

    @AfterInject
    void afterInject() {
        restClient.setRestErrorHandler(bzRestErrorhandler);
        busProvider.getEventBus().register(gridEventHandler);
        user = GameUser.getInstance();
    }

    @Background
    void joinAsync() {
        try {
            long[] vehicles = restClient.join(user.getUsername()).getResult();
            user.setTankId(vehicles[0]);
            user.setMinerId(vehicles[1]);
            user.setBuilderId(vehicles[2]);
            user.setCurrId(user.getTankId());
            user.setTankDirection((byte) 0);
            user.setMinerDirection((byte) 0);
            user.setBuilderDirection((byte) 0);
            gridPollTask.doPoll();
            getInventory();
            vehicleButtonStates.setActiveButtons(user.getActiveUnit().charAt(0));
        } catch (Exception e) {
        }
    }

    public void updateGrid(GridWrapper gw) {
        mGridAdapter.updateList(gw.getGrid());
        updateTankInfo(gw.getGrid());
    }

    private void updateTankInfo(long[][] grid) {
        // for every element in the grid
//        if (user.isAlive()) {
        // run this in a separate thread
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            // updating fun stuff
            ValidityWrapper vw = restClient.getValidity(user.getCurrId());
            vehicleButtonStates.setActiveButtons(user.getActiveUnit().charAt(0));
            vehicleButtonStates.getActiveButtons().setCanBuild(vw.isBuild());
            vehicleButtonStates.getActiveButtons().setCanDismantle(vw.isDismantle());
            vehicleButtonStates.getActiveButtons().setCanEject(vw.isPowerUp());
            vehicleButtonStates.getActiveButtons().setCanMoveDown(vw.isCanMoveBackward());
            vehicleButtonStates.getActiveButtons().setCanMoveUp(vw.isCanMoveForward());
            vehicleButtonStates.getActiveButtons().setCanMine(vw.isMine());

            boolean found = false;
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[i].length; j++) {
                    long val = grid[i][j];
                    long entityVal = val % 100000000;
                    if (entityVal >= 10000000) {
                        long id = (entityVal % 10000000) / 10000;
                        long health = (entityVal % 10000) / 10;

                        if (id == user.getCurrId()) {
                            user.setAlive(true);
                            found = true;
                            if (id == user.getCurrId()) {
                                user.setActiveHealth((int)health);
                            }
                        }
                    }
                }
            }
            if (!found) {
                user.setAlive(false);
            }


        });
        executorService.shutdown();
//        }

        TextView tankText = findViewById(R.id.tankInfo);
        String text = "Unit: " + user.getActiveUnit() + "\n" +
                "HP: " + user.getActiveHealth() + "\n" +
                "Gold: " + user.getInventory()[4] + "\n" +
                "Clay: " + user.getInventory()[0] + " Rock: " + user.getInventory()[1] + " Iron: " + user.getInventory()[2] + " Wood: " + user.getInventory()[3];
        tankText.setText(text);
    }

    protected void getInventory() {
        int[] inventory;
        // rest client likes to crash here so try catch
        try {
            InventoryWrapper iw = restClient.getInventory(user.getUsername(), user.getCurrId());
//            Log.d("INVENTORYWRAPPER", "GOT BACK THE FOLLOWING: " + iw.isPoweredUp());
            inventory = iw.getResult();
            vehicleButtonStates.getActiveButtons().setCanEject(iw.isPoweredUp());
            updateButtons();
        }
        catch (Exception e) {
            inventory = new int[]{0, 0, 0, 0, 0};
            System.out.println("Error getting inventory");
        }
        user.setInventory(inventory);
    }

    @Click({R.id.buttonUp, R.id.buttonDown, R.id.buttonLeft, R.id.buttonRight})
    protected void onButtonMove(View view) {
        final int viewId = view.getId();
        if (user.getActiveUnit().equals("MINER")) {
            user.isMining = false;
        }
//        System.out.println("Current Direction: " + direction +
//                " Current tankId: " + user.getTankId() +
//                " Current UserId: " + user.getUserId());
//        System.out.println(direction);
        if (user.getCurrId() == user.getTankId()) {
            switch (viewId) {
                case R.id.buttonUp: // move forward
                    this.moveAsync(user.getCurrId(), user.getTankDirection());
                    break;
                case R.id.buttonRight: // turn right
                    this.turnAsync(user.getCurrId(), (byte) ((user.getTankDirection() + 2) % 8));
//                System.out.println("GridAdapter: " + mGridAdapter.getCurrId() + " Actual: " + user.getCurrId());
                    break;
                case R.id.buttonDown: // move backward
                    this.moveAsync(user.getCurrId(), (byte) ((user.getTankDirection() + 4) % 8));
                    break;
                case R.id.buttonLeft: // turn left
                    this.turnAsync(user.getCurrId(), (byte) ((user.getTankDirection() + 6) % 8));
                    break;
                default:
                    Log.e(TAG, "Unknown movement button id: " + viewId);
                    break;
            }
//            System.out.println("Tank Direction: " + user.getTankDirection());
        } else if (user.getCurrId() == user.getMinerId()){
            switch (viewId) {
                case R.id.buttonUp: // move forward
                    this.moveAsync(user.getCurrId(), user.getMinerDirection());
                    break;
                case R.id.buttonRight: // turn right
                    this.turnAsync(user.getCurrId(), (byte) ((user.getMinerDirection() + 2) % 8));
//                System.out.println("GridAdapter: " + mGridAdapter.getCurrId() + " Actual: " + user.getCurrId());
                    break;
                case R.id.buttonDown: // move backward
                    this.moveAsync(user.getCurrId(), (byte) ((user.getMinerDirection() + 4) % 8));
                    break;
                case R.id.buttonLeft: // turn left
                    this.turnAsync(user.getCurrId(), (byte) ((user.getMinerDirection() + 6) % 8));
                    break;
                default:
                    Log.e(TAG, "Unknown movement button id: " + viewId);
                    break;
            }
//            System.out.println("Miner Direction: " + user.getMinerDirection());
        } else {
            switch (viewId) {
                case R.id.buttonUp: // move forward
                    this.moveAsync(user.getCurrId(), user.getBuilderDirection());
                    break;
                case R.id.buttonRight: // turn right
                    this.turnAsync(user.getCurrId(), (byte) ((user.getBuilderDirection() + 2) % 8));
//                System.out.println("GridAdapter: " + mGridAdapter.getCurrId() + " Actual: " + user.getCurrId());
                    break;

                case R.id.buttonDown: // move backward
                    this.moveAsync(user.getCurrId(), (byte) ((user.getBuilderDirection() + 4) % 8));
                    break;
                case R.id.buttonLeft: // turn left
                    this.turnAsync(user.getCurrId(), (byte) ((user.getBuilderDirection() + 6) % 8));
                    break;
                default:
                    Log.e(TAG, "Unknown movement button id: " + viewId);
                    break;

            }
        }

    }

    @Background
    void moveAsync(long currId, byte direction) {
//        System.out.println("TankID: " +  user.getTankId() + " MinerID: " + user.getMinerId() + " CurrID: " + currId);
        if (user.isBuilding) return;
        MoveWrapper mw = restClient.move(currId, direction);
        getInventory();

        // Update buttons based on result
        if (mw != null) {
            vehicleButtonStates.getActiveButtons().setCanMoveUp(mw.isForwardOpen());
            vehicleButtonStates.getActiveButtons().setCanMoveDown(mw.isBackwardOpen());
            vehicleButtonStates.getActiveButtons().setCanBuild(mw.isBuild());
            vehicleButtonStates.getActiveButtons().setCanDismantle(mw.isDismantle());
        }

    }

    @Background
    void turnAsync(long currId, byte direction) {
//        System.out.println("TankID: " +  user.getTankId() + " MinerID: " + user.getMinerId() + " CurrID: " + user.getCurrId());
        if (user.isBuilding) return;
        MoveWrapper mw = restClient.turn(currId, direction);
        if (mw != null && mw.isResult()) {
            if (currId == user.getTankId())
                user.setTankDirection(direction);
            else if (currId == user.getMinerId())
                user.setMinerDirection(direction);
            else
                user.setBuilderDirection(direction);
        }

        // Update buttons based on result
        if (mw != null) {
            vehicleButtonStates.getActiveButtons().setCanMoveUp(mw.isForwardOpen());
            vehicleButtonStates.getActiveButtons().setCanMoveDown(mw.isBackwardOpen());
            vehicleButtonStates.getActiveButtons().setCanBuild(mw.isBuild());
            vehicleButtonStates.getActiveButtons().setCanDismantle(mw.isDismantle());
        }
    }

    @Click(R.id.buttonEject)
    @Background
    protected void onButtonEject() {
        restClient.eject(user.getCurrId());
        getInventory();
    }

    @Click(R.id.buttonFire)
    @Background
    protected void onButtonFire() {
        if (user.getActiveUnit().equals("MINER")) {
            user.isMining = false;
            restClient.fire(user.getCurrId(), 0);
        } else if (user.getActiveUnit().equals("TANK")) {
            user.isMining = false;
            restClient.fire(user.getCurrId(), 1);
        }
        else if (user.getActiveUnit().equals("BUILDER")) {
            user.isMining = false;
            restClient.fire(user.getCurrId(), 2);
        }
    }

    @Background
    void shakeDetector() {
        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Runnable run = new Runnable() {
            @Override
            public void run() {
                // All we want it to do is fire
                onButtonFire();
            }
        };
        ShakeDetection.init(sm, run);
    }

    @Click(R.id.buttonMine)
    @Background
    protected void onButtonMine() {
        user.toggleMining();
        while (user.isMining) {
            BooleanWrapper bw = restClient.mine(user.getMinerId(), user.getMinerTerrain());
            getInventory();
        }
    }

    @Click(R.id.buttonBuild)
    protected void onButtonBuild() {
        // Initializing the popup menu and giving the reference as current context
        PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.buttonBuild));
        popupMenu.getMenuInflater().inflate(R.menu.client, popupMenu.getMenu());
        popupMenu.setForceShowIcon(true);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.pontoon:
                        buildAsync(1);
                        break;
                    case R.id.road:
                        buildAsync(2);
                        break;
                    case R.id.wood_wall:
                        buildAsync(3);
                        break;
                    case R.id.brick_wall:
                        buildAsync(4);
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
        runOnUiThread(new Runnable() { // Brings back miner controls. Should remove any other unit controls
            @Override
            public void run() {popupMenu.show();}
        });
    }

    @Background
    protected void buildAsync(long structureId) {
        user.isBuilding = true;
        BuildWrapper bw = restClient.build(user.getCurrId(), structureId);
        user.isBuilding = false;
        getInventory();

        if (bw != null) {
            vehicleButtonStates.getActiveButtons().setCanBuild(bw.isBuild());
            vehicleButtonStates.getActiveButtons().setCanDismantle(bw.isDismantle());
        }
    }

    @Click(R.id.buttonDismantle)
    @Background
    protected void onButtonDismantle() {
        user.isDismantling = true;
        BuildWrapper bw = restClient.dismantle(user.getCurrId());
        user.isDismantling = false;
//        Log.d("DISMANTLE RES", "DISMANTLE: " + bw.isDismantle() + "BUILD: " + bw.isBuild());
        getInventory();

        if (bw != null) {
            vehicleButtonStates.getActiveButtons().setCanBuild(bw.isBuild());
            vehicleButtonStates.getActiveButtons().setCanDismantle(bw.isDismantle());
        }
    }

    @Click({R.id.buttonSelectMiner, R.id.buttonSelectTank, R.id.buttonSelectBuilder})
    @Background
    protected void onButtonChangeUnit(View view) {
        final int viewId = view.getId();
        ImageButton minerButton = findViewById(R.id.buttonMine);
        ImageButton buildButton = findViewById(R.id.buttonBuild);
        ImageButton dismantleButton = findViewById(R.id.buttonDismantle);

        switch (viewId) {
            case R.id.buttonSelectMiner:
                user.setCurrId(user.getMinerId());
                vehicleButtonStates.setActiveButtons('M');
                break;
            case R.id.buttonSelectTank:
                user.setCurrId(user.getTankId());
                vehicleButtonStates.setActiveButtons('T');
                break;
            case R.id.buttonSelectBuilder:
                user.setCurrId(user.getBuilderId());
                vehicleButtonStates.setActiveButtons('B');
            default:
                break;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateButtons();
            }
        });

    }

    @Click(R.id.buttonLeave)
    @Background
    void leaveGame() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(ClientActivity.this);
                builder.setMessage("Are you sure you want to leave the game?");
                builder.setPositiveButton("Leave Game", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Leave game: User clicked "Leave Game"
                        leaveAsync(user.getUsername());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing: User clicked "Cancel"
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    void login() {
        Intent intent = new Intent(this, AuthenticateActivity_.class);
        startActivity(intent);
        vehicleButtonStates.rebootInstance();
        finish();
    }

    @Click(R.id.buttonReplay)
    @Background
    void addDevResources() {
        restClient.devAddResources(user.getUsername());
        getInventory();
    }

    void replay() {
        Intent replayIntent = new Intent(this, ReplayActivity_.class);
        startActivity(replayIntent);
    }

    @Background
    void leaveAsync(String username) {
        user.isMining = false;
        gridPollTask.stopPoll();
        BackgroundExecutor.cancelAll("grid_poller_task", true);
        restClient.leave(username);
        login();
    }

    public void updateButtons() {
        VehicleButtons vb = vehicleButtonStates.getActiveButtons();

        if (!user.isAlive()) {
            if (user.getActiveUnit().charAt(0) == vehicleButtonStates.getActiveVehicle()) {
                vb.setDead();
            }
        }

        if (!vb.canMoveUp) {
            ImageButton moveUp = findViewById(R.id.buttonUp);
            moveUp.setEnabled(false);
            moveUp.setColorFilter(Color.GRAY);
        }
        if (!vb.canMoveDown) {
            ImageButton moveDown = findViewById(R.id.buttonDown);
            moveDown.setEnabled(false);
            moveDown.setColorFilter(Color.GRAY);
        }
        if (!vb.canTurnLeft) {
            ImageButton turnLeft = findViewById(R.id.buttonLeft);
            turnLeft.setEnabled(false);
            turnLeft.setColorFilter(Color.GRAY);
        }
        if (!vb.canTurnRight) {
            ImageButton turnRight = findViewById(R.id.buttonRight);
            turnRight.setEnabled(false);
            turnRight.setColorFilter(Color.GRAY);
        }
        if (!vb.canFire) {
            ImageButton fire = findViewById(R.id.buttonFire);
            fire.setEnabled(false);
            fire.setColorFilter(Color.GRAY);
        }
        if (!vb.canEject) {
            ImageButton eject = findViewById(R.id.buttonEject);
            eject.setEnabled(false);
            eject.setColorFilter(Color.GRAY);
        }
        if (!vb.canMine) {
            ImageButton mine = findViewById(R.id.buttonMine);
            mine.setEnabled(false);
            mine.setColorFilter(Color.GRAY);
        }
        if (!vb.canBuild) {
            ImageButton build = findViewById(R.id.buttonBuild);
            build.setEnabled(false);
            build.setColorFilter(Color.GRAY);
        }
        if (!vb.canDismantle) {
            ImageButton dismantle = findViewById(R.id.buttonDismantle);
            dismantle.setEnabled(false);
            dismantle.setColorFilter(Color.GRAY);
        }
        if (!vb.canSelectTank) {
            ImageButton selectTank = findViewById(R.id.buttonSelectTank);
            selectTank.setEnabled(false);
            selectTank.setColorFilter(Color.GRAY);
        }
        if (!vb.canSelectBuilder) {
            ImageButton selectBuilder = findViewById(R.id.buttonSelectBuilder);
            selectBuilder.setEnabled(false);
            selectBuilder.setColorFilter(Color.GRAY);
        }
        if (!vb.canSelectMiner) {
            ImageButton selectMiner = findViewById(R.id.buttonSelectMiner);
            selectMiner.setEnabled(false);
            selectMiner.setColorFilter(Color.GRAY);
        }


        if (vb.canMoveUp) {
            ImageButton moveUp = findViewById(R.id.buttonUp);
            moveUp.setEnabled(true);
            moveUp.setColorFilter(null);
        }
        if (vb.canMoveDown) {
            ImageButton moveDown = findViewById(R.id.buttonDown);
            moveDown.setEnabled(true);
            moveDown.setColorFilter(null);
        }
        if (vb.canTurnLeft) {
            ImageButton turnLeft = findViewById(R.id.buttonLeft);
            turnLeft.setEnabled(true);
            turnLeft.setColorFilter(null);
        }
        if (vb.canTurnRight) {
            ImageButton turnRight = findViewById(R.id.buttonRight);
            turnRight.setEnabled(true);
            turnRight.setColorFilter(null);
        }
        if (vb.canFire) {
            ImageButton fire = findViewById(R.id.buttonFire);
            fire.setEnabled(true);
            fire.setColorFilter(null);
        }
        if (vb.canEject) {
            ImageButton eject = findViewById(R.id.buttonEject);
            eject.setEnabled(true);
            eject.setColorFilter(null);
        }
        if (vb.canMine) {
            ImageButton mine = findViewById(R.id.buttonMine);
            mine.setEnabled(true);
            mine.setColorFilter(null);
        }
        if (vb.canBuild) {
            ImageButton build = findViewById(R.id.buttonBuild);
            build.setEnabled(true);
            build.setColorFilter(null);
        }
        if (vb.canDismantle) {
            ImageButton dismantle = findViewById(R.id.buttonDismantle);
            dismantle.setEnabled(true);
            dismantle.setColorFilter(null);
        }
        if (vb.canSelectTank) {
            ImageButton selectTank = findViewById(R.id.buttonSelectTank);
            selectTank.setEnabled(true);
            selectTank.setColorFilter(null);
        }
        if (vb.canSelectBuilder) {
            ImageButton selectBuilder = findViewById(R.id.buttonSelectBuilder);
            selectBuilder.setEnabled(true);
            selectBuilder.setColorFilter(null);
        }
        if (vb.canSelectMiner) {
            ImageButton selectMiner = findViewById(R.id.buttonSelectMiner);
            selectMiner.setEnabled(true);
            selectMiner.setColorFilter(null);
        }
    }
}
