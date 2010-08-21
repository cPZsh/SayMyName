package at.bartinger.phonestate.listener.screen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenReceiver extends BroadcastReceiver {

	private final ScreenStateListener listener;

	public ScreenReceiver(final ScreenStateListener listener) {
		this.listener = listener;
	}

	@Override
	public void onReceive(final Context context, final Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
			listener.onScreenOff();
		} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
			listener.onScreenOn();
		}
	}

}
