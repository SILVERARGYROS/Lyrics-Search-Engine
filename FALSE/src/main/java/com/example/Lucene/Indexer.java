package com.example.Lucene;

import java.io.BufferedReader;
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
			BufferedReader br = new BufferedReader(new FileReader(file));
			String brLine;
			while((brLine = br.readLine()) != null){
				String currentLine = brLine.toString();
	
				// Field contentField = new Field(LuceneConstants.CONTENTS, currentLine, TextField.TYPE_STORED);
	

				String currentLineField[] = currentLine.split(",");	//,id,singer_name,name,type,year

				// index singer_name
				Field singerNameField = new Field("singer_name", currentLineField[2], TextField.TYPE_STORED);
				// index album_name
				Field albumNameField = new Field("album_name", currentLineField[3], TextField.TYPE_STORED);
				// index album_type
				Field albumTypeField = new Field("album_type", currentLineField[4], TextField.TYPE_STORED);
				// index album_year
				Field albumYearField = new Field("album_year", currentLineField[5], TextField.TYPE_STORED);
	
				// document.add(contentField);
				document = new Document();
				document.add(singerNameField);
				document.add(albumNameField);
				document.add(albumTypeField);
				document.add(albumYearField);
			}
			br.close();
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
			BufferedReader br = new BufferedReader(new FileReader(file));
			String brLine;
			while((brLine = br.readLine()) != null){
				String currentLine = brLine.toString();
				// Field contentField = new Field(LuceneConstants.CONTENTS, currentLine, TextField.TYPE_STORED);
				
				String currentLineField[] = currentLine.split(",");	// ,song_id,singer_name,song_name,song_href

				// index singer_name
				Field singerNameField = new Field("singer_name", currentLineField[2], TextField.TYPE_STORED);
				// index album_name
				Field songNameField = new Field("song_name", currentLineField[3], TextField.TYPE_STORED);
				// index lyrics
				Field lyricsField = new Field("lyrics", "Not Defined", TextField.TYPE_STORED);

				// document.add(contentField);
				document = new Document();
				document.add(singerNameField);
				document.add(songNameField);
				document.add(lyricsField);
			}
			br.close();
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
			BufferedReader br = new BufferedReader(new FileReader(file));
			String tempLine = "";
			String fileContent = "";

			// Storing whole file into a 
			while((tempLine = br.readLine()) != null){
				fileContent += tempLine.toString() + "\n";
			}
			
			
			String currentContent[] = fileContent.split("\"\n");
			System.out.println("DEBUG currentContent.length  == " + currentContent.length);
			System.out.println("DEBUG currentContent  == " + currentContent[0]);
			
			for(int i = 0; i < currentContent.length; i += 2){
				// Field contentField = new Field(LuceneConstants.CONTENTS, currentLine, TextField.TYPE_STORED);

				String lyricFields[] = currentContent[i].split(",");	// ,link,artist,song_name,lyrics
				System.out.println("DEBUG lyricFields.length  == " + lyricFields.length);
				System.out.println("DEBUG lyricFields  == " + lyricFields[0] + "||" + lyricFields[1] + "||" + lyricFields[2]  + "||" + lyricFields[3]);
				// index singer_name
				Field singerNameField = new Field("singer_name", lyricFields[2], TextField.TYPE_STORED);
				// index album_name
				Field songNameField = new Field("song_name", lyricFields[3], TextField.TYPE_STORED);
				// index lyrics
				Field lyricsField = new Field("lyrics", currentContent[i+1].strip(), TextField.TYPE_STORED);

				// document.add(contentField);
				document = new Document();
				document.add(singerNameField);
				document.add(songNameField);
				document.add(lyricsField);
			}
			br.close();
			return document;
		} catch (IndexOutOfBoundsException e) {
			System.out.println("File Corrupted. Please raise error flag/UI.");
		} catch (IOException e){
			System.out.println("File not found. Please raise error flag/UI.");
		}
		return document;
	}

}