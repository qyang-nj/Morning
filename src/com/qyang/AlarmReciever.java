package com.qyang;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		Intent intent = new Intent(context, TimeOutActivity.class);
		
		/* http://developer.android.com/reference/android/content/Intent.html#FLAG_ACTIVITY_NEW_TASK */
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

}
