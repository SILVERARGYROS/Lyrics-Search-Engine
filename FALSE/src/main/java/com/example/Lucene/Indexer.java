package com.example.Lucene;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import com.opencsv.CSVReader;

public class Indexer {
	private IndexWriter writer;
	private String indexDirectoryPath;

	public Indexer(String indexDirectoryPath) throws IOException {
		// this directory will contain the indexes
		this.indexDirectoryPath = indexDirectoryPath;
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
		int num = writer.numRamDocs();
		writer.commit();

		return num;

	}
	
	public int createSongIndex(String songDataDirPath, String lyricsDataDirPath, FileFilter filter) throws IOException {

		// Index all song files
		File[] songFiles = new File(songDataDirPath).listFiles();
		for (File file : songFiles) {
			if (!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)) {
				indexFile(file, "songs");
			}
		}
		int num = writer.numRamDocs();
		writer.commit();
		// writer.close();


		/* DEBUG CODE */

		// try {
		// 	Searcher searcher = new Searcher(indexDirectoryPath);
	
		// 	long startTime = System.currentTimeMillis();
		// 	TopDocs hits = searcher.search("\"Taylor Swift lyrics\"");
		// 	long endTime = System.currentTimeMillis();

		// 	System.out.println("\u001B[31m" + hits.totalHits + " documents found. Time :" + (endTime - startTime) + "\u001B[0m");
		// } catch (Exception e) {
		// 	System.out.println(e);
		// 	// TODO: handle exception
		// }

		// DELAYYYYYYYYYYY //

		// try {
		// 	TimeUnit.SECONDS.sleep(3);
		// } catch (InterruptedException e) {
		// 	// TODO Auto-generated catch block
		// 	e.printStackTrace();
		// }
		// initializeWriter(Paths.get(indexDirectoryPath));

		/* DEBUG CODE */


		// Index all lyrics files
		System.out.println(new File(lyricsDataDirPath).getCanonicalPath());
		File[] lyricFiles = new File(lyricsDataDirPath).listFiles();
		for (File file : lyricFiles) {
			if (!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)) {
				indexFile(file, "lyrics");
			}
		}
		writer.commit();

		return writer.numRamDocs();
	}

	private void indexFile(File file, String datatype) throws IOException {
		System.out.println("Indexing " + file.getCanonicalPath());
		ArrayList<Document> finalDocumentList = new ArrayList<>();

		if(datatype.equalsIgnoreCase("albums")){
			finalDocumentList = getAlbumDocument(file);
		}
		else if (datatype.equalsIgnoreCase("songs")){
			finalDocumentList = getSongDocument(file);
		}
		else if (datatype.equalsIgnoreCase("lyrics")){
			finalDocumentList = getLyricsDocument(file);
		}
		else{
			System.out.println("File type not recognized. Please code more carefully.");
		}
		System.out.println("DEBUG DATATYPE = " + datatype);

		for(Document document: finalDocumentList){
			writer.addDocument(document);
		}
		writer.commit();
	}

	private ArrayList<Document> getAlbumDocument(File file) {
		ArrayList<Document> docList = new ArrayList<>();
		try {
			// index file contents
			FileReader fr = new FileReader(file);
			CSVReader reader = new CSVReader(fr);
			String[] currentRecord;
			while((currentRecord = reader.readNext()) != null){ // id,singer_name,name,type,year
				Document document = null;
				// index general field
				String generalString = "";
				for(String field : currentRecord){
					generalString += field + " ";
				}
				TextField generalField = new TextField("general", generalString.strip().toLowerCase(), Store.YES);
				// index album_id
				TextField albumIdField = new TextField("album_id", currentRecord[0].toLowerCase(), Store.YES);
				// index singer_name
				TextField singerNameField = new TextField("singer_name", currentRecord[1].toLowerCase(), Store.YES);
				// index album_name
				TextField albumNameField = new TextField("album_name", currentRecord[2].toLowerCase(), Store.YES);
				// index album_type
				TextField albumTypeField = new TextField("album_type", currentRecord[3].toLowerCase(), Store.YES);
				// index album_year
				TextField albumYearField = new TextField("album_year", currentRecord[4].toLowerCase(), Store.YES);

				// document.add(contentField);
				document = new Document();
				document.add(generalField);
				document.add(albumIdField);
				document.add(singerNameField);
				document.add(albumNameField);
				document.add(albumTypeField);
				document.add(albumYearField);

				docList.add(document);
			}
			reader.close();
			fr.close();
		} catch (IndexOutOfBoundsException e) {
			System.out.println("File Corrupted. Please raise error flag/UI.");
		} catch (IOException e){
			System.out.println("File not found. Please raise error flag/UI.");
		}
		return docList;
	}

	private ArrayList<Document> getSongDocument(File file) {
		ArrayList<Document> docList = new ArrayList<>();
		try {
			Document document = null;
			// index file contents
			FileReader fr = new FileReader(file);
			CSVReader reader = new CSVReader(fr);
			String[] currentRecord;
			while((currentRecord = reader.readNext()) != null){ // song_id,singer_name (album_name),song_name,song_href
				// index general field
				String generalString = "";
				for(String field : currentRecord){
					generalString += field + " ";
				}
				TextField generalField = new TextField("general", generalString.strip().toLowerCase(), Store.YES);
				// index song_id
				TextField songIdField = new TextField("song_id", currentRecord[1].toLowerCase(), Store.YES);
				// index album_name
				TextField albumNameField = new TextField("album_name", currentRecord[2].toLowerCase(), Store.YES);
				// index singer_name
				TextField songHrefField = new TextField("song_href", currentRecord[4].toLowerCase(), Store.YES);
				// index album_name
				TextField songNameField = new TextField("song_name", currentRecord[3].toLowerCase(), Store.YES);
				// index lyrics
				TextField lyricsField = new TextField("lyrics", "not_defined", Store.YES);
				
				// document.add(contentField);
				document = new Document();
				document.add(generalField);
				document.add(songIdField);
				document.add(albumNameField);
				document.add(songHrefField);
				document.add(songNameField);
				document.add(lyricsField);

				System.out.println("LOOP DEBUGU documentfield song_name == " + document.get("song_name"));
				System.out.println("LOOP DEBUGU documentfield album_name == " + document.get("album_name"));
				System.out.println("LOOP DEBUGU documentfield song_href == " + document.get("song_href"));

				docList.add(document);
			}
			reader.close();
			fr.close();
		} catch (IndexOutOfBoundsException e) {
			System.out.println("File Corrupted. Please raise error flag/UI.");
		} catch (IOException e){
			System.out.println("File not found. Please raise error flag/UI.");
		}
		return docList;
	}

	private ArrayList<Document> getLyricsDocument(File file) {
		ArrayList<Document> docList = new ArrayList<>();
		try {
			Document document = null;
			// index file contents
			FileReader fr = new FileReader(file);
			CSVReader reader = new CSVReader(fr);
			String[] currentRecord;

			while((currentRecord = reader.readNext()) != null){ // link (href),artist (album_name),song_name,lyrics
				// index general field
				String generalString = "";
				for(String field : currentRecord){
					generalString += field + " ";
				}
				TextField generalField = new TextField("general", generalString.strip().toLowerCase(), Store.YES);
				// index song_id
				TextField songIdField = new TextField("song_id", "not_defined", Store.YES);
				// index album_name
				TextField albumNameField = new TextField("album_name", currentRecord[2].toLowerCase(), Store.YES);
				// index singer_name
				TextField songHrefField = new TextField("song_href", currentRecord[1].toLowerCase(), Store.YES);
				// index album_name
				TextField songNameField = new TextField("song_name", currentRecord[3].toLowerCase(), Store.YES);
				// index lyrics
				TextField lyricsField = new TextField("lyrics", currentRecord[4].toLowerCase(), Store.YES);

				document = new Document();
				document.add(generalField);
				document.add(songIdField);
				document.add(albumNameField);
				document.add(songHrefField);
				document.add(songNameField);
				document.add(lyricsField);

				System.out.println("LOOP DEBUGU documentfield song_name == " + document.get("song_name"));
				System.out.println("LOOP DEBUGU documentfield album_name == " + document.get("album_name"));
				System.out.println("LOOP DEBUGU documentfield song_href == " + document.get("song_href"));

				docList.add(document);
			}
			reader.close();
			fr.close();
		} catch (IndexOutOfBoundsException e) {
			System.out.println("File Corrupted. Please raise error flag/UI.");
		} catch (IOException e){
			System.out.println("File not found. Please raise error flag/UI.");
		} /* catch (ParseException e) {
			System.out.println("Broken Index Search Query. Please raise error flag/UI.");
		} */
		return docList;
	}

}