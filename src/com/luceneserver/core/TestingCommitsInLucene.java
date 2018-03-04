package com.luceneserver.core;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.QueryBuilder;


public class TestingCommitsInLucene {

	public static void main(String[] args) throws IOException {
		Directory lDirectory = FSDirectory.open(new File("/tmp/testindex").toPath());
		IndexWriterConfig lWriterConfig = new IndexWriterConfig(new StandardAnalyzer());
		lWriterConfig.setOpenMode(OpenMode.CREATE);
		IndexWriter lWriter = new IndexWriter(lDirectory, lWriterConfig);
		lWriter.commit();
		IndexReader lReader = DirectoryReader.open(lWriter);
		
		Document lDoc = new Document();
		lDoc.add(new StoredField("field", "abc"));
		lWriter.addDocument(lDoc);
		//IndexReader lReader = DirectoryReader.open(lWriter);
		//lWriter.commit();
		IndexSearcher lSearcher = new IndexSearcher(DirectoryReader.openIfChanged((DirectoryReader)(lReader)));
		printIndexContents(lWriter, lReader, lSearcher);
	}
	
	
	private static void printIndexContents(IndexWriter pWriter, IndexReader pReader, IndexSearcher pSearcher) throws IOException
	{
		
		ScoreDoc[] lHits = pSearcher.search(new MatchAllDocsQuery(), 10000).scoreDocs;
		for(ScoreDoc lDoc: lHits)
		{
			System.out.println(pSearcher.doc(lDoc.doc).get("field"));
		}
		
	}

}
