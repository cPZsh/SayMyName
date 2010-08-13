package org.mailboxer.saymyname.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.mailboxer.saymyname.R;
import org.mailboxer.saymyname.receiver.GmailReceiver;
import org.mailboxer.saymyname.utils.Settings;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;
import android.widget.Toast;

import com.google.tts.ConfigurationManager;

public class SayMyName extends PreferenceActivity {
	private static final int saymynameRingtoneCode = 42;
	private static final int ringtoneCode = 43;

	private PreferenceScreen screen;
	private SharedPreferences shared;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		showDonateDialog();

		getPreferenceManager().setSharedPreferencesName(Settings.SHARED_PREFERENCES);

		shared = getPreferenceManager().getSharedPreferences();

		if (!shared.getBoolean("2.7", false)) {
			final SharedPreferences.Editor editor = shared.edit();
			editor.putBoolean("showSilenceScreen", false);
			editor.putBoolean("2.7", true);
			editor.commit();
		}

		try {
			createPackageContext("org.mailboxer.android", 0);

			Toast.makeText(SayMyName.this, "You are using an old version of SayMyName.", Toast.LENGTH_LONG).show();
			Toast.makeText(SayMyName.this, "Please uninstall 'SayMyName'.", Toast.LENGTH_LONG).show();

			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=org.mailboxer.android")));

			finish();
			return;
		} catch (final Exception e) {}

		try {
			createPackageContext("com.google.tts", 0);

			if (!ConfigurationManager.allFilesExist()) {
				try {
					final Intent intentTTS = new Intent();
					intentTTS.setComponent(new ComponentName("com.google.tts", "com.google.tts.ConfigurationManager"));

					startActivity(intentTTS);

					finish();
					return;
				} catch (final ActivityNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (final NameNotFoundException e) {
			final Uri marketUri = Uri.parse("market://details?id=com.google.tts");
			final Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
			startActivity(marketIntent);

			finish();
			return;
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
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://code.google.com/p/roadtoadc/wiki/HelpHelpHelp")));

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
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.appbrain.com/app/org.mailboxer.saymyname.donate?install")));

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

		saveSilentRingtone();
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

	private void saveSilentRingtone() {
		new Thread() {
			@Override
			public void run() {
				File dir;
				if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
					// dir =
					// getExternalFilesDir(Environment.DIRECTORY_RINGTONES);
					// dirs = new File[] {new
					// File("/mnt/sdcard/Android/data/org.mailboxer.saymyname/files/Ringtones"),
					// new
					// File("/mnt/sdcard/Android/data/org.mailboxer.saymyname/files/Notifications")};
					dir = new File("/mnt/sdcard/Android/data/org.mailboxer.saymyname/files/Ringtones");
				} else {
					// dirs = new File[] {new
					// File("/sdcard/media/audio/ringtones"), new
					// File("/sdcard/media/audio/notifications")};
					dir = new File("/sdcard/media/audio/ringtones");
				}
				dir.mkdirs();

				boolean doesntExist = true;
				final File file = new File(dir + File.separator + "silent.mp3");

				if (!file.exists()) {
					doesntExist = true;
				} else {
					if (shared.getInt("silentRingtoneIndex", -42) != -42) {
						doesntExist = false;
					}
				}

				if (doesntExist) {
					int deleted = getContentResolver().delete(Media.INTERNAL_CONTENT_URI, MediaColumns.TITLE + "=" + "'Silent'", null);
					Log.d("SayMyName", "deleted " + deleted + " internal 'silent'-ringtones");
					deleted = getContentResolver().delete(Media.EXTERNAL_CONTENT_URI, MediaColumns.TITLE + "=" + "'Silent'", null);
					Log.d("SayMyName", "deleted " + deleted + " external 'silent'-ringtones");

					try {
						final InputStream input = getResources().openRawResource(R.raw.silent);
						final FileOutputStream output = new FileOutputStream(file);

						int bytal;
						do {
							bytal = input.read();
							output.write(bytal);
						} while (bytal != -1);

						input.close();
						output.close();

						final ContentValues values = new ContentValues();
						values.put(MediaColumns.DATA, file.getAbsolutePath());
						values.put(MediaColumns.TITLE, "Silent");
						values.put(MediaColumns.SIZE, 18432);
						values.put(MediaColumns.MIME_TYPE, "audio/mp3");
						values.put(AudioColumns.ARTIST, "TomTasche");
						values.put(AudioColumns.ALBUM, "SayMyName");
						values.put(AudioColumns.DURATION, 1071);
						values.put(AudioColumns.IS_RINGTONE, true);
						values.put(AudioColumns.IS_NOTIFICATION, true);
						values.put(AudioColumns.IS_ALARM, false);
						values.put(AudioColumns.IS_MUSIC, false);

						final Uri silentUri = MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath());
						final Uri newUri = getContentResolver().insert(silentUri, values);

						final SharedPreferences.Editor editor = shared.edit();
						final RingtoneManager ringManager = new RingtoneManager(getApplicationContext());
						final int index = ringManager.getRingtonePosition(newUri);
						editor.putInt("silentRingtoneIndex", index);
						editor.commit();
					} catch (final FileNotFoundException e) {} catch (final IOException e) {} catch (final NullPointerException e) {}
				}
			}
		}.start();
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
	protected void onDestroy() {
		new GmailReceiver().onReceive(SayMyName.this, null);

		super.onDestroy();
	}
}