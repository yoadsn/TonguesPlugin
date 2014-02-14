/**
 * 
 */
package org.gamesforpeace.tongues;

import org.apache.commons.lang.Validate;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.gamesforpeace.tongues.translation.TranslationRequestExecutor;

/**
 * @author Yoad Snapir
 * This class acts on chat events from the Bukkit server
 */
public final class ChatListener implements Listener {
	
	private final TranslationRequestExecutor translationExecutor;
	
	public ChatListener(TranslationRequestExecutor translationExecutor) {
		Validate.notNull(translationExecutor, "A translation executor is expected");
		
		this.translationExecutor = translationExecutor;
	}
	
	/**
	 * Handles the AsyncPlayerChatEvent - Not required to be called when the chat event was canceled
	 * @param event
	 */
	@EventHandler(ignoreCancelled = true)
	public void onPlayerChatEvent(AsyncPlayerChatEvent event) {
		
		Validate.notNull(event, "Ignoring an unexpected null event");
		
		// Post a message translation job
		translationExecutor.postTranslationRequest(event.getMessage(), event.getPlayer(), event.getRecipients());
	}

}
