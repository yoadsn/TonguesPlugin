package org.gamesforpeace.tongues;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.gamesforpeace.tongues.logging.BaseLogger;
import org.gamesforpeace.tongues.logging.FileLogger;
import org.gamesforpeace.tongues.logging.LogentriesLogger;
import org.gamesforpeace.tongues.logging.TimeBasedLogFilenameProducer;
import org.gamesforpeace.tongues.persistence.PlayerGroupsPersister;
import org.gamesforpeace.tongues.persistence.PlayerLanguageStorePersister;
import org.gamesforpeace.tongues.translation.BingTranslator;
import org.gamesforpeace.tongues.translation.ChatTranslationRequestExecutor;
import org.gamesforpeace.tongues.translation.TranslationRequestExecutor;
import org.gamesforpeace.tongues.translation.Translator;

public final class TonguesPlugin extends JavaPlugin implements ChatMessenger, TranslationRequestExecutor, CommandPoster, ChatDestinationResolver {
	
	private final String LANG_STORE_FILENAME = "langStore.json";
	private final String GROUPS_STORE_FILENAME = "groupsStore.json";
	private TranslationRequestExecutor translationRequestExecutor;
	private ListenCommandExecutor listenCommandExecutor;
	private PlayerLanguageStore langStore;
	private ChatLogger chatLogger = null;
	private HashMap<String, HashSet<String>> groupsStore;
	
	@Override
    public void onEnable(){
				
		// Will create the default configuration file structure
		saveDefaultConfig();
		
		//getLogger().fine("Using Bing Translation authentication values -> ClientID:" + getConfig().getString("bingTranslation.clientID") + " SecretKey:" + getConfig().getString("bingTranslation.secretKey"));
		Translator translator = new BingTranslator(getConfig().getString("bingTranslation.clientID"), getConfig().getString("bingTranslation.secretKey"));
		
		langStore = new ConcurrentPlayerLanguageStore(translator.getSupportedLanguages(),  translator.getDefaultLanguage());
		
		LoadPlayerLanguages();
		
		LoadGroups();
		
		setupChatLoggers();
		
		translationRequestExecutor = new ChatTranslationRequestExecutor(translator, langStore, this);
		listenCommandExecutor = new ListenCommandExecutor();
		
		//getLogger().info("Supported languages: " + translator.getSupportedLanguages().toString());
		
		getServer().getPluginManager().registerEvents(new ChatListener(this, this), this);
		getServer().getPluginCommand("tongues.setlang").setExecutor(new SetLangCommandExecutor(langStore, getServer()));
		getServer().getPluginCommand("tongues.whisper").setExecutor(new WhisperCommandExecutor(this, this));
		getServer().getPluginCommand("tongues.talk").setExecutor(new TalkCommandExecutor(this, this, this, this));
		getServer().getPluginCommand("tongues.listen").setExecutor(listenCommandExecutor);
    }

	private void setupChatLoggers() {
		Set<BaseLogger> allLoggers = new HashSet<BaseLogger>();
		
		Boolean useFileChatLogging = getConfig().getBoolean("chatLogging.file.enabled");
		Boolean useLogentriesChatLogging = getConfig().getBoolean("chatLogging.logentries.enabled");
		
		if (useFileChatLogging) {
			String fileNamePattern = new TimeBasedLogFilenameProducer().getLogFilename(getDataFolder().getAbsolutePath(), "chat", "chat");
			FileLogger fLogger = new FileLogger(fileNamePattern, getLogger());
			allLoggers.add(fLogger);
		}
		
		if (useLogentriesChatLogging) {
			LogentriesLogger leLogger = new LogentriesLogger(
					getConfig().getString("chatLogging.logentries.token"),
					getConfig().getBoolean("chatLogging.logentries.debug"));
			allLoggers.add(leLogger);
		}
		
		chatLogger = new ChatLogger(allLoggers, "%1$s|%2$s|%3$s|%4$s|%5$s");
	}

	private void LoadPlayerLanguages() {
		// Attempt to load any existing languages configuration of players from persistent store
		try {
			PlayerLanguageStorePersister langStorePersister = new PlayerLanguageStorePersister(getDataFolder(), LANG_STORE_FILENAME, getLogger());
			if (!langStorePersister.load(langStore)) {
				getLogger().warning("Could not load player languages configuration file upon plugin enable.");
			}
		} catch (IOException e) {
			getLogger().warning("Unable to access player languages persistence store for loading. Skipping.");
		}
	}

	private void LoadGroups() {
		try {
			PlayerGroupsPersister groupsPersister = new PlayerGroupsPersister(getDataFolder(), GROUPS_STORE_FILENAME, getLogger());
			groupsStore = groupsPersister.load();
		} catch (IOException e) {
		}
		
		if (groupsStore == null) {
			groupsStore = new HashMap<String, HashSet<String>>();
			getLogger().warning("Unable to access player groups persistence store for loading. Skipping.");
		}
	}
 
    @Override
    public void onDisable() {
    	// Attempt to store any existing languages configuration of players into persistent store
		PlayerLanguageStorePersister langStorePersister = null;
		try {
			langStorePersister = new PlayerLanguageStorePersister(getDataFolder(), LANG_STORE_FILENAME, getLogger());
			if (!langStorePersister.persist(langStore)) {
				getLogger().warning("Could not persist player languages upon plugin disable.");
			}
		} catch (IOException e) {
			//getLogger().info(e.getMessage());
			getLogger().warning("Unable to access player languages persistence store for storing. Skipping.");
		}
    }
	
	public void sendTranslatedMessageAsync(String message, Player source, Player dest) {
		
		final Player sendFrom = source;
		final Player sendTo = dest;
		final String sendMessage = message;
		final String destinationLanguage = langStore.getLanguageForPlayer(sendTo.getName());
		
		new BukkitRunnable() {
			
			public void run() {
				String chatMessage = String.format("<%1s (%2s)> %3s", sendFrom.getDisplayName(), destinationLanguage,  sendMessage);
				sendTo.sendMessage(chatMessage);
				
				chatLogger.log(sendFrom, sendTo, chatMessage);
			}
		}.runTask(this);
	}

	public void sendMessageSync(String message, Player source, Set<Player> dests) {
		for (Player destPlayer : dests) {
			String chatMessage = String.format("<%1s> %2s", source.getDisplayName(), message);
			destPlayer.sendMessage(chatMessage);
			chatLogger.log(source, destPlayer, chatMessage);
		}
		
		for (Player glistenPlayer : GetOnlineGloballyListeningPlayers()) {
			if (glistenPlayer.getDisplayName() != source.getDisplayName()) {
				glistenPlayer.sendMessage(ChatColor.GOLD + String.format("<G> <%1s> %2s", source.getDisplayName(), message));
			}
		}
	}
	
	private Set<Player> GetOnlineGloballyListeningPlayers() {
		Set<String> listeningPlayerNames = listenCommandExecutor.getEnabledListeningPlayers();
		Set<Player> listeningOnlinePlayers = new HashSet<Player>();
		for (String name : listeningPlayerNames) {
			Player player = this.getServer().getPlayer(name);
			if (player == null) {
				listenCommandExecutor.removeListeningPlayer(name);
			} else {
				listeningOnlinePlayers.add(player);
			}
		}
		
		return listeningOnlinePlayers;
	}

	/**
	 * This implementation delegates the request using the bukkit async invocation API to the real executor
	 */
	public void postTranslationRequest(String message, Player sourcePlayer, Set<Player> destinationPlayers) {
		final String theMessage = message;
		final Player theSender = sourcePlayer;
		final Set<Player> destinations = destinationPlayers;
		final Set<Player> globallyListening = GetOnlineGloballyListeningPlayers();
		globallyListening.remove(sourcePlayer);
		
		globallyListening.addAll(destinations);
		
		new BukkitRunnable() {
			 
            public void run() {
            	translationRequestExecutor.postTranslationRequest(theMessage, theSender, globallyListening);
            }
 
        }.runTaskAsynchronously(this);
        
	}

	public void postCommand(CommandSender sender, String commandLine) {
		Bukkit.dispatchCommand(sender, commandLine);
	}

	public Player getOnlinePlayer(String name) {
		return getServer().getPlayer(name);
	}

	public Set<Player> getAllOnlinePlayers() {
		return new CopyOnWriteArraySet<Player>(Arrays.asList(getServer().getOnlinePlayers()));
	}

	public Set<Player> getGroupPlayers(String groupName) {
		if (groupsStore.containsKey(groupName)) {

			HashSet<String> groupPlayerNames = groupsStore.get(groupName); 
			
			Set<Player> groupPlayers = new HashSet<Player>(groupPlayerNames.size());
			for (String name : groupPlayerNames) {
				Player player = getOnlinePlayer(name);
				if (player != null) {
					groupPlayers.add(player);
				}
			}
			
			return groupPlayers;
		}
		
		return null;
	}
}
