package org.gamesforpeace.tongues.persistence;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class PlayerLanguageStorePersisterTest {
	
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
	
	//TODO: Refactor out the file object serialization and persistence - to allow testing of BL
}
