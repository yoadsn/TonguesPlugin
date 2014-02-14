package org.gamesforpeace.tongues;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.gamesforpeace.tongues.translation.TranslationRequestExecutor;
import org.junit.Test;
import org.mockito.Matchers;


public class ChatListenerTest {	

	@Test(expected = IllegalArgumentException.class)
	public void testChatListenerNullPlugin() {
		new ChatListener(null);
	}
	
	@Test
	public void testOnPlayerChatEventPostsToExecutor() {
		TranslationRequestExecutor transReqExec = mock(TranslationRequestExecutor.class); 
		ChatListener SUT = new ChatListener(transReqExec);
		
		SUT.onPlayerChatEvent(mock(AsyncPlayerChatEvent.class));
		
		verify(transReqExec).postTranslationRequest(anyString(), any(Player.class), Matchers.<Set<Player>>any());
	}

}
