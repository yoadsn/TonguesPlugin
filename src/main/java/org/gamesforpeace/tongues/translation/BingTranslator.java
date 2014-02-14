package org.gamesforpeace.tongues.translation;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

public class BingTranslator implements Translator{
	
	private final HashSet<String> supportedLanguages;
	
	public BingTranslator(String clientID, String secretKey) {
		
		Validate.notEmpty(clientID);
		Validate.notEmpty(secretKey);
		
		// Setup the translator service.
		Translate.setClientId(clientID);
		Translate.setClientSecret(secretKey);
		
		// Populate supported languages list
		supportedLanguages = new HashSet<String>();
		for (Language lang : Language.values()) {
			supportedLanguages.add(lang.name().toLowerCase()); 
		}
	}

	/**
	 * Translate the incoming message assuming source language and into dest
	 */
	public String translate(String message, String sourceLang, String destLang) {
		
		//  No need to translate?
		if (sourceLang.equals(destLang)) {
			return message;
		}
		
		Language source = Language.valueOf(sourceLang.toUpperCase());
		Language dest = Language.valueOf(destLang.toUpperCase());
		
		try {
			return Translate.execute(message, source, dest);
		} catch (Exception e) {

			e.printStackTrace();
			
			return null;
		}
	}

	public Set<String> getSupportedLanguages() {
		return supportedLanguages;
	}

	public String getDefaultLanguage() {
		return Language.AUTO_DETECT.name();	
	}
	
}
