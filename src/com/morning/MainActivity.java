package com.morning;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;

import com.morning.data.AlarmEntityManager;
import com.morning.ui.TypefaceSpan;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new ListFragment()).commit();
		}
		
		//TODO: Learn this part of code.
		SpannableString s = new SpannableString(getResources().getString(R.string.app_name));
		s.setSpan(new TypefaceSpan(this, "Amatic-Bold.ttf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		ActionBar actionBar = getActionBar();
		actionBar.setTitle(s);
		
		AlarmEntityManager.init(this);
	}
}
