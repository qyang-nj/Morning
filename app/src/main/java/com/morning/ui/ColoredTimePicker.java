package com.morning.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.morning.R;

import java.lang.reflect.Field;

/**
 * Created by Qing Yang on 1/29/15.
 */
public class ColoredTimePicker extends TimePicker {
    
    String fields[] = new String[] {"mHourSpinner", "mMinuteSpinner", "mAmPmSpinner"};

    public ColoredTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        Field selectionDivider = null;
        try {
            selectionDivider = NumberPicker.class.getDeclaredField("mSelectionDivider");
            /* Very important!!! */
            selectionDivider.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        for (String f : fields) {
            try {
                Field field = TimePicker.class.getDeclaredField(f);
                /* Very important!!! */
                field.setAccessible(true);
                NumberPicker np = (NumberPicker) field.get(this);
                selectionDivider.set(np, getResources().getDrawable(R.color.main_color));
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}

