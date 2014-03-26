package org.gamesforpeace.tongues.translation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.gamesforpeace.tongues.ChatMessenger;
import org.gamesforpeace.tongues.PlayerLanguageStore;

public class ChatTranslationRequestExecutor implements TranslationRequestExecutor {

	private final PlayerLanguageStore playerLanguageStore;
	private final Translator translator;
	private final ChatMessenger messenger;

	public ChatTranslationRequestExecutor(Translator translator, PlayerLanguageStore playerLanguageStore,
			ChatMessenger messenger) {
		Validate.notNull(translator, "A translator is expected");
		Validate.notNull(playerLanguageStore, "A language store is expected");
		Validate.notNull(messenger, "A messenger is required");

		this.translator = translator;
		this.playerLanguageStore = playerLanguageStore;
		this.messenger = messenger;
	}

	public void postTranslationRequest(String message, Player sourcePlayer, Set<Player> destinationPlayers) {

		HashSet<String> destLanguages = new HashSet<String>();
		HashMap<Player, String> languagesOfPlayers = new HashMap<Player, String>();
		HashMap<String, String> translationsOfMessages = new HashMap<String, String>();

		// Always treat the source player langauge as the default, causing auto detection of translation
		String langOfSourcePlayer = playerLanguageStore.getDefaultLanguage();

		// Extracting dest Languages
		for (Player destPlayer : destinationPlayers) {
			String langOfDestPlayer = playerLanguageStore.getLanguageForPlayer(destPlayer.getUniqueId());

			// A dest with the default lang - should not get a translation.
			if (!langOfDestPlayer.equalsIgnoreCase(playerLanguageStore.getDefaultLanguage())
					// And so does a dest with the same lang as the source
					&& !langOfDestPlayer.equalsIgnoreCase(langOfSourcePlayer)) {

				destLanguages.add(langOfDestPlayer);
				languagesOfPlayers.put(destPlayer, langOfDestPlayer);
			}
		}

		if (destLanguages.size() > 0)
			translationsOfMessages = getMessageTranslations(message, langOfSourcePlayer, destLanguages);

		// Sending Translations to players who need them.
		for (Entry<Player, String> destPlayerAndLang : languagesOfPlayers.entrySet()) {
			String translatedMessage = translationsOfMessages.get(destPlayerAndLang.getValue());

			if (translatedMessage != null && !translatedMessage.equals(message))
				messenger.sendMessage(translatedMessage, sourcePlayer, destPlayerAndLang.getKey());
		}

	}

	private HashMap<String, String> getMessageTranslations(String message, String sourceLanguage,
			HashSet<String> destLanguages) {

		HashMap<String, String> translationsOfMessages = new HashMap<String, String>();
		//
		for (String destLang : destLanguages) {
			String translatedMessage = translator.translate(message, sourceLanguage, destLang);
			if (translatedMessage != null) {
				translationsOfMessages.put(destLang, translatedMessage);
			}
		}

		return translationsOfMessages;
	}

}
