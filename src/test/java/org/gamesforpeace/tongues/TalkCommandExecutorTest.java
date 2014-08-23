package org.gamesforpeace.tongues;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.gamesforpeace.tongues.translation.TranslationRequestExecutor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.mockito.cglib.core.CollectionUtils;

public class TalkCommandExecutorTest {

	private TalkCommandExecutor SUT;
	private CommandPoster cmdPosterMock;
	private ChatDestinationResolver destResolver;
	private ChatMessenger chatMsgr;
	private TranslationRequestExecutor transReqExec;
	
	@Captor
    private ArgumentCaptor<Set<Player>> destSetCaptor;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		cmdPosterMock = mock(CommandPoster.class);
		destResolver = mock(ChatDestinationResolver.class);
		chatMsgr = mock(ChatMessenger.class);
		transReqExec = mock(TranslationRequestExecutor.class);
		
		SUT = new TalkCommandExecutor(cmdPosterMock, destResolver, chatMsgr, transReqExec);
	}
	
	private Player getExecutingPlayer()
	{
		Player player = mock(Player.class);
		when(player.hasPermission(TalkCommandExecutor.PERMISSION_TO_TALK)).thenReturn(true);
		when(player.hasPermission(TalkCommandExecutor.PERMISSION_TO_TALK_TO_ALL)).thenReturn(true);
		return player;
	}
	
	private Player getSingleDestinationPlayer() {
		return mock(Player.class);
	}
	
	private Set<Player> getMultipleDestinationPlayers() {
		Set<Player> destPlayers = new HashSet<Player>();
		destPlayers.add(mock(Player.class));
		destPlayers.add(mock(Player.class));
		destPlayers.add(mock(Player.class));
		return destPlayers;
	}
	
	@Test
	public void commandNotFromAPlayerNoAllowed() {
		ConsoleCommandSender nonPlayer = mock(ConsoleCommandSender.class);
		
		assertFalse(SUT.onCommand(nonPlayer, null, "", null));
		
		verify(nonPlayer).sendMessage(TalkCommandExecutor.ERR_NOT_A_PLAYER_TALK);
		verifyZeroInteractions(transReqExec);
	}
	
	@Test
	public void commandNoArgsIgnored() {
		Player player = getExecutingPlayer();
		
		String[] cmdArgs = new String[0];
		assertFalse(SUT.onCommand(player, null, "", cmdArgs));
		
		verify(player, never()).sendMessage(anyString());
		verifyZeroInteractions(transReqExec);
	}
	
	@Test
	public void noCommandModifierSendsWhisperSingleArg() {
		Player player = getExecutingPlayer();
		
		String[] cmdArgs = new String[] { "SomeText" };
		assertTrue(SUT.onCommand(player, null, "", cmdArgs));
		
		verify(cmdPosterMock).postCommand(eq(player), eq("whisper SomeText"));
		verifyZeroInteractions(transReqExec);
	}
	
	@Test
	public void noCommandModifierSendsWhisperMultipleArgs() {
		Player player = getExecutingPlayer();
		
		String[] cmdArgs = new String[] { "SomeText", "SomeText2" };
		assertTrue(SUT.onCommand(player, null, "", cmdArgs));
		
		verify(cmdPosterMock).postCommand(eq(player), eq("whisper SomeText SomeText2"));
		verifyZeroInteractions(transReqExec);
	}
	
	@Test
	public void noCommandModifierWithoutPermmissionIsDenied() {
		Player player = getExecutingPlayer();
		when(player.hasPermission(TalkCommandExecutor.PERMISSION_TO_TALK)).thenReturn(false);
		
		String[] cmdArgs = new String[] { "SomeText" };
		assertFalse(SUT.onCommand(player, null, "", cmdArgs));
		
		verify(player).sendMessage(eq(TalkCommandExecutor.ERR_NO_PERMISSION));
		verifyZeroInteractions(cmdPosterMock);
		verifyZeroInteractions(transReqExec);
	}
	
	@Test
	public void playerNameCommandModifierSendsToPlayerSingleWord() {
		Player player = getExecutingPlayer();
		Player destPlayer = getSingleDestinationPlayer();
		
		when(destResolver.getOnlinePlayer("player1")).thenReturn(destPlayer);
		
		String[] cmdArgs = new String[] { "player1", "SomeText" };
		assertTrue(SUT.onCommand(player, null, "", cmdArgs));
		
		verify(chatMsgr).sendMessageSync(eq("SomeText"), eq(player), destSetCaptor.capture());
		Set<Player> destPlayersFromSend = destSetCaptor.getValue();
		assertTrue(destPlayersFromSend.contains(destPlayer));
		assertEquals(1, destPlayersFromSend.size());
		
		verify(transReqExec).postTranslationRequest(eq("SomeText"), eq(player), destSetCaptor.capture());
		Set<Player> destPlayersFromTranslate = destSetCaptor.getValue();
		assertTrue(destPlayersFromTranslate.contains(destPlayer));
		assertEquals(1, destPlayersFromTranslate.size());
	}
	
	@Test
	public void playerNameCommandModifierSendsToPlayerSentence() {
		Player player = getExecutingPlayer();
		Player destPlayer = getSingleDestinationPlayer();
		
		when(destResolver.getOnlinePlayer("player1")).thenReturn(destPlayer);
		
		String[] cmdArgs = new String[] { "player1", "SomeText", "SomeText2" };
		assertTrue(SUT.onCommand(player, null, "", cmdArgs));
		
		verify(chatMsgr).sendMessageSync(eq("SomeText SomeText2"), eq(player), destSetCaptor.capture());
		Set<Player> destPlayersFromSend = destSetCaptor.getValue();
		assertTrue(destPlayersFromSend.contains(destPlayer));
		assertEquals(1, destPlayersFromSend.size());
		
		verify(transReqExec).postTranslationRequest(eq("SomeText SomeText2"), eq(player), destSetCaptor.capture());
		Set<Player> destPlayersFromTranslate = destSetCaptor.getValue();
		assertTrue(destPlayersFromTranslate.contains(destPlayer));
		assertEquals(1, destPlayersFromTranslate.size());
	}
	
	@Test
	public void playerNameCommandModifierWIthoutMessageIgnored() {
		Player player = getExecutingPlayer();
		Player destPlayer = getSingleDestinationPlayer();
		
		when(destResolver.getOnlinePlayer("player1")).thenReturn(destPlayer);
		
		String[] cmdArgs = new String[] { "player1" };
		assertFalse(SUT.onCommand(player, null, "", cmdArgs));
		
		verifyZeroInteractions(chatMsgr);
		verifyZeroInteractions(transReqExec);
	}
	
	@Test
	public void playerNameCommandModifierWithoutPermmissionIsDenied() {
		Player player = getExecutingPlayer();
		when(player.hasPermission(TalkCommandExecutor.PERMISSION_TO_TALK)).thenReturn(false);
		
		String[] cmdArgs = new String[] { "player1", "SomeText" };
		assertFalse(SUT.onCommand(player, null, "", cmdArgs));
		
		verify(player).sendMessage(eq(TalkCommandExecutor.ERR_NO_PERMISSION));
		verifyZeroInteractions(destResolver);
		verifyZeroInteractions(cmdPosterMock);
		verifyZeroInteractions(transReqExec);
	}
	
	@Test
	public void allKeywordCommandModifierSendsToAll() {
		Player player = getExecutingPlayer();
		Set<Player> destPlayers = getMultipleDestinationPlayers();
		String[] cmdArgs = new String[] { TalkCommandExecutor.TALK_TO_ALL_MODIFIER, "SomeText", "SomeText2" };
		when(destResolver.getAllOnlinePlayers()).thenReturn(destPlayers);
		
		assertTrue(SUT.onCommand(player, null, "", cmdArgs));

		verify(chatMsgr).sendMessageSync(eq("SomeText SomeText2"), eq(player), destSetCaptor.capture());
		assertEquals(destPlayers, destSetCaptor.getValue());
		
		verify(transReqExec).postTranslationRequest(eq("SomeText SomeText2"), eq(player), destSetCaptor.capture());
		assertEquals(destPlayers, destSetCaptor.getValue());
	}
	
	@Test
	public void allKeywordCommandModifierIgnoredIfNoMessage() {
		Player player = getExecutingPlayer();
		String[] cmdArgs = new String[] { TalkCommandExecutor.TALK_TO_ALL_MODIFIER };

		assertFalse(SUT.onCommand(player, null, "", cmdArgs));

		verifyZeroInteractions(chatMsgr);
		verifyZeroInteractions(transReqExec);
	}
	
	@Test
	public void allKeywordCommandModifierWithoutPermmissionIsDenied() {
		Player player = getExecutingPlayer();
		when(player.hasPermission(TalkCommandExecutor.PERMISSION_TO_TALK_TO_ALL)).thenReturn(false);
		
		String[] cmdArgs = new String[] { TalkCommandExecutor.TALK_TO_ALL_MODIFIER, "SomeText" };
		assertFalse(SUT.onCommand(player, null, "", cmdArgs));
		
		verify(player).sendMessage(eq(TalkCommandExecutor.ERR_NO_PERMISSION));
		verifyZeroInteractions(destResolver);
		verifyZeroInteractions(cmdPosterMock);
		verifyZeroInteractions(transReqExec);
	}
}
