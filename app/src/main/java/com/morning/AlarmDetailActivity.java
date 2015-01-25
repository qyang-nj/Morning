package com.morning;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.morning.data.RepeatOption;
import com.morning.ui.AlarmSettingItem2Text;

import java.util.EnumSet;


public class AlarmDetailActivity extends AlarmAbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_detail);
        
        final AlarmSettingItem2Text itemSound = (AlarmSettingItem2Text) findViewById(R.id.itemSound);
        itemSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        final AlarmSettingItem2Text itemRepeat = (AlarmSettingItem2Text) findViewById(R.id.itemRepeat);
        itemRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RepeatDialogFragment repeatDialog = new RepeatDialogFragment();
                //repeatDialog.setRepeat(RepeatOption.getSetFromValue(alarm.getRepeat()));
                repeatDialog.setNoticeListener(new RepeatDialogFragment.NoticeListener() {
                    @Override
                    public void onDialogPositiveClick(EnumSet<RepeatOption> repeats) {
                        //alarm.setRepeat(RepeatOption.getValueFromSet(repeats));
                        //TextView tv = (TextView) view.findViewById(R.id.lblContent);
                        itemRepeat.setExplanation(RepeatOption.formatSet(RepeatOption.getSetFromValue(RepeatOption.getValueFromSet(repeats))));
                    }
                });
                repeatDialog.show(getFragmentManager(), "repeat");
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.alarm_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_done) {
            return true;
        } else if (id == R.id.action_cancel) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
