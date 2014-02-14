package org.gamesforpeace.tongues.translation;

import java.util.Set;

public interface Translator {
	public String translate(String message, String sourceLang, String destLang);
	
	public Set<String> getSupportedLanguages();
	
	public String getDefaultLanguage();
}
