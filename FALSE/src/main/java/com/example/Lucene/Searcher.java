package com.example.Lucene;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.simple.SimpleQueryParser;

public class Searcher {
	IndexSearcher indexSearcher;
	Directory indexDirectory;
	IndexReader indexReader;
	QueryParser queryParser;
	Query query;

	public Searcher(String indexDirectoryPath) throws IOException {
		Path indexPath = Paths.get(indexDirectoryPath);
		indexDirectory = FSDirectory.open(indexPath);
		indexReader = DirectoryReader.open(indexDirectory);	
		indexSearcher = new IndexSearcher(indexReader);
		indexSearcher.setSimilarity(LuceneSettings.getSIMILARITY_METHOD());
		queryParser = new QueryParser("General", new StandardAnalyzer());
	}

	public Searcher(String indexDirectoryPath, String fieldName) throws IOException {
		Path indexPath = Paths.get(indexDirectoryPath);
		indexDirectory = FSDirectory.open(indexPath);
		indexReader = DirectoryReader.open(indexDirectory);	
		indexSearcher = new IndexSearcher(indexReader);
		indexSearcher.setSimilarity(LuceneSettings.getSIMILARITY_METHOD());
		queryParser = new QueryParser(fieldName, new StandardAnalyzer());
	}

	public Query constructSimpleQuery(String searchQuery) throws ParseException{
		return queryParser.parse(searchQuery);
	}

	public Query constructCombinedQuery(String[] queryFields, String[] queryValues, BooleanClause.Occur occurance) throws ParseException{
		ArrayList<Query> queryList = new ArrayList<>();
		for(int i = 0; i < queryValues.length; i++){
			System.out.println("DEBUG QUERY IS: " + queryValues[i]);
			if(queryValues[i].strip().isEmpty()){
				System.out.println("DEBUG CONTNINUE: " + queryValues[i]);
				continue;
			}
			Query currentQuery = new  SimpleQueryParser(new StandardAnalyzer(), queryFields[i]).parse(queryValues[i]);
			queryList.add(currentQuery);
		}

		// Dynamically construct the query using builder;
		BooleanQuery.Builder builder = new BooleanQuery.Builder();
		for(Query query: queryList){
			builder.add(query, occurance);
		}
		return builder.build();

	}

	public TopDocs search(String searchQuery) throws IOException, ParseException {
		query = queryParser.parse(searchQuery);
		System.out.println("query: " + query.toString());
		return indexSearcher.search(query, LuceneSettings.getMAX_SEARCH());
	}

	public TopDocs search(Query searchQuery) throws IOException, ParseException {
		query = searchQuery;
		System.out.println("query: " + query.toString());
		return indexSearcher.search(query, LuceneSettings.getMAX_SEARCH());
	}

	public Document[] getDocuments(TopDocs topDocs) throws CorruptIndexException, IOException {
		Document[] documents = new Document[topDocs.scoreDocs.length];
		int i = 0;

		for(ScoreDoc scoreDoc: topDocs.scoreDocs){
			documents[i++] = getDocument(scoreDoc);
		}

		return documents;
	}

	public Document getDocument(ScoreDoc scoreDoc) throws CorruptIndexException, IOException {
		return indexSearcher.doc(scoreDoc.doc);
	}

	public void close() throws IOException {
		indexReader.close();
		indexDirectory.close();
	}
}