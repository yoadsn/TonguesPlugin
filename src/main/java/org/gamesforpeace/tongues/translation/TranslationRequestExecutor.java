package org.gamesforpeace.tongues.translation;

import java.util.Set;

import org.bukkit.entity.Player;

public interface TranslationRequestExecutor {

	public void postTranslationRequest(String message, Player sourcePlayer, Set<Player> destinationPlayers);
}
