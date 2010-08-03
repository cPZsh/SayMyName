package org.mailboxer.saymyname.contact;

import org.mailboxer.saymyname.utils.Settings;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts.ContactMethods;
import android.provider.Contacts.ContactMethodsColumns;
import android.provider.Contacts.PeopleColumns;
import android.provider.Contacts.Phones;
import android.provider.Contacts.PhonesColumns;

@SuppressWarnings("deprecation")
public class CupcakeContact extends Contact {
	public CupcakeContact(final Context context, final Settings settings, final String incomingNumber) {
		super(context, settings, incomingNumber);
	}

	@Override
	protected void lookupNumber() {
		Cursor cur = null;
		try {
			cur = context.getContentResolver().query(Uri.withAppendedPath(Phones.CONTENT_FILTER_URL, Uri.encode(incomingNumber)), new String[] {PeopleColumns.DISPLAY_NAME, PhonesColumns.TYPE}, null, null, null);

			final int nameIndex = cur.getColumnIndex(PeopleColumns.DISPLAY_NAME);
			final int typeIndex = cur.getColumnIndex(PhonesColumns.TYPE);

			if (cur.moveToFirst()) {
				name = cur.getString(nameIndex);
				type = cur.getString(typeIndex);
			} else {
				name = UNKNOWN;
			}
		} catch (final Exception e) {
			e.printStackTrace();
			name = UNKNOWN;
		} finally {
			if (cur != null) {
				cur.close();
			}
		}
	}

	@Override
	protected void lookupMail() {
		Cursor cur = null;
		try {
			cur = context.getContentResolver().query(ContactMethods.CONTENT_URI, new String[] {PeopleColumns.DISPLAY_NAME, ContactMethodsColumns.TYPE}, ContactMethodsColumns.DATA + "=" + "'" + Uri.encode(incomingNumber) + "'", null, null);

			final int nameIndex = cur.getColumnIndex(PeopleColumns.DISPLAY_NAME);
			final int typeIndex = cur.getColumnIndex(ContactMethodsColumns.TYPE);

			if (cur.moveToFirst()) {
				name = cur.getString(nameIndex);
				type = cur.getString(typeIndex);
			} else {
				name = UNKNOWN;
			}
		} catch (final Exception e) {
			e.printStackTrace();
			name = UNKNOWN;
		} finally {
			if (cur != null) {
				cur.close();
			}
		}
	}
}