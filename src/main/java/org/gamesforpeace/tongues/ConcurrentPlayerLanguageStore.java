package org.gamesforpeace.tongues;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentPlayerLanguageStore implements PlayerLanguageStore {

	private final HashSet<String> supportedLanguages; 
	private final ConcurrentHashMap<String, String> languageStore;
	private final String defaultLanguage;
	
	public ConcurrentPlayerLanguageStore(Set<String> supportedLnaguages, String defaultLanguage) {
		this.supportedLanguages = new HashSet<String>(supportedLnaguages);
		languageStore = new ConcurrentHashMap<String, String>();
		this.defaultLanguage = defaultLanguage;
	}
	
	public String getLanguageForPlayer(String playerName) {
		
		String playerStoredLang = languageStore.get(playerName);
		if (playerStoredLang == null) {
			playerStoredLang = defaultLanguage;
			languageStore.put(playerName, defaultLanguage);
		}

		return playerStoredLang;
	}

	public void setLanguageForPlayer(String playerName, String language) {
		languageStore.put(playerName, language);
	}
	
	public void clearLanguageForPlayer(String playerName) {
		languageStore.remove(playerName);
	}

	public Boolean isLanguageSupported(String language) {
		return supportedLanguages.contains(language);
	}
	
	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	public Map<String, String> getAllPlayerLanguages() {
		return Collections.unmodifiableMap(languageStore);
	}

	public void setPlayerLanguages(Map<String, String> playerLanguagesToSet) {
		for (Entry<String, String> languagePlayerSetting : playerLanguagesToSet.entrySet()) {
			setLanguageForPlayer(languagePlayerSetting.getKey(), languagePlayerSetting.getValue());
		}
	}
	
}
