package org.gamesforpeace.tongues.translation;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.gamesforpeace.tongues.ChatMessenger;
import org.gamesforpeace.tongues.PlayerLanguageStore;
import org.junit.Test;

public class ChatTranslationRequestExecutorTest {

	@Test(expected = IllegalArgumentException.class)
	public void testTranslatorisNull() {

		new ChatTranslationRequestExecutor(null, mock(PlayerLanguageStore.class), mock(ChatMessenger.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testLanguageStoreIsNull() {
		new ChatTranslationRequestExecutor(mock(Translator.class), null, mock(ChatMessenger.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMessengerIsNull() {
		new ChatTranslationRequestExecutor(mock(Translator.class), mock(PlayerLanguageStore.class), null);
	}

	@Test
	public void testDependenciesExists() {
		new ChatTranslationRequestExecutor(mock(Translator.class), mock(PlayerLanguageStore.class), mock(ChatMessenger.class));
	}

	@Test
	public void testSourcePlayerLanguageNotFoundUsesDefault() {
		// Setup
		SUTBuilder builder = SUTBuilder.getSutBuilder();
		builder
		.withSourcePlayerLang(builder.getDefaultLang())
		.addDestPlayerWithLang("other");

		// Act
		builder.buildSUT().postTranslationRequest(
				builder.getDefaultSourceMessage(), builder.getSourcePlayer(),
				builder.getDestPlayersHash());

		// Assert
		verify(builder.getTranslator()).translate(builder.getDefaultSourceMessage(), builder.getDefaultLang(), "other");
		verify(builder.getMessenger()).sendMessage(builder.getDefaultTranslatedMessage(), builder.getSourcePlayer(), builder.getDestPlayers().get(0));
	}

	@Test
	public void testDestPlayerLanguageNotFoundIgnoring() {
		// Setup
		SUTBuilder builder = SUTBuilder.getSutBuilder();
		builder
		.addDestPlayerWithLang(builder.getDefaultLang());

		// Act
		builder.buildSUT().postTranslationRequest(
				builder.getDefaultSourceMessage(), builder.getSourcePlayer(),
				builder.getDestPlayersHash());

		// Assert
		verifyZeroInteractions(builder.getTranslator());
		verifyZeroInteractions(builder.getMessenger());
	}

	@Test
	public void testNoDestPlayers() {
		// Setup
		SUTBuilder builder = SUTBuilder.getSutBuilder();

		// Act
		builder.buildSUT().postTranslationRequest(
				builder.getDefaultSourceMessage(), builder.getSourcePlayer(),
				builder.getDestPlayersHash());

		// Assert
		verifyZeroInteractions(builder.getTranslator());
		verifyZeroInteractions(builder.getMessenger());
	}

	@Test
	public void testAlwaysAssumingSourcePlayerLanguageIsDefaultLanguage() {
		// Setup
		SUTBuilder builder = SUTBuilder.getSutBuilder();
		builder
		.withSourcePlayerLang("same")
		.addDestPlayerWithLang("same");

		// Act
		builder.buildSUT().postTranslationRequest(
				builder.getDefaultSourceMessage(), builder.getSourcePlayer(),
				builder.getDestPlayersHash());

		// Assert
		verify(builder.getTranslator()).translate(builder.getDefaultSourceMessage(), builder.getDefaultLang(), "same");
	}
	
	@Test
	public void testNotSendingMessageWhenTranslationEqualsSource() {
		// Setup
		SUTBuilder builder = SUTBuilder.getSutBuilder();
		builder
		.withTranslatedMessage(builder.getDefaultSourceMessage())
		.addDestPlayerWithLang("same");

		// Act
		builder.buildSUT().postTranslationRequest(
				builder.getDefaultSourceMessage(), builder.getSourcePlayer(),
				builder.getDestPlayersHash());

		// Assert
		verifyZeroInteractions(builder.getMessenger());
	}

	@Test
	public void testTranslatingTwoDifferentDestLanguages() {
		// Setup
		SUTBuilder builder = SUTBuilder.getSutBuilder();
		builder
		.addDestPlayerWithLang("other")
		.addDestPlayerWithLang("other2");

		// Act
		builder.buildSUT().postTranslationRequest(
				builder.getDefaultSourceMessage(), builder.getSourcePlayer(),
				builder.getDestPlayersHash());

		// Assert
		verify(builder.getTranslator(), times(1)).translate(anyString(), eq(builder.getDefaultLang()), eq("other"));
		verify(builder.getTranslator(), times(1)).translate(anyString(), eq(builder.getDefaultLang()), eq("other2"));
	}

	@Test
	public void testTranslatingTwoSameDestLanguages() {
		// Setup
		SUTBuilder builder = SUTBuilder.getSutBuilder();
		builder
		.addDestPlayerWithLang("other")
		.addDestPlayerWithLang("other");

		// Act
		builder.buildSUT().postTranslationRequest(
				builder.getDefaultSourceMessage(), builder.getSourcePlayer(),
				builder.getDestPlayersHash());

		// Assert
		verify(builder.getTranslator(), times(1)).translate(anyString(), eq(builder.getDefaultLang()), eq("other"));
	}

	@Test
	public void testNoDestPlayerNoMessageSent() {
		// Setup
		SUTBuilder builder = SUTBuilder.getSutBuilder();

		// Act
		builder.buildSUT().postTranslationRequest(
				builder.getDefaultSourceMessage(), builder.getSourcePlayer(),
				builder.getDestPlayersHash());

		// Assert
		verifyZeroInteractions(builder.getMessenger());
	}

	@Test
	public void testSingleDestPlayerSingleMessage() {
		// Setup
		SUTBuilder builder = SUTBuilder.getSutBuilder();
		builder.addDestPlayerWithLang("Other");

		// Act
		builder.buildSUT().postTranslationRequest(
				builder.getDefaultSourceMessage(), builder.getSourcePlayer(),
				builder.getDestPlayersHash());

		// Assert
		verify(builder.getMessenger()).sendMessage(anyString(), eq(builder.getSourcePlayer()), eq(builder.getDestPlayers().get(0)));
	}

	public void testMultiDestPlayerMultiMessage() {
		// Setup
		SUTBuilder builder = SUTBuilder.getSutBuilder();
		builder.addDestPlayerWithLang("Other").addDestPlayerWithLang("Other2");

		// Act
		builder.buildSUT().postTranslationRequest(
				builder.getDefaultSourceMessage(), builder.getSourcePlayer(),
				builder.getDestPlayersHash());

		// Assert
		verify(builder.getMessenger()).sendMessage(anyString(), builder.getSourcePlayer(), builder.getDestPlayers().get(0));
		verify(builder.getMessenger()).sendMessage(anyString(), builder.getSourcePlayer(), builder.getDestPlayers().get(1));
	}

	@Test
	public void testTranslatedMessageIsSent() {
		// Setup
		SUTBuilder builder = SUTBuilder.getSutBuilder();
		builder.addDestPlayerWithLang("Other");

		// Act
		builder.buildSUT().postTranslationRequest(
				builder.getDefaultSourceMessage(), builder.getSourcePlayer(),
				builder.getDestPlayersHash());

		// Assert
		verify(builder.getTranslator()).translate(anyString(), anyString(),
				anyString());
		verify(builder.getMessenger()).sendMessage(
				builder.getDefaultTranslatedMessage(),
				builder.getSourcePlayer(), builder.getDestPlayers().get(0));
	}

	private static class SUTBuilder {
		// Mocks
		private Translator translator;
		private PlayerLanguageStore langStore;
		private ChatMessenger messenger;
		private Player sourcePlayer;
		private List<String> destPlayerLangs;
		private List<Player> destPlayers;

		// Default Values
		private String defaultLang;
		private UUID defaultSourcePlayerUUID;
		private String defaultSourcePlayerLanguage;

		private String defaultSourceMessage;
		private String defaultTranslatedMessage;

		private SUTBuilder() {
			defaultLang = "";
			
			defaultSourcePlayerUUID = UUID.randomUUID();
			defaultSourcePlayerLanguage = "English";

			defaultSourceMessage = "SRCMSG";
			defaultTranslatedMessage = "TRANSLATED";

			destPlayerLangs = new LinkedList<String>();
			destPlayers = new LinkedList<Player>();
		}

		public SUTBuilder withSourcePlayerLang(String lang) {
			this.defaultSourcePlayerLanguage = lang;
			return this;
		}

		public SUTBuilder addDestPlayerWithLang(String lang) {
			destPlayerLangs.add(lang);
			return this;
		}
		
		public SUTBuilder withTranslatedMessage(String translatedMessage) {
			defaultTranslatedMessage = translatedMessage;
			return this;
		}

		public ChatTranslationRequestExecutor buildSUT() {

			// Mocks
			translator = mock(Translator.class);
			langStore = mock(PlayerLanguageStore.class);
			messenger = mock(ChatMessenger.class);

			sourcePlayer = mock(Player.class);

			for (String destPlayerLang : destPlayerLangs) {
				Player destPlayer = mock(Player.class);
				UUID uuid = UUID.randomUUID();
				when(destPlayer.getUniqueId()).thenReturn(uuid);
				when(langStore.getLanguageForPlayer(uuid)).thenReturn(
						destPlayerLang);

				destPlayers.add(destPlayer);
			}

			// Stubs
			when(sourcePlayer.getUniqueId())
					.thenReturn(defaultSourcePlayerUUID);
			when(langStore.getLanguageForPlayer(defaultSourcePlayerUUID))
					.thenReturn(defaultSourcePlayerLanguage);

			when(
					translator.translate(eq(defaultSourceMessage), anyString(),
							anyString())).thenReturn(defaultTranslatedMessage);
			
			when(translator.getDefaultLanguage()).thenReturn(defaultLang);
			when(langStore.getDefaultLanguage()).thenReturn(defaultLang);

			ChatTranslationRequestExecutor sut = new ChatTranslationRequestExecutor(getTranslator(),
					getLangStore(), getMessenger());

			return sut;
		}

		static SUTBuilder getSutBuilder() {
			return new SUTBuilder();
		}

		// Accessors

		public Translator getTranslator() {
			return translator;
		}

		public PlayerLanguageStore getLangStore() {
			return langStore;
		}

		public ChatMessenger getMessenger() {
			return messenger;
		}
		
		public String getDefaultLang()
		{
			return defaultLang;
		}

		public String getDefaultSourcePlayerLanguage() {
			return defaultSourcePlayerLanguage;
		}

		public Player getSourcePlayer() {
			return sourcePlayer;
		}

		public List<Player> getDestPlayers() {
			return destPlayers;
		}

		public HashSet<Player> getDestPlayersHash() {
			return new HashSet<Player>(destPlayers);
		}

		public String getDefaultSourceMessage() {
			return defaultSourceMessage;
		}

		public String getDefaultTranslatedMessage() {
			return defaultTranslatedMessage;
		}
	}
}
