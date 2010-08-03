package org.mailboxer.saymyname.utils;

public class Formatter {
	private final Settings settings;
	private final String text;

	public Formatter(final String text, final Settings settings) {
		this.text = text;
		this.settings = settings;
	}

	public String format() {
		String formatted = text;

		if (settings.isCutName()) {
			formatted = text.split(" ")[0];
		}

		if (settings.isCutNameAfterSpecialCharacters()) {
			formatted = simpleSplit(text, settings.getSpecialCharacters());
		}

		return formatted;
	}

	private String simpleSplit(final String source, final String specialCharacters) {
		// Cut string after first special character
		// Standard split function uses regular expression, which
		// casue problems while splitting by '\' '-' '.' etc.

		final StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < source.length(); i++) {
			for (int j = 0; j < specialCharacters.length(); j++) {
				if (source.charAt(i) == specialCharacters.charAt(j)) {
					return stringBuilder.toString();
				}
			}

			stringBuilder.append(source.charAt(i));
		}

		return stringBuilder.toString();
	}
}