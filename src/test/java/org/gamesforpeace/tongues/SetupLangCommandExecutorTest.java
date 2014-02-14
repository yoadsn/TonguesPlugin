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

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

public class SetupLangCommandExecutorTest {

	PlayerLanguageStore langStore;
	MyLangCommandExecutor sut;
	Player player;
	UUID playerUUID;
	String prevPlayerLang = "english";
	String newPlayerLang = "hebrew";
	
	@Before
	public void setUp() throws Exception {
		langStore = mock(PlayerLanguageStore.class);
		player = mock(Player.class);
		playerUUID = UUID.randomUUID();
		
		when(player.getUniqueId()).thenReturn(playerUUID);
		when(langStore.GetLanguageForPlayer(playerUUID)).thenReturn(prevPlayerLang);
		when(langStore.isLanguageSupported(newPlayerLang)).thenReturn(true);
		
		sut = new MyLangCommandExecutor(langStore);
	}

	@Test
	public void testCommandNotFromPlayer() {
		
		ConsoleCommandSender nonPlayerSender = mock(ConsoleCommandSender.class);
		
		assertFalse(sut.onCommand(nonPlayerSender, null, null, null));
		
		verify(nonPlayerSender).sendMessage(anyString());
		verifyZeroInteractions(langStore);
	}
	
	@Test
	public void testCommandNoArgs() {
		
		assertTrue(sut.onCommand(player, null, null, new String[0]));
		
		verify(player).sendMessage(anyString());
		verify(langStore).GetLanguageForPlayer(playerUUID);
	}
	
	@Test
	public void testCommandTooManyArgs() {
		
		assertFalse(sut.onCommand(player, null, null, new String[2]));
		
		verify(player).sendMessage(anyString());
		verifyZeroInteractions(langStore);
	}
	
	@Test
	public void testLanguageNotSupported() {
		
		when(langStore.isLanguageSupported(newPlayerLang)).thenReturn(false);
		
		assertFalse(sut.onCommand(player, null, null, new String[] { newPlayerLang }));
		
		verify(player).sendMessage(anyString());
		verify(langStore).isLanguageSupported(newPlayerLang);
		verify(langStore, never()).setLanguageForPlayer(Matchers.any(UUID.class), anyString());
	}
	
	@Test
	public void testClearingLanguageSetup() {
		
		assertTrue(sut.onCommand(player, null, null, new String[] { sut.CLEAR_SETUP_LANG }));
		
		verify(player).sendMessage(anyString());
		verify(langStore, never()).isLanguageSupported(anyString());
		verify(langStore).clearLanguageForPlayer(player.getUniqueId());
	}
	
	@Test
	public void testLanguagePrevLanguageEqualsNewLanguage() {
		
		when(langStore.GetLanguageForPlayer(playerUUID)).thenReturn(newPlayerLang);
		
		assertFalse(sut.onCommand(player, null, null, new String[] { newPlayerLang }));
		
		verify(player).sendMessage(anyString());
		verify(langStore, never()).isLanguageSupported(newPlayerLang);
		verify(langStore, never()).setLanguageForPlayer(Matchers.any(UUID.class), anyString());
	}
	
	@Test
	public void testLanguagePrevLanguageUnequalsNewLanguage() {
		
		assertTrue(sut.onCommand(player, null, null, new String[] { newPlayerLang }));
		
		verify(player).sendMessage(anyString());
		verify(langStore).GetLanguageForPlayer(playerUUID);
		verify(langStore).isLanguageSupported(newPlayerLang);
		verify(langStore).setLanguageForPlayer(playerUUID, newPlayerLang);
	}
	
	@Test
	public void testLanguageArgumentCaseInsensitve() {
		
		sut.onCommand(player, null, null, new String[] { "aBcDeF" });
		
		verify(langStore).isLanguageSupported("abcdef");
	}

}
