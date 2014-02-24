package org.gamesforpeace.tongues;

import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.gamesforpeace.tongues.translation.BingTranslator;
import org.gamesforpeace.tongues.translation.ChatTranslationRequestExecutor;
import org.gamesforpeace.tongues.translation.TranslationRequestExecutor;
import org.gamesforpeace.tongues.translation.Translator;

import com.avaje.ebeaninternal.api.LoadContext;

public final class TonguesPlugin extends JavaPlugin implements ChatMessenger, TranslationRequestExecutor {
	
	private TranslationRequestExecutor translationRequestExecutor;
	
	@Override
    public void onEnable(){
		
		// Will create the default configuration file structure
		saveDefaultConfig();
		
		Translator translator = new BingTranslator(getConfig().getString("bingTranslation.clientID"), getConfig().getString("bingTranslation.secretKey"));
		
		PlayerLanguageStore langStore = new ConcurrentPlayerLanguageStore(translator.getSupportedLanguages(),  translator.getDefaultLanguage());
		translationRequestExecutor = new ChatTranslationRequestExecutor(translator, langStore, this);
		
		getLogger().info("Supported langauges: ");
		for (String lang : translator.getSupportedLanguages()) {
			getLogger().info(lang);
		}
		
		getServer().getPluginManager().registerEvents(new ChatListener(this), this);
		getServer().getPluginCommand("mylang").setExecutor(new MyLangCommandExecutor(langStore));
		getServer().getPluginCommand("whisper").setExecutor(new WhisperCommandExecutor(this));
    }
 
    @Override
    public void onDisable() {
    }
	
	public void sendMessage(String message, Player source, Player dest) {
		
		final Player sendFrom = source;
		final Player sendTo = dest;
		final String sendMessage = message;
		
		new BukkitRunnable() {
			
			public void run() {
				
				sendTo.sendMessage(sendFrom.getDisplayName() + " (Translated): " +  sendMessage);
			}
		}.runTask(this);
	}

	public void sendMessage(String message, Player source, Set<Player> dests) {
		throw new NotImplementedException();
		
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
}
