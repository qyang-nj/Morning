package me.roovent.morning.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.roovent.morning.R;

/**
 * Created by qing on 1/24/15.
 */
public class AlarmSettingItem2Text extends AlarmSettingItem {

    private TextView tvExplanation;

    public AlarmSettingItem2Text(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SettingsItem, 0, 0);
        String explanation = a.getString(R.styleable.SettingsItem_explanation);
        a.recycle();

        setOrientation(LinearLayout.VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        tvExplanation = (TextView) inflater.inflate(R.layout.settings_explanation, null);
        tvExplanation.setText(explanation);

        this.addView(tvExplanation);
    }

    public AlarmSettingItem2Text(Context context) {
        this(context, null);
    }

    public void setExplanation(String explanation) {
        if (tvExplanation != null) {
            tvExplanation.setText(explanation);
        }
    }
}
