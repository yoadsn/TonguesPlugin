package org.gamesforpeace.tongues.persistence;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

import org.gamesforpeace.tongues.ConcurrentPlayerLanguageStore;
import org.gamesforpeace.tongues.PlayerLanguageStore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.cglib.core.CollectionUtils;

public class PlayerLanguageStorePersisterTest {
	
	@Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
	
	@Test(expected = IllegalArgumentException.class)
	public void nullDataFolderNotAllowed() throws IOException {
		new PlayerLanguageStorePersister(null, "a");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void emptyStorageFileNameNotAllowed() throws IOException {
		new PlayerLanguageStorePersister(mock(File.class), "");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void nullStorageFileNameNotAllowed() throws IOException {
		new PlayerLanguageStorePersister(mock(File.class), null);
	}
	
	@Test
	public void dataFolderCreatedIfDoesNotExist() throws IOException {
		File dataFolderMock = mock(File.class);
		
		when(dataFolderMock.exists()).thenReturn(false);
		
		new PlayerLanguageStorePersister(dataFolderMock, "a");
		
		verify(dataFolderMock).createNewFile();
	}
	
	@Test
	public void dataFolderNotCreatedIfExists() throws IOException {
		File dataFolderMock = mock(File.class);
		
		when(dataFolderMock.exists()).thenReturn(true);
		
		new PlayerLanguageStorePersister(dataFolderMock, "a");
		
		verify(dataFolderMock, never()).createNewFile();
	}
	
	@Test
	public void canPersistAndReadFromStore() throws IOException {
		File dataFolderMock = testFolder.newFolder("someFolder");
		
		PlayerLanguageStorePersister sut = new PlayerLanguageStorePersister(dataFolderMock, "theoutputFile");
		PlayerLanguageStore langStore = getPreFilledLanguageStore();
		
		sut.persist(langStore);

		PlayerLanguageStore loadedStore = getEmptyLanguageStore();
		sut.load(loadedStore);
		
		assertEquals(langStore.getAllPlayerLanguages(), loadedStore.getAllPlayerLanguages());
	}
	
	private PlayerLanguageStore getPreFilledLanguageStore()
	{
		PlayerLanguageStore langStore = new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");
		langStore.setLanguageForPlayer("player1", "lang1");
		langStore.setLanguageForPlayer("player2", "lang2");
		langStore.setLanguageForPlayer("player3", "lang3");
		
		return langStore;
	}
	
	private PlayerLanguageStore getEmptyLanguageStore()
	{
		return new ConcurrentPlayerLanguageStore(new HashSet<String>(), "a");
	}
	
	//TODO: Refactor out the file object serialization and persistence - to allow testing of BL
}
