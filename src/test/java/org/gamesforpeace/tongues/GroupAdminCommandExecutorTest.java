package org.gamesforpeace.tongues;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

public class GroupAdminCommandExecutorTest {
	
	private HashMap<String, HashSet<UUID>> mockGroupStore;
	private UUID lookedUpPlayerId = UUID.randomUUID();
	private Player lookedUpPlayer;
	private Server mockedServer;
	private GroupAdminCommandExecutor sut;
	private CommandSender sender;
	
	@Before
	public void setUp() throws Exception {
		
		mockGroupStore = new HashMap<String, HashSet<UUID>>();
		mockedServer = mock(Server.class);
		sut = new GroupAdminCommandExecutor(mockGroupStore);
		sender = mock(CommandSender.class);
		
		lookedUpPlayer = mock(Player.class);
		
		when(mockedServer.getPlayer(lookedUpPlayerId)).thenReturn(lookedUpPlayer);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void groupStoreNullThrows() {
		new GroupAdminCommandExecutor(null);
	}
	
	@Test
	public void noArgsThrows() {
		sut.onCommand(sender, null, null, new String[] {});
		verify(sender).sendMessage(GroupAdminCommandExecutor.ERR_INVALID_ARGS_SUPPLIED);
	}
	
	@Test
	public void addNoArgThrows() {
		sut.onCommand(sender, null, null,
				new String[] { GroupAdminCommandExecutor.ADD_COMMAND });
		verify(sender).sendMessage(GroupAdminCommandExecutor.ERR_INVALID_ARGS_SUPPLIED);
	}
	
	@Test
	public void addSingleArgThrows() {
		sut.onCommand(sender, null, null,
				new String[] { GroupAdminCommandExecutor.ADD_COMMAND, "grp" });
		verify(sender).sendMessage(GroupAdminCommandExecutor.ERR_INVALID_ARGS_SUPPLIED);
	}
	
	@Test
	public void addWildPlayernameThrows() {
		sut.onCommand(sender, null, null,
				new String[] { GroupAdminCommandExecutor.ADD_COMMAND, "grp", GroupAdminCommandExecutor.ALL_PLACEHOLDER });
		verify(sender).sendMessage(GroupAdminCommandExecutor.ERR_INVALID_ARGS_SUPPLIED);
	}
	
	@Test
	public void addNonUUIDPlayernameThrows() {
		sut.onCommand(sender, null, null,
				new String[] { GroupAdminCommandExecutor.ADD_COMMAND, "grp", "ss" });
		verify(sender).sendMessage(GroupAdminCommandExecutor.ERR_INVALID_ARGS_SUPPLIED);
	}
	
	@Test
	public void removeNoArgThrows() {
		sut.onCommand(sender, null, null,
				new String[] { GroupAdminCommandExecutor.REMOVE_COMMAND });
		verify(sender).sendMessage(GroupAdminCommandExecutor.ERR_INVALID_ARGS_SUPPLIED);
	}
	
	@Test
	public void removeSingleArgThrows() {
		sut.onCommand(sender, null, null,
				new String[] { GroupAdminCommandExecutor.REMOVE_COMMAND, "grp" });
		verify(sender).sendMessage(GroupAdminCommandExecutor.ERR_INVALID_ARGS_SUPPLIED);
	}
	
	@Test
	public void removeNonUUIDPlayerThrows() {
		sut.onCommand(sender, null, null,
				new String[] { GroupAdminCommandExecutor.REMOVE_COMMAND, "grp", "dd" });
		verify(sender).sendMessage(GroupAdminCommandExecutor.ERR_INVALID_ARGS_SUPPLIED);
	}
	
	@Test
	public void listPlayerWIthoutWildGroupThrows() {
		sut.onCommand(sender, null, null,
				new String[] { GroupAdminCommandExecutor.LIST_COMMAND, "grp", lookedUpPlayerId.toString() });
		verify(sender).sendMessage(GroupAdminCommandExecutor.ERR_INVALID_ARGS_SUPPLIED);
	}
	
	@Test
	public void addPlayerToExistingGroup() {
		mockGroupStore.put("grp", new HashSet<UUID>());
		sut.onCommand(null, null, null,
				new String[] {GroupAdminCommandExecutor.ADD_COMMAND, "grp", lookedUpPlayerId.toString()});
		
		assertTrue(mockGroupStore.get("grp").contains(lookedUpPlayerId));
	}
	
	@Test
	public void addPlayerAlreadyInGroup() {
		mockGroupStore.put("grp", new HashSet<UUID>(Arrays.asList(lookedUpPlayerId)));
		sut.onCommand(null, null, null,
				new String[] {GroupAdminCommandExecutor.ADD_COMMAND, "grp", lookedUpPlayerId.toString()});
		
		assertTrue(mockGroupStore.get("grp").contains(lookedUpPlayerId));
	}
	
	@Test
	public void addPlayerGroupDoesNotExist() {
		sut.onCommand(null, null, null,
				new String[] {GroupAdminCommandExecutor.ADD_COMMAND, "grp", lookedUpPlayerId.toString()});
		
		assertTrue(mockGroupStore.get("grp").contains(lookedUpPlayerId));
	}
	
	@Test
	public void addPlayerToAllGroups() {
		mockGroupStore.put("grp", new HashSet<UUID>());
		mockGroupStore.put("grp2", new HashSet<UUID>());
		sut.onCommand(null, null, null,
				new String[] {GroupAdminCommandExecutor.ADD_COMMAND, "*", lookedUpPlayerId.toString()});
		
		assertTrue(mockGroupStore.get("grp").contains(lookedUpPlayerId));
		assertTrue(mockGroupStore.get("grp2").contains(lookedUpPlayerId));
	}
	
	@Test
	public void removePlayerFromGroup() {
		mockGroupStore.put("grp", new HashSet<UUID>(Arrays.asList(lookedUpPlayerId, UUID.randomUUID())));
		sut.onCommand(null, null, null,
				new String[] {GroupAdminCommandExecutor.REMOVE_COMMAND, "grp", lookedUpPlayerId.toString()});
		
		assertFalse(mockGroupStore.get("grp").contains(lookedUpPlayerId));
	}
	
	@Test
	public void removePlayerFromGroupNotInGroup() {
		mockGroupStore.put("grp", new HashSet<UUID>(Arrays.asList(UUID.randomUUID())));
		sut.onCommand(null, null, null,
				new String[] {GroupAdminCommandExecutor.REMOVE_COMMAND, "grp", lookedUpPlayerId.toString()});
		
		assertFalse(mockGroupStore.get("grp").contains(lookedUpPlayerId));
	}
	
	@Test
	public void removePlayerFromGroupStaysInOtherGroups() {
		mockGroupStore.put("grp", new HashSet<UUID>(Arrays.asList(lookedUpPlayerId, UUID.randomUUID())));
		mockGroupStore.put("grp2", new HashSet<UUID>(Arrays.asList(lookedUpPlayerId)));
		sut.onCommand(null, null, null,
				new String[] {GroupAdminCommandExecutor.REMOVE_COMMAND, "grp", lookedUpPlayerId.toString()});
		
		assertFalse(mockGroupStore.get("grp").contains(lookedUpPlayerId));
		assertTrue(mockGroupStore.get("grp2").contains(lookedUpPlayerId));
	}
	
	@Test
	public void removePlayerFromAllGroups() {
		mockGroupStore.put("grp", new HashSet<UUID>(Arrays.asList(lookedUpPlayerId, UUID.randomUUID())));
		mockGroupStore.put("grp2", new HashSet<UUID>(Arrays.asList(lookedUpPlayerId, UUID.randomUUID())));
		sut.onCommand(null, null, null,
				new String[] {GroupAdminCommandExecutor.REMOVE_COMMAND, "*", lookedUpPlayerId.toString()});
		
		assertFalse(mockGroupStore.get("grp").contains(lookedUpPlayerId));
		assertFalse(mockGroupStore.get("grp2").contains(lookedUpPlayerId));
	}
	
	@Test
	public void removeAllPlayersFromGroup() {
		mockGroupStore.put("grp", new HashSet<UUID>(Arrays.asList(lookedUpPlayerId, UUID.randomUUID())));
		sut.onCommand(null, null, null,
				new String[] {GroupAdminCommandExecutor.REMOVE_COMMAND, "grp", "*"});
		
		assertFalse(mockGroupStore.containsKey("grp"));
	}
	
	@Test
	public void removeWildGroupWildPlayer() {
		mockGroupStore.put("grp", new HashSet<UUID>(Arrays.asList(lookedUpPlayerId)));
		mockGroupStore.put("grp2", new HashSet<UUID>(Arrays.asList(lookedUpPlayerId)));
		sut.onCommand(null, null, null,
				new String[] {GroupAdminCommandExecutor.REMOVE_COMMAND, "*", "*"});
		
		assertFalse(mockGroupStore.containsKey("grp"));
		assertFalse(mockGroupStore.containsKey("grp2"));
	}
	
	@Test
	public void listReturnsAnything() {
		mockGroupStore.put("grp", new HashSet<UUID>(Arrays.asList(lookedUpPlayerId)));
		mockGroupStore.put("grp2", new HashSet<UUID>(Arrays.asList(lookedUpPlayerId)));
		sut.onCommand(sender, null, null,
				new String[] {GroupAdminCommandExecutor.LIST_COMMAND});
	}
}
