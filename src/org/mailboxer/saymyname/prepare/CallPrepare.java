package org.mailboxer.saymyname.prepare;

import org.mailboxer.saymyname.contact.Contact;
import org.mailboxer.saymyname.utils.Formatter;
import org.mailboxer.saymyname.utils.Settings;

import android.os.Bundle;
import android.telephony.TelephonyManager;

public class CallPrepare extends Prepare {
	public CallPrepare(final Bundle extras) {
		number = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
	}

	@Override
	public String[] prepare(final Contact contact, final Settings settings) {
		String name = contact.getName();
		if ("".equals(name)) {
			if (settings.getRingtoneIndex() >= 0) {
				queue = new String[2];
				queue[0] = "";
				queue[1] = RINGTONE + settings.getRingtoneIndex();
			} else {
				return new String[] {DELAY + "0", ""};
			}

			return queue;
		}

		final String type = contact.getType();

		final Formatter format = new Formatter(name, settings);
		name = format.format();

		String text = settings.getCallerFormat();
		text = text.replaceFirst("%", name);
		// text = text.replaceFirst("&", type);

		final int repeat = settings.getCallerRepeatTimes();
		if (settings.getRingtoneIndex() >= 0) {
			queue = new String[repeat * 2 + 1];
		} else {
			queue = new String[repeat * 2];
		}

		queue[0] = DELAY + settings.getCallerSpeechDelay();

		final int delay = settings.getCallerRepeatSeconds();
		for (int i = 1; i < queue.length; i++) {
			queue[i] = text;

			if (++i < queue.length) {
				queue[i] = DELAY + delay;
			}
		}

		if (settings.getRingtoneIndex() >= 0) {
			queue[queue.length - 1] = RINGTONE + settings.getRingtoneIndex();
		}

		if (queue != null) {
			return queue;
		} else {
			return new String[] {DELAY + "0", ""};
		}
	}
}