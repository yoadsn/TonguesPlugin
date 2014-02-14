package org.gamesforpeace.tongues;

import java.util.HashSet;
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
	
	public String GetLanguageForPlayer(UUID playerUUID) {
		
		String playerStoredLang = languageStore.get(playerUUID);
		if (playerStoredLang == null) {
			playerStoredLang = defaultLanguage;
			languageStore.put(playerUUID, defaultLanguage);
		}

		return playerStoredLang;
	}

	public void setLanguageForPlayer(UUID playerUUID, String language) {
		
		languageStore.put(playerUUID, language);
		
	}
	
	public void clearLanguageForPlayer(UUID playerUUID) {
		languageStore.remove(playerUUID);
	}

	public Boolean isLanguageSupported(String language) {
		return supportedLanguages.contains(language);
	}
	
	public String getDefaultLanguage() {
		return defaultLanguage;
	}
	
}
