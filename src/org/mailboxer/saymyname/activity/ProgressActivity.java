package org.mailboxer.saymyname.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.mailboxer.saymyname.R;
import org.mailboxer.saymyname.utils.Settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.google.tts.ConfigurationManager;

public class ProgressActivity extends Activity {
	private SharedPreferences shared;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.progressbar);
		setProgressBarIndeterminateVisibility(true);

		shared = getSharedPreferences(Settings.SHARED_PREFERENCES, Context.MODE_WORLD_WRITEABLE);

		checkOldVersion();

		saveSilentRingtone();

		if (!ttsInstalled()) {
			showDialog();
		} else {
			setResult(RESULT_OK);

			finish();
			return;
		}
	}

	private void checkOldVersion() {
		try {
			createPackageContext("org.mailboxer.android", 0);

			Toast.makeText(this, "You have installed two versions of SayMyName.", Toast.LENGTH_LONG).show();
			Toast.makeText(this, "Please uninstall the version called 'SayMyName'.", Toast.LENGTH_LONG).show();

			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=org.mailboxer.android")));
		} catch (final Exception e) {}
	}

	@Override
	protected void onResume() {
		checkOldVersion();
		if (!ttsInstalled()) {
			showDialog();
		} else {
			setResult(RESULT_OK);
			finish();
			return;
		}

		super.onResume();
	}

	private void showDialog() {
		final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Setup");
		dialog.setMessage("Unfortunately, you need to install one additional piece of software, to make SayMyName work correctly. Why? This additional software adds support for even more languages and all available Android versions! Sorry for the confusion.");
		dialog.setPositiveButton("Install via Market", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int whichButton) {
				final Uri marketUri = Uri.parse("market://details?id=com.google.tts");
				final Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
				startActivity(marketIntent);
			}
		});
		dialog.setNegativeButton("Install via Website", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int whichButton) {
				final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://eyes-free.googlecode.com/files/tts_2.0_market.apk"));
				startActivity(intent);
			}
		});
		dialog.show();
	}

	private boolean ttsInstalled() {
		try {
			createPackageContext("com.google.tts", 0);

			if (!ConfigurationManager.allFilesExist()) {
				try {
					final Intent intentTTS = new Intent();
					intentTTS.setComponent(new ComponentName("com.google.tts", "com.google.tts.ConfigurationManager"));

					startActivity(intentTTS);

					return false;
				} catch (final ActivityNotFoundException e) {
					e.printStackTrace();
				}
			}

			return true;
		} catch (final NameNotFoundException e) {
			return false;
		}
	}

	private void saveSilentRingtone() {
		File dir = null;
		if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
			// try {
			// final Method getExternalFilesDir =
			// Context.class.getMethod("getExternalFilesDir", new Class[]
			// {String.class});
			// dir = (File) getExternalFilesDir.invoke(null, "Ringtones");
			// } catch (final NoSuchMethodException nsme) {} catch (final
			// IllegalArgumentException e) {} catch (final
			// IllegalAccessException e) {} catch (final
			// InvocationTargetException e) {}
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

	@Override
	public void onBackPressed() {
		checkOldVersion();
		if (!ttsInstalled()) {
			showDialog();
		} else {
			setResult(RESULT_OK);
			finish();
			return;
		}

		Toast.makeText(this, "Please wait! I'm working.", Toast.LENGTH_LONG).show();
	}
}