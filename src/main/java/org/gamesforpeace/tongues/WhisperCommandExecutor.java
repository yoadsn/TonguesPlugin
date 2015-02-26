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
	public static final int DEFAULT_WHISPER_RADIUS = 10;
	public static final String MSG_NOBODY_TO_WHISPER_TO = "There is no player to whisper to around you";
	public static final String ERR_EMPTY_MESSAGE = "You must provide a message to whisper";
	public static final String ERR_NOT_A_PLAYER_WHISPER = "The whisper command must come from a player";
	public static final String MSG_WHISPER_PREFIX_FMT = "%1s (whispered): %2s";
	public static final String ERR_NO_PERMISSION = "You do not have permission to perform this operation";

	ChatMessenger chatMsgr;
	TranslationRequestExecutor translationRequestExecutor;
	public String MSG_YOU_WHISPERED_PREFIX_FMT = "You whispered: %1s";
	private double radiusOfX = DEFAULT_WHISPER_RADIUS;
	private double radiusOfY = DEFAULT_WHISPER_RADIUS;
	private double radiusOfZ = DEFAULT_WHISPER_RADIUS;

	public WhisperCommandExecutor(ChatMessenger chatMsgr, TranslationRequestExecutor translationReqExecutor) {
		this(chatMsgr, translationReqExecutor, DEFAULT_WHISPER_RADIUS);
	}
	
	public WhisperCommandExecutor(ChatMessenger chatMsgr, TranslationRequestExecutor translationReqExecutor, int radiusToUse) {
		Validate.notNull(translationReqExecutor);
		Validate.notNull(chatMsgr);
		Validate.isTrue(radiusToUse > 0);
		
		this.translationRequestExecutor = translationReqExecutor;
		this.chatMsgr = chatMsgr;
		this.radiusOfX = this.radiusOfY = this.radiusOfZ = radiusToUse;
	}

	public WhisperCommandExecutor(ChatMessenger chatMsgr, TranslationRequestExecutor translationReqExecutor, double radiusOfX,
			double radiusOfY, double radiusOfZ) {
		Validate.notNull(translationReqExecutor);
		Validate.notNull(chatMsgr);
		this.radiusOfX = radiusOfX;
		this.radiusOfY = radiusOfY;
		this.radiusOfZ = radiusOfZ;

		this.translationRequestExecutor = translationReqExecutor;
		this.chatMsgr = chatMsgr;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		Boolean success = false;
		String responseMessage = "";

		// This command can only be received from a player
		if (!(sender instanceof Player)) {
			responseMessage = ERR_NOT_A_PLAYER_WHISPER;
		} else {
			Player playerSender = (Player)sender; 
			if (!sender.hasPermission("tongues.whisper")) {
				responseMessage = ERR_NO_PERMISSION;
			} else if (args.length == 0) {
				responseMessage = ERR_EMPTY_MESSAGE;
			} else {
				// Guesstimate the size of the final message
				StringBuilder sb = new StringBuilder(args.length * 4);
				for (String part : args) {
					sb.append(part);
					sb.append(" ");
				}

				String finalMessage = sb.toString().trim();

				if (finalMessage.isEmpty()) {
					responseMessage = ERR_EMPTY_MESSAGE;
				} else {
					Set<Player> playersInRange = getAnyPlayersInRange(playerSender);
					if (playersInRange.size() == 0) {
						responseMessage = MSG_NOBODY_TO_WHISPER_TO;
					} else {
						// Send the whispers
						chatMsgr.sendMessageSync(generateWhisperMessage(playerSender, finalMessage), playerSender, playersInRange);
						
						// Post translation requests for the whispers.
						translationRequestExecutor
								.postTranslationRequest(finalMessage, playerSender, playersInRange);

						// Prepare the confirmation message for the sender
						responseMessage = String.format(MSG_YOU_WHISPERED_PREFIX_FMT, finalMessage);
					}

					success = true;
				}
			}
		}

		sender.sendMessage(responseMessage);
		return success;

	}

	private String generateWhisperMessage(Player playerSender, String finalMessage) {
		return String.format(WhisperCommandExecutor.MSG_WHISPER_PREFIX_FMT, playerSender.getDisplayName(), finalMessage);
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
