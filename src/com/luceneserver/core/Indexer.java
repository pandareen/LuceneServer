package com.luceneserver.core;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.SortedNumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.ConcurrentMergeScheduler;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.MergeScheduler;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.suggest.DocumentDictionary;
import org.apache.lucene.search.suggest.Lookup;
import org.apache.lucene.search.suggest.analyzing.AnalyzingInfixSuggester;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.QueryBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.File;

import com.luceneserver.analyzers.custom.ContainsAnalyzer;
import com.luceneserver.analyzers.custom.NGramExample;
import com.luceneserver.constants.LuceneConstants;

public class Indexer implements LuceneConstants {

	private static Directory lIndexDirectory;
	private static IndexWriter sIndexWriter;
	private static DirectoryReader sDirectoryReader;
	private static IndexSearcher sIndexSearcher;
	private static boolean isIntitialized = false;
	private static Document lBookDocument = new Document();

	private static void init() throws IOException {

		if (isIntitialized == true) {
			return;
		}

		lIndexDirectory = FSDirectory.open(new File(LUCENE_INDEX_HOME + File.separator + INDEX_NAME).toPath());
		IndexWriterConfig lIndexWriterConfig = new IndexWriterConfig(new ContainsAnalyzer());

		lIndexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
		sIndexWriter = new IndexWriter(lIndexDirectory, lIndexWriterConfig);
		sDirectoryReader = DirectoryReader.open(sIndexWriter);
		sIndexWriter.commit();
		sIndexSearcher = new IndexSearcher(sDirectoryReader);
		isIntitialized = true;

	}

	private static void buildIndex() throws Exception {

		init();

		// if(sIndexSearcher.getIndexReader().numDocs() >= 1)
		// {
		// return;
		// }

		System.out.println("Building index...");

		JSONParser parser = new JSONParser();
		Object obj = parser.parse(new FileReader("data/books.json"));

		JSONArray bookObjectArray = (JSONArray) obj;

		System.out.println("Indexing Started...");

		for (int i = 0; i < bookObjectArray.size(); i++) {
			JSONObject lBookObject = (JSONObject) bookObjectArray.get(i);
			String lstrTitle = lBookObject.get("Title").toString();
			long llPages = Long.parseLong(lBookObject.get("Pages").toString());
			long llISBN10 = 1;
			long llISBN13 = 1;
			try {
				llISBN10 = Long.parseLong(lBookObject.get("ISBN10").toString());
				llISBN13 = Long.parseLong(lBookObject.get("ISBN13").toString());
			} catch (Exception e) {
			}
			String lstrCover = lBookObject.get("Cover").toString();
			String lstrAuthor = lBookObject.get("Author").toString();
			String lstrPublisher = lBookObject.get("Publisher").toString();
			lBookDocument = StaticBookDocument.getBookdocument(lstrTitle, llPages, llISBN10, llISBN13, lstrCover, lstrAuthor, lstrPublisher);
			//addBookToIndex(sIndexWriter, lstrTitle, lstrCover, lstrAuthor, lstrPublisher, llPages, llISBN10, llISBN13);
			sIndexWriter.addDocument(lBookDocument);
		}
		

		sIndexWriter.commit();

		System.out.println("Indexing Finished...");

	}

	private static void addBookToIndex(IndexWriter pIndexWriter, String pTitle, String pCover, String pAuthor,
			String pPublisher, long pPages, long pISBN10, long pISBN13) throws IOException {

		Document lDocument = new Document();
		lDocument.add(new TextField("title", pTitle, Field.Store.NO));
		lDocument.add(new TextField("cover", pCover, Field.Store.NO));
		lDocument.add(new TextField("author", pAuthor, Field.Store.NO));
		lDocument.add(new TextField("publisher", pPublisher, Field.Store.NO));
		

		lDocument.add(new StoredField("title", pTitle));
		lDocument.add(new StoredField("cover", pCover));
		lDocument.add(new StoredField("author", pAuthor));
		lDocument.add(new StoredField("publisher", pPublisher));
		
		
		lDocument.add(new SortedDocValuesField("title", new BytesRef(pTitle)));
		lDocument.add(new SortedDocValuesField("cover", new BytesRef(pCover)));
		lDocument.add(new SortedDocValuesField("author", new BytesRef(pAuthor)));
		lDocument.add(new SortedDocValuesField("publisher", new BytesRef(pPublisher)));
		
		
		lDocument.add(new LongPoint("pages", pPages));
		lDocument.add(new LongPoint("isbn10", pISBN10));
		lDocument.add(new LongPoint("isbn13", pISBN13));
		
		lDocument.add(new StoredField("pages", pPages));
		lDocument.add(new StoredField("isbn10", pISBN10));
		lDocument.add(new StoredField("isbn13", pISBN13));
		
		lDocument.add(new SortedNumericDocValuesField("pages", pPages));
		lDocument.add(new SortedNumericDocValuesField("isbn10", pISBN10));
		lDocument.add(new SortedNumericDocValuesField("isbn13", pISBN13));

		pIndexWriter.addDocument(lDocument);
	}

	private static void getBooksFromIndex() throws IOException, ParseException {

		IndexReader lReader = DirectoryReader.open(lIndexDirectory);
		IndexSearcher lSearcher = new IndexSearcher(lReader);

		QueryBuilder lQueryBuilder = new QueryBuilder(new ContainsAnalyzer());

		Query lBooksContainingName = lQueryBuilder.createBooleanQuery("author", "albert");

		TotalHitCountCollector collector = new TotalHitCountCollector();
		lSearcher.search(new MatchAllDocsQuery(), collector);

		System.out.println("TOTAL HITS: " + collector.getTotalHits());

	}

	private static void autoSuggest() throws Exception {
		IndexReader lReader = DirectoryReader.open(lIndexDirectory);
		Dictionary lDictionary = new DocumentDictionary(lReader, "title", "title_weight", null, "publisher");
		AnalyzingInfixSuggester suggestor = new AnalyzingInfixSuggester(new RAMDirectory(), new StandardAnalyzer());

		suggestor.build(lDictionary);

		HashSet<BytesRef> lSet = new HashSet<BytesRef>();
		lSet.add(new BytesRef("Berkley Trade".getBytes()));

		List<Lookup.LookupResult> lookupResultList = suggestor.lookup("Under", lSet, false, 10000);

		System.out.println("Suggested List : " + lookupResultList.size() + " suggestion");
		for (Lookup.LookupResult lookupResult : lookupResultList) {
			System.out.println(lookupResult.key + ": " + lookupResult.value);
		}
		suggestor.close();
	}

	public static void main(String args[]) throws Exception {
		init();
		Callable<Integer> callableObj = () -> {
			buildIndex();
			return 0;

		};
		List<Callable<Integer>> llistCallables = new ArrayList<Callable<Integer>>();
		for (int i = 0; i < 50; i++) {
			llistCallables.add(callableObj);
		}
		ExecutorService executor = Executors.newFixedThreadPool(1);
		double startTime = System.nanoTime();
		List<Future<Integer>> futures = executor.invokeAll(llistCallables);
		for (Future<?> future : futures) {
			try {
				future.get();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			}
		}
		while (executor.isTerminated()) {

		}
		System.out.println("Finished executing all threads");
		
		sIndexWriter.close();
		double endTime = System.nanoTime();
		System.out.println("Time Taken for indexing: " + ((endTime - startTime) / 1000000000));
		getBooksFromIndex();
	}

	private static void getFacetResults() throws IOException {

		IndexReader lReader = DirectoryReader.open(lIndexDirectory);
		IndexSearcher lSearcher = new IndexSearcher(lReader);

		Query lQuery = new TermQuery(new Term("author", "albert"));
	}

}

class SuggestionAnalyzer extends Analyzer {

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {

		Tokenizer source = new StandardTokenizer();

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
		lResult = new SnowballFilter(lResult, "English");
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