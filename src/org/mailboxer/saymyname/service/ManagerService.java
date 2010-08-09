package org.mailboxer.saymyname.service;

import org.mailboxer.saymyname.R;
import org.mailboxer.saymyname.contact.Contact;
import org.mailboxer.saymyname.prepare.CallPrepare;
import org.mailboxer.saymyname.prepare.MailPrepare;
import org.mailboxer.saymyname.prepare.Prepare;
import org.mailboxer.saymyname.prepare.SmsPrepare;
import org.mailboxer.saymyname.receiver.NotificationReceiver;
import org.mailboxer.saymyname.tts.Speaker;
import org.mailboxer.saymyname.utils.RingtoneTimer;
import org.mailboxer.saymyname.utils.Settings;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class ManagerService extends Service {
	private boolean running;
	private int previousVolume;

	private Speaker speaker;
	private Settings settings;
	private AudioManager audioManager;
	private RingtoneTimer ringtoneTimer;
	private NotificationReceiver notificationReceiver;
	private NotificationManager notificationManager;

	@Override
	public void onStart(final Intent intent, final int startId) {
		if (!running) {
			init(intent);
		}
	}

	private synchronized void init(final Intent intent) {
		running = true;

		final Intent headsetIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_HEADSET_PLUG));

		if (intent == null) {
			stopSelf();
			return;
		}

		notificationReceiver = new NotificationReceiver();
		registerReceiver(notificationReceiver, new IntentFilter(NotificationReceiver.NOTIFICATION_BROADCAST));

		final PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1993, new Intent(NotificationReceiver.NOTIFICATION_BROADCAST), 0);

		final Notification notification = new Notification(R.drawable.icon, null, 0);
		notification.setLatestEventInfo(this, "SayMyName speaking...", "If you want SayMyName to stop speaking, press here", pendingIntent);

		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(18, notification);

		try {
			settings = new Settings(this);
		} catch (final Exception e) {
			e.printStackTrace();

			stopSelf();
			return;
		}

		if (!settings.isStartSomething()) {
			stopSelf();
			return;
		}

		if (headsetIntent != null && settings.isDiscreet() && headsetIntent.getIntExtra("state", -1) == 0) {
			stopSelf();
			return;
		}

		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		if (settings.isRespectSilent() && audioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			stopSelf();
			return;
		}

		previousVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

		if (settings.isUseRingtoneVolume()) {
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamVolume(AudioManager.STREAM_RING) * 2, 0);
		} else {
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, settings.getWantedVolume(), 0);
		}

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

				final Contact contact = Contact.getInstance(ManagerService.this, settings, prepare.getNumber(), 12);
				final String[] queue = prepare.prepare(contact, settings);

				ringtoneTimer = new RingtoneTimer(ManagerService.this);

				speaker = new Speaker(ManagerService.this, queue, ringtoneTimer, settings.isDiscreet());
			}
		}.start();
	}

	@Override
	public void onDestroy() {
		Log.e("smn", "stop service");

		if (speaker != null) {
			speaker.stop();
		}

		if (ringtoneTimer != null) {
			ringtoneTimer.stop();
		}

		if (notificationManager != null) {
			notificationManager.cancel(18);
		}

		if (audioManager != null) {
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, previousVolume, 0);
		}

		if (notificationReceiver != null) {
			unregisterReceiver(notificationReceiver);
		}

		running = false;
	}

	@Override
	public IBinder onBind(final Intent intent) {
		return null;
	}
}