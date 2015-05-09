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
	public void canSetLangForTheFirstTimeAndGetItBack() {
				
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");
		
		UUID somePlayerId = UUID.randomUUID();
		store.setLanguageForPlayer(somePlayerId, "b");
		assertEquals("b", store.getLanguageForPlayer(somePlayerId));
	}
	
	@Test
	public void canResetLAngAndGetItBack() {
				
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");
		
		UUID somePlayerId = UUID.randomUUID();
		store.setLanguageForPlayer(somePlayerId, "b");
		store.setLanguageForPlayer(somePlayerId, "c");
		assertEquals("c", store.getLanguageForPlayer(somePlayerId));
	}
	
	@Test
	public void testAllAndOnlyLanguagesStoredAreReturned() {
				
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");
		
		UUID somePlayerId1 = UUID.randomUUID();
		UUID somePlayerId2 = UUID.randomUUID();
		store.setLanguageForPlayer(somePlayerId1, "b");
		store.setLanguageForPlayer(somePlayerId2, "c");
		Map<UUID, String> allPlayerLangs = store.getAllPlayerLanguages();
		Map<UUID, String> modifiableCopy = new HashMap<UUID, String>(allPlayerLangs);
		assertEquals("b", modifiableCopy.remove(somePlayerId1));
		assertEquals("c", modifiableCopy.remove(somePlayerId2));
		assertEquals(0, modifiableCopy.size());
	}
	
	@Test
	public void testRemovedLanguagesAreNotReturnedForAllList() {
				
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");
		
		UUID somePlayerId1 = UUID.randomUUID();
		UUID somePlayerId2 = UUID.randomUUID();
		store.setLanguageForPlayer(somePlayerId1, "b");
		store.setLanguageForPlayer(somePlayerId2, "c");
		store.clearLanguageForPlayer(somePlayerId1);
		Map<UUID, String> allPlayerLangs = store.getAllPlayerLanguages();
		Map<UUID, String> modifiableCopy = new HashMap<UUID, String>(allPlayerLangs);
		assertEquals("c", modifiableCopy.remove(somePlayerId2));
		assertEquals(0, modifiableCopy.size());
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void allLanguagesStoreResultIsReadOnly() {
				
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");

		Map<UUID, String> allPlayerLangs = store.getAllPlayerLanguages();
		allPlayerLangs.remove("anything");
	}
	
	@Test
	public void testMultiLanguageSetAddsAllConfiguration() {
		UUID somePlayerId1 = UUID.randomUUID();
		UUID somePlayerId2 = UUID.randomUUID();
		
		Map<UUID, String> configToAdd = new HashMap<UUID, String>();
		configToAdd.put(somePlayerId1, "a");
		configToAdd.put(somePlayerId2, "b");
		
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");
		
		store.setPlayerLanguages(configToAdd);
		
		for (Entry<UUID, String> configPair : configToAdd.entrySet()) {
			assertEquals(configPair.getValue(), store.getLanguageForPlayer(configPair.getKey()));
		}
	}
	
	@Test
	public void testMultiLanguageSetOverridesExistingConfiguration() {
		UUID somePlayerId1 = UUID.randomUUID();
		Map<UUID, String> configToAdd = new HashMap<UUID, String>();
		configToAdd.put(somePlayerId1, "b");
		
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");
		store.setLanguageForPlayer(somePlayerId1, "z");
		
		store.setPlayerLanguages(configToAdd);
		
		assertEquals("b", store.getLanguageForPlayer(somePlayerId1));
	}
	
	@Test
	public void testMultiLanguageSetLeavesOtherConfigUntouched() {
		UUID somePlayerId1 = UUID.randomUUID();
		UUID somePlayerId2 = UUID.randomUUID();
		Map<UUID, String> configToAdd = new HashMap<UUID, String>();
		configToAdd.put(somePlayerId1, "b");
		
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");
		store.setLanguageForPlayer(somePlayerId2, "z");
		
		store.setPlayerLanguages(configToAdd);
		
		assertEquals("z", store.getLanguageForPlayer(somePlayerId2));
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
