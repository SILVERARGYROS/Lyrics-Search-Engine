package com.example.Lucene;

import java.io.File;
import java.io.IOException;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
public class LuceneManager {
	// Project path
	private static String path; // Initialized on start

	// Song variables
	private static String songIndexDir = "";
	private static String songDataDir = "";
	private static String lyricsDataDir = "";
	private static Indexer songIndexer;
	private static Searcher songSearcher;

	// Album variables
	private static String albumIndexDir = "";
	private static String albumDataDir = "";
	private static Indexer albumIndexer;
	private static Searcher albumSearcher;




	public void run(String[] args) throws IOException{
		initializeTester();
	}

	public void initializeTester() {
		try {
			path = new File(".").getCanonicalPath();
			System.out.println("PROJECT RUNNING PATH: " + path);

			// Initializing song file input path
			songIndexDir = path + "\\FALSE\\Index\\songs";
			albumIndexDir = path + "\\FALSE\\Index\\albums";
			
			// Initializing album file input path
			songDataDir = path + "\\FALSE\\Data\\songs";
			lyricsDataDir = path + "\\FALSE\\Data\\lyrics";
			albumDataDir = path + "\\FALSE\\Data\\albums";

			songIndexer = createSongIndex(songIndexDir, songDataDir, lyricsDataDir);
			albumIndexer = createAlbumIndex(albumIndexDir, albumDataDir);
			// this.search("Taylor Swift"); // Here we enter the query for Search
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Indexer createAlbumIndex(String indexDir, String dataDir) throws IOException {
		Indexer indexer = new Indexer(indexDir);

		long startTime = System.currentTimeMillis();
		int numIndexed = indexer.createAlbumIndex(dataDir, new TextFileFilter());
		indexer.close();
		long endTime = System.currentTimeMillis();
		System.out.println(numIndexed + " Album(s) indexed, time taken: " + (endTime-startTime) + " ms");

		return indexer;
	}

	private Indexer createSongIndex(String indexDir, String songDataDir, String lyricsDataDir) throws IOException {
		Indexer indexer = new Indexer(indexDir);

		long startTime = System.currentTimeMillis();
		int numIndexed = indexer.createSongIndex(songDataDir, lyricsDataDir, new TextFileFilter());
		indexer.close();
		long endTime = System.currentTimeMillis();
		System.out.println(numIndexed + " Songs(s) indexed, time taken: " + (endTime-startTime) + " ms");

		return indexer;
	}

	public void searchSongs(String searchQuery) throws IOException, ParseException {
		search(searchQuery, songIndexDir, songSearcher);
	}

	public void searchAlbums(String searchQuery) throws IOException, ParseException {
		search(searchQuery, albumIndexDir, albumSearcher);
	}
		
	private void search(String searchQuery, String indexDir, Searcher searcher) throws IOException, ParseException {
		searcher = new Searcher(indexDir);
		long startTime = System.currentTimeMillis();
		TopDocs hits = searcher.search(searchQuery);
		long endTime = System.currentTimeMillis();
		System.out.println(hits.totalHits + " documents found. Time :" + (endTime - startTime));
		for(ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = searcher.getDocument(scoreDoc);
			System.out.println("File: " + doc.get(LuceneConstants.FILE_PATH));
		}
		searcher.close();
	}
}
