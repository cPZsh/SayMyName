package org.mailboxer.saymyname.locale;

import org.mailboxer.saymyname.utils.Settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public final class Receiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final Intent intent) {
		if (com.twofortyfouram.Intent.ACTION_FIRE_SETTING.equals(intent.getAction())) {
			final boolean startCaller = intent.getBooleanExtra("org.mailboxer.saymyname.extra.START_CALLER", false);
			final boolean startSMS = intent.getBooleanExtra("org.mailboxer.saymyname.extra.START_SMS", false);
			final boolean startEMail = intent.getBooleanExtra("org.mailboxer.saymyname.extra.START_EMAIL", false);

			final SharedPreferences.Editor editor = context.getSharedPreferences(Settings.SHARED_PREFERENCES, Context.MODE_WORLD_WRITEABLE).edit();

			editor.putBoolean("saycaller", startCaller);
			editor.putBoolean("saysms", startSMS);
			editor.putBoolean("sayemail", startEMail);
			editor.commit();
		}
	}
}