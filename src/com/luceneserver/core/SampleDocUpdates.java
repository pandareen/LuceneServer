package com.luceneserver.core;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.RAMDirectory;

public class SampleDocUpdates {

	public static void main(String[] args) throws IOException {
		IndexWriter writer = new IndexWriter(new RAMDirectory(), new IndexWriterConfig());
		IndexReader reader;
		IndexSearcher searcher;
		
		
		// first document in the index
		Document initialDoc = new Document();
		// adding a text field
		initialDoc.add(new TextField("foo_text", "abc", Store.YES));
		// adding a numeric field
		initialDoc.add(new LongPoint("foo_number", 1000));
		// adding stored field to display hits
		initialDoc.add(new StoredField("foo_number", 1000));
		
		
		writer.addDocument(initialDoc);
		
		
		// second document in index which should update the first one..
		Document newDoc = new Document();
		newDoc.add(new TextField("foo_text", "def", Store.YES));
		newDoc.add(new LongPoint("foo_number", 2000));
		newDoc.add(new StoredField("foo_number", 2000));
		
		
		// update doc with foo_text:abc with the newDoc instance.
		writer.updateDocument(new Term("foo_text", "abc"), newDoc);
		
		reader = DirectoryReader.open(writer);
		searcher = new IndexSearcher(reader);
		
		ScoreDoc[] scoreDocs = searcher.search(new MatchAllDocsQuery(), 1000).scoreDocs;
		
		for (ScoreDoc scoreDoc : scoreDocs) {
			System.out.println(searcher.doc(scoreDoc.doc).get("foo_text")+"\t"+searcher.doc(scoreDoc.doc).get("foo_number"));
			//'def	2000'
		}
		

	}

}
