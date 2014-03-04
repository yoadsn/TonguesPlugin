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
	Player executingPlayer;
	Server mockedServer;
	UUID playerUUID;
	String prevPlayerLang = "english";
	String newPlayerLang = "hebrew";
	
	@Before
	public void setUp() throws Exception {
		langStore = mock(PlayerLanguageStore.class);
		mockedServer = mock(Server.class);
		player = mock(Player.class);
		executingPlayer = mock(Player.class);
		playerUUID = UUID.randomUUID();
		
		when(player.getUniqueId()).thenReturn(playerUUID);
		when(player.getName()).thenReturn(playerName);
		when(langStore.getDefaultLanguage()).thenReturn("");
		when(langStore.getLanguageForPlayer(playerUUID)).thenReturn(prevPlayerLang);
		when(langStore.isLanguageSupported(newPlayerLang)).thenReturn(true);
		
		sut = new SetLangCommandExecutor(langStore, mockedServer);
	}
	
	/**
	 * Command syntax checks
	 */
	
	@Test
	public void testCommandTooFewArgs() {
		
		assertFalse(sut.onCommand(player, null, null, new String[0]));
		
		verify(player).sendMessage(SetLangCommandExecutor.ERR_INVALID_COMMAND_ARGUMENTS);
		verifyZeroInteractions(langStore);
	}
	
	@Test
	public void testCommandTooManyArgs() {
		
		assertFalse(sut.onCommand(player, null, null, new String[3]));
		
		verify(player).sendMessage(SetLangCommandExecutor.ERR_INVALID_COMMAND_ARGUMENTS);
		verifyZeroInteractions(langStore);
	}
	
	/**
	 * Tests for console activated command
	 */
	
	@Test
	public void testSetupOwnLanguageFromConsoleNotAllowed() {
		
		ConsoleCommandSender nonPlayerSender = mock(ConsoleCommandSender.class);
		
		assertFalse(sut.onCommand(nonPlayerSender, null, null, new String[] { newPlayerLang }));
		
		verify(nonPlayerSender).sendMessage(SetLangCommandExecutor.ERR_OWN_LANG_SETUP_MUST_COME_FROM_PLAYER);
		verifyZeroInteractions(langStore);
	}
	
	/**
	 * Test for command operated on other player
	 */
	
	@Test
	public void queryOtherPlayerLangNotSetup() {
		
		ConsoleCommandSender nonPlayerSender = mock(ConsoleCommandSender.class);
		when(mockedServer.getPlayer(playerName)).thenReturn(player);
		when(player.isOnline()).thenReturn(true);
		when(langStore.getLanguageForPlayer(playerUUID)).thenReturn("");
		
		assertTrue(sut.onCommand(nonPlayerSender, null, null, new String[] { playerName, "?" }));
		
		verify(nonPlayerSender).sendMessage(String.format(SetLangCommandExecutor.MSG_NO_LANG_CONFIGURED_FOR_PLAYER_FMT, playerName));
		verify(langStore).getLanguageForPlayer(playerUUID);
	}
	
	@Test
	public void queryOtherPlayerLangSetup() {
		
		ConsoleCommandSender nonPlayerSender = mock(ConsoleCommandSender.class);
		when(mockedServer.getPlayer(playerName)).thenReturn(player);
		when(player.isOnline()).thenReturn(true);
		when(langStore.getLanguageForPlayer(playerUUID)).thenReturn(newPlayerLang);
		
		assertTrue(sut.onCommand(nonPlayerSender, null, null, new String[] { playerName, "?" }));
		
		verify(nonPlayerSender).sendMessage(String.format(SetLangCommandExecutor.MSG_LANG_OF_PLAYER_IS_FMT, playerName, newPlayerLang));
		verify(langStore).getLanguageForPlayer(playerUUID);
	}
	
	@Test
	public void setupOtherOfflinePlayerNotAllowed() {
		
		when(mockedServer.getPlayer(playerName)).thenReturn(player);
		when(player.isOnline()).thenReturn(false);
		
		assertFalse(sut.onCommand(executingPlayer, null, null, new String[] {playerName, newPlayerLang}));
		
		verify(executingPlayer).sendMessage(SetLangCommandExecutor.ERR_SETUP_OF_LANGAUGE_FOR_OFFLINE_PLAYER_NOT_ALLOWED);
		verifyZeroInteractions(langStore);
	}
	
	@Test
	public void setupOtherPlayerLanguage() {
		
		ConsoleCommandSender nonPlayerSender = mock(ConsoleCommandSender.class);
		when(mockedServer.getPlayer(playerName)).thenReturn(player);
		when(player.getUniqueId()).thenReturn(playerUUID);
		when(player.isOnline()).thenReturn(true);
		
		assertTrue(sut.onCommand(nonPlayerSender, null, null, new String[] {playerName, newPlayerLang}));
		
		verify(nonPlayerSender).sendMessage(String.format(SetLangCommandExecutor.MSG_PLAYER_LANG_CHANGED_FMT, playerName, newPlayerLang));
		verify(langStore).setLanguageForPlayer(playerUUID, newPlayerLang);
	}
	
	/**
	 * Test for command operated on self
	 */
	
	@Test
	public void queryOwnLangNotSetup() {
		
		when(langStore.getLanguageForPlayer(playerUUID)).thenReturn("");
		
		assertTrue(sut.onCommand(player, null, null, new String[] { "?" }));
		
		verify(player).sendMessage(String.format(SetLangCommandExecutor.MSG_NO_LANG_CONFIGURED_FOR_PLAYER_FMT, playerName));
		verify(langStore).getLanguageForPlayer(playerUUID);
	}
	
	@Test
	public void queryOwnLangSetup() {
		
		when(langStore.getLanguageForPlayer(playerUUID)).thenReturn(newPlayerLang);
	
		assertTrue(sut.onCommand(player, null, null, new String[] { "?" }));
		
		verify(player).sendMessage(String.format(SetLangCommandExecutor.MSG_LANG_OF_PLAYER_IS_FMT, playerName, newPlayerLang));
		verify(langStore).getLanguageForPlayer(playerUUID);
	}
	
	/**
	 * Language related checks
	 */
	
	@Test
	public void testLanguageNotSupported() {
		
		when(langStore.isLanguageSupported(newPlayerLang)).thenReturn(false);
		
		assertFalse(sut.onCommand(player, null, null, new String[] { newPlayerLang }));
		
		verify(player).sendMessage(String.format(SetLangCommandExecutor.ERR_LANG_NOT_SUPPORTED_FMT, newPlayerLang));
		verify(langStore).isLanguageSupported(newPlayerLang);
		verify(langStore, never()).setLanguageForPlayer(Matchers.any(UUID.class), anyString());
	}
	
	@Test
	public void testClearingLanguageSetup() {
		
		assertTrue(sut.onCommand(player, null, null, new String[] { sut.CLEAR_SETUP_LANG }));
		
		verify(player).sendMessage(SetLangCommandExecutor.MSG_CLEARED_SETUP_LANG);
		verify(langStore, never()).isLanguageSupported(anyString());
		verify(langStore).clearLanguageForPlayer(player.getUniqueId());
	}
	
	@Test
	public void testLanguagePrevLanguageEqualsNewLanguage() {
		
		when(langStore.getLanguageForPlayer(playerUUID)).thenReturn(newPlayerLang);
		
		assertFalse(sut.onCommand(player, null, null, new String[] { newPlayerLang }));
		
		verify(player).sendMessage(String.format(SetLangCommandExecutor.MSG_LANG_IS_ALREADY_SETUP_FTM, newPlayerLang));
		verify(langStore, never()).isLanguageSupported(newPlayerLang);
		verify(langStore, never()).setLanguageForPlayer(Matchers.any(UUID.class), anyString());
	}
	
	@Test
	public void testLanguagePrevLanguageUnequalsNewLanguage() {
		
		assertTrue(sut.onCommand(player, null, null, new String[] { newPlayerLang }));
		
		verify(player).sendMessage(String.format(SetLangCommandExecutor.MSG_PLAYER_LANG_CHANGED_FMT, playerName, newPlayerLang));
		
		verify(langStore).getLanguageForPlayer(playerUUID);
		verify(langStore).isLanguageSupported(newPlayerLang);
		verify(langStore).setLanguageForPlayer(playerUUID, newPlayerLang);
	}
	
	@Test
	public void testLanguageArgumentCaseInsensitve() {
		
		sut.onCommand(player, null, null, new String[] { "aBcDeF" });
		
		verify(langStore).isLanguageSupported("abcdef");
	}

}
