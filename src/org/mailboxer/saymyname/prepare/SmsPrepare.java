package org.mailboxer.saymyname.prepare;

import org.mailboxer.saymyname.contact.Contact;
import org.mailboxer.saymyname.utils.Formatter;
import org.mailboxer.saymyname.utils.Settings;
import org.mailboxer.saymyname.utils.Sms;
import org.mailboxer.saymyname.utils.Transliterator;

import android.os.Bundle;

public class SmsPrepare extends Prepare {
	public SmsPrepare(final Bundle extras) {
		final Sms sms = Sms.getInstance(extras);
		number = sms.getNumber();
		message = sms.getMessage();
	}

	@Override
	public String[] prepare(final Contact contact, final Settings settings) {
		String name = contact.getName();
		if ("".equals(name)) {
			return new String[] {DELAY + "0", ""};
		}

		final String type = contact.getType();

		final Formatter format = new Formatter(name, settings);
		name = format.format();

		String text = settings.getSmsFormat();
		text = text.replaceFirst("%", name);
		text = text.replaceFirst("&", type);

		if (settings.isSmsRead()) {
			queue = new String[4];
			queue[0] = DELAY + settings.getSmsSpeechDelay();
			queue[1] = name;
			queue[2] = DELAY + settings.getSmsReadDelay();
			if (settings.isTransliterate()) {
				queue[3] = Transliterator.transliterate(message);
			} else {
				queue[3] = message;
			}
		} else {
			queue = new String[2];
			queue[0] = DELAY + settings.getSmsSpeechDelay();
			queue[1] = name;
		}

		if (queue != null) {
			return queue;
		} else {
			return new String[] {DELAY + "0", ""};
		}
	}
}