package org.mailboxer.saymyname.receiver;

import org.mailboxer.saymyname.service.GmailService;
import org.mailboxer.saymyname.utils.Settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class GmailReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(final Context context, final Intent intent) {
		final SharedPreferences preferences = context.getSharedPreferences(Settings.SHARED_PREFERENCES, Context.MODE_WORLD_WRITEABLE);
		if (preferences.getBoolean("sayemail", true)) {
			context.startService(new Intent(context, GmailService.class));
		}
	}
}