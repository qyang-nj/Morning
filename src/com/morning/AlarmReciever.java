package com.morning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

public class AlarmReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		Intent intent = new Intent(context, AlertActivity.class);

		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

}
