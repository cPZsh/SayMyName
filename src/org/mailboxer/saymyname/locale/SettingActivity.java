package org.mailboxer.saymyname.locale;

import org.mailboxer.saymyname.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.TextView;

public class SettingActivity extends Activity {
	private boolean isCancelled = false;

	@Override
	public void finish() {
		if (isCancelled) {
			setResult(RESULT_CANCELED);
		} else {
			final boolean startCaller = ((CheckBox) findViewById(R.id.enableCallerCheck)).isChecked();
			final boolean startSMS = ((CheckBox) findViewById(R.id.enableSMSCheck)).isChecked();
			final boolean startEMail = ((CheckBox) findViewById(R.id.enableEMailCheck)).isChecked();

			// This is the store-and-forward Intent to ourselves.
			final Intent returnIntent = new Intent();

			// this extra is the data to ourselves: either for the Activity or
			// the BroadcastReceiver
			returnIntent.putExtra("org.mailboxer.saymyname.extra.START_CALLER", startCaller);
			returnIntent.putExtra("org.mailboxer.saymyname.extra.START_SMS", startSMS);
			returnIntent.putExtra("org.mailboxer.saymyname.extra.START_EMAIL", startEMail);

			String blurb;
			if (startCaller) {
				blurb = getResources().getString(R.string.preference_saycaller_title) + " " + getResources().getString(R.string.locale_enabled) + "; ";
			} else {
				blurb = getResources().getString(R.string.preference_saycaller_title) + " " + getResources().getString(R.string.locale_disabled) + "; ";
			}

			if (startSMS) {
				blurb += getResources().getString(R.string.preference_saysms_title) + " " + getResources().getString(R.string.locale_enabled);
			} else {
				blurb += getResources().getString(R.string.preference_saysms_title) + " " + getResources().getString(R.string.locale_disabled);
			}

			if (startEMail) {
				blurb += getResources().getString(R.string.preference_sayemail_title) + " " + getResources().getString(R.string.locale_enabled);
			} else {
				blurb += getResources().getString(R.string.preference_sayemail_title) + " " + getResources().getString(R.string.locale_disabled);
			}

			// this is the blurb shown in the Locale UI
			if (blurb.length() > com.twofortyfouram.Intent.MAXIMUM_BLURB_LENGTH) {
				returnIntent.putExtra(com.twofortyfouram.Intent.EXTRA_STRING_BLURB, blurb.substring(0, com.twofortyfouram.Intent.MAXIMUM_BLURB_LENGTH));
			} else {
				returnIntent.putExtra(com.twofortyfouram.Intent.EXTRA_STRING_BLURB, blurb);
			}

			setResult(RESULT_OK, returnIntent);
		}

		super.finish();
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.locale_activity);

		// Set up the breadcrumbs in the titlebar
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.locale_ellipsizing_title);

		String breadcrumbString = getIntent().getStringExtra(com.twofortyfouram.Intent.EXTRA_STRING_BREADCRUMB);

		// Locale guarantees that the breadcrumb string will be present, but
		// checking for null anyway makes your Activity more
		// robust and re-usable
		if (breadcrumbString == null) {
			breadcrumbString = getString(R.string.app_name);
		} else {
			breadcrumbString = String.format("%s%s%s", breadcrumbString, com.twofortyfouram.Intent.BREADCRUMB_SEPARATOR, getString(R.string.app_name)); //$NON-NLS-1$
		}

		((TextView) findViewById(R.id.locale_ellipsizing_title_text)).setText(breadcrumbString);
		setTitle(breadcrumbString); // although not actually necessary, this is
		// helpful when starting sub-Activities

		// if savedInstanceState == null, then we are entering the Activity
		// directly from Locale and we need to check whether the
		// Intent has data or is clean (e.g. whether we editing an old setting
		// or creating a new one)
		if (savedInstanceState == null) {
			((CheckBox) findViewById(R.id.enableCallerCheck)).setChecked(getIntent().getBooleanExtra("org.mailboxer.saymyname.extra.START_CALLER", false));
			((CheckBox) findViewById(R.id.enableSMSCheck)).setChecked(getIntent().getBooleanExtra("org.mailboxer.saymyname.extra.START_SMS", false));
			((CheckBox) findViewById(R.id.enableEMailCheck)).setChecked(getIntent().getBooleanExtra("org.mailboxer.saymyname.extra.START_EMAIL", false));
		}
		// if savedInstanceState != null, there is no need to restore any
		// Activity state directly (e.g. onSaveInstanceState()).
		// This is handled by the TextView automatically.

		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.locale_menu, menu);

		return true;
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_dontsave: {
				isCancelled = true;
				finish();
				return true;
			}

			case R.id.menu_save: {
				finish();
				return true;
			}

			case R.id.menu_help: {
				final Intent helpIntent = new Intent(com.twofortyfouram.Intent.ACTION_HELP);

				helpIntent.putExtra("com.twofortyfouram.locale.intent.extra.HELP_URL", "http://code.google.com/p/roadtoadc/wiki/LocalePluginHelp"); //$NON-NLS-1$ //$NON-NLS-2$

				// insert the breadcrumbs. Note: the title was set in onCreate
				helpIntent.putExtra(com.twofortyfouram.Intent.EXTRA_STRING_BREADCRUMB, getTitle().toString());

				startActivity(helpIntent);
				return true;
			}
			default: {
				// we shouldn't ever fall through to this
			}
		}

		return super.onMenuItemSelected(featureId, item);
	}
}
