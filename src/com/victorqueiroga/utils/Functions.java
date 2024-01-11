package com.victorqueiroga.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class Functions {

	public static Properties loadProperties(String propertiesPath) throws IOException {
		Properties properties = new Properties();
		try (InputStream input = Files.newInputStream(Paths.get(propertiesPath))) {
			properties.load(input);
		}
		return properties;
	}

	public static List<String> loadResponsibles(String responsiblesPath) throws IOException {
		return Files.readAllLines(Paths.get(responsiblesPath));
	}

	public static Iterator<FileStore> getFileStores() {
		FileSystem fileSystem = FileSystems.getDefault();
		Iterable<FileStore> it = fileSystem.getFileStores();
		Iterator<FileStore> iterator = it.iterator();

		return iterator;
	}

}
