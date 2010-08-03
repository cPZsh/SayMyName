package org.mailboxer.saymyname.widget;

import org.mailboxer.saymyname.R;
import org.mailboxer.saymyname.utils.Settings;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.Toast;

public class ToggleWidget extends AppWidgetProvider {
	public static String SMN_WIDGET_ACTION = "org.mailboxer.saymyname.widget.action";
	private RemoteViews remoteViews;

	@Override
	public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
		remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);

		final Intent active = new Intent(context, ToggleWidget.class);
		active.setAction(SMN_WIDGET_ACTION);

		final PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
		remoteViews.setOnClickPendingIntent(R.id.widget_button, actionPendingIntent);

		appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
	}

	@Override
	public void onReceive(final Context context, final Intent intent) {
		remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);

		// v1.5 fix that doesn't call onDelete Action
		final String action = intent.getAction();
		if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
			final int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

			if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
				onDeleted(context, new int[] {appWidgetId});
			}
		} else {
			if (intent.getAction().equals(SMN_WIDGET_ACTION)) {
				final SharedPreferences preferences = context.getSharedPreferences(Settings.SHARED_PREFERENCES, Context.MODE_WORLD_WRITEABLE);

				final boolean activated = preferences.getBoolean("saysomething", true);

				if (!activated) {
					Toast.makeText(context, "SayMyName activated", Toast.LENGTH_SHORT).show();
					remoteViews.setImageViewResource(R.id.widget_button, R.layout.activated);
				} else {
					Toast.makeText(context, "SayMyName deactivated", Toast.LENGTH_SHORT).show();
					remoteViews.setImageViewResource(R.id.widget_button, R.layout.deactivated);
				}

				final SharedPreferences.Editor editor = preferences.edit();
				editor.putBoolean("saysomething", !activated);
				editor.commit();
			}

			super.onReceive(context, intent);
		}
	}
}