package org.mailboxer.saymyname.utils;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.media.AudioManager;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.SeekBar;

// from:
// http://groups.google.com/group/android-developers/browse_thread/thread/e49931fb0e203f64
public class SeekBarPreference extends DialogPreference {
	private final Context context;
	private SeekBar volumeLevel;

	public SeekBarPreference(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	@Override
	protected void onPrepareDialogBuilder(final Builder builder) {
		final LinearLayout layout = new LinearLayout(context);
		layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		layout.setMinimumWidth(400);
		layout.setPadding(20, 20, 20, 20);

		volumeLevel = new SeekBar(context);
		volumeLevel.setMax(((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		volumeLevel.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		volumeLevel.setProgress(getPersistedInt(20));

		layout.addView(volumeLevel);

		builder.setView(layout);

		super.onPrepareDialogBuilder(builder);
	}

	@Override
	protected void onDialogClosed(final boolean positiveResult) {
		if (positiveResult) {
			persistInt(volumeLevel.getProgress());
		}
	}
}