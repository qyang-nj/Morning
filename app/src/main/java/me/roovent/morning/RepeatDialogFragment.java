package me.roovent.morning;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.EnumSet;

import me.roovent.morning.model.RepeatOption;

public class RepeatDialogFragment extends DialogFragment {
    private String[] labels;
    private boolean[] checks = null;
    private NoticeListener listener;

    public RepeatDialogFragment() {
        super();

        RepeatOption[] repeats = RepeatOption.values();
        labels = new String[repeats.length];
        for (int i = 0; i < labels.length; ++i) {
            labels[i] = repeats[i].toString();
        }
    }

    public void setRepeat(EnumSet<RepeatOption> repeats) {
        checks = new boolean[labels.length];
        for (RepeatOption ro : repeats) {
            checks[ro.ordinal()] = true;
        }
    }

    public void setNoticeListener(NoticeListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.repeat);

        builder.setMultiChoiceItems(labels, checks, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (checks == null) {
                    checks = new boolean[labels.length];
                }
                checks[which] = isChecked;
            }
        });

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (listener != null) {
                    EnumSet<RepeatOption> repeats = EnumSet.noneOf(RepeatOption.class);
                    for (int i = 0; i < checks.length; ++i) {
                        if (checks[i]) {
                            repeats.add(RepeatOption.values()[i]);
                        }
                    }
                    listener.onDialogPositiveClick(repeats);
                }
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        return builder.create();
    }
    public interface NoticeListener {
        public void onDialogPositiveClick(EnumSet<RepeatOption> repeats);
    }
}
