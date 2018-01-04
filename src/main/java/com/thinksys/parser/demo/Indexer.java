package com.thinksys.parser.demo;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class Indexer {

	final static Logger logger = Logger.getLogger(Indexer.class);
	public IndexWriter indexWriter;

	public  void createIndex() throws IOException{

		Directory indexDirectory;
		try {
			indexDirectory = FSDirectory.open(Constants.LUCENE_INDEX_DIRECTORY);
			IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
			indexWriter = new IndexWriter(indexDirectory,config);
			indexWriter.deleteAll();
			File[] docfiles = new File(Constants.LUCENE_DOCUMENT_DIRECTORY).listFiles();

			//	long startTime = System.currentTimeMillis();
			for (File file : docfiles) {

				Parser parser = new AutoDetectParser();
				BodyContentHandler handler = new BodyContentHandler();
				Metadata metadata = new Metadata();
				InputStream inputstream = new FileInputStream(file);
				ParseContext context = new ParseContext();
				parser.parse(inputstream, handler, metadata, context);
				String	fileContent=handler.toString();
				Document document = createDocument(file,metadata,fileContent);	
				indexWriter.addDocument(document);
				indexWriter.commit();
				indexWriter.deleteUnusedFiles();
				logger.info("file Indexed ::" +file.getName());
			}
			//long endTime = System.currentTimeMillis();
			//logger.info(indexWriter.maxDoc() + " File indexed, Time taken: " + (endTime - startTime) + " ms");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TikaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		indexWriter.close();
	}


	private static Document createDocument(File file,Metadata metadata,String text ) throws IOException{

		Document document = new Document();
		FieldType type = new FieldType();
		String fileName = file.getName();		
		String filepath=file.getAbsolutePath();
		type.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
		type.setStored(true);
		type.setStoreTermVectors(true);
		type.setTokenized(true);
		type.setStoreTermVectorOffsets(true);
		document.add(new Field("file", fileName, type));
		document.add(new Field("fileNameWithOutExt ", FilenameUtils.removeExtension(fileName),type));
		document.add( new Field("path", filepath, type));
		document.add( new Field("text",text, type));

		return document;
	}   


}
