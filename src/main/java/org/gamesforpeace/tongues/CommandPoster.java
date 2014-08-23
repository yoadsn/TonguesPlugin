package org.gamesforpeace.tongues;

import org.bukkit.command.CommandSender;

public interface CommandPoster {
	public void postCommand(CommandSender sender, String commandLine);
}
