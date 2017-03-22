package com.luceneserver.core;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.standard.ClassicAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.suggest.DocumentDictionary;
import org.apache.lucene.search.suggest.Lookup;
import org.apache.lucene.search.suggest.analyzing.AnalyzingInfixSuggester;
import org.apache.lucene.search.suggest.analyzing.FreeTextSuggester;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.QueryBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Indexer {

	private static final Directory lIndexDirectory = new RAMDirectory();

	private static void buildIndex() throws Exception {
		IndexWriterConfig lIndexWriterConfig = new IndexWriterConfig();
		IndexWriter lIndexWriter = new IndexWriter(lIndexDirectory, lIndexWriterConfig);
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(new FileReader("data/books.json"));

		JSONArray bookObjectArray = (JSONArray) obj;

		for (int i = 0; i < bookObjectArray.size(); i++) 
		{
			JSONObject lBookObject = (JSONObject) bookObjectArray.get(i);
			String lstrTitle = lBookObject.get("Title").toString();
			String lstrCover = lBookObject.get("Cover").toString();
			String lstrAuthor = lBookObject.get("Author").toString();
			String lstrPublisher = lBookObject.get("Publisher").toString();
			addBookToIndex(lIndexWriter, lstrTitle, lstrCover, lstrAuthor, lstrPublisher);
		}

		lIndexWriter.commit();
		lIndexWriter.close();
		
	}

	private static void addBookToIndex(IndexWriter pIndexWriter, String pTitle, String pCover, String pAuthor,
			String pPublisher) throws IOException {
		Document lDocument = new Document();
		lDocument.add(new TextField("title", pTitle, Field.Store.YES));
		lDocument.add(new TextField("cover", pCover, Field.Store.YES));
		lDocument.add(new TextField("author", pAuthor, Field.Store.YES));
		lDocument.add(new TextField("publisher", pPublisher, Field.Store.YES));

		pIndexWriter.addDocument(lDocument);
	}

	private static void getBooksFromIndex() throws IOException, ParseException 
	{
		IndexReader lReader = DirectoryReader.open(lIndexDirectory);
		IndexSearcher lSearcher = new IndexSearcher(lReader);

		QueryBuilder lQueryBuilder = new QueryBuilder(new ClassicAnalyzer());

		Query lBooksContainingName = lQueryBuilder.createBooleanQuery("author", "Alb");

		TopDocs lHits = lSearcher.search(lBooksContainingName, 10000);
		ScoreDoc[] lDocs = lHits.scoreDocs;
		System.out.println("Found " + lDocs.length + " books.");
		for(int i = 0 ; i < lDocs.length; i++)
		{
			System.out.println(lSearcher.doc(lDocs[i].doc).get("author"));
		}
	}

	private static void autoSuggest() throws Exception {
		IndexReader lReader = DirectoryReader.open(lIndexDirectory);
		Dictionary lDictionary = new DocumentDictionary(lReader, "title", "title_weight", null, "publisher");
		AnalyzingInfixSuggester suggestor = new AnalyzingInfixSuggester(new RAMDirectory(), new StandardAnalyzer());
		
		suggestor.build(lDictionary);
		
		HashSet<BytesRef> lSet = new HashSet<BytesRef>();
		lSet.add(new BytesRef("Berkley Trade".getBytes()));
		
		List<Lookup.LookupResult> lookupResultList = suggestor.lookup("Under", lSet, false, 10000);
		
		System.out.println("Suggested List : "+lookupResultList.size() + " suggestion");
		for (Lookup.LookupResult lookupResult : lookupResultList) 
		{
			System.out.println(lookupResult.key + ": " + lookupResult.value);
		}
		suggestor.close();
	}

	public static void main(String args[]) throws Exception {
		buildIndex();
		getBooksFromIndex();
		//autoSuggest();
	}

}

class SuggestionAnalyzer extends Analyzer {

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		
		Tokenizer source = new WhitespaceTokenizer();
		
		TokenStream result = new LowerCaseFilter(source);
		result = new NGramTokenFilter(result, 1, 29);
		return new TokenStreamComponents(source, result);
	}
}

class UserSearchAnalyzer extends Analyzer {

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		Tokenizer lSource = new KeywordTokenizer();
		
		TokenStream lResult = new LowerCaseFilter(lSource);
		
		return new TokenStreamComponents(lSource, lResult);
	}
}

class QAnalyzer extends Analyzer {

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		
		Tokenizer source = new WhitespaceTokenizer();
		
		TokenStream result = new LowerCaseFilter(source);
		result = new NGramTokenFilter(result, 1, 1000);
		return new TokenStreamComponents(source, result);
	}
}

/**
 * In QueryBuilder, the term must/should/may Occur? simple search such as
 * finding 'The' in title is failing
 */