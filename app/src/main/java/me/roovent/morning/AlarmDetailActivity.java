package me.roovent.morning;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.commons.io.FilenameUtils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;

import me.roovent.morning.model.Alarm;
import me.roovent.morning.model.RepeatOption;
import me.roovent.morning.ui.AlarmSettingItem2Text;


public class AlarmDetailActivity extends AlarmAbstractActivity {
    private static final int REQUEST_SELECT_RINGTONE = 5;

    private Alarm mAlarm;

    private TimePicker mTimePicker;
    private EditText mEtName;
    private AlarmSettingItem2Text mSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_detail);

        Intent intent = getIntent();
        int alarmId = intent.getIntExtra(Alarm.KEY_ALARM_ID, -1);
        if (alarmId < 0) {
            mAlarm = new Alarm();
            mAlarm.ringtone = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM).toString();
        } else {
            mAlarm = getHelper().getAlarmDao().queryForId(alarmId);
        }

        mTimePicker = (TimePicker) findViewById(R.id.timePicker);
        mTimePicker.setCurrentHour(mAlarm.hour);
        mTimePicker.setCurrentMinute(mAlarm.minute);

        mEtName = (EditText) findViewById(R.id.name);
        mEtName.setText(mAlarm.name);

        final AlarmSettingItem2Text itemSound = (AlarmSettingItem2Text) findViewById(R.id.itemSound);
        final Uri u = (mAlarm.ringtone == null ? null : Uri.parse(mAlarm.ringtone));
        itemSound.setExplanation(RingtoneUtils.getRingtoneName(this, u));
        itemSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getResources().getString(R.string.ringtones));
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, u);
                startActivityForResult(intent, REQUEST_SELECT_RINGTONE);
            }
        });
        mSound = itemSound;

        final AlarmSettingItem2Text itemRepeat = (AlarmSettingItem2Text) findViewById(R.id.itemRepeat);
        itemRepeat.setExplanation(RepeatOption.formatSet(RepeatOption.getSetFromValue(mAlarm.repeat), this));
        itemRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RepeatDialogFragment repeatDialog = new RepeatDialogFragment();
                repeatDialog.setRepeat(RepeatOption.getSetFromValue(mAlarm.repeat));
                repeatDialog.setNoticeListener(new RepeatDialogFragment.NoticeListener() {
                    @Override
                    public void onDialogPositiveClick(EnumSet<RepeatOption> repeats) {
                        mAlarm.repeat = RepeatOption.getValueFromSet(repeats);
                        itemRepeat.setExplanation(RepeatOption.formatSet(repeats, AlarmDetailActivity.this));
                    }
                });
                repeatDialog.show(getFragmentManager(), "repeat");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.alarm_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_done) {
            mAlarm.hour = mTimePicker.getCurrentHour();
            mAlarm.minute = mTimePicker.getCurrentMinute();
            mAlarm.name = mEtName.getText().toString();
            mAlarm.enabled = true;

            getHelper().getAlarmDao().createOrUpdate(mAlarm);
            Toast.makeText(this, SimpleDateFormat.getInstance().format(new Date(mAlarm.getNextTime())), Toast.LENGTH_LONG).show();

            finish();
            return true;
        } else if (id == R.id.action_cancel) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SELECT_RINGTONE) {
                Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                Log.d(getClass().getName(), "Selected ringtone: " + uri);
                if (uri == null) { /* Select None or Silent */
                    mAlarm.ringtone = null;
                    mSound.setExplanation(getString(R.string.ringtone_none));
                } else {
                    if (RingtoneUtils.isValidated(this, uri)) {
                        mAlarm.ringtone = uri.toString();
                        mSound.setExplanation(RingtoneUtils.getRingtoneName(this, uri));
                    } else {
                        Toast.makeText(this, getString(R.string.warning_invalid_ringtone), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private static class RingtoneUtils {
        static private String[] validatedExtList = {"3gp", "mp3", "ogg", "wav"};

        static public boolean isValidated(Context context, Uri uri) {
            boolean validated;

            if (uri == null) {
                validated = false;
            } else {
                android.media.Ringtone rt = RingtoneManager.getRingtone(context, uri);
                if (rt == null) {
                    if ("file".equals(uri.getScheme())) { /* file:// */
                        String path = uri.getPath();
                        String ext = FilenameUtils.getExtension(path);
                        validated = Arrays.asList(validatedExtList).contains(ext);
                    } else if ("content".equals(uri.getScheme())) { /* content:// */
                        validated = true;
                    } else { /* neither 'file://' nor content '://' */
                        validated = false;
                    }
                } else { /* is a Ringtone */
                    validated = true;
                }
            }

            return validated;
        }

        static public String getRingtoneName(Context context, Uri uri) {
            String name;
            if (uri == null) {
                name = context.getString(R.string.ringtone_none);
            } else {
                android.media.Ringtone rt = RingtoneManager.getRingtone(context, uri);
                if (rt == null) {
                    if ("file".equals(uri.getScheme())) { /* file:// */
                        String path = uri.getPath();
                        name = FilenameUtils.getBaseName(path);
                    } else if ("content".equals(uri.getScheme())) { /* content:// */
                        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
                        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (cursor.moveToFirst()) {
                            name = cursor.getString(nameIndex);
                        } else {
                            name = "";
                            Log.e(RingtoneUtils.class.getName(), "Get ringtone name fails: " + uri);
                        }
                    } else { /* neither 'file://' nor 'content://' */
                        name = "";
                        Log.e(RingtoneUtils.class.getName(), "Get ringtone name fails: " + uri);
                    }
                } else {
                    name = rt.getTitle(context);
                }
            }

            return name;
        }
    }
}
