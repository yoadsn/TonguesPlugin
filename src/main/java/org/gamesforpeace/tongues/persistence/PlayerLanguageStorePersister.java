package org.gamesforpeace.tongues.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.gamesforpeace.tongues.PlayerLanguageStore;

public class PlayerLanguageStorePersister {
	
	public final String storageFileName;
	private File datafolder = null;
	private File dataFile = null;
	
	public PlayerLanguageStorePersister(File dataFolder, String storageFileName) throws IOException {
		Validate.notNull(dataFolder);
		Validate.notEmpty(storageFileName);
		
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
			Map<String, String> allPlayerLangs = langStore.getAllPlayerLanguages();
			FileOutputStream fouts;
			try {
				fouts = new FileOutputStream(outputDataFile);
				ObjectOutputStream objouts = new ObjectOutputStream(fouts);
				objouts.writeObject(allPlayerLangs);
				objouts.close();
				
				return true;
			} catch (FileNotFoundException e) {

			} catch (IOException e) {

			}
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public boolean load(PlayerLanguageStore langStore) {
		File inputDataFile = getDataFile();
		if (inputDataFile != null) {
			
			FileInputStream fins;
			try {
				fins = new FileInputStream(inputDataFile);
				ObjectInputStream objins = new ObjectInputStream(fins);
				Map<String, String> allPlayerLangs = (Map<String, String>) objins.readObject();
				objins.close();
				
				langStore.setPlayerLanguages(allPlayerLangs);
				
				return true;
			} catch (FileNotFoundException e) {

			} catch (IOException e) {

			} catch (ClassNotFoundException e) {
				
			}
		}
		
		return false;
	}
	
}
