package org.mailboxer.saymyname.tts;

import org.mailboxer.saymyname.prepare.Prepare;
import org.mailboxer.saymyname.service.ManagerService;
import org.mailboxer.saymyname.utils.RingtoneTimer;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.tts.TTS;

@SuppressWarnings("deprecation")
public class Speaker {
	private TTS speaker;
	private Thread sleepThread;
	private final Context context;
	private final RingtoneTimer timer;
	private final String[] queue;

	public Speaker(final ManagerService service, final String[] queue, final RingtoneTimer timer, final boolean discreet) {
		new Thread() {
			@Override
			public void run() {
				try {
					speaker = new TTS(service, new StartListener(Speaker.this), false);
				} catch (final Exception e) {
					e.printStackTrace();

					service.stopService(new Intent(service, ManagerService.class));
				}
			}
		}.start();

		context = service;
		this.queue = queue;
		this.timer = timer;
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
		speaker.speak(text, 0, null);
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
	}
}