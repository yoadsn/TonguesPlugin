package org.gamesforpeace.tongues;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

public class SetLangCommandExecutorTest {

	PlayerLanguageStore langStore;
	SetLangCommandExecutor sut;
	Player player;
	String playerName = "playerName";
	UUID playerId = UUID.randomUUID();
	String executingPlayerName = "execplayerName";
	Player executingPlayer;
	UUID executingPlayerId = UUID.randomUUID();
	Server mockedServer;
	String prevPlayerLang = "english";
	String newPlayerLang = "hebrew";
	ConsoleCommandSender nonPlayerSender;
	
	@Before
	public void setUp() throws Exception {
		langStore = mock(PlayerLanguageStore.class);
		mockedServer = mock(Server.class);
		
		player = mock(Player.class);
		executingPlayer = mock(Player.class);
		nonPlayerSender = mock(ConsoleCommandSender.class);
		
		when(player.getName()).thenReturn(playerName);
		when(player.getUniqueId()).thenReturn(playerId);
		when(player.isOnline()).thenReturn(true);
		
		when(executingPlayer.getName()).thenReturn(playerName);
		when(executingPlayer.getUniqueId()).thenReturn(executingPlayerId);
		when(executingPlayer.isOnline()).thenReturn(true);
		when(executingPlayer.hasPermission("tongues.setlang")).thenReturn(true);
		
		when(mockedServer.getPlayer(playerName)).thenReturn(player);
		
		when(langStore.getDefaultLanguage()).thenReturn("");
		when(langStore.getLanguageForPlayer(playerId)).thenReturn(prevPlayerLang);
		when(langStore.getLanguageForPlayer(executingPlayerId)).thenReturn(prevPlayerLang);
		when(langStore.isLanguageSupported(newPlayerLang)).thenReturn(true);
		
		when(nonPlayerSender.hasPermission("tongues.setlang")).thenReturn(true);
		when(nonPlayerSender.hasPermission("tongues.setlang.others")).thenReturn(true);
		
		sut = new SetLangCommandExecutor(langStore, mockedServer);
	}
	
	/**
	 * Command syntax checks
	 */
	
	@Test
	public void testCommandTooFewArgs() {
		
		assertFalse(sut.onCommand(executingPlayer, null, null, new String[0]));
		
		verify(executingPlayer).sendMessage(SetLangCommandExecutor.ERR_INVALID_COMMAND_ARGUMENTS);
		verifyZeroInteractions(langStore);
	}
	
	@Test
	public void testCommandTooManyArgs() {
		
		assertFalse(sut.onCommand(executingPlayer, null, null, new String[3]));
		
		verify(executingPlayer).sendMessage(SetLangCommandExecutor.ERR_INVALID_COMMAND_ARGUMENTS);
		verifyZeroInteractions(langStore);
	}
	
	/**
	 * Tests for console activated command
	 */
	
	@Test
	public void testSetupOwnLanguageFromConsoleNotAllowed() {
		
		assertFalse(sut.onCommand(nonPlayerSender, null, null, new String[] { newPlayerLang }));
		
		verify(nonPlayerSender).sendMessage(SetLangCommandExecutor.ERR_OWN_LANG_SETUP_MUST_COME_FROM_PLAYER);
		verifyZeroInteractions(langStore);
	}
	
	/**
	 * Test for command operated on other player by console
	 */
	
	@Test
	public void queryOtherPlayerLangNotSetupfromConsole() {
		
		when(langStore.getLanguageForPlayer(playerId)).thenReturn("");
		
		assertTrue(sut.onCommand(nonPlayerSender, null, null, new String[] { playerName, "?" }));
		
		verify(nonPlayerSender).sendMessage(String.format(SetLangCommandExecutor.MSG_NO_LANG_CONFIGURED_FOR_PLAYER_FMT, playerName));
		verify(langStore).getLanguageForPlayer(playerId);
	}
	
	@Test
	public void queryOtherPlayerLangSetupfromConsole() {
		
		
		when(langStore.getLanguageForPlayer(playerId)).thenReturn(newPlayerLang);
		
		assertTrue(sut.onCommand(nonPlayerSender, null, null, new String[] { playerName, "?" }));
		
		verify(nonPlayerSender).sendMessage(String.format(SetLangCommandExecutor.MSG_LANG_OF_PLAYER_IS_FMT, playerName, newPlayerLang));
		verify(langStore).getLanguageForPlayer(playerId);
	}
	
	@Test
	public void setupOtherOfflinePlayerNotAllowedfromConsole() {
		
		when(player.isOnline()).thenReturn(false);
		
		assertFalse(sut.onCommand(nonPlayerSender, null, null, new String[] {playerName, newPlayerLang}));
		
		verify(nonPlayerSender).sendMessage(SetLangCommandExecutor.ERR_SETUP_OF_LANGAUGE_FOR_OFFLINE_PLAYER_NOT_ALLOWED);
		verifyZeroInteractions(langStore);
	}
	
	@Test
	public void setupOtherPlayerLanguagefromConsole() {
		
		assertTrue(sut.onCommand(nonPlayerSender, null, null, new String[] {playerName, newPlayerLang}));
		
		verify(nonPlayerSender).sendMessage(String.format(SetLangCommandExecutor.MSG_PLAYER_LANG_CHANGED_FMT, playerName, newPlayerLang));
		verify(langStore).setLanguageForPlayer(playerId, newPlayerLang);
	}
	
	/**
	 * Test for command operated on other player by player
	 */
	
	@Test
	public void noPermissionsToSetOtherPlayerLang() {
		when(executingPlayer.hasPermission("tongues.setlang.others")).thenReturn(false);
		
		assertFalse(sut.onCommand(executingPlayer, null, null, new String[] { "" , "" }));
		
		verify(executingPlayer).sendMessage(SetLangCommandExecutor.ERR_NO_PERMISSION);
		verifyZeroInteractions(langStore);
	}
	
	@Test
	public void queryOtherPlayerLangNotSetup() {
		when(executingPlayer.hasPermission("tongues.setlang.others")).thenReturn(true);
		when(langStore.getLanguageForPlayer(playerId)).thenReturn("");
		
		assertTrue(sut.onCommand(executingPlayer, null, null, new String[] { playerName, "?" }));
		
		verify(executingPlayer).sendMessage(String.format(SetLangCommandExecutor.MSG_NO_LANG_CONFIGURED_FOR_PLAYER_FMT, playerName));
		verify(langStore).getLanguageForPlayer(playerId);
	}
	
	@Test
	public void queryOtherPlayerLangSetup() {
		when(executingPlayer.hasPermission("tongues.setlang.others")).thenReturn(true);
		when(langStore.getLanguageForPlayer(playerId)).thenReturn(newPlayerLang);
		
		assertTrue(sut.onCommand(executingPlayer, null, null, new String[] { playerName, "?" }));
		
		verify(executingPlayer).sendMessage(String.format(SetLangCommandExecutor.MSG_LANG_OF_PLAYER_IS_FMT, playerName, newPlayerLang));
		verify(langStore).getLanguageForPlayer(playerId);
	}
	
	@Test
	public void setupOtherOfflinePlayerNotAllowed() {
		when(executingPlayer.hasPermission("tongues.setlang.others")).thenReturn(true);
		when(player.isOnline()).thenReturn(false);
		
		assertFalse(sut.onCommand(executingPlayer, null, null, new String[] {playerName, newPlayerLang}));
		
		verify(executingPlayer).sendMessage(SetLangCommandExecutor.ERR_SETUP_OF_LANGAUGE_FOR_OFFLINE_PLAYER_NOT_ALLOWED);
		verifyZeroInteractions(langStore);
	}
	
	@Test
	public void setupOtherPlayerLanguage() {
		when(executingPlayer.hasPermission("tongues.setlang.others")).thenReturn(true);
		assertTrue(sut.onCommand(executingPlayer, null, null, new String[] {playerName, newPlayerLang}));
		
		verify(executingPlayer).sendMessage(String.format(SetLangCommandExecutor.MSG_PLAYER_LANG_CHANGED_FMT, playerName, newPlayerLang));
		verify(langStore).setLanguageForPlayer(playerId, newPlayerLang);
	}
	
	/**
	 * Test for command operated on self
	 */
	
	@Test
	public void noPermissionsToSetOwnLang() {
		when(executingPlayer.hasPermission("tongues.setlang")).thenReturn(false);
		
		assertFalse(sut.onCommand(executingPlayer, null, null, new String[] { "" }));
		
		verify(executingPlayer).sendMessage(SetLangCommandExecutor.ERR_NO_PERMISSION);
		verifyZeroInteractions(langStore);
	}
	
	@Test
	public void queryOwnLangNotSetup() {
		
		when(langStore.getLanguageForPlayer(executingPlayerId)).thenReturn("");
		
		assertTrue(sut.onCommand(executingPlayer, null, null, new String[] { "?" }));
		
		verify(executingPlayer).sendMessage(String.format(SetLangCommandExecutor.MSG_NO_LANG_CONFIGURED_FOR_PLAYER_FMT, playerName));
		verify(langStore).getLanguageForPlayer(executingPlayerId);
	}
	
	@Test
	public void queryOwnLangSetup() {
		
		when(langStore.getLanguageForPlayer(executingPlayerId)).thenReturn(newPlayerLang);
	
		assertTrue(sut.onCommand(executingPlayer, null, null, new String[] { "?" }));
		
		verify(executingPlayer).sendMessage(String.format(SetLangCommandExecutor.MSG_LANG_OF_PLAYER_IS_FMT, playerName, newPlayerLang));
		verify(langStore).getLanguageForPlayer(executingPlayerId);
	}
	
	/**
	 * Language related checks
	 */
	
	@Test
	public void languageNotSupported() {
		
		when(langStore.isLanguageSupported(newPlayerLang)).thenReturn(false);
		
		assertFalse(sut.onCommand(executingPlayer, null, null, new String[] { newPlayerLang }));
		
		verify(executingPlayer).sendMessage(String.format(SetLangCommandExecutor.ERR_LANG_NOT_SUPPORTED_FMT, newPlayerLang));
		verify(langStore).isLanguageSupported(newPlayerLang);
		verify(langStore, never()).setLanguageForPlayer(Matchers.any(UUID.class), anyString());
	}
	
	@Test
	public void clearingLanguageSetup() {
		
		assertTrue(sut.onCommand(executingPlayer, null, null, new String[] { sut.CLEAR_SETUP_LANG }));
		
		verify(executingPlayer).sendMessage(SetLangCommandExecutor.MSG_CLEARED_SETUP_LANG);
		verify(langStore, never()).isLanguageSupported(anyString());
		verify(langStore).clearLanguageForPlayer(executingPlayer.getUniqueId());
	}
	
	@Test
	public void languagePrevLanguageEqualsNewLanguage() {
		
		when(langStore.getLanguageForPlayer(executingPlayerId)).thenReturn(newPlayerLang);
		
		assertFalse(sut.onCommand(executingPlayer, null, null, new String[] { newPlayerLang }));
		
		verify(executingPlayer).sendMessage(String.format(SetLangCommandExecutor.MSG_LANG_IS_ALREADY_SETUP_FTM, newPlayerLang));
		verify(langStore, never()).isLanguageSupported(newPlayerLang);
		verify(langStore, never()).setLanguageForPlayer(Matchers.any(UUID.class), anyString());
	}
	
	@Test
	public void languagePrevLanguageUnequalsNewLanguage() {
		
		assertTrue(sut.onCommand(executingPlayer, null, null, new String[] { newPlayerLang }));
		
		verify(executingPlayer).sendMessage(String.format(SetLangCommandExecutor.MSG_PLAYER_LANG_CHANGED_FMT, playerName, newPlayerLang));
		
		verify(langStore).getLanguageForPlayer(executingPlayerId);
		verify(langStore).isLanguageSupported(newPlayerLang);
		verify(langStore).setLanguageForPlayer(executingPlayerId, newPlayerLang);
	}
	
	@Test
	public void languageArgumentCaseInsensitve() {
		
		sut.onCommand(executingPlayer, null, null, new String[] { "aBcDeF" });
		
		verify(langStore).isLanguageSupported("abcdef");
	}

}
