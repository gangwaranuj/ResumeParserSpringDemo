package com.thinksys.parser.demo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class Test {

	private static Logger logger = Logger.getLogger(Test.class);

	public static void main(String[] args) throws IOException, ParseException {

		try {

			/*********** indexing *************/

			Indexer indexer = new Indexer();
			indexer.createIndex();
			indexer.indexWriter.close();
			logger.info("Indexing Completed.");

			/************** indexing completed ****************/


			/*************** input searchkeyword *********************/

			//InputStreamReader r = new InputStreamReader(System.in);
			//BufferedReader br = new BufferedReader(r);
			//logger.info("Enter your searchkeyword");
			//String skill = (br.readLine()).toLowerCase();

			/************************/


			/*********************load property file******************************/			
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			InputStream is = classloader.getResourceAsStream("constants.properties");
			Properties prop=new Properties();
			prop.load(is);
			String skill=prop.getProperty("search.keyword").toLowerCase();;

			/***************************************************/			
			
			

			/*************** searcher *********************/

			TopDocs hits;
			Searcher searcher = new Searcher();
			hits = searcher.search(Paths.get(prop.getProperty("lucene.index.dir")), skill);


			if(hits.totalHits>0){
				//String directoryname=Constants.DEVELOPER_FOLDER_DIRECTORY+skill;
				String directoryname=prop.getProperty("output.dir")+skill;
				createNewDirectory(directoryname);
				for (ScoreDoc scoreDoc : hits.scoreDocs) {

					Document document = searcher.getDocument(scoreDoc);
					File sourceFile = new File(prop.getProperty("input.dir")+ document.get("file") );
					File destinationDir = new File(directoryname+"/"+sourceFile.getName());
					FileUtils.copyFile(sourceFile, destinationDir);
					logger.info("File name: " + document.get("file") + "," + "  File Location :: " + document.get("path"));
					
					
				}
			}
			/**********************************************/
		
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error: " + e);
		}
	}

	public static void createNewDirectory(String directoryname){

		File file = new File(directoryname);
		if (!file.exists()) {
			if (file.mkdir()) {
				logger.info("Directory is created! "+ directoryname);
			} else {
				logger.info("Failed to create directory!");
			}
		}
	}
}
