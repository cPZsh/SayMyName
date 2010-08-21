package org.mailboxer.saymyname.activity;

import org.mailboxer.saymyname.R;
import org.mailboxer.saymyname.receiver.GmailReceiver;
import org.mailboxer.saymyname.utils.Settings;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class SayMyName extends PreferenceActivity {
	private static final int saymynameRingtoneCode = 42;
	private static final int ringtoneCode = 43;

	private boolean progressSuccessful;

	private PreferenceScreen screen;
	private SharedPreferences shared;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		startProgress();

		getPreferenceManager().setSharedPreferencesName(Settings.SHARED_PREFERENCES);

		shared = getPreferenceManager().getSharedPreferences();

		if (!shared.getBoolean("2.7", false)) {
			final SharedPreferences.Editor editor = shared.edit();
			editor.putBoolean("showSilenceScreen", false);
			editor.putBoolean("2.7", true);
			editor.commit();
		}

		addPreferencesFromResource(R.xml.preferences);

		screen = getPreferenceScreen();

		screen.findPreference("contactChooser").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(final Preference preference) {
				startActivity(new Intent(SayMyName.this, ContactChooser.class));

				return false;
			}
		});

		screen.findPreference("ringtone").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(final Preference preference) {
				final SharedPreferences shared = preference.getSharedPreferences();
				if (shared.getBoolean(preference.getKey(), false)) {
					final Intent ringtoneIntent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
					ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
					ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "SayMyName Ringtone Chooser");
					ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);

					startActivityForResult(ringtoneIntent, saymynameRingtoneCode);
				} else {
					startActivityForResult(new Intent(RingtoneManager.ACTION_RINGTONE_PICKER), ringtoneCode);
				}

				return false;
			}
		});

		screen.findPreference("tts").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(final Preference preference) {
				final Intent intentTTS = new Intent();
				intentTTS.setComponent(new ComponentName("com.google.tts", "com.google.tts.ConfigurationManager"));

				try {
					startActivity(intentTTS);
				} catch (final ActivityNotFoundException e) {}

				return false;
			}
		});

		screen.findPreference("trouble").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(final Preference preference) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://code.google.com/p/roadtoadc/wiki/Troubleshooting")));

				return false;
			}
		});

		screen.findPreference("twitter").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(final Preference preference) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://twitter.com/TomTasche")));

				return false;
			}
		});

		screen.findPreference("translate").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(final Preference preference) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://code.google.com/p/roadtoadc/wiki/TranslateMe")));

				return false;
			}
		});

		screen.findPreference("blog").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(final Preference preference) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://code.google.com/p/roadtoadc/wiki/Contribute")));

				return false;
			}
		});

		screen.findPreference("donate").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(final Preference preference) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://code.google.com/p/roadtoadc/wiki/Donate")));

				return false;
			}
		});

		screen.findPreference("apps_opensource").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(final Preference preference) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://en.wikipedia.org/wiki/List_of_Open_Source_Android_Applications")));

				return false;
			}
		});

		screen.findPreference("apps_log").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(final Preference preference) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.appbrain.com/app/com.xtralogic.android.logcollector?install")));

				return false;
			}
		});

		screen.findPreference("apps_ringdroid").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(final Preference preference) {
				final Intent intentRingdroid = new Intent("android.intent.action.GET_CONTENT");
				final Uri ringtone = RingtoneManager.getActualDefaultRingtoneUri(SayMyName.this, RingtoneManager.TYPE_RINGTONE);
				intentRingdroid.setDataAndType(ringtone, "audio/");

				try {
					startActivity(intentRingdroid);
				} catch (final ActivityNotFoundException e) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.appbrain.com/app/com.ringdroid?install")));
				}

				return false;
			}
		});

		new GmailReceiver().onReceive(SayMyName.this, null);
	}

	private void startProgress() {
		final Intent progressIntent = new Intent(this, ProgressActivity.class);
		progressIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		progressIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		progressIntent.addFlags(65536); // Intent.FLAG_ACTIVITY_NO_ANIMATION
		progressIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(progressIntent);
	}

	private void showDonateDialog() {
		try {
			createPackageContext("org.mailboxer.saymyname.donate", 0);

			Toast.makeText(this, "Thanks for donating! :)", Toast.LENGTH_LONG).show();

			return;
		} catch (final Exception e) {}

		final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(R.string.dialog_donate_title);
		dialog.setMessage(R.string.dialog_donate_message);
		dialog.setPositiveButton(R.string.dialog_donate_button_ok, new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int whichButton) {
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=org.mailboxer.saymyname.donate")));
					Toast.makeText(getApplicationContext(), R.string.dialog_donate_toast_thanks, Toast.LENGTH_LONG).show();
					Toast.makeText(getApplicationContext(), R.string.dialog_donate_toast_info, Toast.LENGTH_LONG).show();
				} catch (final Exception e) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://code.google.com/p/roadtoadc/wiki/Donate")));
				}
			}
		});
		dialog.setNegativeButton("SayMyName...", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int whichButton) {}
		});
		dialog.show();
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if (resultCode != RESULT_CANCELED) {
			if (requestCode == saymynameRingtoneCode) {
				final Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

				final RingtoneManager ringManager = new RingtoneManager(getApplicationContext());
				if (uri != null) {
					final int index = ringManager.getRingtonePosition(uri);

					final SharedPreferences.Editor editor = shared.edit();
					editor.putInt("selectedRingtoneIndex", index);
					editor.commit();
				} else if (shared.getInt("silentRingtoneIndex", -42) >= 0) {
					final SharedPreferences.Editor editor = shared.edit();
					editor.putInt("selectedRingtoneIndex", -42);
					editor.commit();
				}

				RingtoneManager.setActualDefaultRingtoneUri(SayMyName.this, RingtoneManager.TYPE_RINGTONE, ringManager.getRingtoneUri(shared.getInt("silentRingtoneIndex", -42)));
			} else if (requestCode == ringtoneCode) {
				final Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

				if (uri != null) {
					RingtoneManager.setActualDefaultRingtoneUri(SayMyName.this, RingtoneManager.TYPE_RINGTONE, uri);

					final SharedPreferences.Editor editor = shared.edit();
					editor.putInt("selectedRingtoneIndex", -42);
					editor.commit();
				}
			}
		} else {
			if (requestCode == ringtoneCode) {
				((CheckBoxPreference) screen.findPreference("ringtone")).setChecked(true);
			} else {
				((CheckBoxPreference) screen.findPreference("ringtone")).setChecked(false);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.saymyname_menu, menu);

		return true;
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_donate: {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://code.google.com/p/roadtoadc/wiki/Donate")));
				return true;
			}

			case R.id.menu_about: {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/profiles/tomtasche?hl=de#about")));
				return true;
			}

			case R.id.menu_help: {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://code.google.com/p/roadtoadc/wiki/Troubleshooting")));
				return true;
			}

			case R.id.menu_share: {
				final Intent shareIntent = new Intent(Intent.ACTION_SEND);
				// shareIntent.putExtra(Intent.EXTRA_TEXT,
				// "http://bit.ly/SMNDessert");
				shareIntent.putExtra(Intent.EXTRA_TEXT, "http://www.appbrain.com/app/org.mailboxer.saymyname");
				shareIntent.setType("text/plain");
				shareIntent.addCategory(Intent.CATEGORY_DEFAULT);
				// startActivity(shareIntent);
				startActivity(Intent.createChooser(shareIntent, "sending mail"));

				return true;
			}

			default: {}
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onRestart() {
		if (!progressSuccessful) {
			startProgress();
		}

		super.onRestart();
	}

	@Override
	protected void onDestroy() {
		new GmailReceiver().onReceive(SayMyName.this, null);

		super.onDestroy();
	}
}