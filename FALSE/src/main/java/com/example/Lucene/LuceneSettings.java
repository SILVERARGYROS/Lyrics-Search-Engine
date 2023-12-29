package com.example.Lucene;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
// import org.apache.lucene.analysis.core.KeywordAnalyzer;
// import org.apache.lucene.analysis.core.SimpleAnalyzer;
// import org.apache.lucene.analysis.custom.CustomAnalyzer;
// import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;

// import java.util.HashMap;
// import java.util.Map;

// import org.apache.lucene.analysis.Analyzer;

public class LuceneSettings {
	public static int MAX_SEARCH = 10;
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