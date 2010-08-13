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
import android.view.KeyEvent;
import android.view.MotionEvent;

public class OverlayCallscreen extends Activity {

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
	}

	@Override
	public void onWindowFocusChanged(final boolean hasFocus) {
		if (!hasFocus) {
			setContentView(R.layout.main);
		}
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	protected void onNewIntent(final Intent intent) {
		if (((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getCallState() != TelephonyManager.CALL_STATE_RINGING) {
			shutdown();
		}
	}

	@Override
	public void onUserInteraction() {
		shutdown();
	}

	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		shutdown();
		return false;
	}

	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		shutdown();
		return false;
	}

	private void shutdown() {
		setVisible(false);
		stopService(new Intent(this, ManagerService.class));
		finish();
	}
}