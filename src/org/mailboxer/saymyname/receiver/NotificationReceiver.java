package org.mailboxer.saymyname.receiver;

import org.mailboxer.saymyname.service.ManagerService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
	public static final String NOTIFICATION_BROADCAST = "org.mailboxer.saymyname.notification.clicked";

	@Override
	public void onReceive(final Context context, final Intent intent) {
		context.stopService(new Intent(context, ManagerService.class));
	}
}