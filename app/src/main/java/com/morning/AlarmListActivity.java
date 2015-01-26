package com.morning;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.morning.model.Alarm;

public class AlarmListActivity extends AlarmAbstractActivity {
    private AlarmListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, "SDK Version: " + android.os.Build.VERSION.SDK_INT);

        setContentView(R.layout.activity_alarm_list);

        final AlarmListAdapter adapter = new AlarmListAdapter(this, getHelper().getAlarmDao().queryForAll());
        mAdapter = adapter;

        GridView list = (GridView) findViewById(R.id.grid);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Alarm alarm = (Alarm) mAdapter.getItem(position);
                alarm.enabled = !alarm.enabled;
                getHelper().getAlarmDao().update(alarm);

                mAdapter.notifyDataSetChanged();
            }
        });
        list.setEmptyView(findViewById(R.id.empty_list_view));
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
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
