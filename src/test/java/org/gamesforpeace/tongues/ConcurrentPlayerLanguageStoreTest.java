package org.gamesforpeace.tongues;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.junit.Test;

public class ConcurrentPlayerLanguageStoreTest {

	@Test
	public void testGetPlayerNotFound() {
				
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");
		
		assertEquals("a", store.getLanguageForPlayer(UUID.randomUUID()));
	}
	
	@Test
	public void testSetLangForNewPlayer() {
				
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");
		
		UUID uuid = UUID.randomUUID();
		store.setLanguageForPlayer(uuid, "b");
		assertEquals("b", store.getLanguageForPlayer(uuid));
	}
	
	@Test
	public void testSetLangForExistingPlayer() {
				
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");
		
		UUID uuid = UUID.randomUUID();
		store.setLanguageForPlayer(uuid, "b");
		store.setLanguageForPlayer(uuid, "c");
		assertEquals("c", store.getLanguageForPlayer(uuid));
	}
	
	@Test
	public void testGetPlayerFound() {
				
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");
		
		UUID uuid = UUID.randomUUID();
		store.setLanguageForPlayer(uuid, "b");
		assertEquals("b", store.getLanguageForPlayer(uuid));
	}
	
	@Test
	public void testAllAndOnlyLanguagesStoredAreReturned() {
				
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");
		
		UUID uuid1 = UUID.randomUUID();
		store.setLanguageForPlayer(uuid1, "b");
		UUID uuid2 = UUID.randomUUID();
		store.setLanguageForPlayer(uuid2, "c");
		Map<UUID, String> allPlayerLangs = store.getAllPlayerLanguages();
		Map<UUID, String> modifiableCopy = new HashMap<UUID, String>(allPlayerLangs);
		assertEquals("b", modifiableCopy.remove(uuid1));
		assertEquals("c", modifiableCopy.remove(uuid2));
		assertEquals(0, modifiableCopy.size());
	}
	
	@Test
	public void testRemovedLanguagesAreNotReturnedForAllList() {
				
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");
		
		UUID uuid1 = UUID.randomUUID();
		store.setLanguageForPlayer(uuid1, "b");
		UUID uuid2 = UUID.randomUUID();
		store.setLanguageForPlayer(uuid2, "c");
		store.clearLanguageForPlayer(uuid1);
		Map<UUID, String> allPlayerLangs = store.getAllPlayerLanguages();
		Map<UUID, String> modifiableCopy = new HashMap<UUID, String>(allPlayerLangs);
		assertEquals("c", modifiableCopy.remove(uuid2));
		assertEquals(0, modifiableCopy.size());
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void allLanguagesStoreResultIsReadOnly() {
				
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");

		Map<UUID, String> allPlayerLangs = store.getAllPlayerLanguages();
		allPlayerLangs.remove(UUID.randomUUID());
	}
	
	@Test
	public void testMultiLanguageSetAddsAllConfiguration() {
		Map<UUID, String> configToAdd = new HashMap<UUID, String>();
		configToAdd.put(UUID.randomUUID(), "a");
		configToAdd.put(UUID.randomUUID(), "b");
		
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");
		
		store.setPlayerLanguages(configToAdd);
		
		for (Entry<UUID, String> configPair : configToAdd.entrySet()) {
			assertEquals(configPair.getValue(), store.getLanguageForPlayer(configPair.getKey()));
		}
	}
	
	@Test
	public void testMultiLanguageSetOverridesExistingConfiguration() {
		Map<UUID, String> configToAdd = new HashMap<UUID, String>();
		UUID uuid = UUID.randomUUID();
		configToAdd.put(uuid, "b");
		
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");
		store.setLanguageForPlayer(uuid, "z");
		
		store.setPlayerLanguages(configToAdd);
		
		assertEquals("b", store.getLanguageForPlayer(uuid));
	}
	
	@Test
	public void testMultiLanguageSetLeavesOtherConfigUntouched() {
		Map<UUID, String> configToAdd = new HashMap<UUID, String>();
		configToAdd.put(UUID.randomUUID(), "b");
		
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");
		UUID uuid = UUID.randomUUID();
		store.setLanguageForPlayer(uuid, "z");
		
		store.setPlayerLanguages(configToAdd);
		
		assertEquals("z", store.getLanguageForPlayer(uuid));
	}
	
	@Test
	public void testLanguageSupported() {
				
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(Arrays.asList("a", "b")), "a");
		
		assertTrue(store.isLanguageSupported("a"));
		assertTrue(store.isLanguageSupported("b"));
	}
	
	@Test
	public void testLanguageNotSupportedSupported() {
				
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(Arrays.asList("a", "b")), "a");
		
		assertFalse(store.isLanguageSupported("c"));
	}
	
	@Test
	public void testNoSupportedLangauges() {
				
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");
		
		assertFalse(store.isLanguageSupported("c"));
	}

}
