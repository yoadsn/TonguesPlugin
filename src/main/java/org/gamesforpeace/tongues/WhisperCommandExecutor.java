package org.gamesforpeace.tongues;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.gamesforpeace.tongues.translation.TranslationRequestExecutor;

public class WhisperCommandExecutor implements CommandExecutor {
	public static final double DEFAULT_WHISPER_RADIUS = 10;
	public static final String MSG_NOBODY_TO_WHISPER_TO = "There is no player to whisper to around you";
	public static final String ERRMSG_EMPTY_MESSAGE = "You must provide a message to whisper";
	public static final String ERRMSG_NOT_A_PLAYER_WHISPER = "The whisper command must come from a player";
	public static final String MSG_WHISPER_PREFIX_FMT = "%1s (whispered): %2s";

	TranslationRequestExecutor translationRequestExecutor;
	public String MSG_YOU_WHISPERED_PREFIX_FMT = "You whispered: %1s";
	private double radiusOfX = DEFAULT_WHISPER_RADIUS;
	private double radiusOfY = DEFAULT_WHISPER_RADIUS;
	private double radiusOfZ = DEFAULT_WHISPER_RADIUS;

	public WhisperCommandExecutor(TranslationRequestExecutor translationReqExecutor) {
		Validate.notNull(translationReqExecutor);

		this.translationRequestExecutor = translationReqExecutor;
	}

	public WhisperCommandExecutor(TranslationRequestExecutor translationReqExecutor, double radiusOfX,
			double radiusOfY, double radiusOfZ) {
		Validate.notNull(translationReqExecutor);
		this.radiusOfX = radiusOfX;
		this.radiusOfY = radiusOfY;
		this.radiusOfZ = radiusOfZ;

		this.translationRequestExecutor = translationReqExecutor;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		Boolean success = false;
		String message = "";

		// This command can only be received from a player
		if (!(sender instanceof Player)) {
			message = ERRMSG_NOT_A_PLAYER_WHISPER;
		} else {
			if (args.length == 0) {
				message = ERRMSG_EMPTY_MESSAGE;
			} else {
				// Guesstimate the size of the final message
				StringBuilder sb = new StringBuilder(args.length * 4);
				for (String part : args) {
					sb.append(part);
					sb.append(" ");
				}

				String finalMessage = sb.toString().trim();

				if (finalMessage.isEmpty()) {
					message = ERRMSG_EMPTY_MESSAGE;
				} else {
					Set<Player> playersInRange = getAnyPlayersInRange((Player) sender);
					if (playersInRange.size() == 0) {
						message = MSG_NOBODY_TO_WHISPER_TO;
					} else {
						// Send the whispers
						for (Player dest : playersInRange) {
							dest.sendMessage(String.format(WhisperCommandExecutor.MSG_WHISPER_PREFIX_FMT,
									((Player) sender).getDisplayName(), finalMessage));
						}
						// Post translation requests for the whispers.
						translationRequestExecutor
								.postTranslationRequest(finalMessage, (Player) sender, playersInRange);

						// Prepare the confirmation message for the sender
						message = String.format(MSG_YOU_WHISPERED_PREFIX_FMT, finalMessage);
					}

					success = true;
				}
			}
		}

		sender.sendMessage(message);
		return success;

	}

	private Set<Player> getAnyPlayersInRange(Player sender) {
		Set<Player> playersInRange = new HashSet<Player>();
		for (Entity possiblePlayer : sender.getNearbyEntities(radiusOfX, radiusOfY, radiusOfZ)) {
			if (possiblePlayer instanceof Player) {
				playersInRange.add((Player) possiblePlayer);
			}
		}

		return playersInRange;
	}

}
