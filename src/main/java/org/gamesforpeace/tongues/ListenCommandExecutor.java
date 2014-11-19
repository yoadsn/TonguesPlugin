package org.gamesforpeace.tongues;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListenCommandExecutor implements CommandExecutor {

	public static final String ERR_NOT_A_PLAYER_LISTEN = "The listenr command must come from a player";
	public static final String ERR_NO_PERMISSION = "You do not have permission to perform this operation";
	public static final String ERR_INVALID_COMMAND_ARGUMENTS = "The provided argument is invalid";
	public static final String MSG_LISTEN_ENABLED = "Global chat listening is enabled";
	public static final String MSG_LISTEN_DISABLED = "Global chat listening is disabled";
	public final Set<String> enabledListeners;

	public ListenCommandExecutor() {
		enabledListeners = new HashSet<String>();
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		Boolean success = false;
		String responseMessage = "";

		// This command can only be received from a player
		if (!(sender instanceof Player)) {
			responseMessage = ERR_NOT_A_PLAYER_LISTEN;
		} else {
			Player playerSender = (Player) sender;
			if (!sender.hasPermission("tongues.listen")) {
				responseMessage = ERR_NO_PERMISSION;
			} else if (args.length != 1) {
				responseMessage = ERR_INVALID_COMMAND_ARGUMENTS;
			} else {
				if (args[0].toLowerCase().equals("on") || args[0].toLowerCase().equals("off")) {
					boolean mode = args[0].toLowerCase().equals("on");

					if (mode) {
						enabledListeners.add(playerSender.getDisplayName());
						responseMessage = MSG_LISTEN_ENABLED;
					} else {
						enabledListeners.remove(playerSender.getDisplayName());
						responseMessage = MSG_LISTEN_DISABLED;
					}
					
					success = true;
				} else {
					responseMessage = ERR_INVALID_COMMAND_ARGUMENTS;
				}
			}
		}

		sender.sendMessage(responseMessage);
		return success;
	}
	
	public Set<String> getEnabledListeningPlayers() {
		return new HashSet<String>(enabledListeners);
	}
	
	public void removeListeningPlayer(String name) {
		enabledListeners.remove(name);
	}
}
