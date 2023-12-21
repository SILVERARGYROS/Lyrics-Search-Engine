package com.example.Lucene;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.opencsv.CSVReader;

public class Indexer {
	private IndexWriter writer;

	public Indexer(String indexDirectoryPath) throws IOException {
		// this directory will contain the indexes
		Path indexPath = Paths.get(indexDirectoryPath);
		if (!Files.exists(indexPath)) {
			Files.createDirectory(indexPath);
		}
		// Path indexPath = Files.createTempDirectory(indexDirectoryPath);
		Directory indexDirectory = FSDirectory.open(indexPath);
		// create the indexer
		IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
		writer = new IndexWriter(indexDirectory, config);
	}

	public void close() throws CorruptIndexException, IOException {
		writer.close();
	}

	public int createAlbumIndex(String dataDirPath, FileFilter filter) throws IOException {
		// get all files in the data directory
		File[] files = new File(dataDirPath).listFiles();
		for (File file : files) {
			if (!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)) {
				indexFile(file, "albums");
			}
		}

		return writer.numRamDocs();
	}

	public int createSongIndex(String songDataDirPath, String lyricsDataDirPath, FileFilter filter) throws IOException {

		// Index all song files
		File[] songFiles = new File(songDataDirPath).listFiles();
		for (File file : songFiles) {
			if (!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)) {
				indexFile(file, "songs");
			}
		}

		// Index all lyrics files
		System.out.println(new File(lyricsDataDirPath).getCanonicalPath());
		File[] lyricFiles = new File(lyricsDataDirPath).listFiles();
		for (File file : lyricFiles) {
			if (!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)) {
				indexFile(file, "lyrics");
			}
		}

		return writer.numRamDocs();
	}

	private void indexFile(File file, String datatype) throws IOException {
		System.out.println("Indexing " + file.getCanonicalPath());
		Document document = null;

		if(datatype.equalsIgnoreCase("albums")){
			document = getAlbumDocument(file);
		}
		else if (datatype.equalsIgnoreCase("songs")){
			document = getSongDocument(file);
		}
		else if (datatype.equalsIgnoreCase("lyrics")){
			document = getLyricsDocument(file);
		}
		else{
			System.out.println("File type not recognized. Please code more carefully.");
		}
		System.out.println("DEBUG DATATYPE = " + datatype);
		writer.addDocument(document);
	}

	private Document getAlbumDocument(File file) {
		Document document = null;
		try {
			// index file contents
			FileReader fr = new FileReader(file);
			CSVReader reader = new CSVReader(fr);
			String[] record;
			while((record = reader.readNext()) != null){
				// index singer_name
				Field singerNameField = new Field("singer_name", record[2], TextField.TYPE_STORED);
				// index album_name
				Field albumNameField = new Field("album_name", record[3], TextField.TYPE_STORED);
				// index album_type
				Field albumTypeField = new Field("album_type", record[4], TextField.TYPE_STORED);
				// index album_year
				Field albumYearField = new Field("album_year", record[5], TextField.TYPE_STORED);
	
				// document.add(contentField);
				document = new Document();
				document.add(singerNameField);
				document.add(albumNameField);
				document.add(albumTypeField);
				document.add(albumYearField);
			}
			reader.close();
			fr.close();
		} catch (IndexOutOfBoundsException e) {
			System.out.println("File Corrupted. Please raise error flag/UI.");
		} catch (IOException e){
			System.out.println("File not found. Please raise error flag/UI.");
		}
		return document;
	}

	private Document getSongDocument(File file) {
		Document document = null;
		try {
			// index file contents
			FileReader fr = new FileReader(file);
			CSVReader reader = new CSVReader(fr);
			String[] record;
			while((record = reader.readNext()) != null){
				// index singer_name
				Field singerNameField = new Field("singer_name", record[2], TextField.TYPE_STORED);
				// index album_name
				Field songNameField = new Field("song_name", record[3], TextField.TYPE_STORED);
				// index lyrics
				Field lyricsField = new Field("lyrics", "Not Defined", TextField.TYPE_STORED);

				// document.add(contentField);
				document = new Document();
				document.add(singerNameField);
				document.add(songNameField);
				document.add(lyricsField);
			}
			reader.close();
			fr.close();
		} catch (IndexOutOfBoundsException e) {
			System.out.println("File Corrupted. Please raise error flag/UI.");
		} catch (IOException e){
			System.out.println("File not found. Please raise error flag/UI.");
		}
		return document;
	}

	private Document getLyricsDocument(File file) {
		Document document = null;
		try {
			// index file contents
			FileReader fr = new FileReader(file);
			CSVReader reader = new CSVReader(fr);
			String[] record;

			// Storing whole file into a 
			while((record = reader.readNext()) != null){
				/* System.out.println("DEBUG LYRICS LINE:");
				for(String field :record){
					System.out.println(" " + field);
				} */

				// index singer_name
				Field singerNameField = new Field("singer_name", record[2], TextField.TYPE_STORED);
				// index album_name
				Field songNameField = new Field("song_name", record[3], TextField.TYPE_STORED);
				// index lyrics
				Field lyricsField = new Field("lyrics", record[4].strip(), TextField.TYPE_STORED);

				// document.add(contentField);
				document = new Document();
				document.add(singerNameField);
				document.add(songNameField);
				document.add(lyricsField);
			}
			reader.close();
			fr.close();
			return document;
		} catch (IndexOutOfBoundsException e) {
			System.out.println("File Corrupted. Please raise error flag/UI.");
		} catch (IOException e){
			System.out.println("File not found. Please raise error flag/UI.");
		}
		return document;
	}

}