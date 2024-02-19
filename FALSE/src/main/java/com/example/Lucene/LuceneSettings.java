package com.example.Lucene;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Map.Entry;

import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;

// import org.apache.lucene.analysis.core.KeywordAnalyzer;
// import org.apache.lucene.analysis.core.SimpleAnalyzer;
// import org.apache.lucene.analysis.custom.CustomAnalyzer;
// import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;

// import java.util.HashMap;
// import java.util.Map;

// import org.apache.lucene.analysis.Analyzer;

public class LuceneSettings {

	// Settings dir
	private static String settingsDir = "";

	// main settings
	private static int MAX_SEARCH;
	private static Similarity SIMILARITY_METHOD;
	private static String SCRAPING_SOURCE;

	private static Map<Integer, Similarity> SIMILARITY_POOL = new HashMap<>();
	private static Map<Integer, String> SCRAPING_POOL = new HashMap<>();

	public static Map<Integer, Similarity> getSIMILARITY_POOL() {
		return SIMILARITY_POOL;
	}

	public static Map<Integer, String> getSCRAPING_POOL() {
		return SCRAPING_POOL;
	}

	public static int getMAX_SEARCH() {
		return MAX_SEARCH;
	}

	public static void setMAX_SEARCH(int x) {
		MAX_SEARCH = x;
	}

	public static Similarity getSIMILARITY_METHOD() {
		return SIMILARITY_METHOD;
	}

	public static void setSIMILARITY_METHOD(int x) {
		SIMILARITY_METHOD = SIMILARITY_POOL.get(x);
	}

	public static String getSCRAPING_SOURCE() {
		return SCRAPING_SOURCE;
	}

	public static void setSCRAPING_SOURCE(int x) {
		SCRAPING_SOURCE = SCRAPING_POOL.get(x);
	}

	public static void setSCRAPING_SOURCE(String source) {
		SCRAPING_SOURCE = source;
	}
	

	public static int getSimilarityCode(){
		for (Entry<Integer, Similarity> entry : SIMILARITY_POOL.entrySet()) {
			if (Objects.equals(SIMILARITY_METHOD, entry.getValue())) {
				return entry.getKey();
			}
		}
		return -1;
	}

	public static int getScrapingCode(){
		for (Entry<Integer, String> entry : SCRAPING_POOL.entrySet()) {
			if (Objects.equals(SCRAPING_SOURCE, entry.getValue())) {
				return entry.getKey();
			}
		}
		return -1;
	}

	public static void InstantiateSettings(String path) throws IOException{
		// Instantiate similarity pool
		SIMILARITY_POOL.put(1, new BM25Similarity());
		SIMILARITY_POOL.put(2, new ClassicSimilarity());
		SIMILARITY_POOL.put(3, new CTFIDFSimilarity());

		// Instantiate scraping pool
		SCRAPING_POOL.put(1, "A-Z Lyrics");
		SCRAPING_POOL.put(2, "Genius");
		SCRAPING_POOL.put(3, "MusixMatch");
		SCRAPING_POOL.put(4, "MusicMatch");
		SCRAPING_POOL.put(5, "LyricsFreak");
		// SCRAPING_POOL.put(6, "MusixMatch");

		settingsDir = path;
		File f = new File(settingsDir);
		if(f.exists() && !f.isDirectory()) { 
			pullSettingsFromFile(settingsDir);
		}
		else{
			// Loading default settings
			MAX_SEARCH = 100;
			SIMILARITY_METHOD = SIMILARITY_POOL.get(1);
			SCRAPING_SOURCE = SCRAPING_POOL.get(2);
			pushSettingsToFile();
		}
	}

	public static void pullSettingsFromFile(String path) throws FileNotFoundException{
		// Reading settings from file
		String settingString = "";
		Scanner scanner = new Scanner(new File(path));
		while(scanner.hasNextLine()){
			settingString += scanner.nextLine() + "\n";
		}
		scanner.close();

		// System.out.println("DEBUG settingsString == " + settingString);
		String[] settingCodes = settingString.split("\n");
		// System.out.println("DEBUG settingsCoodes == " + settingCodes[0]);
		int[] settings = new int[settingCodes.length];
		// System.out.println("DEBUG length ==" + settingCodes.length);
		for(int i = 0; i < settingCodes.length; i++){
			System.out.println("DEBUG settings == " + settingCodes[i]);
			settings[i] = Integer.parseInt(settingCodes[i].strip());
		}

		// Instantiating settings
		MAX_SEARCH = settings[0];
		SIMILARITY_METHOD = SIMILARITY_POOL.get(settings[1]);
		SCRAPING_SOURCE = SCRAPING_POOL.get(settings[2]);
	}

	public static void pushSettingsToFile() throws IOException{
		FileWriter fw = new FileWriter(settingsDir,false); 
		fw.write(MAX_SEARCH + "\n" + getSimilarityCode() + "\n" + getScrapingCode());
		fw.close();
	}






	// Analyzer softwareAnalyzer = new 

	// public static CustomAnalyzer customStandardAnalyzer;
	// public static CustomAnalyzer cNameAnalyzer;
	// public static PerFieldAnalyzerWrapper perFieldAnalyzer;
	
	// LuceneSettings() throws IOException{

	// 	// StandardAnalyzer could not be instantiated due to 
	// 	// Lucene's modularity issues, so I remade it instead.
	// 	customStandardAnalyzer = CustomAnalyzer.builder()
	// 						.withTokenizer("standard")
	// 						.addTokenFilter("standard")
	// 						.addTokenFilter("lowercase")
	// 						.addTokenFilter("porterstem")
	// 						.addTokenFilter("stop", "ignoreCase", "false", "words", "stopwords.txt", "format", "wordset")
	// 						.build();

	// 	// Map to set up perFieldAnalyzer
	// 	Map<String,Analyzer> analyzerPerField = new HashMap<>();
	// 	analyzerPerField.put("General", new KeywordAnalyzer());
	// 	analyzerPerField.put("Artist", new SimpleAnalyzer());
	// 	analyzerPerField.put("Song_Link", new KeywordAnalyzer());
	// 	analyzerPerField.put("Song", new SimpleAnalyzer());
	// 	analyzerPerField.put("Lyrics", customStandardAnalyzer);
	// 	analyzerPerField.put("Album", new SimpleAnalyzer());
	// 	analyzerPerField.put("Album_Type", new SimpleAnalyzer());
	// 	analyzerPerField.put("Year", new KeywordAnalyzer());
	// 	perFieldAnalyzer = new PerFieldAnalyzerWrapper(customStandardAnalyzer, analyzerPerField);
	// }
}