package org.gamesforpeace.tongues;

import java.util.Set;

import org.bukkit.entity.Player;
import org.gamesforpeace.tongues.logging.BaseLogger;


public class ChatLogger {
	
	private final Set<BaseLogger> loggers;
	private final String logMessageFormat;
	
	public ChatLogger(Set<BaseLogger> loggers, String logMessageFormat) {
		if (loggers == null) throw new IllegalArgumentException();
		if (logMessageFormat == null) throw new IllegalArgumentException();
		
		this.loggers = loggers;
		this.logMessageFormat = logMessageFormat;
	}
	
	public void log(Player source, Player destination, String message) {
		if (source == null) return;
		if (destination == null) return;
		if (message == null) return;
		
		for (BaseLogger log : loggers) {
			log.log(String.format(logMessageFormat, source.getName(), source.getDisplayName(), destination.getName(), destination.getDisplayName(), message));
		}
	}
}
