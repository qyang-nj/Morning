package com.morning;

import java.io.IOException;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.morning.data.AlarmEntity;
import com.morning.data.ImageManager;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class AlertActivity extends Activity {
	private AlarmEntity alarm = null;
	private MediaPlayer ringtonePlayer = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_alert);
		getActionBar().hide();
		
		Intent in = getIntent();
		alarm = in.getParcelableExtra(Constants.INTEND_KEY_ALARM);
		assert alarm != null;

		TextView lblCurrentTime = (TextView) findViewById(R.id.lblCurrentTime);
		Calendar cal = Calendar.getInstance();
		lblCurrentTime.setText(DateFormat.format("hh:mm a", cal.getTime()));

		ImageView imgShown = (ImageView) findViewById(R.id.imgShown);
		imgShown.setImageBitmap(ImageManager.getRandomImage());

		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		findViewById(R.id.btn_snooze).setOnTouchListener(
				mDelayHideTouchListener);
		findViewById(R.id.btn_off).setOnTouchListener(
				mDelayHideTouchListener);

		Uri uri = alarm.getRingtone() == null ? RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_ALARM) : Uri.parse(alarm
				.getRingtone());

		try {
			MediaPlayer player = new MediaPlayer();
			player.setDataSource(this, uri);
			final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
				player.setAudioStreamType(AudioManager.STREAM_RING);
				player.setLooping(true);
				player.prepare();
				player.start();
			}
			ringtonePlayer = player;
		} catch (IllegalArgumentException e) {
			Log.e(Constants.TAG, e.getMessage());
		} catch (SecurityException e) {
			Log.e(Constants.TAG, e.getMessage());
		} catch (IllegalStateException e) {
			Log.e(Constants.TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(Constants.TAG, e.getMessage());
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (ringtonePlayer.isPlaying()) {
			ringtonePlayer.stop();
		}
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (ringtonePlayer.isPlaying()) {
				ringtonePlayer.stop();
			}
			finish();
			return false;
		}
	};
}
