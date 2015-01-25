package com.morning.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.morning.R;

/**
 * Created by qing on 1/24/15.
 */
public class AlarmSettingItem extends LinearLayout {
    private TextView tvCaption;

    public AlarmSettingItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SettingsItem, 0, 0);
        String caption = a.getString(R.styleable.SettingsItem_caption);
        a.recycle();

        setOrientation(LinearLayout.VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.settings_item, this, true);

        //TODO: Move to global
        int dp = (int)(10 * context.getResources().getDisplayMetrics().density);
        
        this.setPadding(dp, dp, dp, dp);

        tvCaption = (TextView) findViewById(R.id.caption);
        tvCaption.setText(caption);

        this.setBackground(getResources().getDrawable(R.drawable.setting_item_backgroud));
    }

    public AlarmSettingItem(Context context) {
        this(context, null);
    }

    public void setCaption(String caption) {
        if (tvCaption != null) {
            tvCaption.setText(caption);
        }
    }
}
