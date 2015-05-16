package org.gamesforpeace.tongues.persistence;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.commons.lang.Validate;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

public class PlayerGroupsPersister {
	
	private final Logger logger;
	private final Gson gson;
	public final String storageFileName;
	private File datafolder = null;
	private File dataFile = null;
	
	public PlayerGroupsPersister(File dataFolder, String storageFileName, Logger logger) throws IOException {
		Validate.notNull(dataFolder);
		Validate.notEmpty(storageFileName);
		
		this.logger = logger;
		this.gson = new GsonBuilder().setPrettyPrinting().create();
		this.datafolder = dataFolder;
		this.storageFileName = storageFileName;
		
		if (!dataFolder.exists()) dataFolder.createNewFile();
	}
	
	private File getDataFile() {
		if (dataFile == null) {
			dataFile = new File(datafolder, storageFileName);
		}
		
		return dataFile;
	}
	
	public boolean persist(HashMap<String, HashSet<UUID>> playerGroups) {
		File outputDataFile = getDataFile();
		if (outputDataFile != null) {
			
			try {
				
				OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(outputDataFile, false), Charsets.UTF_8);
				BufferedWriter writer = new BufferedWriter(osw);
				
				try{
					Type storedType = new TypeToken<HashMap<String, HashSet<UUID>>>() { }.getType();
					writer.write(gson.toJson(playerGroups, storedType));
				} finally {
					writer.close();
				}
				
				return true;
			} catch (FileNotFoundException e) {
				logger.warning(e.toString());
			} catch (IOException e) {
				logger.warning(e.toString());
			} catch (Exception e) {
				logger.warning(e.toString());
			}
			
		}
		
		return false;
	}
	
	public HashMap<String, HashSet<UUID>> load() {
		File inputDataFile = getDataFile();
		if (inputDataFile != null) {
			
			try {
				InputStreamReader isr = new InputStreamReader( new FileInputStream(inputDataFile), Charsets.UTF_8);
				JsonReader reader = new JsonReader(isr);
				
				Type typeOfHashMap = new TypeToken<HashMap<String, HashSet<UUID>>>() { }.getType();
				HashMap<String, HashSet<UUID>> playerGroups = gson.fromJson(reader, typeOfHashMap);
				
				reader.close();
				
				return playerGroups;
			} catch (FileNotFoundException e) {
				logger.warning(e.toString());
			} catch (IOException e) {
				logger.warning(e.toString());
			} catch (Exception e) {
				logger.warning(e.toString());
			}
		}
		
		if (inputDataFile.exists()) {
			logger.info("Possible format error. Copying old groups store file aside before it is overridden with a new empty store file in the correct format.");
			File badFormatFile = new File(datafolder, "bad_format_" + storageFileName);
			try {
				Files.copy(inputDataFile, badFormatFile);
			} catch (IOException e) {
				logger.warning(e.toString());
			}
		}
		
		return null;
	}
}
