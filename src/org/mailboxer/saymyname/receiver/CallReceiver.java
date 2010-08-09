package org.mailboxer.saymyname.receiver;

import org.mailboxer.saymyname.activity.OverlayCallscreen;
import org.mailboxer.saymyname.prepare.Prepare;
import org.mailboxer.saymyname.service.ManagerService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class CallReceiver extends BroadcastReceiver {
	private static boolean onCall;
	private Context context;

	@Override
	public void onReceive(final Context context, final Intent intent) {
		this.context = context;

		final String newState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

		if (newState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
			if (onCall) {
				if (((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getCallState() == TelephonyManager.CALL_STATE_IDLE) {
					onCall = false;
				} else {
					return;
				}
			}

			final Intent serviceIntent = new Intent(context, ManagerService.class);
			// serviceIntent.putExtra("org.mailboxer.saymyname.number",
			// intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER));
			serviceIntent.putExtra(Prepare.CALL, intent.getExtras());
			context.startService(serviceIntent);

			final Intent overlayIntent = new Intent(context, OverlayCallscreen.class);
			overlayIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			overlayIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			overlayIntent.addFlags(65536); // Intent.FLAG_ACTIVITY_NO_ANIMATION
			overlayIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			overlayIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

			// overlayIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			context.startActivity(overlayIntent);
		} else if (newState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
			onCall = true;
			shutdown();
		} else if (newState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
			onCall = false;
			shutdown();
		}
	}

	private void shutdown() {
		final Intent serviceIntent = new Intent(context, ManagerService.class);
		context.stopService(serviceIntent);
	}
}