package com.morning;

import android.app.Activity;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.morning.MainActivity.Callback;
import com.morning.RepeatDialogFragment.NoticeListener;
import com.morning.data.AlarmEntity;
import com.morning.data.RepeatOption;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class SettingsItemAdapter extends BaseAdapter {

    private Activity context;
    private AlarmEntity alarm;
    private List<Item> items;
    private int layouts[] = new int[]{R.layout.settings_item_1text1edit, R.layout.settings_item_2text};
    /* Constructor */
    public SettingsItemAdapter(final Activity context, final AlarmEntity alarm) {
        this.context = context;
        this.alarm = alarm;

	/* NAME */
        Item itName = new Item();
        itName.title = context.getResources().getString(R.string.name);
        itName.content = alarm.getName();
        itName.layoutIndex = 0;

	/* RINGTONE */
        Item itRingtone = new Item();
        itRingtone.title = "SOUND";
        Uri u = alarm.getRingtone() == null ? null : Uri.parse(alarm.getRingtone());
        itRingtone.content = u == null ? "" : RingtoneManager.getRingtone(context, u).getTitle(context);
        itRingtone.layoutIndex = 1;
        itRingtone.click = new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                if (!(context instanceof MainActivity)) {
                    return;
                }
                MainActivity activity = (MainActivity) context;
                activity.selectRingtone(new Callback() {
                    @Override
                    public void callback(Object... objects) {
                        Uri uri = (Uri) objects[0];
                        alarm.setRingtone(uri.toString());

                        Ringtone rt = RingtoneManager.getRingtone(context, uri);
                        TextView tv = (TextView) view.findViewById(R.id.lblContent);
                        tv.setText(rt.getTitle(context));
                    }
                });
            }
        };

	/* REPEAT */
        Item itRepeat = new Item();
        itRepeat.title = "REPEAT";
        itRepeat.content = RepeatOption.formatSet(RepeatOption.getSetFromValue(alarm.getRepeat()));
        itRepeat.layoutIndex = 1;
        itRepeat.click = new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                RepeatDialogFragment repeatDialog = new RepeatDialogFragment();
                repeatDialog.setRepeat(RepeatOption.getSetFromValue(alarm.getRepeat()));
                repeatDialog.setNoticeListener(new NoticeListener() {
                    @Override
                    public void onDialogPositiveClick(EnumSet<RepeatOption> repeats) {
                        alarm.setRepeat(RepeatOption.getValueFromSet(repeats));
                        TextView tv = (TextView) view.findViewById(R.id.lblContent);
                        tv.setText(RepeatOption.formatSet(RepeatOption.getSetFromValue(alarm.getRepeat())));
                    }
                });
                repeatDialog.show(context.getFragmentManager(), "repeat");
            }
        };

        this.items = Arrays.asList(itName, itRingtone, itRepeat);
    }

    public void clickItem(AdapterView<?> parent, View view, int position, long id) {
        OnItemClickListener click = items.get(position).click;
        if (click != null) {
            click.onItemClick(parent, view, position, id);
        }
    }

    public void Sync() {
        Item itName = items.get(0);
        alarm.setName(itName.content);
    }

    @Override
    public int getCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Item item = items.get(position);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(layouts[item.layoutIndex], null);

            holder.txtTitle = (TextView) convertView.findViewById(R.id.lblTitle);
            holder.txtContent = (TextView) convertView.findViewById(R.id.lblContent);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtTitle.setText(item.title);
        holder.txtContent.setText(item.content);

        if (holder.txtContent instanceof EditText) {
            holder.txtContent.setOnFocusChangeListener(new OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        item.content = ((EditText) v).getText().toString();
                    }
                }
            });
        }

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).layoutIndex;
    }

    class Item {
        String title;
        String content;
        int layoutIndex;
        OnItemClickListener click;
    }

    class ViewHolder {
        TextView txtTitle;
        TextView txtContent;
    }
}
