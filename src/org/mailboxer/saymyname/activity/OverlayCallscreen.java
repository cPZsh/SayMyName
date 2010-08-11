package org.mailboxer.saymyname.activity;

import org.mailboxer.saymyname.R;
import org.mailboxer.saymyname.utils.Settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class OverlayCallscreen extends Activity {
	private Thread thread;

	private TextView toast_text;

    private ImageView image;

    private Toast toast;

    private View layout;
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setVisible(false);

		final SharedPreferences preferences = getSharedPreferences(Settings.SHARED_PREFERENCES, Context.MODE_WORLD_WRITEABLE);
		if (!preferences.getBoolean("showSilenceScreen", false)) {
			finish();
			return;
		} else if (!preferences.getBoolean("saysomething", true)) {
			finish();
			return;
		}

		if (((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getCallState() != TelephonyManager.CALL_STATE_RINGING) {
			finish();
			return;
		}

//		setVisible(true);

//		toast = new Toast(this);
//
//		// example of how to make a custom toast
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//		// define some kind of toast_layout.xml file in your res/layout directory and inflate it here
		layout = inflater.inflate(R.layout.toast_layout, null);
		image = (ImageView) layout.findViewById(R.id.image_icon);
		toast_text = (TextView) layout.findViewById(R.id.toast_text);

//		// programmatically set parameters
//		toast.setGravity(Gravity.TOP, 0, 0);
//		toast.setDuration(Toast.LENGTH_SHORT);
//		toast.setView(layout);

		String text = "I'm a custom toast.";
		toast_text.setText(text);
		image.setImageResource(R.drawable.icon);

		setContentView(layout);
		
//		// and here is the hack
//		fireLongToast();
	}

	private void fireLongToast() {

		Thread t = new Thread() {
			public void run() {
				int count = 0;
				try {
					while (true && count < 10) {
						toast.show();
						sleep(1850);
						count++;

						// do some logic that breaks out of the while loop
					}
				} catch (Exception e) {
					Log.e("LongToast", "", e);
				}
			}
		};
		t.start();
	}
	
	@Override
	public void onUserInteraction() {
		Log.e("smn", "interaction");
		super.onUserInteraction();
	}
}

	//		setContentView(R.layout.main);
	//
	//		thread = new Thread() {
	//			@Override
	//			public void run() {
	//				try {
	//					for (int i = 0; i < 20; i++) {
	//						sleep(1000);
	//
	//						if (((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getCallState() != TelephonyManager.CALL_STATE_RINGING) {
	//							break;
	//						}
	//					}
	//				} catch (final InterruptedException e) {}
	//
	//				finish();
	//			}
	//		};
	//		thread.start();
	//	}
	//
	//	// @Override
	//	// protected void onNewIntent(final Intent intent) {
	//	// if (((TelephonyManager)
	//	// getSystemService(Context.TELEPHONY_SERVICE)).getCallState() !=
	//	// TelephonyManager.CALL_STATE_RINGING) {
	//	// shutdown();
	//	// }
	//	// }
	//
	//
	//	private void shutdown() {
	//		stopService(new Intent(this, ManagerService.class));
	//
	//		if (thread != null) {
	//			thread.interrupt();
	//		}
	//
	//		finish();
	//	}
	//}