package org.gamesforpeace.tongues;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.verification.VerificationModeFactory;

public class ListenCommandExecutorTest {

	String playerName = "playerName";
	Player executingPlayer;
	ConsoleCommandSender nonPlayerSender;
	ListenCommandExecutor sut;
	
	@Before
	public void setUp() throws Exception {
		
		executingPlayer = mock(Player.class);
		nonPlayerSender = mock(ConsoleCommandSender.class);
		
		when(executingPlayer.getDisplayName()).thenReturn(playerName);
		when(executingPlayer.isOnline()).thenReturn(true);
		when(executingPlayer.hasPermission("tongues.listen")).thenReturn(true);
		
		when(nonPlayerSender.hasPermission("tongues.listen")).thenReturn(true);
		
		sut = new ListenCommandExecutor();
	}
	
	@Test
	public void notWorkingFromConsole() {
		
		assertFalse(sut.onCommand(nonPlayerSender, null, null, new String[] { "on" }));
		
		verify(nonPlayerSender).sendMessage(ListenCommandExecutor.ERR_NOT_A_PLAYER_LISTEN);
		assertFalse(sut.getEnabledListeningPlayers().size() > 0);
	}
	
	@Test
	public void playerSenderNoPermissions() {
		when(executingPlayer.hasPermission("tongues.listen")).thenReturn(false);
		assertFalse(sut.onCommand(executingPlayer, null, null, new String[] { "on" }));
		
		verify(executingPlayer).sendMessage(ListenCommandExecutor.ERR_NO_PERMISSION);
		assertTrue(sut.getEnabledListeningPlayers().size() == 0);
	}
	
	@Test
	public void playerSenderInvalidSingleArg() {
		assertFalse(sut.onCommand(executingPlayer, null, null, new String[] { "ddd" }));
		
		verify(executingPlayer).sendMessage(ListenCommandExecutor.ERR_INVALID_COMMAND_ARGUMENTS);
	}
	
	@Test
	public void playerSenderInvalidSingleMultiArg() {
		assertFalse(sut.onCommand(executingPlayer, null, null, new String[] { "on", "sdfsd" }));
		
		verify(executingPlayer).sendMessage(ListenCommandExecutor.ERR_INVALID_COMMAND_ARGUMENTS);
	}
	
	@Test
	public void addingAPlayer() {
		
		assertTrue(sut.onCommand(executingPlayer, null, null, new String[] { "on" }));
		
		verify(executingPlayer).sendMessage(ListenCommandExecutor.MSG_LISTEN_ENABLED);
		assertEquals(1, sut.getEnabledListeningPlayers().size());
		assertTrue(sut.getEnabledListeningPlayers().contains(playerName));
	}
	
	@Test
	public void addingAPlayerIgnoresCase1() {
		
		assertTrue(sut.onCommand(executingPlayer, null, null, new String[] { "On" }));
		
		verify(executingPlayer).sendMessage(ListenCommandExecutor.MSG_LISTEN_ENABLED);
		assertEquals(1, sut.getEnabledListeningPlayers().size());
		assertTrue(sut.getEnabledListeningPlayers().contains(playerName));
	}
	
	@Test
	public void addingAPlayerIgnoresCase2() {
		
		assertTrue(sut.onCommand(executingPlayer, null, null, new String[] { "oN" }));
		
		verify(executingPlayer).sendMessage(ListenCommandExecutor.MSG_LISTEN_ENABLED);
		assertEquals(1, sut.getEnabledListeningPlayers().size());
		assertTrue(sut.getEnabledListeningPlayers().contains(playerName));
	}
	
	@Test
	public void addingAPlayerAgain() {
		
		sut.onCommand(executingPlayer, null, null, new String[] { "on" });
		assertTrue(sut.onCommand(executingPlayer, null, null, new String[] { "on" }));
		
		verify(executingPlayer, VerificationModeFactory.times(2)).sendMessage(ListenCommandExecutor.MSG_LISTEN_ENABLED);
		assertEquals(1, sut.getEnabledListeningPlayers().size());
		assertTrue(sut.getEnabledListeningPlayers().contains(playerName));
	}
	
	@Test
	public void removingAPLayer() {
		
		sut.onCommand(executingPlayer, null, null, new String[] { "on" });
		assertTrue(sut.onCommand(executingPlayer, null, null, new String[] { "off" }));
		
		verify(executingPlayer).sendMessage(ListenCommandExecutor.MSG_LISTEN_DISABLED);
		assertEquals(0, sut.getEnabledListeningPlayers().size());
		assertFalse(sut.getEnabledListeningPlayers().contains(playerName));
	}
	
	@Test
	public void removingAPLayerAgain() {
		
		sut.onCommand(executingPlayer, null, null, new String[] { "on" });
		sut.onCommand(executingPlayer, null, null, new String[] { "off" });
		assertTrue(sut.onCommand(executingPlayer, null, null, new String[] { "off" }));
		
		verify(executingPlayer, VerificationModeFactory.times(2)).sendMessage(ListenCommandExecutor.MSG_LISTEN_DISABLED);
		assertEquals(0, sut.getEnabledListeningPlayers().size());
		assertFalse(sut.getEnabledListeningPlayers().contains(playerName));
	}
	
	@Test
	public void removingAPLayerNeverEnabled() {
		
		assertTrue(sut.onCommand(executingPlayer, null, null, new String[] { "off" }));
		
		verify(executingPlayer).sendMessage(ListenCommandExecutor.MSG_LISTEN_DISABLED);
		assertEquals(0, sut.getEnabledListeningPlayers().size());
		assertFalse(sut.getEnabledListeningPlayers().contains(playerName));
	}
}
