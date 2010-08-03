package org.mailboxer.saymyname.receiver;

import org.mailboxer.saymyname.prepare.Prepare;
import org.mailboxer.saymyname.service.ManagerService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class SmsReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final Intent intent) {
		if (((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getCallState() != TelephonyManager.CALL_STATE_IDLE) {
			return;
		}

		// final Bundle bundle = intent.getExtras();
		// final Object[] pdusObj = (Object[]) bundle.get("pdus");
		//
		// final SmsMessage[] messages = new SmsMessage[pdusObj.length];
		// for (int i = 0; i < pdusObj.length; i++) {
		// messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
		// }
		//
		// final StringBuilder sb = new StringBuilder();
		//
		// if (messages.length > 1) {
		// for (final SmsMessage currentMessage : messages) {
		// sb.append(currentMessage.getDisplayMessageBody());
		// sb.append('\n');
		// }
		// } else {
		// sb.append(messages[0].getDisplayMessageBody());
		// }
		//
		// final Intent serviceIntent = new Intent(context,
		// ManagerService.class);
		// serviceIntent.putExtra("org.mailboxer.saymyname.number",
		// messages[0].getDisplayOriginatingAddress());
		// serviceIntent.putExtra("org.mailboxer.saymyname.message",
		// sb.toString());
		// serviceIntent.putExtra("SMS", bundle);
		// context.startService(serviceIntent);

		final Intent serviceIntent = new Intent(context, ManagerService.class);
		serviceIntent.putExtra(Prepare.SMS, intent.getExtras());
		context.startService(serviceIntent);
	}
}