package com.morning;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.morning.data.AlarmDbHandler;
import com.morning.data.AlarmEntity;

public class AlarmListCursorAdapter extends CursorAdapter {
	private LayoutInflater inflater;

	public AlarmListCursorAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public void bindView(View view, final Context context, Cursor cursor) {
		final AlarmEntity alarm = AlarmDbHandler.getAlarmFromCursor(cursor);
		view.setTag(alarm);

		TextView time = (TextView) view.findViewById(R.id.lblTime);
		time.setText(alarm.toString());

		TextView title = (TextView) view.findViewById(R.id.lblName);
		title.setText(alarm.getName());

		ImageView btnMore = (ImageView) view.findViewById(R.id.btnMore);
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
										AlarmDbHandler.getInstance().delAlarm(alarm);
										notifyDataSetChanged();
									}
								});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
		});

		if (alarm.isEnabled()) {
			view.setBackgroundColor(context.getResources().getColor(
					R.color.main_color));
		} else {
			view.setBackgroundDrawable(context.getResources()
					.getDrawable(R.drawable.itemborder));
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return inflater.inflate(R.layout.alarm_item, null);
	}
}
