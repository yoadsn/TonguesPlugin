package org.gamesforpeace.tongues;

import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

public interface ChatDestinationResolver {

	Player getOnlinePlayer(UUID id);
	
	Player getOnlinePlayer(String name);

	Set<Player> getAllOnlinePlayers();

	Set<Player> getGroupPlayers(String groupName);
}
