package org.gamesforpeace.tongues.logging;

import java.util.Date;

public abstract class BaseLogger {
	public abstract void log(String message);
	
	protected String getTime() {
        return String.format("%tFT%<tTZ|", new Date());
    }
}
