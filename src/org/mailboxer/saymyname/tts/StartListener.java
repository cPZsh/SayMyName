package org.mailboxer.saymyname.tts;

import com.google.tts.TTS.InitListener;

@SuppressWarnings("deprecation")
public class StartListener implements InitListener {
	private final Speaker speaker;

	public StartListener(final Speaker speaker) {
		this.speaker = speaker;
	}

	@Override
	public void onInit(final int status) {
		speaker.start();
	}
}