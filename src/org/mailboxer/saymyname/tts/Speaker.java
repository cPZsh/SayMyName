package org.mailboxer.saymyname.tts;

import org.mailboxer.saymyname.activity.OverlayCallscreen;
import org.mailboxer.saymyname.prepare.Prepare;
import org.mailboxer.saymyname.service.ManagerService;
import org.mailboxer.saymyname.utils.RingtoneTimer;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import at.bartinger.phonestate.listener.PhoneState;
import at.bartinger.phonestate.listener.PhoneState.PhoneStateListener;

import com.google.tts.TTS;
import com.google.tts.TTSEngine;

@SuppressWarnings("deprecation")
public class Speaker {
	private TTS speaker;
	private Thread sleepThread;
	private final Context context;
	private final RingtoneTimer timer;
	private final String[] queue;
	private final PhoneState phoneState;

	public Speaker(final ManagerService service, final String[] queue, final RingtoneTimer timer, final boolean discreet) {
		new Thread() {
			@Override
			public void run() {
				try {
					speaker = new TTS(service, new StartListener(Speaker.this), false);
					speaker.setEngine(TTSEngine.PICO);
				} catch (final Exception e) {
					e.printStackTrace();

					service.stopService(new Intent(service, ManagerService.class));

					Toast.makeText(service, "please open SayMyName", Toast.LENGTH_LONG).show();
				}
			}
		}.start();

		context = service;
		this.queue = queue;
		this.timer = timer;

		phoneState = new PhoneState(context, new PhoneStateListener() {
			@Override
			public void onDisplayDown() {
				context.stopService(new Intent(context, ManagerService.class));
			}

			@Override
			public void onDisplayUp() {}

			@Override
			public void onRandomMotion(final boolean isScreenOn) {}

			@Override
			public void onScreenOff() {}

			@Override
			public void onScreenOn() {}
		});
	}

	public void start() {
		if (speaker == null) {
			context.stopService(new Intent(context, ManagerService.class));
			return;
		}

		speaker.setSpeechRate(100);
		speaker.setOnSpeechCompletedListener(new RepeatedSpeechListener(this, context, queue, timer));

		final int sleep = Integer.parseInt(queue[0].substring(Prepare.DELAY.length()));
		if (queue[0].startsWith(Prepare.DELAY) && sleep > 0) {
			sleepThread = new Thread() {
				@Override
				public void run() {
					try {
						sleep(sleep);

						speak(queue[1]);
					} catch (final InterruptedException e) {}
				};
			};
			sleepThread.start();
		} else {
			speak(queue[1]);
		}
	}

	public void speak(final String text) {
		final Intent overlayIntent = new Intent(context, OverlayCallscreen.class);
		overlayIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		overlayIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		overlayIntent.addFlags(65536); // Intent.FLAG_ACTIVITY_NO_ANIMATION
		overlayIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		overlayIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		// overlayIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(overlayIntent);

		if (speaker != null) {
			speaker.speak(text, 0, null);
		}
	}

	public void stop() {
		Log.e("smn", "stop speaker");

		if (sleepThread != null) {
			sleepThread.interrupt();
		}

		if (speaker != null) {
			speaker.stop();
			speaker.shutdown();
		}

		if (phoneState != null) {
			phoneState.stopAccelerometer();
		}
	}
}