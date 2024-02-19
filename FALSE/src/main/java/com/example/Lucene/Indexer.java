package com.example.Lucene;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.example.App;
import com.opencsv.CSVReader;

public class Indexer {
	private IndexWriter writer;
	private String indexDirectoryPath;
	FieldType fieldConf;

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
		config.setSimilarity(LuceneSettings.getSIMILARITY_METHOD());
		writer = new IndexWriter(indexDirectory, config);

		// https://stackoverflow.com/questions/35809035/how-to-get-positions-from-a-document-term-vector-in-lucene
		fieldConf = new FieldType();
		fieldConf.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
		fieldConf.setStoreTermVectors(true);
		fieldConf.setStoreTermVectorOffsets(true);
		fieldConf.setStoreTermVectorPayloads(true);
		fieldConf.setStoreTermVectorPositions(true);
		fieldConf.setTokenized(true);
	}

	public void close() throws CorruptIndexException, IOException {
		writer.close();
	}

	public void commit() throws CorruptIndexException, IOException {
		writer.commit();
	}

	public int createAlbumIndex(String dataDirPath, FileFilter filter) throws IOException, ParseException {
		// If files already exist, index propably already created.
		// So besides .lock files, there should be no other files.
		File[] fileList = new File(indexDirectoryPath).listFiles();
		if(fileList.length>1){
			return 0;
		}

		// Committing an empty document to initialize the index 
		writer.addDocument(new Document());
		writer.commit();

		int albumNumDocs = 0;
		// // get all files in the data directory
		// File[] files = new File(dataDirPath).listFiles();
		// for (File file : files) {
		// 	if (!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)) {
		// 		albumNumDocs = indexFile(file, "albums", true);
		// 	}
		// }
		// writer.commit();

		return albumNumDocs;
	}
	
	public int[] createSongIndex(String songDataDirPath, String lyricsDataDirPath, FileFilter filter) throws IOException, ParseException {
		// If files already exist, index propably already created.
		// So besides .lock files, there should be no other files.
		File[] files = new File(indexDirectoryPath).listFiles();
		if(files.length>1){
			int[] x = {0,0};
			return x;
		}

		int songNumDocs = 0;
		int lyricsNumDocs = 0;

		// Committing an empty document to initialize the index 
		writer.addDocument(new Document());
		writer.commit();

		// // Index all song files
		// File[] songFiles = new File(songDataDirPath).listFiles();
		// for (File file : songFiles) {
		// 	if (!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)) {
		// 		songNumDocs = indexFile(file, "songs", true);
		// 	}
		// }
		// writer.commit();


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


		// // Index all lyrics files
		// System.out.println(new File(lyricsDataDirPath).getCanonicalPath());
		// File[] lyricFiles = new File(lyricsDataDirPath).listFiles();
		// for (File file : lyricFiles) {
		// 	if (!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)) {
		// 		lyricsNumDocs = indexFile(file, "lyrics", true);
		// 	}
		// }
		// writer.commit();

		int numDocs[] = {songNumDocs, lyricsNumDocs};
		return numDocs;
	}

	public int indexFile(File file, String datatype, boolean ignoreFirstLine) throws IOException, ParseException {
		System.out.println("Indexing " + file.getCanonicalPath());

		int numDocs = 0;

		if(datatype.equalsIgnoreCase("albums")){
			ArrayList<Document> receivedList = getAlbumDocuments(file, ignoreFirstLine);
			
			// Checking if album already exists

			// 1. Create a Searcher
			// System.out.println("DEBUUUUUG Index directory == " + indexDirectoryPath);
			Searcher searcher = new Searcher(this.indexDirectoryPath);

			for(Document currentDocument: receivedList){
				// 2. Create TermQueries for every common field
				Query artistNameQuery = new QueryParser("Artist", new StandardAnalyzer()).parse("\"" + currentDocument.get("Artist") + "\"");
				Query albumNameQuery = new QueryParser("Album", new StandardAnalyzer()).parse("\"" + currentDocument.get("Album") + "\"");
				Query albumTypeQuery = new QueryParser("Album_Type", new StandardAnalyzer()).parse("\"" + currentDocument.get("Album_Type") + "\"");
				Query albumYearQuery = new QueryParser("Year", new StandardAnalyzer()).parse("\"" + currentDocument.get("Year") + "\"");
				
				// 3. Build booleanQuery
				BooleanQuery booleanQuery = new BooleanQuery.Builder()
				.add(artistNameQuery, Occur.MUST)
				.add(albumNameQuery, Occur.MUST)
				.add(albumTypeQuery, Occur.MUST)
				.add(albumYearQuery, Occur.MUST)
				.build();
					
				// 4. Check if document already exists and act accordingly 
				TopDocs results = searcher.search(booleanQuery);					
				System.out.println("TotalHits == " + results.totalHits.value);
				if (results.scoreDocs.length == 0){	// no match found
					writer.addDocument(currentDocument);
					// writer.commit(); // commit method too expensive to be run in each iteration
					numDocs++;
				}
				else{ // found matching document(s)
					// If there is a perfect match then  
					// there is no need to add the document again.
					continue;
				}
			}
			searcher.close();
		}
		else if (datatype.equalsIgnoreCase("songs")){
			ArrayList<Document> receivedList = getSongDocuments(file);
			
			// Checking if song already exists

			// 1. Create a Searcher
			// System.out.println("DEBUUUUUG Index directory == " + indexDirectoryPath);
			Searcher searcher = new Searcher(this.indexDirectoryPath);

			for(Document currentDocument: receivedList){

				// DEBUG
				// System.out.println("DEBUG currentDocument song_name == " + currentDocument.get("Song"));
				// System.out.println("DEBUG currentDocument Artist == " + currentDocument.get("Artist"));
				// System.out.println("DEBUG currentDocument song_href == " + currentDocument.get("Song_Link"));

				// 2. Create TermQueries for every common field
				Query songNameQuery = new QueryParser("Song", new StandardAnalyzer()).parse("\"" + currentDocument.get("Song") + "\"");
				Query albumNameQuery = new QueryParser("Artist", new StandardAnalyzer()).parse("\"" + currentDocument.get("Artist") + "\"");
	
				// DEBUG
				// System.out.println("DEBUG songNameQuery song_name == " + songNameQuery);
				// System.out.println("DEBUG albumNameQuery Artist == " + albumNameQuery);


				// 3. Build booleanQuery
				BooleanQuery booleanQuery = new BooleanQuery.Builder()
				.add(songNameQuery, Occur.MUST)
				.add(albumNameQuery, Occur.MUST)
				.build();
					
				// 4. Check if document already exists and act accordingly 
				TopDocs results = searcher.search(booleanQuery);					
				System.out.println("TotalHits == " + results.totalHits.value);
				if (results.scoreDocs.length == 0){	// no match found
					writer.addDocument(currentDocument);
				}
				else{ // found matching document(s)
					System.out.println("DUPLICATE DEBUG: DUPLICATE DOCUMENT FOUND");
	
					// Get first matching document
					System.out.println("DEBUG TFIDF SCORE == " + results.scoreDocs[0].score);
					Document matchingDocument = searcher.getDocument(results.scoreDocs[0]);
	
					// Delete old document(s)
					writer.deleteDocuments(booleanQuery);
					
					// Construct and add new (/corrected) document 
					Document correctedDocument = new Document();
					correctedDocument.add(matchingDocument.getField("General"));
					correctedDocument.add(currentDocument.getField("Artist"));
					correctedDocument.add(currentDocument.getField("Song_Link"));
					correctedDocument.add(matchingDocument.getField("Song"));
					correctedDocument.add(matchingDocument.getField("Lyrics"));

					writer.addDocument(correctedDocument);
				}
				// writer.commit(); // commit method too expensive to be run in each iteration
				numDocs++;
			}
			searcher.close();
		}
		else if (datatype.equalsIgnoreCase("lyrics")){
			ArrayList<Document> receivedList = getLyricsDocuments(file);
			
			// Checking if song already exists

			// 1. Create a Searcher
			// System.out.println("DEBUUUUUG Index directory == " + indexDirectoryPath);
			Searcher searcher = new Searcher(this.indexDirectoryPath);


			for(Document currentDocument: receivedList){

				// DEBUG
				// System.out.println("DEBUG currentDocument song_name == " + currentDocument.get("Song"));
				// System.out.println("DEBUG currentDocument Artist == " + currentDocument.get("Artist"));
				// System.out.println("DEBUG currentDocument song_href == " + currentDocument.get("Song_Link"));

				// 2. Create TermQueries for every common field
				Query songNameQuery = new QueryParser("Song", new StandardAnalyzer()).parse("\"" + currentDocument.get("Song") + "\"");
				Query albumNameQuery = new QueryParser("Artist", new StandardAnalyzer()).parse("\"" + currentDocument.get("Artist") + "\"");
	
				// DEBUG
				// System.out.println("DEBUG songNameQuery song_name == " + songNameQuery);
				// System.out.println("DEBUG albumNameQuery Artist == " + albumNameQuery);


				// 3. Build booleanQuery
				BooleanQuery booleanQuery = new BooleanQuery.Builder()
				.add(songNameQuery, Occur.MUST)
				.add(albumNameQuery, Occur.MUST)
				.build();
					
				// 4. Check if document already exists and act accordingly 
				TopDocs results = searcher.search(booleanQuery);					
				System.out.println("TotalHits == " + results.totalHits.value);
				if (results.scoreDocs.length == 0){	// no match found
					writer.addDocument(currentDocument);
				}
				else{ // found matching document(s)
					System.out.println("DUPLICATE DEBUG: DUPLICATE DOCUMENT FOUND");
	
					// Get first matching document
					Document matchingDocument = searcher.getDocument(results.scoreDocs[0]);
	
					// Delete old document(s)
					writer.deleteDocuments(booleanQuery);
					
					// Construct and add new (/corrected) document 
					Document correctedDocument = new Document();
					correctedDocument.add(currentDocument.getField("General"));
					correctedDocument.add(matchingDocument.getField("Artist"));
					correctedDocument.add(matchingDocument.getField("Song_Link"));
					correctedDocument.add(currentDocument.getField("Song"));
					correctedDocument.add(currentDocument.getField("Lyrics"));

					writer.addDocument(correctedDocument);
				}
				// writer.commit(); // commit method too expensive to be run in each iteration
				numDocs++;
			}
			searcher.close();
		}
		else{
			System.out.println("File type not recognized. Please code more carefully.");
		}
		System.out.println("DEBUG DATATYPE = " + datatype);
		return numDocs;
	}

	public ArrayList<Document> getAlbumDocuments(File file, boolean ignoreFirstLine) throws FileNotFoundException, IOException {
		ArrayList<Document> docList = new ArrayList<>();
		// index file contents
		FileReader fr = new FileReader(file);
		CSVReader reader = new CSVReader(fr);
		String[] currentRecord;
		while((currentRecord = reader.readNext()) != null){ // id,singer_name,name,type,year
			if(ignoreFirstLine){
				ignoreFirstLine = false;
				continue;
			}

			String[] currentRecordFields = {currentRecord[2], currentRecord[3], currentRecord[4], currentRecord[5]};
			Document document = createAlbumDocument(currentRecordFields);

			docList.add(document);
		}
		reader.close();
		fr.close();
		return docList;
	}

	public ArrayList<Document> getSongDocuments(File file) throws FileNotFoundException, IOException {
		ArrayList<Document> docList = new ArrayList<>();
		Document document = null;
		// index file contents
		FileReader fr = new FileReader(file);
		CSVReader reader = new CSVReader(fr);
		String[] currentRecord;
		while((currentRecord = reader.readNext()) != null){ // Song_ID,singer_name (Artist),song_name,song_href
			
			// Mapping the fields to create document
			String[] currentRecordFields = {currentRecord[2], currentRecord[4], currentRecord[3], "not_defined"};
			document = createSongDocument(currentRecordFields);

			System.out.println("LOOP DEBUGU documentfield Song == " + document.get("Song"));
			System.out.println("LOOP DEBUGU documentfield Artist == " + document.get("Artist"));
			System.out.println("LOOP DEBUGU documentfield Song_Link == " + document.get("Song_Link"));

			docList.add(document);
		}
		reader.close();
		fr.close();
		return docList;
	}

	public ArrayList<Document> getLyricsDocuments(File file) {
		ArrayList<Document> docList = new ArrayList<>();
		try {
			Document document = null;
			// index file contents
			FileReader fr = new FileReader(file);
			CSVReader reader = new CSVReader(fr);
			String[] currentRecord;

			while((currentRecord = reader.readNext()) != null){ // link (href),artist (Artist),song_name,lyrics

				// Mapping the fields to create document
				String[] currentRecordFields = {currentRecord[2], currentRecord[1], currentRecord[3], currentRecord[4]};
				document = createSongDocument(currentRecordFields);

				System.out.println("LOOP DEBUGU documentfield song_name == " + document.get("Song"));
				System.out.println("LOOP DEBUGU documentfield Artist == " + document.get("Artist"));
				System.out.println("LOOP DEBUGU documentfield song_href == " + document.get("Song_Link"));

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

	public Document createAlbumDocument(String[] fields){
		// index general field
		TextField generalField = new TextField("General", constructGeneralFieldString(Arrays.asList(fields)).strip().toLowerCase(), Field.Store.YES);
		// index artist name
		TextField artistNameField = new TextField("Artist", fields[0].toLowerCase().replace(" lyrics", ""), Field.Store.YES);
		// index artist name
		TextField albumNameField = new TextField("Album", fields[1].toLowerCase(), Field.Store.YES);
		// index album_type
		TextField albumTypeField = new TextField("Album_Type", fields[2].toLowerCase(), Field.Store.YES);
		// index album_year
		TextField albumYearField = new TextField("Year", fields[3].toLowerCase(), Field.Store.YES);

		// document.add(contentField);
		Document document = new Document();
		document.add(generalField);
		document.add(artistNameField);
		document.add(albumNameField);
		document.add(albumTypeField);
		document.add(albumYearField);

		return document;
	}

	public Document createSongDocument(String[] fields){
		// index general field
		TextField generalField = new TextField("General", constructGeneralFieldString(Arrays.asList(fields)).strip().toLowerCase(), Field.Store.YES);
		// index artist name
		TextField artistName = new TextField("Artist", fields[0].toLowerCase().replace(" lyrics", ""), Field.Store.YES);
		// index link
		fieldConf.setTokenized(false);
		TextField songHrefField = new TextField("Song_Link", fields[1].toLowerCase(), Field.Store.YES);
		fieldConf.setTokenized(true);
		// index Song Name
		TextField songNameField = new TextField("Song", fields[2].toLowerCase(), Field.Store.YES);
		// index lyrics
		TextField lyricsField = new TextField("Lyrics", constructLyricsFieldString(fields[3].toLowerCase()), Field.Store.YES);

		Document document = new Document();
		document.add(generalField);
		document.add(artistName);
		document.add(songHrefField);
		document.add(songNameField);
		document.add(lyricsField);

		return document;
	}

	public void addAlbum(ArrayList<String> fieldList) throws IOException{
		// Convert List to array
		String[] fields = new String[fieldList.size()];
		int i = 0;
		for(String field: fieldList){
			fields[i++] = field;
		}

		Document document = createAlbumDocument(fields);
		writer.addDocument(document);
	}

	public void addSong(ArrayList<String> fieldList) throws IOException{
		// Convert List to array
		String[] fields = new String[fieldList.size()];
		int i = 0;
		for(String field: fieldList){
			fields[i++] = field;
		}

		Document document = createSongDocument(fields);
		writer.addDocument(document);
	}

	public void removeDocument(ScoreDoc scoreDoc){
		Searcher searcher;
		try {
			searcher = new Searcher(this.indexDirectoryPath);
			Document document = searcher.getDocument(scoreDoc);

			Query query = new QueryParser("General", new StandardAnalyzer()).parse("\"" + document.get("General") + "\"");
			BooleanQuery booleanQuery = new BooleanQuery.Builder()
			.add(query, Occur.MUST)
			.build();
			searcher.close();
			writer.deleteDocuments(booleanQuery);
		} catch (IOException e) {
			System.out.println("Error getting requested document. Error: " + e);
		} catch (ParseException e) {
			System.out.println("Error finding requested document. Error: " + e);
		}
	}

	public String constructGeneralFieldString(Collection<String> collection){
		String generalString = "";
		for(String field: collection){
			generalString += field + "_";
		}
		return generalString;
	}

	public String constructLyricsFieldString(String lyrics){
		lyrics = lyricsFilter(lyrics, "pre chorus");
		lyrics = lyricsFilter(lyrics, "pre-chorus");
		lyrics = lyricsFilter(lyrics, "chorus");
		lyrics = lyricsFilter(lyrics, "post chorus");
		lyrics = lyricsFilter(lyrics, "post-chorus");
		return lyrics;
	}

	public String lyricsFilter(String lyrics, String pattern){
		// Debug 
		System.out.println(App.BLUE + "Lyrics: \n" + lyrics + App.RESET);
		System.out.println(App.BLUE + "pattern == " + pattern + App.RESET);
		
		// find text block
		String block;
		try{
			block = lyrics.split("\\[" + pattern)[1].replace(":", "").replace("\\]", "").split("\n\n")[0].strip() + "\n\n";
		}
		catch(IndexOutOfBoundsException e){
			System.out.println("tag not found, skipping...");
			return lyrics;
		}
		lyrics = lyrics.replace(block, "");

		// find occurances
		String[] occurances = lyrics.split("\\[");
		for(String occurance: occurances){
			String tag = occurance.split("\\]")[0];
			System.out.println(App.YELLOW + "DEBUG tag == " + tag + App.RESET);
			
			if (tag.contains(pattern) || tag.equalsIgnoreCase(pattern + ":") || tag.equalsIgnoreCase("repeat " + pattern) || tag.equalsIgnoreCase("repeat-" + pattern)){
				lyrics = lyrics.replace("[" + tag + "]", block);
			}	
			else if(tag.contains(pattern) && (tag.contains("x")))
			{
				int iterations = Integer.parseInt(tag.replace(pattern, "").replace("x", "").replace("(", "").replace(")", "").replace(" ", ""));
				String megaBlock = "";
				for(int i = 0; i < iterations; i++){
					megaBlock += block;
				}
				lyrics = lyrics.replace("[" + tag + "]", megaBlock);
			}
		}

		// Debug 
		System.out.println(App.RED + "Lyrics: \n" + lyrics + App.RESET);
		return lyrics;
	}
}