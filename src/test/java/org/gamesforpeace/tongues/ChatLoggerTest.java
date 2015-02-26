package org.gamesforpeace.tongues;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Matchers.*;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.gamesforpeace.tongues.logging.BaseLogger;
import org.junit.Before;
import org.junit.Test;

public class ChatLoggerTest {
	
	Player sourcePlayer = null;
	Player destPlayer = null;
	String message = null;
	String logMessageFormat = null;
	BaseLogger mockLogger = null;
	Set<BaseLogger> allMockLoggers = null;
	ChatLogger SUT = null;
	
	@Before
	public void setUp() throws Exception {
		sourcePlayer = mock(Player.class);
		destPlayer = mock(Player.class);
		message = "Some message";
		mockLogger = mock(BaseLogger.class);
		allMockLoggers = new HashSet<BaseLogger>();
		allMockLoggers.add(mockLogger);
		logMessageFormat = "<%1$s|%2$s|%3$s|%4$s|%5$s>";
		
		SUT = new ChatLogger(allMockLoggers, logMessageFormat);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void loggersNullThrows() {
		new ChatLogger(null, logMessageFormat);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void formatNullThrows() {
		new ChatLogger(allMockLoggers, null);
	}
	
	@Test
	public void willNotLogWhenSourceIsNull() {
		SUT.log(null, destPlayer, message);
		
		verifyZeroInteractions(mockLogger);
	}
	
	@Test
	public void willNotLogWhenDestIsNull() {
		SUT.log(sourcePlayer, null, message);
		
		verifyZeroInteractions(mockLogger);
	}
	
	@Test
	public void willNotLogWhenMessageIsNull() {
		SUT.log(sourcePlayer, destPlayer, null);
		
		verifyZeroInteractions(mockLogger);
	}
	
	@Test
	public void willLog() {
		SUT.log(sourcePlayer, destPlayer, message);
		
		verify(mockLogger).log(anyString());
	}
	
	@Test
	public void willUseCorrectMessageFormat() {
		SUT.log(sourcePlayer, destPlayer, message);
		
		String expctedMessage = String.format(logMessageFormat,
				sourcePlayer.getName(), sourcePlayer.getDisplayName(),
				destPlayer.getName(), destPlayer.getDisplayName(), message);
		verify(mockLogger).log(eq(expctedMessage));
	}
}
