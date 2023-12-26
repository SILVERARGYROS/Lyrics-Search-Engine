package com.example.Lucene;

import java.io.File;
import java.io.FileFilter;
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
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
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

	public void commit() throws CorruptIndexException, IOException {
		writer.commit();
	}

	public int createAlbumIndex(String dataDirPath, FileFilter filter) throws IOException {

		// Committing an empty document to initialize the index 
		writer.addDocument(new Document());
		writer.commit();

		// get all files in the data directory
		File[] files = new File(dataDirPath).listFiles();
		for (File file : files) {
			if (!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)) {
				indexFile(file, "albums");
			}
		}
		int albumNumDocs = writer.numRamDocs();
		writer.commit();

		return albumNumDocs;

	}
	
	public int[] createSongIndex(String songDataDirPath, String lyricsDataDirPath, FileFilter filter) throws IOException {

		int songNumDocs = 0;
		int lyricsNumDocs = 0;

		// Committing an empty document to initialize the index 
		writer.addDocument(new Document());
		writer.commit();

		// Index all song files
		File[] songFiles = new File(songDataDirPath).listFiles();
		for (File file : songFiles) {
			if (!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)) {
				songNumDocs = indexFile(file, "songs");
			}
		}
		writer.commit();


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
				lyricsNumDocs = indexFile(file, "lyrics");
			}
		}
		writer.commit();

		int numDocs[] = {songNumDocs, lyricsNumDocs};
		return numDocs;
	}

	public int indexFile(File file, String datatype) throws IOException {
		System.out.println("Indexing " + file.getCanonicalPath());

		int numDocs = 0;

		if(datatype.equalsIgnoreCase("albums")){
			ArrayList<Document> receivedList = getAlbumDocument(file);
			
			// Checking if album already exists

			// 1. Create a Searcher
			// System.out.println("DEBUUUUUG Index directory == " + indexDirectoryPath);
			Searcher searcher = new Searcher(this.indexDirectoryPath);

			for(Document currentDocument: receivedList){

				try{
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
				}catch(ParseException e){
					System.out.println("Parse incorrect. Please raise error flag/UI. EXCEPTION: " + e);
				}
			}
		}
		else if (datatype.equalsIgnoreCase("songs")){
			ArrayList<Document> receivedList = getSongDocument(file);
			
			// Checking if song already exists

			// 1. Create a Searcher
			// System.out.println("DEBUUUUUG Index directory == " + indexDirectoryPath);
			Searcher searcher = new Searcher(this.indexDirectoryPath);

			for(Document currentDocument: receivedList){

				// DEBUG
				// System.out.println("DEBUG currentDocument song_name == " + currentDocument.get("Song"));
				// System.out.println("DEBUG currentDocument Artist == " + currentDocument.get("Artist"));
				// System.out.println("DEBUG currentDocument song_href == " + currentDocument.get("Song_Link"));

				try{
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
						correctedDocument.add(matchingDocument.getField("General"));
						correctedDocument.add(currentDocument.getField("Song_ID"));
						correctedDocument.add(currentDocument.getField("Artist"));
						correctedDocument.add(currentDocument.getField("Song_Link"));
						correctedDocument.add(matchingDocument.getField("Song"));
						correctedDocument.add(matchingDocument.getField("Lyrics"));

						writer.addDocument(correctedDocument);
					}
					// writer.commit(); // commit method too expensive to be run in each iteration
					numDocs++;
				}catch(ParseException e){
					System.out.println("Parse incorrect. Please raise error flag/UI. EXCEPTION: " + e);
				}
			}
		}
		else if (datatype.equalsIgnoreCase("lyrics")){
			ArrayList<Document> receivedList = getLyricsDocument(file);
			
			// Checking if song already exists

			// 1. Create a Searcher
			// System.out.println("DEBUUUUUG Index directory == " + indexDirectoryPath);
			Searcher searcher = new Searcher(this.indexDirectoryPath);


			for(Document currentDocument: receivedList){

				// DEBUG
				// System.out.println("DEBUG currentDocument song_name == " + currentDocument.get("Song"));
				// System.out.println("DEBUG currentDocument Artist == " + currentDocument.get("Artist"));
				// System.out.println("DEBUG currentDocument song_href == " + currentDocument.get("Song_Link"));

				try{

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
						correctedDocument.add(matchingDocument.getField("Song_ID"));
						correctedDocument.add(matchingDocument.getField("Artist"));
						correctedDocument.add(matchingDocument.getField("Song_Link"));
						correctedDocument.add(currentDocument.getField("Song"));
						correctedDocument.add(currentDocument.getField("Lyrics"));

						writer.addDocument(correctedDocument);
					}
					// writer.commit(); // commit method too expensive to be run in each iteration
					numDocs++;
				}catch(ParseException e){
					System.out.println("Parse incorrect. Please raise error flag/UI. EXCEPTION: " + e);
				}
			}
		}
		else{
			System.out.println("File type not recognized. Please code more carefully.");
		}
		System.out.println("DEBUG DATATYPE = " + datatype);
		return numDocs;
	}

	public ArrayList<Document> getAlbumDocument(File file) {
		ArrayList<Document> docList = new ArrayList<>();
		try {
			// index file contents
			FileReader fr = new FileReader(file);
			CSVReader reader = new CSVReader(fr);
			String[] currentRecord;
			while((currentRecord = reader.readNext()) != null){ // id,singer_name,name,type,year
				Document document = null;
				// index general field
				TextField generalField = new TextField("General", constructGeneralFieldString(Arrays.asList(currentRecord)).strip().toLowerCase(), Store.YES);
				// index album_id
				TextField albumIdField = new TextField("Album_ID", currentRecord[1].toLowerCase(), Store.YES);
				// index singer_name
				TextField artistNameField = new TextField("Artist", currentRecord[2].toLowerCase().replace(" lyrics", ""), Store.YES);
				// index Artist
				TextField albumNameField = new TextField("Album", currentRecord[3].toLowerCase(), Store.YES);
				// index album_type
				TextField albumTypeField = new TextField("Album_Type", currentRecord[4].toLowerCase(), Store.YES);
				// index album_year
				TextField albumYearField = new TextField("Year", currentRecord[5].toLowerCase(), Store.YES);

				// document.add(contentField);
				document = new Document();
				document.add(generalField);
				document.add(albumIdField);
				document.add(artistNameField);
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

	public ArrayList<Document> getSongDocument(File file) {
		ArrayList<Document> docList = new ArrayList<>();
		try {
			Document document = null;
			// index file contents
			FileReader fr = new FileReader(file);
			CSVReader reader = new CSVReader(fr);
			String[] currentRecord;
			while((currentRecord = reader.readNext()) != null){ // Song_ID,singer_name (Artist),song_name,song_href
				// index general field
				TextField generalField = new TextField("General", constructGeneralFieldString(Arrays.asList(currentRecord)).strip().toLowerCase(), Store.YES);
				// index Song_ID
				TextField songIdField = new TextField("Song_ID", currentRecord[1].toLowerCase(), Store.YES);
				// index Artist
				TextField artistNameField = new TextField("Artist", currentRecord[2].toLowerCase().replace(" lyrics", ""), Store.YES);
				// index singer_name
				TextField songHrefField = new TextField("Song_Link", currentRecord[4].toLowerCase(), Store.YES);
				// index Artist
				TextField songNameField = new TextField("Song", currentRecord[3].toLowerCase(), Store.YES);
				// index lyrics
				TextField lyricsField = new TextField("Lyrics", "not_defined", Store.YES);
				
				// document.add(contentField);
				document = new Document();
				document.add(generalField);
				document.add(songIdField);
				document.add(artistNameField);
				document.add(songHrefField);
				document.add(songNameField);
				document.add(lyricsField);

				System.out.println("LOOP DEBUGU documentfield Song == " + document.get("Song"));
				System.out.println("LOOP DEBUGU documentfield Artist == " + document.get("Artist"));
				System.out.println("LOOP DEBUGU documentfield Song_Link == " + document.get("Song_Link"));

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

	public ArrayList<Document> getLyricsDocument(File file) {
		ArrayList<Document> docList = new ArrayList<>();
		try {
			Document document = null;
			// index file contents
			FileReader fr = new FileReader(file);
			CSVReader reader = new CSVReader(fr);
			String[] currentRecord;

			while((currentRecord = reader.readNext()) != null){ // link (href),artist (Artist),song_name,lyrics
				// index general field
				TextField generalField = new TextField("General", constructGeneralFieldString(Arrays.asList(currentRecord)).strip().toLowerCase(), Store.YES);
				// index Song_ID
				TextField songIdField = new TextField("Song_ID", "not_defined", Store.YES);
				// index Artist
				TextField albumNameField = new TextField("Artist", currentRecord[2].toLowerCase().replace(" lyrics", ""), Store.YES);
				// index singer_name
				TextField songHrefField = new TextField("Song_Link", currentRecord[1].toLowerCase(), Store.YES);
				// index Artist
				TextField songNameField = new TextField("Song", currentRecord[3].toLowerCase(), Store.YES);
				// index lyrics
				TextField lyricsField = new TextField("Lyrics", currentRecord[4].toLowerCase(), Store.YES);

				document = new Document();
				document.add(generalField);
				document.add(songIdField);
				document.add(albumNameField);
				document.add(songHrefField);
				document.add(songNameField);
				document.add(lyricsField);

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

	public void addAlbum(ArrayList<String> fieldList){
		try{
			// index General
			TextField generalField = new TextField("General", constructGeneralFieldString(fieldList).strip().toLowerCase(), Store.YES);
			// index album_id
			TextField albumIdField = new TextField("Album_ID", fieldList.get(0).toLowerCase(), Store.YES);
			// index singer_name
			TextField artistNameField = new TextField("Artist", fieldList.get(1).toLowerCase(), Store.YES);
			// index Artist
			TextField albumNameField = new TextField("Album", fieldList.get(2).toLowerCase(), Store.YES);
			// index album_type
			TextField albumTypeField = new TextField("Album_Type", fieldList.get(3).toLowerCase(), Store.YES);
			// index album_year
			TextField albumYearField = new TextField("Year", fieldList.get(4).toLowerCase(), Store.YES);

			// document.add(contentField);
			Document document = new Document();
			document.add(generalField);
			document.add(albumIdField);
			document.add(artistNameField);
			document.add(albumNameField);
			document.add(albumTypeField);
			document.add(albumYearField);

			writer.addDocument(document);
		}
		catch(IOException e){
			System.out.println("Document could not be added. Exception: " + e);
		}
	}

	public void addSong(ArrayList<String> fieldList){
		try{
			// index General
			TextField generalField = new TextField("General",constructGeneralFieldString(fieldList).strip().toLowerCase(), Store.YES);
			// index Song_ID
			TextField songIdField = new TextField("Song_ID", fieldList.get(0), Store.YES);
			// index Artist
			TextField albumNameField = new TextField("Artist", fieldList.get(1).toLowerCase().replace(" lyrics", ""), Store.YES);
			// index singer_name
			TextField songHrefField = new TextField("Song_Link", fieldList.get(2).toLowerCase(), Store.YES);
			// index Artist
			TextField songNameField = new TextField("Song", fieldList.get(3).toLowerCase(), Store.YES);
			// index lyrics
			TextField lyricsField = new TextField("Lyrics", fieldList.get(4).toLowerCase(), Store.YES);
	
			Document document = new Document();
			document.add(generalField);
			document.add(songIdField);
			document.add(albumNameField);
			document.add(songHrefField);
			document.add(songNameField);
			document.add(lyricsField);
	
			writer.addDocument(document);
		}
		catch(IOException e){
			System.out.println("Document could not be added. Exception: " + e);
		}
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
}