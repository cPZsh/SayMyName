package org.mailboxer.saymyname.tts;

import org.mailboxer.saymyname.receiver.HeadsetReceiver;
import org.mailboxer.saymyname.service.ManagerService;
import org.mailboxer.saymyname.utils.RingtoneTimer;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.tts.TTS;

@SuppressWarnings("deprecation")
public class Speaker {
	private final Context context;
	private TTS speaker;
	private final String[] queue;
	private final RingtoneTimer timer;
	private final boolean discreet;

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
		this.discreet = discreet;
	}

	public void start() {
		if (speaker == null) {
			context.stopService(new Intent(context, ManagerService.class));
			return;
		}
		if (discreet && HeadsetReceiver.state == HeadsetReceiver.STATE_UNPLUGGED) {
			context.stopService(new Intent(context, ManagerService.class));
			return;
		}

		speaker.setSpeechRate(100);
		speaker.setOnSpeechCompletedListener(new RepeatedSpeechListener(this, context, queue, timer));
		speak(queue[0]);
	}

	public void speak(final String text) {
		if (discreet && HeadsetReceiver.state == HeadsetReceiver.STATE_UNPLUGGED) {
			context.stopService(new Intent(context, ManagerService.class));
			return;
		}

		speaker.speak(text, 0, null);
	}

	public void stop() {
		Log.e("smn", "stop speaker");
		if (speaker != null) {
			speaker.stop();
			speaker.shutdown();
		}
	}
}