package org.gamesforpeace.tongues;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetLangCommandExecutor implements CommandExecutor {

	public static final String ERR_OWN_LANG_SETUP_MUST_COME_FROM_PLAYER = "Only a Player can act on its own language";
	public static final String ERR_SETUP_OF_LANGAUGE_FOR_OFFLINE_PLAYER_NOT_ALLOWED = "You can only setup a language for an online player";
	public static final String ERR_INVALID_COMMAND_ARGUMENTS = "The provided arguments are invalid";
	public static final String MSG_PLAYER_LANG_CHANGED_FMT = "The Langauge of player %1s changed to %2s";
	public static final String MSG_LANG_OF_PLAYER_IS_FMT = "The langauge of player %1s is %2s";
	public static final String MSG_NO_LANG_CONFIGURED_FOR_PLAYER_FMT = "No langauge is configured for player %1s";
	public static final String ERR_LANG_NOT_SUPPORTED_FMT = "The language %1s is not supported";
	public static final String MSG_CLEARED_SETUP_LANG = "Language setup was cleared";
	public static final String MSG_LANG_IS_ALREADY_SETUP_FTM = "Current language is already %1s";
	public static final String ERR_NO_PERMISSION = "You do not have the permissions to perform this operation";

	public final String CLEAR_SETUP_LANG = "none";
	public final String QUERY_SETUP_LANG = "?";

	PlayerLanguageStore langStore;
	Server server;

	public SetLangCommandExecutor(PlayerLanguageStore langStore, Server server) {
		this.langStore = langStore;
		this.server = server;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		Boolean success = false;
		String responseMessage = "";
		
		// Verify basic permission is present
		if (!sender.hasPermission("tongues.setlang")) {
			responseMessage = ERR_NO_PERMISSION;
		} else if (args.length > 2 || args.length == 0) {
			// Syntax check
			responseMessage = ERR_INVALID_COMMAND_ARGUMENTS;
		} else {

			Player subjectOfCommand = null;
			String changeToLang = null;

			// Which player to operate on?
			if (args.length == 2) {
				// This requires an additional permission
				if (!sender.hasPermission("tongues.setlang.others")) {
					
					responseMessage = ERR_NO_PERMISSION;
				} else {

					// Get the player from the server
					Player possibleSubject = server.getPlayer(args[0]);
					if (possibleSubject == null || !possibleSubject.isOnline()) {
						responseMessage = ERR_SETUP_OF_LANGAUGE_FOR_OFFLINE_PLAYER_NOT_ALLOWED;
					} else {
						subjectOfCommand = possibleSubject;
						changeToLang = args[1].toLowerCase();
					}
				}
			} else { // single arg
				// The sender is the subject
				if (!(sender instanceof Player)) {
					responseMessage = ERR_OWN_LANG_SETUP_MUST_COME_FROM_PLAYER;
				} else {
					subjectOfCommand = (Player) sender;
					changeToLang = args[0].toLowerCase();
				}
			}

			// We have a player to operate on
			if (subjectOfCommand != null) {

				String currLang = langStore.getLanguageForPlayer(subjectOfCommand.getUniqueId());

				if (changeToLang.equalsIgnoreCase(QUERY_SETUP_LANG)) {
					if (currLang.equalsIgnoreCase(langStore.getDefaultLanguage())) {
						responseMessage = String.format(MSG_NO_LANG_CONFIGURED_FOR_PLAYER_FMT,
								subjectOfCommand.getName());
					} else {
						responseMessage = String
								.format(MSG_LANG_OF_PLAYER_IS_FMT, subjectOfCommand.getName(), currLang);
					}
					success = true;
				
					// No need to set a new language
				} else if (currLang.equalsIgnoreCase(changeToLang)) { 
					responseMessage = String.format(MSG_LANG_IS_ALREADY_SETUP_FTM, changeToLang);
					
				} else if (changeToLang.equalsIgnoreCase(CLEAR_SETUP_LANG)) {
					langStore.clearLanguageForPlayer(subjectOfCommand.getUniqueId());
					responseMessage = MSG_CLEARED_SETUP_LANG;

					success = true;
				} else {

					if (!langStore.isLanguageSupported(changeToLang)) {
						responseMessage = String.format(ERR_LANG_NOT_SUPPORTED_FMT, changeToLang);

					} else {
						langStore.setLanguageForPlayer(subjectOfCommand.getUniqueId(), changeToLang);
						responseMessage = String.format(MSG_PLAYER_LANG_CHANGED_FMT, subjectOfCommand.getName(),
								changeToLang);

						success = true;
					}
				}
			}
		}

		sender.sendMessage(responseMessage);

		return success;
	}
}
