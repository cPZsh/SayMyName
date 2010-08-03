package org.mailboxer.saymyname.activity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.mailboxer.saymyname.R;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.Contacts.People;
import android.provider.Contacts.PeopleColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

// from: http://code.google.com/p/ringdroid/source/browse/trunk/src/com/ringdroid/ChooseContactActivity.java
@SuppressWarnings("deprecation")
public class ContactChooser extends ListActivity implements TextWatcher {
	private SimpleCursorAdapter mAdapter;
	private TextView mFilter;

	public ContactChooser() {}

	public void afterTextChanged(final Editable s) {
		final String filterStr = mFilter.getText().toString();
		mAdapter.changeCursor(createCursor(filterStr));
	}

	@Override
	public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {}

	private Cursor createCursor(final String filter) {
		String selection;
		if (filter != null && filter.length() > 0) {
			selection = "(DISPLAY_NAME LIKE \"%" + filter + "%\")";
		} else {
			selection = null;
		}
		final Cursor cursor = managedQuery(People.CONTENT_URI, new String[] {BaseColumns._ID, PeopleColumns.CUSTOM_RINGTONE, PeopleColumns.DISPLAY_NAME, PeopleColumns.LAST_TIME_CONTACTED, PeopleColumns.NAME, PeopleColumns.STARRED, PeopleColumns.TIMES_CONTACTED}, selection, null, "STARRED DESC, TIMES_CONTACTED DESC, LAST_TIME_CONTACTED DESC");

		return cursor;
	}

	private void markContact() {
		final Cursor c = mAdapter.getCursor();

		int dataIndex = c.getColumnIndexOrThrow(BaseColumns._ID);

		dataIndex = c.getColumnIndexOrThrow(PeopleColumns.DISPLAY_NAME);
		final String displayName = c.getString(dataIndex);

		try {
			openFileInput(displayName);
			deleteFile(displayName);
			Toast.makeText(this, displayName + " unselected. I will announce him!", Toast.LENGTH_LONG).show();
		} catch (final FileNotFoundException e) {
			try {
				final FileOutputStream stream = openFileOutput(displayName, Context.MODE_WORLD_WRITEABLE);
				stream.write(1);
				stream.close();
				Toast.makeText(this, displayName + " selected. I won't announce him!", Toast.LENGTH_LONG).show();
			} catch (final FileNotFoundException e1) {} catch (final IOException e1) {}
		}

		finish();
		return;
	}

	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);

		// Inflate our UI from its XML layout description.
		setContentView(R.layout.choose_contact);

		try {
			mAdapter = new SimpleCursorAdapter(this,
			// Use a template that displays a text view
			R.layout.contact_row,
			// Give the cursor to the list adatper
			createCursor(""),
			// Map from database columns...
			new String[] {PeopleColumns.CUSTOM_RINGTONE, PeopleColumns.STARRED, PeopleColumns.DISPLAY_NAME},
			// To widget ids in the row layout...
			new int[] {R.id.row_ringtone, R.id.row_starred, R.id.row_display_name});

			mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
				public boolean setViewValue(final View view, final Cursor cursor, final int columnIndex) {
					final String name = cursor.getColumnName(columnIndex);
					final String value = cursor.getString(columnIndex);
					if (name.equals(PeopleColumns.CUSTOM_RINGTONE)) {
						if (value != null && value.length() > 0) {
							view.setVisibility(View.VISIBLE);
						} else {
							view.setVisibility(View.INVISIBLE);
						}
						return true;
					}
					if (name.equals(PeopleColumns.STARRED)) {
						if (value != null && value.equals("1")) {
							view.setVisibility(View.VISIBLE);
						} else {
							view.setVisibility(View.INVISIBLE);
						}
						return true;
					}

					return false;
				}
			});

			setListAdapter(mAdapter);

			// On click, assign ringtone to contact
			getListView().setOnItemClickListener(new OnItemClickListener() {
				@SuppressWarnings("unchecked")
				public void onItemClick(final AdapterView parent, final View view, final int position, final long id) {
					markContact();
				}
			});

		} catch (final SecurityException e) {}

		mFilter = (TextView) findViewById(R.id.search_filter);
		if (mFilter != null) {
			mFilter.addTextChangedListener(this);
		}
	}

	@Override
	public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {}
}