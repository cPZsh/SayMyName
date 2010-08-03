package org.mailboxer.saymyname.tts;

import org.mailboxer.saymyname.prepare.Prepare;
import org.mailboxer.saymyname.service.ManagerService;
import org.mailboxer.saymyname.utils.RingtoneTimer;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.google.tts.TTS.SpeechCompletedListener;

@SuppressWarnings("deprecation")
public class RepeatedSpeechListener implements SpeechCompletedListener {
	private int index = 1;
	private final Speaker speaker;
	private final Context context;
	private final String[] queue;
	private final RingtoneTimer timer;

	public RepeatedSpeechListener(final Speaker speaker, final Context context, final String[] queue, final RingtoneTimer timer) {
		this.speaker = speaker;
		this.context = context;
		this.queue = queue;
		this.timer = timer;
	}

	@Override
	public void onSpeechCompleted() {
		if (((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getCallState() != TelephonyManager.CALL_STATE_OFFHOOK && index < queue.length) {
			if (queue[index].startsWith(Prepare.DELAY)) {
				try {
					final int sleep = Integer.parseInt(queue[index++].substring(Prepare.DELAY.length()));
					Thread.sleep(sleep);
				} catch (final NumberFormatException e) {
					e.printStackTrace();
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			} else if (queue[index].startsWith(Prepare.RINGTONE)) {
				timer.start(Integer.parseInt(queue[index].substring(Prepare.RINGTONE.length())));
				return;
			}

			if (((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getCallState() != TelephonyManager.CALL_STATE_OFFHOOK) {
				speaker.speak(queue[index++]);
			} else {
				context.stopService(new Intent(context, ManagerService.class));
			}
		} else {
			context.stopService(new Intent(context, ManagerService.class));
		}
	}
}