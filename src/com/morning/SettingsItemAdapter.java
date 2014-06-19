package com.morning;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.morning.RepeatDialogFragment.NoticeListener;
import com.qyang.R;

public class SettingsItemAdapter extends BaseAdapter {

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

	private Activity context;
	private AlarmEntity alarm;
	private List<Item> items;
	private int layouts[] = new int[] { R.layout.settings_item_1text1edit,
			R.layout.settings_item_2text };

	/* Constructor */
	public SettingsItemAdapter(final Activity context, final AlarmEntity alarm) {
		this.context = context;
		this.alarm = alarm;

		/* NAME */
		Item itName = new Item();
		itName.title = "NAME";
		itName.content = alarm.getName();
		itName.layoutIndex = 0;

		/* SOUND */
		Item itSound = new Item();
		itSound.title = "SOUND";
		itSound.content = alarm.getSound();
		itSound.layoutIndex = 1;

		/* REPEAT */
		Item itRepeat = new Item();
		itRepeat.title = "REPEAT";
		itRepeat.content = RepeatOption.set2String(RepeatOption.val2Set(alarm
				.getRepeat()));
		itRepeat.layoutIndex = 1;
		itRepeat.click = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				RepeatDialogFragment repeatDialog = new RepeatDialogFragment();
				repeatDialog.setRepeat(RepeatOption.val2Set(alarm.getRepeat()));
				repeatDialog.setNoticeListener(new NoticeListener() {
					@Override
					public void onDialogPositiveClick(
							EnumSet<RepeatOption> repeats) {
						alarm.setRepeat(RepeatOption.set2Val(repeats));
						TextView tv = (TextView) view
								.findViewById(R.id.lblContent);
						tv.setText(RepeatOption.set2String(RepeatOption
								.val2Set(alarm.getRepeat())));
					}
				});
				repeatDialog.show(context.getFragmentManager(), "repeat");
			}
		};

		this.items = Arrays.asList(itName, itSound, itRepeat);
	}

	public void clickItem(AdapterView<?> parent, View view, int position,
			long id) {
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
			
			holder.txtTitle = (TextView) convertView
					.findViewById(R.id.lblTitle);
			holder.txtContent = (TextView) convertView
					.findViewById(R.id.lblContent);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.txtTitle.setText(item.title);
		holder.txtContent.setText(item.content);
		
		if (holder.txtContent instanceof EditText) {
			holder.txtContent.setOnFocusChangeListener(new OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus){
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
}
