package org.mailboxer.saymyname.receiver;

import org.mailboxer.saymyname.prepare.Prepare;
import org.mailboxer.saymyname.service.ManagerService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class K9MailReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final Intent intent) {
		if (((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getCallState() != TelephonyManager.CALL_STATE_IDLE) {
			return;
		}

		final Intent serviceIntent = new Intent(context, ManagerService.class);
		serviceIntent.putExtra(Prepare.MAIL, intent.getExtras());
		// serviceIntent.putExtra("org.mailboxer.saymyname.number",
		// intent.getStringExtra("com.fsck.k9.intent.extra.FROM"));
		// serviceIntent.putExtra("org.mailboxer.saymyname.message",
		// intent.getStringExtra("com.fsck.k9.intent.extra.SUBJECT"));
		// serviceIntent.putExtra("org.mailboxer.saymyname.mail", "mail");
		context.startService(serviceIntent);
	}
}