package org.mailboxer.saymyname.prepare;

import org.mailboxer.saymyname.contact.Contact;
import org.mailboxer.saymyname.utils.Settings;

public abstract class Prepare {
	public static final String CALL = "org.mailboxer.saymyname.CALL";
	public static final String SMS = "org.mailboxer.saymyname.SMS";
	public static final String MAIL = "org.mailboxer.saymyname.MAIL";

	public static final String DELAY = "org.mailboxer.saymyname.delay.";
	public static final String RINGTONE = "org.mailboxer.saymyname.ringtone.";

	protected String number;
	protected String subject;
	protected String message;
	protected String[] queue;

	public abstract String[] prepare(Contact contact, Settings settings);

	public String getNumber() {
		return number;
	}

	public String getMessage() {
		return message;
	}
}