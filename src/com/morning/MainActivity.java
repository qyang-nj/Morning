package com.morning;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Window;

import com.morning.data.AlarmDbHandler;
import com.morning.data.ImageManager;
import com.morning.ui.TypefaceSpan;

public class MainActivity extends Activity {
	
	public static interface Callback {
		void callback(Object...objects );
	}
	
	private Callback selectRingtoneCallback;
	
	public void selectRingtone(Callback cb) {
		selectRingtoneCallback = cb;
		Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getResources().getString(R.string.ringtones));
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
		startActivityForResult(intent, Constants.REQUEST_SELET_RINGTONE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//TODO: Learn below line
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		
		setContentView(R.layout.activity_main);
		ActionBar actionBar = getActionBar();
		actionBar.show();

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new ListFragment()).commit();
		}

		// TODO: Learn this part of code.
		SpannableString s = new SpannableString(getResources().getString(
				R.string.app_name));
		s.setSpan(new TypefaceSpan(this, "Amatic-Bold.ttf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		actionBar.setTitle(s);

		AlarmDbHandler.init(this);
		ImageManager.downloadImage(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == Constants.REQUEST_SELET_RINGTONE) {
				Uri uri = intent
						.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

				if (selectRingtoneCallback != null) {
					selectRingtoneCallback.callback(uri);
				}
			}
		}
	}
}
