package org.gamesforpeace.tongues;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import org.junit.Test;

public class ConcurrentPlayerLanguageStoreTest {

	@Test
	public void testGetPlayerNotFound() {
				
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");
		
		assertEquals("a", store.GetLanguageForPlayer(UUID.randomUUID()));
	}
	
	@Test
	public void testSetLangForNewPlayer() {
				
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");
		
		UUID uuid = UUID.randomUUID();
		store.setLanguageForPlayer(uuid, "b");
		assertEquals("b", store.GetLanguageForPlayer(uuid));
	}
	
	@Test
	public void testSetLangForExistingPlayer() {
				
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");
		
		UUID uuid = UUID.randomUUID();
		store.setLanguageForPlayer(uuid, "b");
		store.setLanguageForPlayer(uuid, "c");
		assertEquals("c", store.GetLanguageForPlayer(uuid));
	}
	
	@Test
	public void testGetPlayerFound() {
				
		ConcurrentPlayerLanguageStore store = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");
		
		UUID uuid = UUID.randomUUID();
		store.setLanguageForPlayer(uuid, "b");
		assertEquals("b", store.GetLanguageForPlayer(uuid));
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
