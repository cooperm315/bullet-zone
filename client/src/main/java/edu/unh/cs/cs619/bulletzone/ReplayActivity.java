package edu.unh.cs.cs619.bulletzone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;

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

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.rest.GridPollerTask;
import edu.unh.cs.cs619.bulletzone.rest.GridUpdateEvent;
import edu.unh.cs.cs619.bulletzone.ui.GridAdapter;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;
import edu.unh.cs.cs619.bulletzone.util.SoundPlayer;

@EActivity(R.layout.activity_replay)
public class ReplayActivity extends AppCompatActivity {

    private static final String TAG = "ReplayActivity";

    SoundPlayer soundPlayer = new SoundPlayer(this);

    @Bean
    protected GridAdapter mGridAdapter;

    @ViewById
    protected GridView gridView;

    @Bean
    BusProvider busProvider;

    @NonConfigurationInstance
    @Bean
    GridPollerTask gridPollTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        soundPlayer.playSound("music", R.raw.settings_music, true);
        //Since we are using the @EActivity annotation, anything done past this point will
        //be overridden by the work AndroidAnnotations does. If you need to do more setup,
        //add to the methods under @AfterViews (for view items) or @AfterInject (for Bean items) below
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPlayer.stopAll();
//        busProvider.getEventBus().unregister(gridEventHandler);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try{
            soundPlayer.resumeAll();
        }
        catch (Exception e){
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        soundPlayer.pauseAll();
    }


    // TODO: Figure out why GridView crashes on startup here

//    private Object gridEventHandler = new Object()
//    {
//        @Subscribe
//        public void onUpdateGrid(GridUpdateEvent event) {
//            updateGrid(event.gw);
//        }
//    };

//    @AfterViews
//    protected void afterViewInjection() {
//        gridView.setAdapter(mGridAdapter);
//    }

//    @AfterInject
//    void afterInject() {
//        busProvider.getEventBus().register(gridEventHandler);
//    }

//    public void updateGrid(GridWrapper gw) {
//        mGridAdapter.updateList(gw.getGrid());
//    }

    @Click({R.id.button1x, R.id.button2x, R.id.button3x, R.id.button4x})
    protected void onButtonChangePlaybackSpeed(View view) {
        final int viewId = view.getId();

        switch (viewId) {
            case R.id.button1x: // 1x speed
                // todo: Attach functionality for playback speed change
                break;
            case R.id.button2x: // 2x speed
                // todo: Attach functionality for playback speed change
                break;
            case R.id.button3x: // 3x speed
                // todo: Attach functionality for playback speed change
                break;
            case R.id.button4x: // 4x speed
                // todo: Attach functionality for playback speed change
                break;
            default:
                // todo: Decide default case: I suggest 1x speed, report error
                break;
        }
    }

    @Click(R.id.buttonPlayPause)
    protected void togglePlaying() {
        // todo: Add functionality for playing/pausing the replay

    }

    @Click(R.id.buttonBack)
    protected void onReturnToGame() {
        finish();
    }

}
