package org.gamesforpeace.tongues;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.gamesforpeace.tongues.translation.TranslationRequestExecutor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;


public class WhisperCommandExecutorTest {

	ChatMessenger chatMsgr;
	TranslationRequestExecutor transReqExec;
	String[] cmdArgs;
	String theSentMessage;
	Player sender;
	String senderDisplayName;
	List<Entity> entitiesInRange; 
	WhisperCommandExecutor SUT;
	int radiusToWhisper;
	
	@Before
	public void setUp() throws Exception {
		// Setup default values
		cmdArgs = new String[] {"my", "message"};
		theSentMessage  = "my message";
		senderDisplayName = "sender";
		radiusToWhisper = 10;
		
		// Create mocks
		chatMsgr = mock(ChatMessenger.class);
		transReqExec = mock(TranslationRequestExecutor.class);
		sender = mock(Player.class);
		entitiesInRange = new LinkedList<Entity>();
			
		// Default behaviors
		when(sender.getNearbyEntities(anyDouble(), anyDouble(), anyDouble())).thenReturn(entitiesInRange);
		when(sender.getDisplayName()).thenReturn(senderDisplayName);
		when(sender.hasPermission("tongues.whisper")).thenReturn(true);
		
		// Create SUT
		SUT = new WhisperCommandExecutor(chatMsgr, transReqExec, radiusToWhisper);
	}
	
	private Player addPlayerToRange() {
		Player playerAdded = mock(Player.class); 
		
		entitiesInRange.add(playerAdded);
		
		return playerAdded;
	}
	

	@Test(expected = IllegalArgumentException.class)
	public void testChatMessengerNullTranslator() {
		new WhisperCommandExecutor(null, transReqExec, radiusToWhisper);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testWhisperCommandExecutorNullTranslator() {
		new WhisperCommandExecutor(chatMsgr, null, radiusToWhisper);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void radiusCannotBeZero() {
		new WhisperCommandExecutor(chatMsgr, transReqExec, 0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void radiusCannotBeNegative() {
		new WhisperCommandExecutor(chatMsgr, transReqExec, -1);
	}

	@Test
	public void testCommandNotFromAPlayer() {
		
		ConsoleCommandSender nonPlayer = mock(ConsoleCommandSender.class);
		
		assertFalse(SUT.onCommand(nonPlayer, null, "", null));
		
		verify(nonPlayer).sendMessage(WhisperCommandExecutor.ERR_NOT_A_PLAYER_WHISPER);
		verifyZeroInteractions(transReqExec);
	}
	
	@Test
	public void testNoPermissions() {
		
		//SUT = new WhisperCommandExecutor(chatMsgr, transReqExec, 1, 2, 3);
		when(sender.hasPermission("tongues.whisper")).thenReturn(false);
		
		assertFalse(SUT.onCommand(sender, null, "", cmdArgs));
		
		verify(sender).sendMessage(WhisperCommandExecutor.ERR_NO_PERMISSION);
	}
	
	@Test
	public void testCorrectRadiusUsed() {
		
		SUT = new WhisperCommandExecutor(chatMsgr, transReqExec, 1, 2, 3);
		
		assertTrue(SUT.onCommand(sender, null, "", cmdArgs));
		
		verify(sender).getNearbyEntities(1, 2, 3);
	}
	
	
	@Test
	public void testPlayerAloneInRange() {
		
		assertTrue(SUT.onCommand(sender, null, "", cmdArgs));
		
		verify(sender).sendMessage(WhisperCommandExecutor.MSG_NOBODY_TO_WHISPER_TO);
		
		verifyZeroInteractions(transReqExec);
	}
	
	@Test
	public void testSinglePlayerInRange() {
		
		Player playerAddedToRange = addPlayerToRange();
		
		assertTrue(SUT.onCommand(sender, null, "", cmdArgs));
		
		verify(sender).sendMessage(String.format(SUT.MSG_YOU_WHISPERED_PREFIX_FMT, theSentMessage));
		verify(chatMsgr).sendMessageSync(
				String.format(WhisperCommandExecutor.MSG_WHISPER_PREFIX_FMT, senderDisplayName, theSentMessage),
				sender,
				Sets.newSet(playerAddedToRange));
		verify(transReqExec).postTranslationRequest(theSentMessage, sender, Sets.newSet(playerAddedToRange));
	}
	
	@Test
	public void testMultiPlayerInRange() {
		
		Player playerAddedToRange = addPlayerToRange();
		Player playerAddedToRange2 = addPlayerToRange();
		
		assertTrue(SUT.onCommand(sender, null, "", cmdArgs));
		
		verify(sender).sendMessage(String.format(SUT.MSG_YOU_WHISPERED_PREFIX_FMT, theSentMessage));
		verify(chatMsgr).sendMessageSync(
				String.format(WhisperCommandExecutor.MSG_WHISPER_PREFIX_FMT, senderDisplayName, theSentMessage),
				sender,
				Sets.newSet(playerAddedToRange, playerAddedToRange2));
		verify(transReqExec).postTranslationRequest(theSentMessage, sender, Sets.newSet(playerAddedToRange, playerAddedToRange2));
	}
	
	@Test
	public void testNotOnlyPlayersInRange() {
		
		Player playerAddedToRange = addPlayerToRange();
		Zombie zombieAdded = mock(Zombie.class);
		entitiesInRange.add(zombieAdded);
		
		assertTrue(SUT.onCommand(sender, null, "", cmdArgs));
		
		verify(sender).sendMessage(String.format(SUT.MSG_YOU_WHISPERED_PREFIX_FMT, theSentMessage));
		verify(chatMsgr).sendMessageSync(
				String.format(WhisperCommandExecutor.MSG_WHISPER_PREFIX_FMT, senderDisplayName, theSentMessage),
				sender,
				Sets.newSet(playerAddedToRange));
		verify(transReqExec).postTranslationRequest(theSentMessage, sender, Sets.newSet(playerAddedToRange));
		verifyZeroInteractions(zombieAdded);
	}
	
	@Test
	public void testOnlyNonPlayersInRange() {
		
		Zombie zombieAdded = mock(Zombie.class);
		entitiesInRange.add(zombieAdded);
		
		assertTrue(SUT.onCommand(sender, null, "", cmdArgs));
		
		verify(sender).sendMessage(WhisperCommandExecutor.MSG_NOBODY_TO_WHISPER_TO);
		verifyZeroInteractions(transReqExec);
	}
	
	@Test
	public void testNoMessageProvided() {
		
		String[] emptyArgsList = new String[] {};
		
		assertFalse(SUT.onCommand(sender, null, "", emptyArgsList));
		
		verify(sender).sendMessage(WhisperCommandExecutor.ERR_EMPTY_MESSAGE);
		verifyZeroInteractions(transReqExec);
	}
	
	@Test
	public void usesCorrectRadiusSettings() {
		
		SUT.onCommand(sender, null, "", cmdArgs);
		
		verify(sender).getNearbyEntities(radiusToWhisper, radiusToWhisper, radiusToWhisper);
	}
	
	@Test
	public void usesDefaultRadiusSettings() {
		
		WhisperCommandExecutor SUT = new WhisperCommandExecutor(chatMsgr, transReqExec);
		
		SUT.onCommand(sender, null, "", cmdArgs);
		
		verify(sender).getNearbyEntities(radiusToWhisper, radiusToWhisper, radiusToWhisper);
	}
	
}
