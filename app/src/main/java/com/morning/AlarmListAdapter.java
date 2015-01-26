package com.morning;

import android.content.Context;
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

        Alarm alarm = alarms.get(position);
        vh.time.setText(alarm.toString());
        vh.title.setText(alarm.name);

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
