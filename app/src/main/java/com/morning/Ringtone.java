package com.morning;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;

public class Ringtone implements MediaPlayer.OnPreparedListener {
    private MediaPlayer audioPlayer;
    private boolean isPrepared = false;
    private boolean isPlayed = false;
    private boolean isRingtoneEnable = true; /* For test, set as false. */

    public Ringtone(Context context, Uri uri) {
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
        }
    }

    public void play() {
        if (!isRingtoneEnable || audioPlayer.isPlaying()) {
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
            isPrepared = false;
        }
    }
}
