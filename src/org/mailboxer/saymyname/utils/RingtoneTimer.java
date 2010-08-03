package org.mailboxer.saymyname.utils;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.telephony.TelephonyManager;

public class RingtoneTimer extends Timer {
	private final Context context;
	private Ringtone ring;
	private final RingtoneManager ringManager;

	public RingtoneTimer(final Context context) {
		super(true);
		this.context = context;
		ringManager = new RingtoneManager(context);
	}

	public void start(final int ringtoneIndex) {
		ring = ringManager.getRingtone(ringtoneIndex);
		schedule(new RingtoneTask(), 0, 500);
	}

	public void stop() {
		ringManager.stopPreviousRingtone();
		cancel();
	}

	private class RingtoneTask extends TimerTask {
		@Override
		public void run() {
			if (!ring.isPlaying() && ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getCallState() == TelephonyManager.CALL_STATE_RINGING) {
				ring.play();
			}
		}
	}
}