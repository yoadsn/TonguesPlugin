package org.gamesforpeace.tongues.logging;

import java.io.File;
import java.util.Date;

public class TimeBasedLogFilenameProducer {

	public String getLogFilename(String baseFolder, String category, String baseFilename) {
		
		return baseFolder + File.separator +
				"logs" + File.separator +
				category + File.separator +
				getMonth() + File.separator +
				getDay() + File.separator + baseFilename + "." + System.currentTimeMillis() + ".log";
	}
	
	private String getMonth() {
        return String.format("%tm", new Date());
    }

    private String getDay() {
        return String.format("%td", new Date());
    }
}
