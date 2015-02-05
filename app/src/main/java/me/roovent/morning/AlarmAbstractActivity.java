package me.roovent.morning;

import android.app.ActionBar;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Window;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import me.roovent.morning.model.AlarmDbHelper;
import me.roovent.morning.ui.TypefaceSpan;

/**
 * Created by qing on 1/23/15.
 */
public class AlarmAbstractActivity extends OrmLiteBaseActivity<AlarmDbHelper> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        onCreate(savedInstanceState, getString(R.string.app_name));
    }

    protected void onCreate(Bundle savedInstanceState, String title) {
        super.onCreate(savedInstanceState);

        // TODO: Learn below line
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.show();

            // TODO: Learn this part of code.
            SpannableString s = new SpannableString(title);
            s.setSpan(new TypefaceSpan(this, "Amatic-Bold.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            actionBar.setTitle(s);
        }
    }
}
