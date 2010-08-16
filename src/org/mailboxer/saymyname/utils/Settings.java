package org.mailboxer.saymyname.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {
	public final static String SHARED_PREFERENCES = "org.mailboxer.saymyname.dessert";

	private final boolean respectSilent;
	private final boolean useRingtoneVolume;

	private final int wantedVolume;

	private final String callerFormat;
	private final int callerRepeatSeconds;
	private final int callerSpeechDelay;
	private final int callerRepeatTimes;

	private final boolean cutName;
	private final boolean cutNameAfterSpecialCharacters;

	private final int emailReadDelay;
	private final int emailSpeechDelay;
	private final boolean emailReadSubject;
	private final String emailFormat;
	private final boolean emailReadOnlyMyContacts;

	private final boolean readUnknown;
	private final boolean readNumber;
	private final boolean transliterate;
	private final boolean discreet;

	private final String smsFormat;
	private final boolean smsRead;
	private final int smsReadDelay;
	private final int smsSpeechDelay;

	private final String specialCharacters;
	private final boolean startSayCaller;
	private final boolean startSayEMail;
	private final boolean startSaySMS;
	private final boolean startSomething;

	private final int ringtoneIndex;

	public Settings(final Context context) throws Exception {
		final SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_WORLD_WRITEABLE);

		startSayCaller = preferences.getBoolean("saycaller", true);
		startSaySMS = preferences.getBoolean("saysms", true);
		startSayEMail = preferences.getBoolean("sayemail", true);
		startSomething = preferences.getBoolean("saysomething", true);

		useRingtoneVolume = preferences.getBoolean("useRingtoneVolume", true);
		respectSilent = preferences.getBoolean("respectSilent", true);
		wantedVolume = preferences.getInt("volume", 20);

		callerRepeatSeconds = Integer.valueOf(preferences.getString("callerRepeatSeconds", "3")) * 1000;
		callerSpeechDelay = Integer.valueOf(preferences.getString("callerSpeechDelay", "0")) * 1000;
		callerRepeatTimes = Integer.valueOf(preferences.getString("callerRepeatTimes", "6"));

		callerFormat = preferences.getString("callerFormat", "%");
		smsFormat = preferences.getString("smsFormat", "%");
		emailFormat = preferences.getString("emailFormat", "%");

		cutName = preferences.getBoolean("cutName", true);
		cutNameAfterSpecialCharacters = preferences.getBoolean("cutNameAfterSpecialCharacters", false);
		specialCharacters = preferences.getString("specialCharacters", ":/-(");

		readUnknown = preferences.getBoolean("readUnknown", true);
		readNumber = preferences.getBoolean("readNumber", false);
		transliterate = preferences.getBoolean("transliterate", false);
		discreet = preferences.getBoolean("discreet", false);

		smsRead = preferences.getBoolean("smsRead", false);
		smsReadDelay = Integer.valueOf(preferences.getString("smsReadDelay", "3")) * 1000;
		smsSpeechDelay = Integer.valueOf(preferences.getString("smsSpeechDelay", "0")) * 1000;

		emailReadSubject = preferences.getBoolean("emailReadSubject", false);
		emailReadDelay = Integer.valueOf(preferences.getString("emailReadDelay", "2")) * 1000;
		emailSpeechDelay = Integer.valueOf(preferences.getString("emailSpeechDelay", "0")) * 1000;
		emailReadOnlyMyContacts = preferences.getBoolean("emailReadOnlyMyContacts", false);

		ringtoneIndex = preferences.getInt("selectedRingtoneIndex", -42);
	}

	public String getCallerFormat() {
		return callerFormat;
	}

	public int getCallerRepeatSeconds() {
		return callerRepeatSeconds;
	}

	public int getCallerRepeatTimes() {
		return callerRepeatTimes;
	}

	public int getEMailReadDelay() {
		return emailReadDelay;
	}

	public String getEMailFormat() {
		return emailFormat;
	}

	public String getSmsFormat() {
		return smsFormat;
	}

	public int getSmsReadDelay() {
		return smsReadDelay;
	}

	public String getSpecialCharacters() {
		return specialCharacters;
	}

	public boolean isCutName() {
		return cutName;
	}

	public boolean isCutNameAfterSpecialCharacters() {
		return cutNameAfterSpecialCharacters;
	}

	public boolean isEMailReadSubject() {
		return emailReadSubject;
	}

	public boolean isReadNumber() {
		return readNumber;
	}

	public boolean isReadUnknown() {
		return readUnknown;
	}

	public boolean isSmsRead() {
		return smsRead;
	}

	public boolean isStartSayCaller() {
		return startSayCaller;
	}

	public boolean isStartSayEMail() {
		return startSayEMail;
	}

	public boolean isStartSaySMS() {
		return startSaySMS;
	}

	public boolean isStartSomething() {
		return startSomething;
	}

	public boolean isTransliterate() {
		return transliterate;
	}

	public boolean isDiscreet() {
		return discreet;
	}

	public int getRingtoneIndex() {
		return ringtoneIndex;
	}

	public boolean isRespectSilent() {
		return respectSilent;
	}

	public int getWantedVolume() {
		return wantedVolume;
	}

	public boolean isUseRingtoneVolume() {
		return useRingtoneVolume;
	}

	public int getCallerSpeechDelay() {
		return callerSpeechDelay;
	}

	public int getEmailSpeechDelay() {
		return emailSpeechDelay;
	}

	public int getSmsSpeechDelay() {
		return smsSpeechDelay;
	}

	public boolean isEmailReadOnlyMyContacts() {
		return emailReadOnlyMyContacts;
	}
}