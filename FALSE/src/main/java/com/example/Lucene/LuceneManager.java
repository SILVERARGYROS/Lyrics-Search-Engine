package com.example.Lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;

import java.util.concurrent.ExecutionException;


public class LuceneManager {
	// Project path
	private static String path; // Initialized on start

	// Song variables
	private static String songIndexDir = "";
	private static String songDataDir = "";
	private static String lyricsDataDir = "";

	// Album variables
	private static String albumIndexDir = "";
	private static String albumDataDir = "";

	// Indexers
	Indexer songIndexer;
	Indexer albumIndexer;

	public LuceneManager() throws IOException{
		path = new File(".").getCanonicalPath();
		System.out.println("PROJECT RUNNING PATH: " + path);

		// Initializing song file input path
		songIndexDir = path + "\\FALSE\\Index\\songs";
		albumIndexDir = path + "\\FALSE\\Index\\albums";
		
		// Initializing album file input path
		songDataDir = path + "\\FALSE\\Data\\songs";
		lyricsDataDir = path + "\\FALSE\\Data\\lyrics";
		albumDataDir = path + "\\FALSE\\Data\\albums";

		songIndexer = new Indexer(songIndexDir);
		albumIndexer = new Indexer(albumIndexDir);
	}
	
	public void run(String[] args) throws IOException, ParseException, InterruptedException, ExecutionException{
		initializeIndexes();
		this.simpleSongSearch("achipi", "Artist"); // Here we enter the query for Search
		// getFromSource("SLEEPWALKING", "A-Z Lyrics");
	}

	public void initializeIndexes() throws IOException {
		createAlbumIndex(albumDataDir);
		createSongIndex(songDataDir, lyricsDataDir);
	}

	public void close(){
		
	}

	private void createAlbumIndex(String dataDir) throws IOException {
		long startTime = System.currentTimeMillis();
		int numIndexed = albumIndexer.createAlbumIndex(dataDir, new TextFileFilter());
		long endTime = System.currentTimeMillis();
		System.out.println(numIndexed + " Album(s) indexed, time taken: " + (endTime-startTime) + " ms");
	}

	private void createSongIndex(String songDataDir, String lyricsDataDir) throws IOException {
		long startTime = System.currentTimeMillis();
		int numIndexed[] = songIndexer.createSongIndex(songDataDir, lyricsDataDir, new TextFileFilter());
		long endTime = System.currentTimeMillis();
		System.out.println(numIndexed[0] + " Songs(s) indexed, time taken: "  + numIndexed[1] + " Lyrics(s) indexed, time taken: " + (endTime-startTime) + " ms");
	}
	
	public void addAlbumToIndex(ArrayList<String> fields) throws IOException{
		albumIndexer.addAlbum(fields);
		albumIndexer.commit();
	}

	public void addSongToIndex(ArrayList<String> fields) throws IOException{
		songIndexer.addSong(fields);
		songIndexer.commit();
	}
	
	public void removeAlbumFromIndex(ScoreDoc scoreDoc) throws IOException{
		albumIndexer.removeDocument(scoreDoc);
		albumIndexer.commit();
	}

	public void removeSongFromIndex(ScoreDoc scoreDoc) throws IOException{
		songIndexer.removeDocument(scoreDoc);
		songIndexer.commit();
		
	}

	public void editAlbumFromIndex(ScoreDoc scoreDoc, ArrayList<String> fields) throws CorruptIndexException, IOException{
		albumIndexer.removeDocument(scoreDoc);
		albumIndexer.addSong(fields);
		albumIndexer.commit();
	}

	public void editSongFromIndex(ScoreDoc scoreDoc, ArrayList<String> fields) throws CorruptIndexException, IOException{
		songIndexer.removeDocument(scoreDoc);
		songIndexer.addSong(fields);
		songIndexer.commit();
	}

	public void addAlbumFileToIndex(File file) throws IOException{
		albumIndexer.indexFile(file, "albums");
		albumIndexer.commit();
	}

	public void addSongFileToIndex(File file) throws IOException{
		songIndexer.indexFile(file, "songs");
		songIndexer.commit();
	}

	public void addLyricsFileToIndex(File file) throws IOException{
		songIndexer.indexFile(file, "lyrics");
		songIndexer.commit();
	}

	public void simpleSongSearch(String searchQuery, String fieldName) throws IOException, ParseException {
		simpleSearch(searchQuery, fieldName, songIndexDir);
	}

	public void simpleAlbumSearch(String searchQuery, String fieldName) throws IOException, ParseException {
		simpleSearch(searchQuery, fieldName, albumIndexDir);
	}
		
	private Document[] simpleSearch(String searchQuery, String fieldName, String indexDir) throws IOException, ParseException {
		// Start timer
		long startTime = System.currentTimeMillis();

		// instantiate searcher
		Searcher searcher = new Searcher(indexDir, fieldName);

		// Construct query
		Query query = searcher.constructSimpleQuery(searchQuery);

		// Execute query
		TopDocs hits = searcher.search(query);

		// Get result documents
		Document[] documents = searcher.getDocuments(hits);

		// Close searcher
		searcher.close();
		
		// Stop timer
		long endTime = System.currentTimeMillis();
		System.out.println(hits.totalHits + " documents found. Time :" + (endTime - startTime));
		
		// Score Debug
		for(ScoreDoc scoreDoc : hits.scoreDocs) {
			System.out.println("SCORE DEBUG == " + scoreDoc.score);
			// System.out.println("File: " + doc.get(LuceneConstants.FILE_PATH));
		}
		
		// Return results
		return documents;
	}
	
	// https://reintech.io/blog/java-web-scraping-extracting-data-from-websites
	// https://github.com/jagrosh/JLyrics/blob/master/README.md
	public Document getFromSource(String songName, String source) throws IOException, InterruptedException, ExecutionException{
		LyricsClient client = new LyricsClient(source);
        Lyrics lyrics = client.getLyrics(songName).get();
		
		String[] fields = {lyrics.getTitle(), lyrics.getAuthor(), lyrics.getContent(), lyrics.getSource()};
		Document document = songIndexer.createSongDocument(fields);
  
		System.out.println("Title: " + lyrics.getTitle() + " Author: " + lyrics.getAuthor() + " \nLyrics: \n\n" + lyrics.getContent() + "\n Source: " + lyrics.getSource());
		System.out.println("Out of url method");

		return document;
	}
}