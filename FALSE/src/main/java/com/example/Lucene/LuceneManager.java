package com.example.Lucene;
import com.example.App;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.MatchAllDocsQuery;

import java.util.concurrent.ExecutionException;


public class LuceneManager {
	// Project path
	private static String path; // Initialized on start
	
	// Settings path
	private String settingsDir;

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

	public LuceneManager() throws IOException, ParseException{
		path = new File(".").getCanonicalPath();
		System.out.println("PROJECT RUNNING PATH: " + path);

		// Creating Folder System
		// Creating data folder	
		new File("./SilverLuceneProjectAppData/Data").mkdirs();
		new File("./SilverLuceneProjectAppData/Data/settings").mkdirs();

		// Creating index folder
		new File("./SilverLuceneProjectAppData/Index").mkdirs();
		new File("./SilverLuceneProjectAppData/Index/albums").mkdirs();
		new File("./SilverLuceneProjectAppData/Index/songs").mkdirs();

		// Initializing settings
		settingsDir = path + "/SilverLuceneProjectAppdata/Data/settings/LuceneSettings.conf";
		LuceneSettings.InstantiateSettings(settingsDir);

		// Initializing song file input path
		songIndexDir = path + "/SilverLuceneProjectAppdata/Index/songs";
		albumIndexDir = path + "/SilverLuceneProjectAppdata/Index/albums";
		
		// Initializing album file input path
		// songDataDir = App.class.getResource("data/songs").toString();
		// lyricsDataDir = path + App.class.getResource("data/lyrics").toString();
		// albumDataDir = path + App.class.getResource("data/albums").toString();

		songIndexer = new Indexer(songIndexDir);
		albumIndexer = new Indexer(albumIndexDir);
		initializeIndexes();
	}
	
	public void run(String[] args) throws IOException, ParseException, InterruptedException, ExecutionException{
		// initializeIndexes();
		// this.simpleSongSearch("swift AND taylor", "Artist"); // Here we enter the query for Search
		// getFromSource("SLEEPWALKING", "A-Z Lyrics");
		// close();
	}

	public void initializeIndexes() throws IOException, ParseException {
		createAlbumIndex(albumDataDir);
		createSongIndex(songDataDir, lyricsDataDir);
	}

	public void close(){
		
	}

	private void createAlbumIndex(String dataDir) throws IOException, ParseException {
		long startTime = System.currentTimeMillis();
		int numIndexed = albumIndexer.createAlbumIndex(dataDir, new TextFileFilter());
		long endTime = System.currentTimeMillis();
		System.out.println(numIndexed + " Album(s) indexed, time taken: " + (endTime-startTime) + " ms");
	}

	private void createSongIndex(String songDataDir, String lyricsDataDir) throws IOException, ParseException {
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

	public void addAlbumFileToIndex(File file, boolean ignoreFirstLine) throws IOException, ParseException{
		albumIndexer.indexFile(file, "albums", ignoreFirstLine);
		albumIndexer.commit();
	}

	public void addSongFileToIndex(File file, boolean ignoreFirstLine) throws IOException, ParseException{
		songIndexer.indexFile(file, "songs", ignoreFirstLine);
		songIndexer.commit();
	}

	public void addLyricsFileToIndex(File file, boolean ignoreFirstLine) throws IOException, ParseException{
		songIndexer.indexFile(file, "lyrics", ignoreFirstLine);
		songIndexer.commit();
	}

	public ScoreDoc[] simpleSongSearch(String searchQuery, String fieldName) throws IOException, ParseException {
		return simpleSearch(searchQuery, fieldName, songIndexDir);
	}

	public ScoreDoc[] simpleAlbumSearch(String searchQuery, String fieldName) throws IOException, ParseException {
		return simpleSearch(searchQuery, fieldName, albumIndexDir);
	}
		
	private ScoreDoc[] simpleSearch(String searchQuery, String fieldName, String indexDir) throws IOException, ParseException {
		// Start timer
		long startTime = System.currentTimeMillis();

		// instantiate searcher
		Searcher searcher = new Searcher(indexDir, fieldName);

		// Construct query
		Query query = searcher.constructSimpleQuery(searchQuery);

		// Execute query
		TopDocs hits = searcher.search(query);
		
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
		return hits.scoreDocs;
	}

	public ScoreDoc[] advancedSongSearch(String[] searchQuery, String[] queryType) throws IOException, ParseException {
		return advancedSearch(searchQuery, queryType, songIndexDir);
	}

	public ScoreDoc[] advancedAlbumSearch(String[] searchQuery, String[] queryType) throws IOException, ParseException {
		return advancedSearch(searchQuery, queryType, albumIndexDir);
	}

	public ScoreDoc[] advancedSearch(String[] searchQuery, String[] queryType, String indexDir) throws IOException, ParseException {
		// Start timer
		long startTime = System.currentTimeMillis();

		// instantiate searcher
		Searcher searcher = new Searcher(indexDir);

		// Construct query
		Query query = searcher.constructCombinedQuery(searchQuery, queryType, Occur.MUST);

		// Execute query
		TopDocs hits = searcher.search(query);

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
		return hits.scoreDocs;
	}

	public ScoreDoc[] relatedSongSearch(Document document) throws IOException, ParseException{
		return relatedSearch(document, songIndexDir);
	}

	public ScoreDoc[] relatedAlbumSearch(Document document) throws IOException, ParseException{
		return relatedSearch(document, albumIndexDir);
	}

	public ScoreDoc[] relatedSearch(Document document, String indexDir) throws IOException, ParseException{

		// Start timer
		long startTime = System.currentTimeMillis();

		// instantiate searcher
		Searcher searcher = new Searcher(indexDir);

		// Get field names and searches
		String[] fieldNames = new String[document.getFields().size()];	
		String[] fieldSearches = new String[document.getFields().size()];
		
		int i = 0;
		for(IndexableField field: document.getFields()){
			fieldNames[i] = (field.name());
			fieldSearches[i] = (field.stringValue());
			i++;
		}

		// Construct query
		Query query = searcher.constructCombinedQuery(fieldNames, fieldSearches, Occur.SHOULD);

		System.out.println("\n\nDEEBUG SIMILAR QUERY: " + query + "\n\n");
		// Execute query
		TopDocs hits = searcher.search(query);

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

		return hits.scoreDocs;
	}

	public Document getSongDocument(ScoreDoc scoredoc) throws CorruptIndexException, IOException{
		Searcher searcher = new Searcher(songIndexDir);
		Document document = searcher.getDocument(scoredoc); 
		searcher.close();
		return document;
	}

	public Document getAlbumDocument(ScoreDoc scoredoc) throws CorruptIndexException, IOException{
		Searcher searcher = new Searcher(albumIndexDir);
		Document document = searcher.getDocument(scoredoc); 
		searcher.close();
		return document;
	}

	public Document createSongDocument(String[] fields) throws CorruptIndexException, IOException{
		return songIndexer.createSongDocument(fields);
	}

	public Document createAlbumDocument(String[] fields) throws CorruptIndexException, IOException{
		return songIndexer.createAlbumDocument(fields);
	}
	
	
	// https://reintech.io/blog/java-web-scraping-extracting-data-from-websites
	// https://github.com/jagrosh/JLyrics/blob/master/README.md
	public Document getFromSource(String songName) throws IOException, InterruptedException, ExecutionException{
		Document document = null;
		try{
			LyricsClient client = new LyricsClient(LuceneSettings.getSCRAPING_SOURCE());
			Lyrics lyrics = client.getLyrics(songName).get();
			
			String[] fields = {lyrics.getAuthor(), lyrics.getSource(), lyrics.getTitle(), lyrics.getContent()};
			document = songIndexer.createSongDocument(fields);
	  
			System.out.println("Title: " + lyrics.getTitle() + " Author: " + lyrics.getAuthor() + " \nLyrics: \n\n" + lyrics.getContent() + "\n Source: " + lyrics.getSource());
		}
		catch(Exception e){
			System.out.println("Document not found! Returning null document. Exception: " + e);
		}

		return document;
	}

	public String[] getSongFields(Document document){
		String[] fields = new String[document.getFields().size()];

		fields[0] = document.get("General");
		fields[1] = document.get("Song");
		fields[2] = document.get("Artist");
		fields[3] = document.get("Song_Link");
		fields[4] = document.get("Lyrics");

		return fields;
	}

	public String[] getAlbumFields(Document document){
		String[] fields = new String[document.getFields().size()];

		fields[0] = document.get("General");
		fields[1] = document.get("Album");
		fields[2] = document.get("Artist");
		fields[3] = document.get("Year");
		fields[4] = document.get("Album_Type");

		return fields;
	}

	public void clearCollection() throws IOException, ParseException{
		Query query = new MatchAllDocsQuery();

		// Instantiate Searchers
		Searcher songSearcher = new Searcher(songIndexDir);
		Searcher albumSearcher = new Searcher(albumIndexDir);

		// Get ScoreDocs
		ScoreDoc[] songScoredocs = songSearcher.search(query).scoreDocs;
		ScoreDoc[] albumScoredocs = albumSearcher.search(query).scoreDocs;

		// Remove Documents
		for(ScoreDoc scoredoc: songScoredocs){
			songIndexer.removeDocument(scoredoc);
		}
		for(ScoreDoc scoredoc: albumScoredocs){
			songIndexer.removeDocument(scoredoc);
		}

		songSearcher.close();
		albumSearcher.close();
	}

	public void setMAX_SEARCH(int x){
		LuceneSettings.setMAX_SEARCH(x);
	}

	public void setSIMILARITY_METHOD(int x){
		LuceneSettings.setSIMILARITY_METHOD(x);
	}

	public void setSCRAPING_SOURCE(int x){
		LuceneSettings.setSCRAPING_SOURCE(x);
	}
}