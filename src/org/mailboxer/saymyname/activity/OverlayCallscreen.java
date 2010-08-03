package org.mailboxer.saymyname.activity;

import org.mailboxer.saymyname.R;
import org.mailboxer.saymyname.service.ManagerService;
import org.mailboxer.saymyname.utils.Settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;

public class OverlayCallscreen extends Activity {
	private Thread thread;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVisible(false);

		final SharedPreferences preferences = getSharedPreferences(Settings.SHARED_PREFERENCES, Context.MODE_WORLD_WRITEABLE);
		if (!preferences.getBoolean("showSilenceScreen", false)) {
			finish();
			return;
		} else if (!preferences.getBoolean("saysomething", true)) {
			finish();
			return;
		}

		if (((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getCallState() != TelephonyManager.CALL_STATE_RINGING) {
			finish();
			return;
		}

		setVisible(true);

		setContentView(R.layout.main);

		thread = new Thread() {
			@Override
			public void run() {
				try {
					for (int i = 0; i < 20; i++) {
						sleep(1000);

						if (((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getCallState() != TelephonyManager.CALL_STATE_RINGING) {
							break;
						}
					}
				} catch (final InterruptedException e) {}

				finish();
			}
		};
		thread.start();
	}

	// @Override
	// protected void onNewIntent(final Intent intent) {
	// if (((TelephonyManager)
	// getSystemService(Context.TELEPHONY_SERVICE)).getCallState() !=
	// TelephonyManager.CALL_STATE_RINGING) {
	// shutdown();
	// }
	// }

	@Override
	public void onUserInteraction() {
		shutdown();
		super.onUserInteraction();
	}

	private void shutdown() {
		stopService(new Intent(this, ManagerService.class));

		if (thread != null) {
			thread.interrupt();
		}

		finish();
	}
}