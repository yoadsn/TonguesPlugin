package org.gamesforpeace.tongues;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.gamesforpeace.tongues.translation.TranslationRequestExecutor;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.internal.matchers.StartsWith;


public class ChatListenerTest {	

	@Test(expected = IllegalArgumentException.class)
	public void testChatListenerNullTransReqExec() {
		CommandPoster commandPoster = mock(CommandPoster.class);
		new ChatListener(null, commandPoster);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testChatListenerNullCommandPoster() {
		TranslationRequestExecutor transReqExec = mock(TranslationRequestExecutor.class);
		new ChatListener(transReqExec, null);
	}
	
	
	@Test
	public void testOnPlayerChatEventPostsAWhisperCommand() {
		TranslationRequestExecutor transReqExec = mock(TranslationRequestExecutor.class); 
		CommandPoster commandPoster = mock(CommandPoster.class);
		ChatListener SUT = new ChatListener(transReqExec, commandPoster);
		
		SUT.onPlayerChatEvent(mock(AsyncPlayerChatEvent.class));
		
		verify(commandPoster).postCommand(any(Player.class), startsWith("talk"));
	}
}
