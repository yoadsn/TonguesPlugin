package org.gamesforpeace.tongues.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class PlayerGroupsPersisterTest {
	
	@Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
	
	@Test
	public void canPersistAndReadFromStoreSingleGroup() throws IOException {
		File dataFolderMock = testFolder.newFolder("someFolder");
		
		PlayerGroupsPersister sut = new PlayerGroupsPersister(dataFolderMock, "theoutputFile", null);

		HashMap<String, HashSet<UUID>> storeToWrite = getSingleGroupStore();
		sut.persist(storeToWrite);

		HashMap<String, HashSet<UUID>> readStore = sut.load();
		
		assertStoreContentsEqual(storeToWrite, readStore);
	}
	
	@Test
	public void canPersistAndReadFromStoreMultiGroup() throws IOException {
		File dataFolderMock = testFolder.newFolder("someFolder");
		
		PlayerGroupsPersister sut = new PlayerGroupsPersister(dataFolderMock, "theoutputFile", null);

		HashMap<String, HashSet<UUID>> storeToWrite = getMultiGroupStore();
		sut.persist(storeToWrite);

		HashMap<String, HashSet<UUID>> readStore = sut.load();
		
		assertStoreContentsEqual(storeToWrite, readStore);
	}
	
	public void canPersistAndReadFromStoreEmptyGroup() throws IOException {
		File dataFolderMock = testFolder.newFolder("someFolder");
		
		PlayerGroupsPersister sut = new PlayerGroupsPersister(dataFolderMock, "theoutputFile", null);

		HashMap<String, HashSet<UUID>> storeToWrite = getEmptyGroupStore();
		sut.persist(storeToWrite);

		HashMap<String, HashSet<UUID>> readStore = sut.load();
		
		assertStoreContentsEqual(storeToWrite, readStore);
	}
	
	private HashMap<String, HashSet<UUID>> getSingleGroupStore() {
		HashMap<String, HashSet<UUID>> store = new  HashMap<String, HashSet<UUID>>();
		store.put("g1", new HashSet<UUID>(Arrays.asList(UUID.randomUUID(), UUID.randomUUID())));
		return store;
	}
	
	private HashMap<String, HashSet<UUID>> getMultiGroupStore() {
		HashMap<String, HashSet<UUID>> store = new  HashMap<String, HashSet<UUID>>();
		store.put("g1", new HashSet<UUID>(Arrays.asList(UUID.randomUUID(), UUID.randomUUID())));
		store.put("g2", new HashSet<UUID>(Arrays.asList(UUID.randomUUID(), UUID.randomUUID())));
		return store;
	}
	
	private HashMap<String, HashSet<UUID>> getEmptyGroupStore() {
		HashMap<String, HashSet<UUID>> store = new  HashMap<String, HashSet<UUID>>();
		return store;
	}
	
	private void assertStoreContentsEqual(
			HashMap<String, HashSet<UUID>> expected,
			HashMap<String, HashSet<UUID>> actual) {
		
		if (expected.size() != actual.size()) fail("Store sizes do not match");
		
		for (String group : expected.keySet()) {
			if (!actual.containsKey(group)) fail("some groups in expected where missing from actual");
			
			HashSet<UUID> expectedGroup = expected.get(group);
			HashSet<UUID> actualGroup = actual.get(group);
			if (expectedGroup.size() != actualGroup.size()) fail("expected group size does not match actual"); 
				
			for (UUID player : expectedGroup) {
				if (!actualGroup.contains(player)) fail ("Some players in actual group where missing");
			}
		}
	}
}
