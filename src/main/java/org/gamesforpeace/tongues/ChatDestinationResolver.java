package org.gamesforpeace.tongues;

import java.util.Set;

import org.bukkit.entity.Player;

public interface ChatDestinationResolver {

	Player getOnlinePlayer(String string);

	Set<Player> getAllOnlinePlayers();

	Set<Player> getGroupPlayers(String groupName);
}
