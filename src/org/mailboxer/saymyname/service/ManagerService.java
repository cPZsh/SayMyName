package org.mailboxer.saymyname.service;

import org.mailboxer.saymyname.contact.Contact;
import org.mailboxer.saymyname.prepare.CallPrepare;
import org.mailboxer.saymyname.prepare.MailPrepare;
import org.mailboxer.saymyname.prepare.Prepare;
import org.mailboxer.saymyname.prepare.SmsPrepare;
import org.mailboxer.saymyname.receiver.HeadsetReceiver;
import org.mailboxer.saymyname.tts.Speaker;
import org.mailboxer.saymyname.utils.RingtoneTimer;
import org.mailboxer.saymyname.utils.Settings;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class ManagerService extends Service {
	private Speaker speaker;
	private Settings settings;
	private RingtoneTimer timer;
	private HeadsetReceiver headset;

	// @Override
	// public int onStartCommand(final Intent intent, final int flags, final int
	// startId) {
	// init(intent);
	//
	// // startForeground();
	//
	// return START_STICKY;
	// }

	@Override
	public void onStart(final Intent intent, final int startId) {
		init(intent);

		// setForeground(true);
	}

	private void init(final Intent intent) {
		if (intent == null) {
			stopSelf();
			return;
		}

		headset = new HeadsetReceiver();
		registerReceiver(headset, new IntentFilter(Intent.ACTION_HEADSET_PLUG));

		settings = new Settings(this);

		if (!settings.isStartSomething()) {
			stopSelf();
			return;
		}

		final AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		if (settings.isRespectSilent() && audio.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			stopSelf();
			return;
		}

		audio.setStreamVolume(AudioManager.STREAM_MUSIC, settings.getWantedVolume(), 0);

		new Thread() {
			@Override
			public void run() {
				final Bundle extras = intent.getExtras();
				if (extras == null) {
					stopSelf();
					return;
				}

				final Prepare prepare;
				if (extras.containsKey(Prepare.CALL)) {
					if (!settings.isStartSayCaller()) {
						stopSelf();
						return;
					}

					prepare = new CallPrepare(extras.getBundle(Prepare.CALL));
				} else if (extras.containsKey(Prepare.SMS)) {
					if (!settings.isStartSaySMS()) {
						stopSelf();
						return;
					}

					prepare = new SmsPrepare(extras.getBundle(Prepare.SMS));
				} else if (intent.getExtras().containsKey(Prepare.MAIL)) {
					if (!settings.isStartSayEMail()) {
						stopSelf();
						return;
					}

					prepare = new MailPrepare(extras.getBundle(Prepare.MAIL));
				} else {
					stopSelf();
					return;
				}

				final Contact contact = Contact.getInstance(ManagerService.this, settings, prepare.getNumber());
				final String[] queue = prepare.prepare(contact, settings);

				timer = new RingtoneTimer(ManagerService.this);

				if (settings.isDiscreet() && HeadsetReceiver.state == HeadsetReceiver.STATE_UNPLUGGED) {
					stopSelf();
					return;
				}

				speaker = new Speaker(ManagerService.this, queue, timer, settings.isDiscreet());
			}
		}.start();
	}

	@Override
	public void onDestroy() {
		Log.e("smn", "stop service");

		if (speaker != null) {
			speaker.stop();
		}

		if (timer != null) {
			timer.stop();
		}

		if (headset != null) {
			unregisterReceiver(headset);
		}
	}

	@Override
	public IBinder onBind(final Intent intent) {
		return null;
	}
}