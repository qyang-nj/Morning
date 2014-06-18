package com.qyang;

import java.util.List;
import java.util.Random;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter {
	private List<AlarmEntity> alarms;
	private Context context;

	public ListAdapter(Context context, List<AlarmEntity> alarms) {
		this.context = context;
		this.alarms = alarms;
	}

	@Override
	public int getCount() {
		return alarms.size();
	}

	@Override
	public Object getItem(int position) {
		return alarms.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AlarmEntity alarm = alarms.get(position);

		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.alarm_item, null);
		}
		
		convertView.setTag(alarm);

		TextView time = (TextView) convertView.findViewById(R.id.lblTime);
		time.setText(alarm.toString());

		TextView title = (TextView) convertView.findViewById(R.id.lblName);
		title.setText(alarm.getName());

		//Switch onOff = (Switch) convertView.findViewById(R.id.togActivate);
		//onOff.setChecked(alarm.isActivated());
		if (alarm.isActivated()) {
			convertView.setBackgroundColor(context.getResources().getColor(R.color.main_color));
		}
		return convertView;
	}
}
