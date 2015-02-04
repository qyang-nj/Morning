package com.morning;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.morning.model.Alarm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AlarmListActivity extends AlarmAbstractActivity {
    private AlarmListAdapter mAdapter;
    private GridView mList;
    private List<Alarm> mAlarms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getClass().getName(), "SDK Version: " + android.os.Build.VERSION.SDK_INT);

        setContentView(R.layout.activity_alarm_list);

        mList = (GridView) findViewById(R.id.grid);
        mList.setEmptyView(findViewById(R.id.empty_list_view));
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAlarms = getHelper().getAlarmDao().queryForAll();

        if (mAdapter == null) {
            mAdapter = new AlarmListAdapter(this, mAlarms);
            mList.setAdapter(mAdapter);
            mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Alarm alarm = (Alarm) mAdapter.getItem(position);
                    alarm.enabled = !alarm.enabled;
                    getHelper().getAlarmDao().update(alarm);
                    if (alarm.enabled) {
                        Toast.makeText(AlarmListActivity.this, 
                                SimpleDateFormat.getInstance().format(new Date(alarm.getNextTime())), 
                                Toast.LENGTH_LONG).show();
                    }

                    mAdapter.notifyDataSetChanged();
                    AlarmService.update(AlarmListActivity.this, mAlarms);
                }
            });
        } else {
            mAdapter.setAlarms(mAlarms);
            mAdapter.notifyDataSetChanged();
        }

        AlarmService.update(this, mAlarms);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.alarm_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            Intent intent = new Intent(this, AlarmDetailActivity.class);
            intent.putExtra(Alarm.KEY_ALARM_ID, -1);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_test) {
            Intent intent = new Intent(this, AlarmRingingActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
