package org.gamesforpeace.tongues.logging;

import com.logentries.net.AsyncLogger;

public class LogentriesLogger extends BaseLogger {
	
	private AsyncLogger leLogger;
	
	public LogentriesLogger(String token) {
		this(token, false);
	}
	
	public LogentriesLogger(String token, Boolean debugEnabled) {
		leLogger = new AsyncLogger();
		leLogger.setToken(token);
		leLogger.setDebug(false);
	}

	@Override
	public void log(String message) {
		leLogger.addLineToQueue(getTime() + message);
	}
}
