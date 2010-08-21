package org.mailboxer.saymyname.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class Transliterator {

	public static String transliterate(final String text) {
		final String searchString = "translatedText\":\"";
		String result = "";

		try {
			final String temp = URLEncoder.encode(text);
			final InputStreamReader reader = new InputStreamReader(new URL("http://ajax.googleapis.com/ajax/services/language/translate?v=1.0&q=" + temp + "&langpair=|en").openStream());
			final BufferedReader buffReader = new BufferedReader(reader);

			result = buffReader.readLine();
			result = result.substring(result.indexOf(searchString) + searchString.length(), result.length());
			result = result.substring(0, result.indexOf('"'));
		} catch (final MalformedURLException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final NullPointerException e) {
			e.printStackTrace();
		}

		return result;
	}
}