package org.mailboxer.saymyname.service;

import org.mailboxer.saymyname.prepare.MailPrepare;
import org.mailboxer.saymyname.prepare.Prepare;
import org.mailboxer.saymyname.utils.Settings;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.TelephonyManager;

public class GmailService extends Service {
	private boolean firstRun = false;
	private long biggestIdSeen;
	private String mailAddress;
	private GMailObserver gmailObserver;

	@Override
	public void onCreate() {
		gmailObserver = new GMailObserver(new Handler(), getContentResolver());

		final SharedPreferences preferences = getSharedPreferences(Settings.SHARED_PREFERENCES, Context.MODE_WORLD_WRITEABLE);
		mailAddress = preferences.getString("emailAddress", "failed@gmail.com").toLowerCase();

		try {
			getContentResolver().registerContentObserver(Uri.parse("content://gmail-ls/conversations/" + Uri.encode(mailAddress)), true, gmailObserver);

			gmailObserver.onChange(false);
		} catch (final Exception e) {
			e.printStackTrace();
			stopSelf();
			return;
		}

		super.onCreate();
	}

	@Override
	public void onDestroy() {
		getContentResolver().unregisterContentObserver(gmailObserver);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(final Intent intent) {
		return null;
	}

	private class GMailObserver extends ContentObserver {
		public GMailObserver(final Handler handler, final ContentResolver cr) {
			super(handler);
		}

		@Override
		public void onChange(final boolean selfChange) {
			try {
				final SharedPreferences preferences = getSharedPreferences(Settings.SHARED_PREFERENCES, Context.MODE_WORLD_WRITEABLE);
				if (!preferences.getBoolean("sayemail", true)) {
					return;
				}

				final Cursor conversations = getContentResolver().query(Uri.parse("content://gmail-ls/conversations/" + Uri.encode(mailAddress)), null, null, null, null);
				conversations.moveToFirst();

				final long conversationId = Long.valueOf(conversations.getString(conversations.getColumnIndex("conversation_id")));

				final String subject = conversations.getString(conversations.getColumnIndex("subject"));

				final Cursor messages = getContentResolver().query(Uri.parse("content://gmail-ls/conversations/" + mailAddress + "/" + String.valueOf(conversationId) + "/messages"), null, null, null, null);
				messages.moveToLast();

				final String from = messages.getString(messages.getColumnIndex("fromAddress"));
				final long messageId = Long.valueOf(messages.getString(messages.getColumnIndex("messageId")));

				messages.close();
				conversations.close();

				if (biggestIdSeen < messageId) {
					biggestIdSeen = messageId;

					if (!firstRun) {
						firstRun = !firstRun;
						return;
					}

					if (from == null || from.equals("") || mailAddress.equals(from.substring(from.indexOf('<') + 1, from.indexOf('>')))) {
						return;
					}

					if (((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getCallState() != TelephonyManager.CALL_STATE_IDLE) {
						return;
					}

					final Bundle bundle = new Bundle();
					bundle.putString(MailPrepare.FROM, from);
					bundle.putString(MailPrepare.SUBJECT, subject);

					final Intent serviceIntent = new Intent(GmailService.this, ManagerService.class);
					serviceIntent.putExtra(Prepare.MAIL, bundle);
					startService(serviceIntent);
				}
			} catch (final Exception e) {
				e.printStackTrace();
				stopSelf();
				return;
			}
		}
	}
}