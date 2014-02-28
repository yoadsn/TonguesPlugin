package org.gamesforpeace.tongues;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MyLangCommandExecutor implements CommandExecutor {

	public final String CLEAR_SETUP_LANG = "none";
	PlayerLanguageStore langStore;

	public MyLangCommandExecutor(PlayerLanguageStore langStore) {
		this.langStore = langStore;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		Boolean success = false;
		String responseMessage = "";

		if (!(sender instanceof Player)) {
			responseMessage = "Langauge setup should be performed by a player.";
		} else if (args.length > 1) {
			responseMessage = "Please specify only a single language to setup.";
		} else {

			Player player = (Player) sender;

			String currLang = langStore.getLanguageForPlayer(player.getUniqueId());

			if (args.length == 0) { // Querying the current setup language

				if (currLang.equalsIgnoreCase(langStore.getDefaultLanguage())) {
					responseMessage = "Your language is not setup.";
				} else {
					responseMessage = "Your current setup language is " + currLang;
				}
				success = true;
			} else { // Trying to set a new language
				String changeToLang = args[0].toLowerCase();

				if (currLang.equalsIgnoreCase(changeToLang)) { // No need to set a new language
					responseMessage = "Your current setup language is already " + changeToLang;
				} else {

					if (changeToLang.equalsIgnoreCase(CLEAR_SETUP_LANG)) {
						langStore.clearLanguageForPlayer(player.getUniqueId());
						responseMessage = "Cleared your setup language";
						
						success = true;
					} else {

						if (!langStore.isLanguageSupported(changeToLang)) {
							responseMessage = "The language " + changeToLang + " is not supported.";

						} else {
							langStore.setLanguageForPlayer(player.getUniqueId(), changeToLang);
							responseMessage = "Your setup language is now " + changeToLang;

							success = true;
						}
					}
				}
			}
		}

		sender.sendMessage(responseMessage);

		return success;
	}
}
