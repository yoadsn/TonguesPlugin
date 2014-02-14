package org.gamesforpeace.tongues;

import java.util.UUID;

public interface PlayerLanguageStore {
	
	String GetLanguageForPlayer(UUID playerUUID);
	
	/**
	 * This method does not check to see if the language is supported or not.
	 * It is the callers responsibility to consult "IsLanguageSupported" if required.
	 * @param playerUUID
	 * @param language - assumed to always be a lowercase langauge name
	 * @return
	 */
	void setLanguageForPlayer(UUID playerUUID, String language);
	
	/**
	 * This method clears the setup language of a player
	 * @param playerUUID
	 * @return
	 */
	void clearLanguageForPlayer(UUID playerUUID);

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
}
