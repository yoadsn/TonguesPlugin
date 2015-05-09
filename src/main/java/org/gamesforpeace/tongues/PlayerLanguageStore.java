package org.gamesforpeace.tongues;

import java.util.Map;
import java.util.UUID;

public interface PlayerLanguageStore {
	
	String getLanguageForPlayer(UUID playerId);
	
	/**
	 * This method does not check to see if the language is supported or not.
	 * It is the callers responsibility to consult "IsLanguageSupported" if required.
	 * @param playerId
	 * @param language - assumed to always be a lowercase langauge name
	 * @return
	 */
	void setLanguageForPlayer(UUID playerId, String language);
	
	/**
	 * This method clears the setup language of a player
	 * @param playerId
	 * @return
	 */
	void clearLanguageForPlayer(UUID playerId);

	/**
	 * Checks if this store supports the language
	 * @param language - assumed to always be a lowercase langauge name
	 * @return
	 */
	Boolean isLanguageSupported(String language);
	
	/**
	 * Gets the default language of the this language store
	 * @return the default language of the this language store
	 */
	String getDefaultLanguage();
	
	/**
	 * Gets all non-default player languages stored
	 * @return All stored non-default player langauges
	 */
	Map<UUID, String> getAllPlayerLanguages();
	
	/**
	 * Would use the set of values to override or add to the existing configuration
	 * @param playerLanguagesToSet - Assumed to contain valid languages
	 */
	void setPlayerLanguages(Map<UUID, String> playerLanguagesToSet);
}
