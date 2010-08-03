package org.mailboxer.saymyname.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class HeadsetReceiver extends BroadcastReceiver {
	public static final int STATE_UNPLUGGED = 0;
	public static final int STATE_PLUGGED = 1;

	public static int state = -1;

	@Override
	public void onReceive(final Context context, final Intent intent) {
		state = intent.getIntExtra("state", -1);
	}
}