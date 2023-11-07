package edu.unh.cs.cs619.bulletzone.util;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Plays sounds
 * TODO: Might need to make this able to play multiple sounds at once. Like have a list of MediaPlayers (id is index) and can control them individually.
 */
public class SoundPlayer {
    // ConcurrentHashMap (thread safe) of <name, MediaPlayer>
    ConcurrentHashMap<String, MediaPlayer> mediaPlayers = new ConcurrentHashMap<>();
    MediaPlayer mediaPlayer;
    Context context;

    public SoundPlayer(Context context) {
        this.context = context;
    }

    /**
     * Plays a sound
     * If the sound is already playing, it will stop and play the new sound
     *
     * Remember to call stopSound() when done the app
     *
     * @param name Unique id for MediaPlayer
     *             Allows for multiple sounds to be played at once on different MediaPlayers
     * @param resourceID Resource ID of the sound
     * @param loop Whether or not to loop the sound
     */
    public void playSound(String name, int resourceID, boolean loop) {
        if (mediaPlayers.containsKey(name)) {
            mediaPlayer = mediaPlayers.get(name);
        } else {
            mediaPlayer = MediaPlayer.create(context, resourceID);
            mediaPlayers.put(name, mediaPlayer);
        }
        if (loop) {
            mediaPlayer.setLooping(true);
        }

        //if is playing, stop and reset
        if (mediaPlayer.isPlaying()) {
            stopSound(name);
            playSound(name, resourceID, loop);
        }
        mediaPlayer.start();
    }

    /**
     * Pauses the sound
     */
    public void pauseAll() {
        for (String name : mediaPlayers.keySet()) {
            mediaPlayer = mediaPlayers.get(name);
            mediaPlayer.pause();
        }
    }

    /**
     * Resumes all sounds
     */
    public void resumeAll() {
        for (String name : mediaPlayers.keySet()) {
            mediaPlayer = mediaPlayers.get(name);
            mediaPlayer.start();
        }
    }

    /**
     * Stops the sound
     *
     * @param name Unique id for MediaPlayer
     */
    public void stopSound(String name) {
        if (mediaPlayers.containsKey(name)) {
            mediaPlayer = mediaPlayers.get(name);
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayers.remove(name);
        }
    }

    /**
     * Stops all sounds
     */
    public void stopAll() {
        for (String name : mediaPlayers.keySet()) {
            stopSound(name);
        }
    }
}
