package org.gamesforpeace.tongues;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.hamcrest.core.IsNull;

public class GroupAdminCommandExecutor implements CommandExecutor {

	public static final String ERR_INVALID_ARGS_SUPPLIED = "Invalid arguments";

	public static final String ADD_COMMAND = "add";
	public static final String REMOVE_COMMAND = "remove";
	public static final String LIST_COMMAND = "list";
	public static final String ALL_PLACEHOLDER = "*";

	private final HashMap<String, HashSet<UUID>> groupsStore;

	public GroupAdminCommandExecutor(HashMap<String, HashSet<UUID>> groupsStore) {
		if (null == groupsStore)
			throw new IllegalArgumentException();
		this.groupsStore = groupsStore;
	}

	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		try {
			CommandArguments args = new CommandArguments(arg3);
			if (args.command.equals(ADD_COMMAND)) {
				if (args.isAllGroups) {
					for (String group : groupsStore.keySet()) {
						groupsStore.get(group).add(args.playerUUID);
					}
				} else {
					if (!groupsStore.containsKey(args.groupName)) {
						groupsStore.put(args.groupName, new HashSet<UUID>());
					}

					groupsStore.get(args.groupName).add(args.playerUUID);
				}
				return true;
			} else if (args.command.equals(REMOVE_COMMAND)) {
				if (args.isAllPlayers) {
					if (args.isAllGroups) {
						groupsStore.clear();
					}
					else {
						groupsStore.remove(args.groupName);	
					}
					
				} else {
					if (args.isAllGroups) {
						for (String group : groupsStore.keySet()) {
							groupsStore.get(group).remove(args.playerUUID);
						}
					} else {
						if (groupsStore.containsKey(args.groupName)) {
							groupsStore.get(args.groupName).remove(args.playerUUID);
							if (groupsStore.get(args.groupName).size() == 0) {
								groupsStore.remove(args.groupName);
							}
						}
					}
				}
				return true;
			} else if (args.command.equals(LIST_COMMAND)) {
				arg0.sendMessage("Note, This command is not yet implemeted - This is debug info. only.");
				StringBuilder result = new StringBuilder();
				for (String group : groupsStore.keySet()) {
					result.append(String.format("%1s", group));
					result.append(System.getProperty("line.separator"));
					HashSet<UUID> playersInGroup = groupsStore.get(group);
					for (UUID player : playersInGroup) {
				
						result.append(String.format("  %1s", player.toString()));
						result.append(System.getProperty("line.separator"));
					}
				}
				arg0.sendMessage(result.toString());
				return true;
			}
		} catch (Exception exception) {
		}

		if (arg0 != null) {
			arg0.sendMessage(GroupAdminCommandExecutor.ERR_INVALID_ARGS_SUPPLIED);
		}

		return false;
	}

	public void parseArgs(String[] args, String command) {

	}

	private class CommandArguments {
		public String command;
		public String groupName = null;
		public UUID playerUUID = null;
		public Boolean isAllPlayers = false;
		public Boolean isAllGroups = false;

		public CommandArguments(String[] args) {
			Boolean validParse = false;
			if (args.length > 0) {

				command = args[0];
				switch (command) {
				case ADD_COMMAND: {
					if (args.length == 3) {
						groupName = args[1];
						if (groupName.equals(ALL_PLACEHOLDER)) {
							isAllGroups = true;
						}

						validParse = true;

						if (!args[2].equals(ALL_PLACEHOLDER)) {
							playerUUID = UUID.fromString(args[2]);
						} else {
							validParse = false;
						}
					}
					break;
				}
				case REMOVE_COMMAND: {
					if (args.length == 3) {
						validParse = true;
						groupName = args[1];
						if (groupName.equals(ALL_PLACEHOLDER)) {
							isAllGroups = true;
						}

						if (!args[2].equals(ALL_PLACEHOLDER)) {
							playerUUID = UUID.fromString(args[2]);
						} else {
							isAllPlayers = true;
						}
					}
					break;
				}
				case LIST_COMMAND: {
					validParse = true;
					if (args.length > 1) {
						groupName = args[1];
						if (groupName.equals(ALL_PLACEHOLDER)) {
							isAllGroups = true;
						}

						if (args.length > 2 && groupName.equals(ALL_PLACEHOLDER)) {
							playerUUID = UUID.fromString(args[2]);
						} else {
							validParse = false;
						}
					}
					break;
				}
				}

				if (!validParse)
					throw new IllegalArgumentException();
			}
		}
	}

}
