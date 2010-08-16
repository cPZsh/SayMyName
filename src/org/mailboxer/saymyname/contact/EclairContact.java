package org.mailboxer.saymyname.contact;

import org.mailboxer.saymyname.utils.Settings;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;

public class EclairContact extends Contact {
	public EclairContact(final Context context, final Settings settings, final String incomingNumber) {
		super(context, settings, incomingNumber);
	}

	@Override
	protected void lookupNumber() {
		Cursor cur = null;
		try {
			cur = context.getContentResolver().query(Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, incomingNumber), new String[] {PhoneLookup.DISPLAY_NAME, PhoneLookup.TYPE}, null, null, null);

			final int nameIndex = cur.getColumnIndex(PhoneLookup.DISPLAY_NAME);
			final int typeIndex = cur.getColumnIndex(PhoneLookup.TYPE);

			if (cur.moveToFirst()) {
				name = cur.getString(nameIndex);

				switch (Integer.parseInt(cur.getString(typeIndex))) {
					case Phone.TYPE_MOBILE:
						type = "Mobile";
						break;

					case Phone.TYPE_HOME:
						type = "Home";
						break;

					case Phone.TYPE_WORK:
						type = "Work";
						break;

					default:
						// maybe a custom type
						type = "";
						break;
				}
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
			cur = context.getContentResolver().query(Email.CONTENT_URI, new String[] {Email.DISPLAY_NAME, Email.TYPE}, Email.DATA1 + "=" + "'" + incomingNumber + "'", null, null);

			final int nameIndex = cur.getColumnIndex(Email.DISPLAY_NAME);
			final int typeIndex = cur.getColumnIndex(Email.TYPE);

			if (cur.moveToFirst()) {
				name = cur.getString(nameIndex);
				if (name == null) {
					name = UNKNOWN;
				}

				switch (Integer.parseInt(cur.getString(typeIndex))) {
					case Email.TYPE_MOBILE:
						type = "Mobile";
						break;

					case Email.TYPE_HOME:
						type = "Home";
						break;

					case Email.TYPE_WORK:
						type = "Work";
						break;

					case Email.TYPE_OTHER:
						type = "Other";
						break;

					default:
						// maybe a custom type
						type = "";
						break;
				}
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
