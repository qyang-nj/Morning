package com.morning;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Calendar;


public class AlarmRingingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ringing);

        TextView txtTime = (TextView) findViewById(R.id.txt_time);
        Calendar cal = Calendar.getInstance();
        txtTime.setText(DateFormat.format("hh:mm a", cal.getTime()));
    }
}
