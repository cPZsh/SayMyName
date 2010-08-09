package org.mailboxer.saymyname.utils;

import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

public abstract class Sms {
	private static String number;
	private static String message;

	public static Sms getInstance(final Bundle extras) {
		if (Integer.parseInt(Build.VERSION.SDK) >= 4) {
			return new EclairSms(extras);
		} else {
			return new CupcakeSms(extras);
		}
	}

	public String getMessage() {
		return message;
	}

	public String getNumber() {
		return number;
	}

	private static class EclairSms extends Sms {
		public EclairSms(final Bundle extras) {
			final Object[] pdusObj = (Object[]) extras.get("pdus");

			final SmsMessage[] messages = new SmsMessage[pdusObj.length];
			for (int i = 0; i < pdusObj.length; i++) {
				messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
			}

			String temp = "";

			if (messages.length > 1) {
				for (final SmsMessage currentMessage : messages) {
					temp = temp + currentMessage.getDisplayMessageBody() + '\n';
				}
			} else {
				temp = messages[0].getDisplayMessageBody();
			}

			number = messages[0].getDisplayOriginatingAddress();
			message = temp;
		}
	}

	private static class CupcakeSms extends Sms {
		@SuppressWarnings("deprecation")
		public CupcakeSms(final Bundle extras) {
			final Object[] pdusObj = (Object[]) extras.get("pdus");

			final android.telephony.gsm.SmsMessage[] messages = new android.telephony.gsm.SmsMessage[pdusObj.length];
			for (int i = 0; i < pdusObj.length; i++) {
				messages[i] = android.telephony.gsm.SmsMessage.createFromPdu((byte[]) pdusObj[i]);
			}

			String temp = "";

			if (messages.length > 1) {
				for (final android.telephony.gsm.SmsMessage currentMessage : messages) {
					temp = temp + currentMessage.getDisplayMessageBody() + '\n';
				}
			} else {
				temp = messages[0].getDisplayMessageBody();
			}

			number = messages[0].getDisplayOriginatingAddress();
			message = temp;
		}
	}
}