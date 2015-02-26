package org.gamesforpeace.tongues.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.CharSetUtils;

public class FileLogger extends BaseLogger {

	Logger ambientLogger;
	File logFile;
	OutputStreamWriter writer;
	
	public  FileLogger(String logFileName, Logger ambientLogger)	{
		this.ambientLogger = ambientLogger;
		
		this.logFile = new File(logFileName);
		
		if (!logFile.exists()) {
			logFile.getParentFile().mkdirs();
            try {
            	logFile.createNewFile();
            } catch (IOException e) {
            	ambientLogger.log(Level.SEVERE, "Unable to create file logger for file " + logFileName + ": " + e.getMessage());
            }
		}
		
		try {
			this.writer = new OutputStreamWriter(new FileOutputStream(logFile, true), "UTF8");
		} catch (IOException e) {
			ambientLogger.log(Level.SEVERE, "Unable to create file logger for file " + logFileName + ": " + e.getMessage());
		}
	}
	
	public void log(String message) {
		try {
			writer.write(getTime() + message + System.getProperty("line.separator"));
			writer.flush();
		} catch (IOException e) {
			// Silently ignore - the logger is not operational and it was reported.
		}
	}
	
	public void cleanup()
	{
		try {
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// Silently ignore - the logger is not operational and it was reported.
		}
	}
}
