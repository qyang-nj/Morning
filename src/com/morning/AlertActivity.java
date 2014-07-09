package com.morning;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.morning.data.AlarmEntity;
import com.morning.data.ImageManager;

public class AlertActivity extends Activity {
	private AlarmEntity alarm = null;
	private Ringtone ringtone;

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
		findViewById(R.id.btn_off).setOnTouchListener(mDelayHideTouchListener);

		Uri uri = alarm.getRingtone() == null ? RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_ALARM) : Uri.parse(alarm
				.getRingtone());

		ringtone = new Ringtone(this, uri);
		ringtone.play();
	}

	@Override
	protected void onStop() {
		super.onPause();

		if (alarm.getRepeat() == 0) {
			alarm.setEnabled(false);
			alarm.commit();
		}
		ringtone.stop();
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			int viewId = view.getId();
			if (viewId == R.id.btn_snooze) {
				alarm.setSnooze(Constants.SNOOZE_TIME);
			} else if (viewId == R.id.btn_off) {

			}
			finish();
			return false;
		}
	};
}
