package org.gamesforpeace.tongues;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.gamesforpeace.tongues.persistence.PlayerLanguageStorePersister;
import org.gamesforpeace.tongues.translation.BingTranslator;
import org.gamesforpeace.tongues.translation.ChatTranslationRequestExecutor;
import org.gamesforpeace.tongues.translation.TranslationRequestExecutor;
import org.gamesforpeace.tongues.translation.Translator;

public final class TonguesPlugin extends JavaPlugin implements ChatMessenger, TranslationRequestExecutor, CommandPoster, ChatDestinationResolver {
	
	private final String LANG_STORE_FILENAME = "langStore.json";
	private TranslationRequestExecutor translationRequestExecutor;
	private PlayerLanguageStore langStore;
	
	@Override
    public void onEnable(){
				
		// Will create the default configuration file structure
		saveDefaultConfig();
		
		//getLogger().fine("Using Bing Translation authentication values -> ClientID:" + getConfig().getString("bingTranslation.clientID") + " SecretKey:" + getConfig().getString("bingTranslation.secretKey"));
		Translator translator = new BingTranslator(getConfig().getString("bingTranslation.clientID"), getConfig().getString("bingTranslation.secretKey"));
		
		langStore = new ConcurrentPlayerLanguageStore(translator.getSupportedLanguages(),  translator.getDefaultLanguage());
		
		// Attempt to load any existing languages configuration of players from persistent store
		PlayerLanguageStorePersister langStorePersister = null;
		try {
			langStorePersister = new PlayerLanguageStorePersister(getDataFolder(), LANG_STORE_FILENAME, getLogger());
			if (!langStorePersister.load(langStore)) {
				getLogger().warning("Could not load player languages configuration file upon plugin enable.");
			}
		} catch (IOException e) {
			getLogger().warning("Unable to access player languages persistence store for loading. Skipping.");
		}
		
		translationRequestExecutor = new ChatTranslationRequestExecutor(translator, langStore, this);
		
		//getLogger().info("Supported languages: " + translator.getSupportedLanguages().toString());
		
		getServer().getPluginManager().registerEvents(new ChatListener(this, this), this);
		getServer().getPluginCommand("tongues.setlang").setExecutor(new SetLangCommandExecutor(langStore, getServer()));
		getServer().getPluginCommand("tongues.whisper").setExecutor(new WhisperCommandExecutor(this));
		getServer().getPluginCommand("tongues.talk").setExecutor(new TalkCommandExecutor(this, this, this, this));
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
				
				sendTo.sendMessage(String.format("%1s (%2s): %3s", sendFrom.getDisplayName(), destinationLanguage,  sendMessage));
			}
		}.runTask(this);
	}

	public void sendMessageSync(String message, Player source, Set<Player> dests) {
		for (Player destPlayer : dests) {
			destPlayer.sendMessage(String.format("<%1s> %2s", source.getDisplayName(), message));
		}
	}

	/**
	 * This implementation delegates the request using the bukkit async invocation API to the real executor
	 */
	public void postTranslationRequest(String message, Player sourcePlayer, Set<Player> destinationPlayers) {
		final String theMessage = message;
		final Player theSender = sourcePlayer;
		final Set<Player> destinations = destinationPlayers;
		
		new BukkitRunnable() {
			 
            public void run() {
            	translationRequestExecutor.postTranslationRequest(theMessage, theSender, destinations);
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
}
