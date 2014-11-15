package org.gamesforpeace.tongues.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

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

		HashMap<String, HashSet<String>> storeToWrite = getSingleGroupStore();
		sut.persist(storeToWrite);

		HashMap<String, HashSet<String>> readStore = sut.load();
		
		assertStoreContentsEqual(storeToWrite, readStore);
	}
	
	@Test
	public void canPersistAndReadFromStoreMultiGroup() throws IOException {
		File dataFolderMock = testFolder.newFolder("someFolder");
		
		PlayerGroupsPersister sut = new PlayerGroupsPersister(dataFolderMock, "theoutputFile", null);

		HashMap<String, HashSet<String>> storeToWrite = getMultiGroupStore();
		sut.persist(storeToWrite);

		HashMap<String, HashSet<String>> readStore = sut.load();
		
		assertStoreContentsEqual(storeToWrite, readStore);
	}
	
	public void canPersistAndReadFromStoreEmptyGroup() throws IOException {
		File dataFolderMock = testFolder.newFolder("someFolder");
		
		PlayerGroupsPersister sut = new PlayerGroupsPersister(dataFolderMock, "theoutputFile", null);

		HashMap<String, HashSet<String>> storeToWrite = getEmptyGroupStore();
		sut.persist(storeToWrite);

		HashMap<String, HashSet<String>> readStore = sut.load();
		
		assertStoreContentsEqual(storeToWrite, readStore);
	}
	
	private HashMap<String, HashSet<String>> getSingleGroupStore() {
		HashMap<String, HashSet<String>> store = new  HashMap<String, HashSet<String>>();
		store.put("g1", new HashSet<String>(Arrays.asList("p1", "p2")));
		return store;
	}
	
	private HashMap<String, HashSet<String>> getMultiGroupStore() {
		HashMap<String, HashSet<String>> store = new  HashMap<String, HashSet<String>>();
		store.put("g1", new HashSet<String>(Arrays.asList("p1", "p2")));
		store.put("g2", new HashSet<String>(Arrays.asList("p3", "p4")));
		return store;
	}
	
	private HashMap<String, HashSet<String>> getEmptyGroupStore() {
		HashMap<String, HashSet<String>> store = new  HashMap<String, HashSet<String>>();
		return store;
	}
	
	private void assertStoreContentsEqual(
			HashMap<String, HashSet<String>> expected,
			HashMap<String, HashSet<String>> actual) {
		
		if (expected.size() != actual.size()) fail("Store sizes do not match");
		
		for (String group : expected.keySet()) {
			if (!actual.containsKey(group)) fail("some groups in expected where missing from actual");
			
			HashSet<String> expectedGroup = expected.get(group);
			HashSet<String> actualGroup = actual.get(group);
			if (expectedGroup.size() != actualGroup.size()) fail("expected group size does not match actual"); 
				
			for (String player : expectedGroup) {
				if (!actualGroup.contains(player)) fail ("Some players in actual group where missing");
			}
		}
	}
}
