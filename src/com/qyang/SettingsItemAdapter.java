package com.qyang;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingsItemAdapter extends BaseAdapter {
	private Context context;
	private Object[][] items;

	public SettingsItemAdapter(Context context) {
		this.context = context;
		this.items = new Object[][] { { "NAME", new EditText(context) },
				{ "SOUND", new TextView(context) },
				{ "REPEAT", new TextView(context) }, };
	}

	@Override
	public int getCount() {
		return items.length;
	}

	@Override
	public Object getItem(int position) {
		return items[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.settings_item, null);
			
			TextView title = (TextView) convertView.findViewById(R.id.lblTitle);
			title.setText(items[position][0].toString());
			
			((LinearLayout) convertView).addView((TextView) items[position][1]);
		}

		return convertView;
	}
}
