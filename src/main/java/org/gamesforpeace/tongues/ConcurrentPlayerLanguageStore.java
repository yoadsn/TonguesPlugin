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
	private final ConcurrentHashMap<UUID, String> languageStore;
	private final String defaultLanguage;
	
	public ConcurrentPlayerLanguageStore(Set<String> supportedLnaguages, String defaultLanguage) {
		this.supportedLanguages = new HashSet<String>(supportedLnaguages);
		languageStore = new ConcurrentHashMap<UUID, String>();
		this.defaultLanguage = defaultLanguage;
	}
	
	public String getLanguageForPlayer(UUID playerId) {
		
		String playerStoredLang = languageStore.get(playerId);
		if (playerStoredLang == null) {
			playerStoredLang = defaultLanguage;
			languageStore.put(playerId, defaultLanguage);
		}

		return playerStoredLang;
	}

	public void setLanguageForPlayer(UUID playerId, String language) {
		languageStore.put(playerId, language);
	}
	
	public void clearLanguageForPlayer(UUID playerId) {
		languageStore.remove(playerId);
	}

	public Boolean isLanguageSupported(String language) {
		return supportedLanguages.contains(language);
	}
	
	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	public Map<UUID, String> getAllPlayerLanguages() {
		return Collections.unmodifiableMap(languageStore);
	}

	public void setPlayerLanguages(Map<UUID, String> playerLanguagesToSet) {
		for (Entry<UUID, String> languagePlayerSetting : playerLanguagesToSet.entrySet()) {
			setLanguageForPlayer(languagePlayerSetting.getKey(), languagePlayerSetting.getValue());
		}
	}
	
}
