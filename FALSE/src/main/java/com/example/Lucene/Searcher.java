package com.example.Lucene;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

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
		indexSearcher.setSimilarity(new CTFIDFSimilarity());
		queryParser = new QueryParser("General", new StandardAnalyzer());
	}

	public Searcher(String indexDirectoryPath, String fieldName) throws IOException {
		Path indexPath = Paths.get(indexDirectoryPath);
		indexDirectory = FSDirectory.open(indexPath);
		indexReader = DirectoryReader.open(indexDirectory);	
		indexSearcher = new IndexSearcher(indexReader);
		indexSearcher.setSimilarity(new CTFIDFSimilarity());
		queryParser = new QueryParser(fieldName, new StandardAnalyzer());
	}

	public Query constructSimpleQuery(String searchQuery) throws ParseException{
		return queryParser.parse(searchQuery);
	}

	public TopDocs search(String searchQuery) throws IOException, ParseException {
		query = queryParser.parse(searchQuery);
		System.out.println("query: " + query.toString());
		return indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
	}

	public TopDocs search(Query searchQuery) throws IOException, ParseException {
		query = searchQuery;
		System.out.println("query: " + query.toString());
		return indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
	}

	public Document[] getDocuments(TopDocs topDocs) throws CorruptIndexException, IOException {
		ArrayList<Document> documents = new ArrayList<>();
		for(ScoreDoc scoreDoc: topDocs.scoreDocs){
			documents.add(getDocument(scoreDoc));
		}
		return (Document[])documents.toArray();
	}

	public Document getDocument(ScoreDoc scoreDoc) throws CorruptIndexException, IOException {
		return indexSearcher.doc(scoreDoc.doc);
	}

	public void close() throws IOException {
		indexReader.close();
		indexDirectory.close();
	}
}