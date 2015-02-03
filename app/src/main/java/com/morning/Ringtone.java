package com.morning;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;

public class Ringtone implements MediaPlayer.OnPreparedListener {
    private static final String TAG = Ringtone.class.getName();

    private MediaPlayer audioPlayer;
    private boolean isPrepared = false;
    private boolean isPlayed = false;

    public Ringtone(Context context, Uri uri) {
        audioPlayer = new MediaPlayer();
        try {
            audioPlayer.setDataSource(context, uri);
        } catch (IllegalArgumentException e) {
            Log.e(getClass().getName(), e.getMessage());
        } catch (SecurityException e) {
            Log.e(getClass().getName(), e.getMessage());
        } catch (IllegalStateException e) {
            Log.e(getClass().getName(), e.getMessage());
        } catch (IOException e) {
            Log.e(getClass().getName(), e.getMessage());
        }
        audioPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        audioPlayer.setLooping(true);
        audioPlayer.setOnPreparedListener(this);
        audioPlayer.prepareAsync();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        isPrepared = true;
        if (isPlayed) {
            audioPlayer.start();
            Log.d(TAG, "[Ringtone starts]");
        }
    }

    public void play() {
        if (audioPlayer.isPlaying()) {
            return;
        }

        if (isPrepared) {
            audioPlayer.start();
            Log.d(TAG, "[Ringtone starts]");
        } else {
            isPlayed = true;
        }
    }

    public void stop() {
        if (audioPlayer != null && audioPlayer.isPlaying()) {
            audioPlayer.stop();
            audioPlayer.reset();
            isPrepared = false;
            Log.d(TAG, "[Ringtone stops]");
        }
    }
}
