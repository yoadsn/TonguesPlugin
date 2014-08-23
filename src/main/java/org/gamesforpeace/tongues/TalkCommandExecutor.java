package org.gamesforpeace.tongues;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gamesforpeace.tongues.translation.TranslationRequestExecutor;

public class TalkCommandExecutor implements CommandExecutor {

	public static final String ERR_NOT_A_PLAYER_TALK = "This command must be sent by a player";
	public static final String TALK_TO_ALL_MODIFIER = "*";
	public static final String PERMISSION_TO_TALK = "tongues.talk";
	public static final String PERMISSION_TO_TALK_TO_ALL = "tongues.talk.all";
	public static final String ERR_NO_PERMISSION = "You do not have permission to perform this operation";

	private CommandPoster commandPoster;
	private ChatDestinationResolver destResolver;
	private ChatMessenger chatMessenger;
	private TranslationRequestExecutor translationRequestExecutor;

	public TalkCommandExecutor(CommandPoster cmdPoster, ChatDestinationResolver destResolver, ChatMessenger chatMsgr,
			TranslationRequestExecutor transReqExec) {
		this.commandPoster = cmdPoster;
		this.destResolver = destResolver;
		this.chatMessenger = chatMsgr;
		this.translationRequestExecutor = transReqExec;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (isPlayer(sender) && args.length > 0) {
			if (args[0].equalsIgnoreCase(TALK_TO_ALL_MODIFIER)) {
				return tryTalkingToAll(sender, args);
			} else {
				return tryTalkingToSomePlayers(sender, args);
			}
		}

		return false;
	}

	private boolean tryTalkingToSomePlayers(CommandSender sender, String[] args) {
		if (!permissionVerified(sender, PERMISSION_TO_TALK)) return false;

		Player destPlayer = destResolver.getOnlinePlayer(args[0]);

		if (destPlayer == null) {
			commandPoster.postCommand(sender, "whisper " + StringUtils.join(args, ' '));
			return true;
		} else {
			return tryTalkingToSpecificPlayer(sender, args, destPlayer);
		}
	}

	private boolean tryTalkingToSpecificPlayer(CommandSender sender, String[] args, Player destPlayer) {
		return internalTryTalking(sender, args, new HashSet<Player>(Arrays.asList(destPlayer)));
	}

	private boolean tryTalkingToAll(CommandSender sender, String[] args) {
		if (!permissionVerified(sender, PERMISSION_TO_TALK_TO_ALL)) return false;
		
		return internalTryTalking(sender, args, destResolver.getAllOnlinePlayers());
	}

	private boolean internalTryTalking(CommandSender sender, String[] args, Set<Player> destPlayers) {
		if (args.length < 2)
			return false;

		String msgToSend = StringUtils.join(ArrayUtils.subarray(args, 1, args.length), ' ');

		chatMessenger.sendMessageSync(msgToSend, (Player) sender, destPlayers);
		translationRequestExecutor.postTranslationRequest(msgToSend, (Player) sender, destPlayers);

		return true;
	}

	private boolean isPlayer(CommandSender sender) {
		if (sender instanceof Player)
			return true;

		sender.sendMessage(ERR_NOT_A_PLAYER_TALK);
		return false;
	}
	
	private boolean permissionVerified(CommandSender sender, String permissionToVerify) {
		if (!sender.hasPermission(permissionToVerify))
		{
			sender.sendMessage(ERR_NO_PERMISSION);
			return false;
		}
		return true;
	}
}
