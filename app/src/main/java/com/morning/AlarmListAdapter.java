package com.morning;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.morning.model.Alarm;

import java.util.List;

/**
 * Created by Qing on 1/25/15.
 */
public class AlarmListAdapter extends BaseAdapter {
    private List<Alarm> alarms;
    private Context mContext;

    public AlarmListAdapter(Context context, List<Alarm> alarms) {
        this.alarms = alarms;
        mContext = context;
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
        return alarms.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;

        if (convertView == null) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.alarm_item, parent, false);
            vh = new ViewHolder();
            vh.time = (TextView) v.findViewById(R.id.lblTime);
            vh.title = (TextView) v.findViewById(R.id.lblName);
            vh.btnMore = (ImageView) v.findViewById(R.id.btnMore);
            v.setTag(vh);
            convertView = v;
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final Alarm alarm = alarms.get(position);
        vh.time.setText(alarm.toString());
        vh.title.setText(alarm.name);

        vh.btnMore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

                // set dialog message
                alertDialogBuilder.setCancelable(true).setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(mContext, AlarmDetailActivity.class);
                        intent.putExtra(Alarm.KEY_ALARM_ID, alarm.id);
                        mContext.startActivity(intent);
                    }
                }).setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (mContext instanceof AlarmListActivity) {
                            AlarmListActivity activity = (AlarmListActivity) mContext;
                            activity.getHelper().getAlarmDao().delete(alarm);

                            alarms.remove(alarm);
                            notifyDataSetChanged();
                        }
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        if (alarm.enabled) {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.main_color));
        } else {
            convertView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.itemborder));
        }

        return convertView;
    }

    public void setAlarms(List<Alarm> alarms) {
        this.alarms = alarms;
    }

    private class ViewHolder {
        TextView time;
        TextView title;
        ImageView btnMore;
    }
}
