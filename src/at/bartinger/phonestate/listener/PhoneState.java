package at.bartinger.phonestate.listener;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import at.bartinger.phonestate.listener.motion.Motion;
import at.bartinger.phonestate.listener.motion.MotionListener;
import at.bartinger.phonestate.listener.screen.ScreenReceiver;
import at.bartinger.phonestate.listener.screen.ScreenStateListener;

public class PhoneState {

	public interface PhoneStateListener {

		public void onDisplayUp();

		public void onDisplayDown();

		public void onRandomMotion(boolean isScreenOn);

		public void onScreenOn();

		public void onScreenOff();

	}

	private final int DISPLAY_UP = 0;
	private final int DISPLAY_DOWN = 1;
	private final int DISPLAY_RANDROM = 2;
	private final int SCREEN_ON = 0;
	private final int SCREEN_OFF = 1;

	private final PhoneStateListener listener;

	private int lastMotionEvent = -1;
	private int lastScreenEvent = -1;

	public boolean isFlat = false;
	public boolean isDisplayUp = false;
	public boolean isDisplayDown = false;
	public boolean isScreenOn = true;

	private final Motion motion;

	public PhoneState(final Context context, final PhoneStateListener listener) {
		this.listener = listener;

		// INITIALIZE MOTION LISTENER
		motion = new Motion(context, new MotionListener() {

			@Override
			public void onDisplayUp() {
				if (lastMotionEvent != DISPLAY_UP) {
					lastMotionEvent = DISPLAY_UP;
					PhoneState.this.listener.onDisplayUp();
				}
				isFlat = true;
				isDisplayUp = true;
				isDisplayDown = false;
			}

			@Override
			public void onDisplayDown() {
				if (lastMotionEvent != DISPLAY_DOWN) {
					lastMotionEvent = DISPLAY_DOWN;
					PhoneState.this.listener.onDisplayDown();
				}
				isFlat = true;
				isDisplayUp = false;
				isDisplayDown = true;
			}

			@Override
			public void onRandomMotion() {
				if (lastMotionEvent != DISPLAY_RANDROM) {
					lastMotionEvent = DISPLAY_RANDROM;
					PhoneState.this.listener.onRandomMotion(isScreenOn);
				}
				isFlat = false;
				isDisplayDown = false;
				isDisplayUp = false;

			}
		});

		// INITIALIZE SCREEN RECEIVER
		final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);

		context.registerReceiver(new ScreenReceiver(new ScreenStateListener() {

			@Override
			public void onScreenOn() {
				if (lastScreenEvent != SCREEN_ON) {
					lastScreenEvent = SCREEN_ON;
					PhoneState.this.listener.onScreenOn();
				}
				isScreenOn = true;
			}

			@Override
			public void onScreenOff() {
				if (lastScreenEvent != SCREEN_OFF) {
					lastScreenEvent = SCREEN_OFF;
					PhoneState.this.listener.onScreenOff();
				}
				isScreenOn = false;
			}
		}), filter);

	}

	public void setAccuracy(final float accuracy) {
		motion.accuracy = accuracy;
	}

	public float getAccuracy() {
		return motion.accuracy;
	}

	public void stopAccelerometer() {
		motion.stop();
	}

	public void resumeAccelerometer() {
		motion.resume();
	}

}
