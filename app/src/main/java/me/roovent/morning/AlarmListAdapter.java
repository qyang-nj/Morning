package me.roovent.morning;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.roovent.morning.model.Alarm;

/**
 * Created by Qing on 1/25/15.
 */
public class AlarmListAdapter extends BaseAdapter {
    private List<Alarm> mAlarms;
    private Context mContext;

    public AlarmListAdapter(Context context, List<Alarm> alarms) {
        this.mAlarms = alarms;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mAlarms.size();
    }

    @Override
    public Object getItem(int position) {
        return mAlarms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mAlarms.get(position).id;
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

        final Alarm alarm = mAlarms.get(position);
        vh.time.setText(alarm.getTimeString());
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

                            mAlarms.remove(alarm);
                            notifyDataSetChanged();
                            AlarmService.update(mContext, mAlarms);
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
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                convertView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.itemborder));
            } else {
                convertView.setBackground(mContext.getResources().getDrawable(R.drawable.itemborder));
            }
        }

        return convertView;
    }

    public void setAlarms(List<Alarm> alarms) {
        mAlarms = alarms;
    }

    private class ViewHolder {
        TextView time;
        TextView title;
        ImageView btnMore;
    }
}
