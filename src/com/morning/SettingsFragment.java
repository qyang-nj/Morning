package com.morning;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TimePicker;

import com.morning.data.AlarmDbHandler;
import com.morning.data.AlarmEntity;
import com.morning.data.ImageManager;

public class SettingsFragment extends Fragment {
    private TimePicker timePicker;
    private AlarmEntity alarm = null;
    private boolean isUpdate = false;
    private SettingsItemAdapter adapter;

    public void setDefaultAlarm(AlarmEntity alarm) {
	this.alarm = alarm;
	this.isUpdate = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	if (alarm == null) {
	    alarm = new AlarmEntity();
	}

	View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

	/* Buttons */
	Button button = (Button) rootView.findViewById(R.id.btnDone);
	button.setOnClickListener(new BtnDoneEvent());

	button = (Button) rootView.findViewById(R.id.btnCancel);
	button.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		getActivity().getFragmentManager().popBackStack();
	    }
	});

	/* Settings List */
	ListView listSettings = (ListView) rootView.findViewById(R.id.listSettingItem);
	adapter = new SettingsItemAdapter(getActivity(), alarm);
	listSettings.setAdapter(adapter);

	listSettings.setOnItemClickListener(new OnItemClickListener() {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		adapter.clickItem(parent, view, position, id);
	    }
	});

	/* Time picker */
	this.timePicker = (TimePicker) rootView.findViewById(R.id.timePicker);
	this.timePicker.setCurrentHour(alarm.getHour());
	this.timePicker.setCurrentMinute(alarm.getMinute());

	return rootView;
    }

    class BtnDoneEvent implements OnClickListener {
	@Override
	public void onClick(View view) {
	    alarm.setHour(timePicker.getCurrentHour());
	    alarm.setMinute(timePicker.getCurrentMinute());
	    alarm.setEnabled(true);
	    adapter.Sync();

	    if (isUpdate) {
		AlarmDbHandler.getInstance().updateAlarm(alarm);
	    } else { /* create new */
		AlarmDbHandler.getInstance().addAlarm(alarm);
	    }

	    AlarmServiceHelper.getInstance().updateAlert();
	    //ash.setAlarm(alarm);
	    //ash.updateAlert();
	    getActivity().getFragmentManager().popBackStack();

	    ImageManager.downloadImage(getActivity());
	}
    }
}
