package com.morning;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
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

		ImageView btnMore = (ImageView) convertView.findViewById(R.id.btnMore);
		btnMore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);

				// set dialog message
				alertDialogBuilder
						.setCancelable(true)
						.setPositiveButton("Edit",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										if (!(context instanceof Activity)) {
											return;
										}
										
										AlarmEntity alarm = alarms.get(position);

										FragmentTransaction transaction = ((Activity) context)
												.getFragmentManager()
												.beginTransaction();
										SettingsFragment fragment = new SettingsFragment();
										fragment.setDefaultAlarm(alarm);
										transaction.replace(R.id.container,
												fragment);
										transaction.addToBackStack("Settings");
										transaction.commit();
									}
								})
						.setNegativeButton("Delete",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// if this button is clicked, just close
										// the dialog box and do nothing
									}
								});

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();

			}
		});

		if (alarm.isActivated()) {
			convertView.setBackgroundColor(context.getResources().getColor(
					R.color.main_color));
		}
		return convertView;
	}
}
