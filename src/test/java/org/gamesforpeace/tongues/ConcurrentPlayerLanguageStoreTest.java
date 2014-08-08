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
		
		assertEquals("a", store.getLanguageForPlayer("someNonExistentName"));
	}
	
	@Test
	public void canSetLangForTheFirstTimeAndGetItBack() {
				
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");
		
		store.setLanguageForPlayer("somePlayer", "b");
		assertEquals("b", store.getLanguageForPlayer("somePlayer"));
	}
	
	@Test
	public void canResetLAngAndGetItBack() {
				
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");
		
		store.setLanguageForPlayer("somePlayer", "b");
		store.setLanguageForPlayer("somePlayer", "c");
		assertEquals("c", store.getLanguageForPlayer("somePlayer"));
	}
	
	@Test
	public void testAllAndOnlyLanguagesStoredAreReturned() {
				
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");
		
		store.setLanguageForPlayer("player1", "b");
		store.setLanguageForPlayer("player2", "c");
		Map<String, String> allPlayerLangs = store.getAllPlayerLanguages();
		Map<String, String> modifiableCopy = new HashMap<String, String>(allPlayerLangs);
		assertEquals("b", modifiableCopy.remove("player1"));
		assertEquals("c", modifiableCopy.remove("player2"));
		assertEquals(0, modifiableCopy.size());
	}
	
	@Test
	public void testRemovedLanguagesAreNotReturnedForAllList() {
				
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");
		
		store.setLanguageForPlayer("player1", "b");
		store.setLanguageForPlayer("player2", "c");
		store.clearLanguageForPlayer("player1");
		Map<String, String> allPlayerLangs = store.getAllPlayerLanguages();
		Map<String, String> modifiableCopy = new HashMap<String, String>(allPlayerLangs);
		assertEquals("c", modifiableCopy.remove("player2"));
		assertEquals(0, modifiableCopy.size());
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void allLanguagesStoreResultIsReadOnly() {
				
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");

		Map<String, String> allPlayerLangs = store.getAllPlayerLanguages();
		allPlayerLangs.remove("anything");
	}
	
	@Test
	public void testMultiLanguageSetAddsAllConfiguration() {
		Map<String, String> configToAdd = new HashMap<String, String>();
		configToAdd.put("somePlayer1", "a");
		configToAdd.put("somePlayer2", "b");
		
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");
		
		store.setPlayerLanguages(configToAdd);
		
		for (Entry<String, String> configPair : configToAdd.entrySet()) {
			assertEquals(configPair.getValue(), store.getLanguageForPlayer(configPair.getKey()));
		}
	}
	
	@Test
	public void testMultiLanguageSetOverridesExistingConfiguration() {
		Map<String, String> configToAdd = new HashMap<String, String>();
		configToAdd.put("somePlayer1", "b");
		
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");
		store.setLanguageForPlayer("somePlayer1", "z");
		
		store.setPlayerLanguages(configToAdd);
		
		assertEquals("b", store.getLanguageForPlayer("somePlayer1"));
	}
	
	@Test
	public void testMultiLanguageSetLeavesOtherConfigUntouched() {
		Map<String, String> configToAdd = new HashMap<String, String>();
		configToAdd.put("somePlayer1", "b");
		
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");
		store.setLanguageForPlayer("somePlayer2", "z");
		
		store.setPlayerLanguages(configToAdd);
		
		assertEquals("z", store.getLanguageForPlayer("somePlayer2"));
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
