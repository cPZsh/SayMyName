package org.mailboxer.saymyname.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TransliterationReceiver extends BroadcastReceiver {
	private final String EXTRA_TEXT = "org.mailboxer.saymyname.transliterate.text";
	private String text;

	@Override
	public void onReceive(final Context context, final Intent intent) {
		text = intent.getStringExtra(EXTRA_TEXT);

		System.out.println(text);
	}

	public String getText() {
		return text;
	}
}