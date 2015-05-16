package org.gamesforpeace.tongues.persistence;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.commons.lang.Validate;
import org.gamesforpeace.tongues.PlayerLanguageStore;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

public class PlayerLanguageStorePersister {
	
	private final Logger logger;
	private final Gson gson;
	public final String storageFileName;
	private File datafolder = null;
	private File dataFile = null;
	
	public PlayerLanguageStorePersister(File dataFolder, String storageFileName, Logger logger) throws IOException {
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
	
	public boolean persist(PlayerLanguageStore langStore) {
		File outputDataFile = getDataFile();
		if (outputDataFile != null) {
			Map<UUID, String> allPlayerLangs = langStore.getAllPlayerLanguages();
			
			try {
				
				OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(outputDataFile, false), Charsets.UTF_8);
				BufferedWriter writer = new BufferedWriter(osw);
				
				try{
					Type typeOfHashMap = new TypeToken<Map<UUID, String>>() { }.getType();
					writer.write(gson.toJson(allPlayerLangs, typeOfHashMap));
				} finally {
					writer.close();
				}
		
				dataFile = null;
				return true;
			} catch (FileNotFoundException e) {
				logger.warning(e.toString());
			} catch (IOException e) {
				logger.warning(e.toString());
			} catch (Exception e) {
				logger.warning(e.toString());
			}
			
		}
		
		dataFile = null;
		return false;
	}
	
	public boolean load(PlayerLanguageStore langStore) {
		File inputDataFile = getDataFile();
		if (inputDataFile != null) {
			try {
				InputStreamReader isr = new InputStreamReader( new FileInputStream(inputDataFile), Charsets.UTF_8);
				JsonReader reader = new JsonReader(isr);
				
				Type typeOfHashMap = new TypeToken<Map<UUID, String>>() { }.getType();
				Map<UUID, String> allPlayerLangs = gson.fromJson(reader, typeOfHashMap);

				langStore.setPlayerLanguages(allPlayerLangs);
				
				reader.close();
				
				return true;
			} catch (FileNotFoundException e) {
				logger.warning(e.toString());
			} catch (IOException e) {
				logger.warning(e.toString());
			} catch (Exception e) {
				logger.warning(e.toString());
			}
		}
		
		if (inputDataFile.exists()) {
			logger.info("Possible format error. Copying old language store file aside before it is overridden with a new empty store file in the correct format.");
			File badFormatFile = new File(datafolder, "bad_format_" + storageFileName);
			try {
				Files.copy(inputDataFile, badFormatFile);
			} catch (IOException e) {
				logger.warning(e.toString());
			}
		}
		
		return false;
	}
	
}
