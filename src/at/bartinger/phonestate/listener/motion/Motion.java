package at.bartinger.phonestate.listener.motion;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Use the Motion to detect the motion of the phone
 * 
 * @author Dominic Bartl
 * 
 */

public class Motion implements SensorEventListener {

	private SensorManager mSensorMgr;

	private final MotionListener mShakeListener;
	private final Context mContext;

	public Motion(final Context context, final MotionListener listener) {
		mContext = context;
		resume();
		mShakeListener = listener;
	}

	/**
	 * If you have stoped the listening for the Accelerometer, you can resume it
	 * with resume();
	 */
	public void resume() {
		mSensorMgr = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
		if (mSensorMgr == null) {
			throw new UnsupportedOperationException("Sensors not supported");
		}
		final boolean supported = mSensorMgr.registerListener(this, mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
		if (!supported) {
			mSensorMgr.unregisterListener(this);
			throw new UnsupportedOperationException("Accelerometer not supported");
		}
	}

	/**
	 * Call the stop(); to unregister the Listener
	 */
	public void stop() {
		if (mSensorMgr != null) {
			mSensorMgr.unregisterListener(this);
			mSensorMgr = null;
		}
	}

	@Override
	public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
	// dont using
	}

	@Override
	public void onSensorChanged(final SensorEvent event) {

		final float x = event.values[SensorManager.DATA_X];
		final float y = event.values[SensorManager.DATA_Y];
		final float z = event.values[SensorManager.DATA_Z];

		// Just for displaying the values
		if (testDisplayDown(x, y, z)) {
			mShakeListener.onDisplayDown();
		} else if (testDisplayUp(x, y, z)) {
			mShakeListener.onDisplayUp();
		} else {
			mShakeListener.onRandomMotion();
		}
	}

	public float accuracy = 1.5f;

	private final float DisplayUpX = 0;
	private final float DisplayUpY = 0;
	private final float DisplayUpZ = 10;

	private boolean testDisplayUp(final float x, final float y, final float z) {
		if (x <= DisplayUpX + accuracy && x >= DisplayUpX - accuracy) {
			if (y <= DisplayUpY + accuracy && y >= DisplayUpY - accuracy) {
				if (z <= DisplayUpZ + accuracy && z >= DisplayUpZ - accuracy) {
					return true;
				}
			}
		}
		return false;
	}

	float DisplayDownX = 0;
	float DisplayDownY = 0;
	float DisplayDownZ = -10;

	private boolean testDisplayDown(final float x, final float y, final float z) {
		if (x <= DisplayDownX + accuracy && x >= DisplayDownX - accuracy) {
			if (y <= DisplayDownY + accuracy && y >= DisplayDownY - accuracy) {
				if (z <= DisplayDownZ + accuracy && z >= DisplayDownZ - accuracy) {
					return true;
				}
			}
		}
		return false;
	}
}
