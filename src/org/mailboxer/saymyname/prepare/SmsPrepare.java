package org.mailboxer.saymyname.prepare;

import org.mailboxer.saymyname.contact.Contact;
import org.mailboxer.saymyname.utils.Formatter;
import org.mailboxer.saymyname.utils.Settings;

import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsPrepare extends Prepare {
	public SmsPrepare(final Bundle extras) {
		final Object[] pdusObj = (Object[]) extras.get("pdus");

		final SmsMessage[] messages = new SmsMessage[pdusObj.length];
		for (int i = 0; i < pdusObj.length; i++) {
			messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
		}

		final StringBuilder sb = new StringBuilder();

		if (messages.length > 1) {
			for (final SmsMessage currentMessage : messages) {
				sb.append(currentMessage.getDisplayMessageBody());
				sb.append('\n');
			}
		} else {
			sb.append(messages[0].getDisplayMessageBody());
		}

		number = messages[0].getDisplayOriginatingAddress();
		message = sb.toString();
	}

	@Override
	public String[] prepare(final Contact contact, final Settings settings) {
		String name = contact.getName();
		if ("".equals(name)) {
			return new String[] {""};
		}

		final Formatter format = new Formatter(name, settings);
		name = format.format();

		name = settings.getSmsFormat().replaceFirst("%", name);

		if (settings.isSmsRead()) {
			queue = new String[3];
			queue[0] = name;
			queue[1] = DELAY + settings.getSmsReadDelay();
			queue[2] = message;
		} else {
			queue = new String[1];
			queue[0] = name;
		}

		if (queue != null) {
			return queue;
		} else {
			return new String[] {""};
		}
	}
}