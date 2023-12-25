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

	// Album variables
	private static String albumIndexDir = "";
	private static String albumDataDir = "";




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

			createAlbumIndex(albumIndexDir, albumDataDir);
			createSongIndex(songIndexDir, songDataDir, lyricsDataDir);
			this.searchSongs("\"Taylor Swift lyrics\""); // Here we enter the query for Search
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createAlbumIndex(String indexDir, String dataDir) throws IOException {
		Indexer indexer = new Indexer(indexDir);

		long startTime = System.currentTimeMillis();
		int numIndexed = indexer.createAlbumIndex(dataDir, new TextFileFilter());
		indexer.close();
		long endTime = System.currentTimeMillis();
		System.out.println(numIndexed + " Album(s) indexed, time taken: " + (endTime-startTime) + " ms");
	}

	private void createSongIndex(String indexDir, String songDataDir, String lyricsDataDir) throws IOException {
		Indexer indexer = new Indexer(indexDir);
		long startTime = System.currentTimeMillis();
		int numIndexed = indexer.createSongIndex(songDataDir, lyricsDataDir, new TextFileFilter());
		indexer.close();
		long endTime = System.currentTimeMillis();
		System.out.println(numIndexed + " Songs(s) indexed, time taken: " + (endTime-startTime) + " ms");
	}

	public void searchSongs(String searchQuery) throws IOException, ParseException {
		search(searchQuery, songIndexDir);
	}

	public void searchAlbums(String searchQuery) throws IOException, ParseException {
		search(searchQuery, albumIndexDir);
	}
		
	private void search(String searchQuery, String indexDir) throws IOException, ParseException {
		Searcher searcher = new Searcher(indexDir);

		long startTime = System.currentTimeMillis();
		TopDocs hits = searcher.search(searchQuery);
		long endTime = System.currentTimeMillis();

		System.out.println(hits.totalHits + " documents found. Time :" + (endTime - startTime));
		for(ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = searcher.getDocument(scoreDoc);
			// System.out.println("File: " + doc.get(LuceneConstants.FILE_PATH));
		}

		searcher.close();
	}
}