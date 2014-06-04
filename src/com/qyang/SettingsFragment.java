package com.qyang;

import java.util.EnumSet;

import com.qyang.RepeatDialogFragment.NoticeListener;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

public class SettingsFragment extends Fragment {
	private TimePicker timePicker;
	private EditText txtbxName;
	private AlarmEntity alarm = null;
	private boolean isUpdate = false;

	public void setDefaultAlarm(AlarmEntity alarm) {
		this.alarm = alarm;
		this.isUpdate = true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (alarm == null) {
			alarm = new AlarmEntity();
		}

		View rootView = inflater.inflate(R.layout.fragment_settings, container,
				false);

		Button button = (Button) rootView.findViewById(R.id.btnDone);
		button.setOnClickListener(new BtnDoneEvent());

		button = (Button) rootView.findViewById(R.id.btnCancel);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), TimeOutActivity.class);
				getActivity().startActivity(intent);
			}
		});

		button = (Button) rootView.findViewById(R.id.btnSetRepeat);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				RepeatDialogFragment repeatDialog = new RepeatDialogFragment();
				repeatDialog.setRepeat(RepeatOption.val2Set(alarm.getRepeat()));
				repeatDialog.setNoticeListener(new NoticeListener() {
					@Override
					public void onDialogPositiveClick(EnumSet<RepeatOption> repeats) {
						alarm.setRepeat(RepeatOption.set2Val(repeats));
					}
				});
				repeatDialog.show(getFragmentManager(), "repeat");
			}
		});

		this.timePicker = (TimePicker) rootView.findViewById(R.id.timePicker);
		this.timePicker.setCurrentHour(alarm.getHour());
		this.timePicker.setCurrentMinute(alarm.getMinute());

		this.txtbxName = (EditText) rootView.findViewById(R.id.txtbxName);
		this.txtbxName.setText(alarm.getName());

		return rootView;
	}

	class BtnDoneEvent implements OnClickListener {
		@Override
		public void onClick(View view) {
			alarm.setName(txtbxName.getEditableText().toString());
			
			if (isUpdate) {
				new DbHandler(getActivity()).updateAlarm(alarm);
			} else { /* create new */
				new DbHandler(getActivity()).addAlarm(alarm);
			}
			
			AlarmServiceHelper ash = new AlarmServiceHelper(getActivity());
			ash.setAlarm(alarm.getHour(), alarm.getMinute());
			getActivity().getFragmentManager().popBackStack();
		}
	}
}
