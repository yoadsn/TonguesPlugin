/**
 * 
 */
package org.gamesforpeace.tongues;

import java.util.Set;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
	private final CommandPoster commandPoster; 
	
	public ChatListener(TranslationRequestExecutor translationExecutor, CommandPoster commandPoster) {
		Validate.notNull(translationExecutor, "A translation executor is expected");
		Validate.notNull(commandPoster, "A command poster is expected");
		
		this.translationExecutor = translationExecutor;
		this.commandPoster = commandPoster;
	}
	
	/**
	 * Handles the AsyncPlayerChatEvent - Not required to be called when the chat event was canceled
	 * @param event
	 */
	@EventHandler(ignoreCancelled = true)
	public void onPlayerChatEvent(AsyncPlayerChatEvent event) {
		
		Validate.notNull(event, "Ignoring an unexpected null event");
		
		/*
		// Post a message translation job
		translationExecutor.postTranslationRequest(event.getMessage(), event.getPlayer(), event.getRecipients());
		*/
		
		event.setCancelled(true);
		
		commandPoster.postCommand(event.getPlayer(), "talk " + event.getMessage());
	}

}
