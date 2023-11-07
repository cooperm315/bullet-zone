package edu.unh.cs.cs619.bulletzone;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import edu.unh.cs.cs619.bulletzone.model.GameUser;
import edu.unh.cs.cs619.bulletzone.util.SoundPlayer;

@EActivity(R.layout.activity_main_menu)
public class MainMenuActivity extends AppCompatActivity {
    GameUser user;

    @ViewById
    TextView status_message;

    @Bean
    AuthenticationController controller;

    SoundPlayer soundPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Since we are using the @EActivity annotation, anything done past this point will
        //be overridden by the work AndroidAnnotations does. If you need to do more setup,
        //add to the methods under @AfterViews (for view items) or @AfterInject (for Bean items) below
        soundPlayer = new SoundPlayer(this);
        soundPlayer.playSound("music", R.raw.menu_music, true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            soundPlayer.resumeAll();
        } catch (Exception e) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        soundPlayer.pauseAll();
    }

    @AfterViews
    protected void afterViewInjection() {
        //Put any view-setup code here (that you might normally put in onCreate)
    }

    @AfterInject
    void afterInject() {
        //Put any Bean-related setup code here (the you might normally put in onCreate)
        user = GameUser.getInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPlayer.stopAll();
    }

    /**
     * Logs in an existing user
     */
    @Click(R.id.button_login)
    @Background
    protected void onButtonLogin() {
        Intent intent = new Intent(MainMenuActivity.this, AuthenticateActivity_.class);
        startActivity(intent);
        finish();
    }
}