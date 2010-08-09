package org.mailboxer.saymyname.contact;

import java.io.FileNotFoundException;

import org.mailboxer.saymyname.R;
import org.mailboxer.saymyname.utils.Settings;

import android.content.Context;
import android.os.Build;
import android.util.Log;

public abstract class Contact {
	public static String UNKNOWN = "unknown";

	private final String EXTRA_TEXT = "org.mailboxer.saymyname.transliteration.TEXT";
	private final String EXTRA_ID = "org.mailboxer.saymyname.transliteration.ID";
	private final String INTENT_ACTION = "org.mailboxer.saymyname.transliteration.TRANSLITERATE_ACTION";
	private final String INTENT_FINISHED_ACTION = "org.mailboxer.saymyname.transliteration.TRANSLITERATE_FINISHED_ACTION";

	protected Context context;
	protected Settings settings;
	protected String incomingNumber;

	protected String name;
	protected String number;
	protected String type;

	public static Contact getInstance(final Context context, final Settings settings, final String incomingNumber, final int startId) {
		UNKNOWN = context.getResources().getString(R.string.caller_unknown);

		if (Integer.parseInt(Build.VERSION.SDK) >= 5) {
			final Contact eclairContact = new EclairContact(context, settings, incomingNumber);
			final String temp = eclairContact.name;

			if (UNKNOWN.equals(temp) || "".equals(temp) || incomingNumber.equals(temp)) {
				return new CupcakeContact(context, settings, incomingNumber);
			} else {
				return eclairContact;
			}
		} else {
			return new CupcakeContact(context, settings, incomingNumber);
		}
	}

	public Contact(final Context context, final Settings settings, final String incomingNumber) {
		this.context = context;
		this.settings = settings;
		this.incomingNumber = incomingNumber;
		number = incomingNumber;

		if (incomingNumber == null || "".equals(incomingNumber)) {
			name = UNKNOWN;
			return;
		}

		if (incomingNumber.contains("@")) {
			try {
				this.incomingNumber = incomingNumber.substring(incomingNumber.indexOf('<') + 1, incomingNumber.indexOf('>'));
				lookupMail();
			} catch (final StringIndexOutOfBoundsException e) {
				e.printStackTrace();

				lookupMail();
			}

			return;
		} else if (incomingNumber.matches("^[+]\\d+||\\d+")) {
			lookupNumber();
		} else {
			// send this to analytics
			// i hope this is a special name
			name = incomingNumber;
		}
	}

	protected abstract void lookupNumber();

	protected abstract void lookupMail();

	public void transliterate() {
	// final TransliterationReceiver receiver = new TransliterationReceiver();
	// context.registerReceiver(receiver, new
	// IntentFilter(INTENT_FINISHED_ACTION));

	// Log.e("smn", "send bc");
	// Intent broadcastIntent = new Intent();
	// broadcastIntent.setAction(INTENT_ACTION);
	// broadcastIntent.putExtra(EXTRA_TEXT, name);
	// broadcastIntent.putExtra(EXTRA_ID, System.currentTimeMillis());
	// context.sendBroadcast(broadcastIntent);

	// new Thread() {
	// public void run() {
	// try {
	// sleep(2000);
	//					
	// name = receiver.getText();
	//					
	// context.unregisterReceiver(receiver);
	// } catch (InterruptedException e) {}
	// };
	// }.start();

	// name = "upsi";

	// final String searchString = "translatedText\":\"";
	// String result = "";
	//
	// try {
	// final String text = URLEncoder.encode(name);
	// final InputStreamReader reader = new InputStreamReader(new
	// URL("http://ajax.googleapis.com/ajax/services/language/translate?v=1.0&q="
	// + text + "&langpair=|en").openStream());
	// final BufferedReader buffReader = new BufferedReader(reader);
	//
	// result = buffReader.readLine();
	// result = result.substring(result.indexOf(searchString) +
	// searchString.length(), result.length());
	// result = result.substring(0, result.indexOf('"'));
	// } catch (final MalformedURLException e) {
	// e.printStackTrace();
	// } catch (final IOException e) {
	// e.printStackTrace();
	// } catch (final NullPointerException e) {
	// e.printStackTrace();
	// }
	//
	// name = result;
	}

	public String getName() {
		Log.d("SayMyName", "Contact's name is: " + name + " number: " + incomingNumber + " bzw: " + number);

		try {
			if (name.contains("/")) {
				name = name.replaceAll("/", "");
			}

			context.openFileInput(name);
			return "";
		} catch (final FileNotFoundException e) {}

		if (UNKNOWN.equals(name)) {
			if (settings.isReadUnknown()) {
				return name;
			} else {
				if (settings.isReadNumber()) {
					return incomingNumber;
				} else {
					return "";
				}
			}
		} else {
			if (settings.isTransliterate()) {
				transliterate();
			}
			return name;
		}
	}

	public String getType() {
		// type to string
		return type;
	}
}