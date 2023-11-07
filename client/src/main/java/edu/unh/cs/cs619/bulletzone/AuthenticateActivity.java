package edu.unh.cs.cs619.bulletzone;

import android.content.Intent;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
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

@EActivity(R.layout.activity_authenticate)
public class AuthenticateActivity extends AppCompatActivity {
    GameUser user;
    @ViewById
    EditText username_editText;

    @ViewById
    EditText password_editText;

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
        soundPlayer.playSound("music" ,R.raw.settings_music, true);


        // Handle back button
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(AuthenticateActivity.this, MainMenuActivity_.class);
                startActivity(intent);
                finish();
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
     * Registers a new user and logs them in
     */
    @Click(R.id.register_button)
    @Background
    protected void onButtonRegister() {
        String username = username_editText.getText().toString();
        String password = password_editText.getText().toString();

        // empty checking
        if (username.isEmpty() || password.isEmpty()) {
            setStatus("Username and password cannot be empty.");
            return;
        }

        boolean status = controller.register(username, password);
        if (!status) {
            setStatus("User " + username + " already exists or server error.\nPlease login or try with a different username.");
        } else { //register successful
            // automatically login user after registration
            long userID = (controller.login(username, password));
            if (userID < 0) {
                setStatus("Registration unsuccessful--inconsistency with server.");
            }
            else {
                user.setUsername(username);
                Intent intent = new Intent(AuthenticateActivity.this, ClientActivity_.class);
                startActivity(intent);
                finish();
            }
        }
    }

    /**
     * Logs in an existing user
     */
    @Click(R.id.login_button)
    @Background
    protected void onButtonLogin() {
        String username = username_editText.getText().toString();
        String password = password_editText.getText().toString();

        // empty checking
        if (username.isEmpty() || password.isEmpty()) {
            setStatus("Username and password cannot be empty.");
            return;
        }
        long userID = (controller.login(username, password));
        if (userID < 0) {
            setStatus("Invalid username and/or password.\nPlease try again.");
        } else { // login successful
            user.setUsername(username);
            Intent intent = new Intent(AuthenticateActivity.this, ClientActivity_.class);
            startActivity(intent);
            finish();
        }
    }

    @UiThread
    protected void setStatus(String message) {
        status_message.setText(message);
    }


}