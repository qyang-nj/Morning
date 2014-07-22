package com.morning;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

public class Ringtone implements MediaPlayer.OnPreparedListener {
    private AudioManager audioManager;
    private MediaPlayer audioPlayer;
    private int userVolume;
    private boolean isPrepared = false;
    private boolean isPlayed = false;
    private int streamType;
    private boolean isRingtoneEnable = false; /* For test, set as false. */

    public Ringtone(Context context, Uri uri) {    
        streamType = AudioManager.STREAM_ALARM;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        
        userVolume = audioManager.getStreamVolume(streamType);
        audioManager.setStreamVolume(streamType, audioManager.getStreamMaxVolume(streamType),
                AudioManager.FLAG_PLAY_SOUND);

        audioPlayer = new MediaPlayer();
        try {
            audioPlayer.setDataSource(context, uri);
        } catch (IllegalArgumentException e) {
            Log.e(Constants.TAG, e.getMessage());
        } catch (SecurityException e) {
            Log.e(Constants.TAG, e.getMessage());
        } catch (IllegalStateException e) {
            Log.e(Constants.TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(Constants.TAG, e.getMessage());
        }
        audioPlayer.setAudioStreamType(streamType);
        audioPlayer.setLooping(true);
        audioPlayer.setOnPreparedListener(this);
        audioPlayer.prepareAsync();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        isPrepared = true;
        if (isPlayed) {
            audioPlayer.start();
        }
    }

    public void play() {
        if (!isRingtoneEnable) {
            return;
        }

        if (isPrepared) {
            audioPlayer.start();
        } else {
            isPlayed = true;
        }
    }

    public void stop() {
        if (audioPlayer != null && audioPlayer.isPlaying()) {
            audioPlayer.stop();
            audioPlayer.reset();
            audioManager.setStreamVolume(streamType, userVolume, AudioManager.FLAG_PLAY_SOUND);
            isPrepared = false;
        }
    }
}
