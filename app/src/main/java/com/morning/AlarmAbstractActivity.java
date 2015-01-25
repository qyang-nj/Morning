package com.morning;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Window;

import com.morning.ui.TypefaceSpan;

/**
 * Created by qing on 1/23/15.
 */
public class AlarmAbstractActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: Learn below line
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.show();

            // TODO: Learn this part of code.
            SpannableString s = new SpannableString(getResources().getString(R.string.app_name));
            s.setSpan(new TypefaceSpan(this, "Amatic-Bold.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            actionBar.setTitle(s);
        }
    }
}
