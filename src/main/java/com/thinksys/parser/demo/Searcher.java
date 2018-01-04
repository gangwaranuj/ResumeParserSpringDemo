package com.thinksys.parser.demo;

import java.io.IOException;
import java.nio.file.Path;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import com.google.protobuf.TextFormat.ParseException;

public class Searcher {

	final static Logger logger = Logger.getLogger(Searcher.class);
	
	IndexSearcher indexSearcher;
	Query query;
	QueryParser queryParser;

	public Searcher() {
		super();
	}

	public TopDocs search(Path luceneIndexDirectory, String searchquery) throws IOException, ParseException, org.apache.lucene.queryparser.classic.ParseException{

		TopDocs hits =null;
		BooleanQuery booleanQuery =null ;
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Constants.LUCENE_INDEX_DIRECTORY));
		indexSearcher = new IndexSearcher(reader);
		queryParser = new QueryParser("text", new StandardAnalyzer());
		String[] skills=searchquery.split(",");
		long searchstartTime = System.currentTimeMillis();
		
		for(String skill:skills){
			
			query = queryParser.parse(skill);
			booleanQuery = new BooleanQuery.Builder().add(query, BooleanClause.Occur.MUST).build();
			hits=indexSearcher.search(booleanQuery,10);
			
		}
		long searchendTime = System.currentTimeMillis();
		logger.info("Total = "+hits.totalHits + " documents found. Time taken :" + (searchendTime - searchstartTime) + " ms");
		return hits;	
	}

	public Document getDocument(ScoreDoc scoreDoc) 
			throws CorruptIndexException, IOException{
		return indexSearcher.doc(scoreDoc.doc);	
	}

}
