package org.gamesforpeace.tongues;

import java.util.Set;

import org.bukkit.entity.Player;

public interface ChatMessenger {

	public void sendTranslatedMessageAsync(String message, Player source, Player dest);
	
	public void sendMessageSync(String message, Player source, Set<Player> dests);
}
