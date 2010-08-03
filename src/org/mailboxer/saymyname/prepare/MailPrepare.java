package org.mailboxer.saymyname.prepare;

import org.mailboxer.saymyname.contact.Contact;
import org.mailboxer.saymyname.utils.Formatter;
import org.mailboxer.saymyname.utils.Settings;

import android.os.Bundle;

public class MailPrepare extends Prepare {
	public static final String FROM = "com.fsck.k9.intent.extra.FROM";
	public static final String SUBJECT = "com.fsck.k9.intent.extra.SUBJECT";

	public MailPrepare(final Bundle extras) {
		number = extras.getString("com.fsck.k9.intent.extra.FROM");
		subject = extras.getString("com.fsck.k9.intent.extra.SUBJECT");
	}

	@Override
	public String[] prepare(final Contact contact, final Settings settings) {
		String name = contact.getName();
		if ("".equals(name)) {
			return new String[] {""};
		}

		final Formatter format = new Formatter(name, settings);
		name = format.format();

		name = settings.getEMailFormat().replaceFirst("%", name);

		if (settings.isEMailReadSubject()) {
			queue = new String[3];
			queue[0] = name;
			queue[1] = DELAY + settings.getEMailReadDelay();
			queue[2] = subject;
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