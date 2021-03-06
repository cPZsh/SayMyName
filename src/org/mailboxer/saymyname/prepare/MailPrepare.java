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
			return new String[] {DELAY + "0", ""};
		} else if (name.equals(Contact.UNKNOWN) && settings.isEmailReadOnlyMyContacts()) {
			return new String[] {DELAY + "0", ""};
		}

		final String type = contact.getType();

		final Formatter format = new Formatter(name, settings);
		name = format.format();

		String text = settings.getEMailFormat();
		text = text.replaceFirst("%", name);
		text = text.replaceFirst("&", type);

		if (settings.isEMailReadSubject()) {
			queue = new String[4];
			queue[0] = DELAY + settings.getEmailSpeechDelay();
			queue[1] = name;
			queue[2] = DELAY + settings.getEMailReadDelay();
			queue[3] = subject;
		} else {
			queue = new String[2];
			queue[0] = DELAY + settings.getEmailSpeechDelay();
			queue[1] = name;
		}

		if (queue != null) {
			return queue;
		} else {
			return new String[] {DELAY + "0", ""};
		}
	}
}