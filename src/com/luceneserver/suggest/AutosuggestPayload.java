package com.luceneserver.suggest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.suggest.Lookup.LookupResult;
import org.apache.lucene.search.suggest.analyzing.AnalyzingInfixSuggester;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.luceneserver.pojo.Book;

public class AutosuggestPayload {
	static AnalyzingInfixSuggester lSuggester;

	static {
		try {
			lSuggester = new AnalyzingInfixSuggester(new RAMDirectory(),
					new StandardAnalyzer());
			List<Book> lBookList = new ArrayList<Book>();
		} catch (IOException lException) {
			lException.printStackTrace();
		}
	}

	private static void prepareSuggestIndex() throws Exception {
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(new FileReader("data/books.json"));

		JSONArray bookObjectArray = (JSONArray) obj;

		for (int i = 0; i < bookObjectArray.size(); i++) {
			JSONObject lBookObject = (JSONObject) bookObjectArray.get(i);
			Book lBook = new Book();
			lBook.setTitle(lBookObject.get("Title").toString());
			lBook.setAuthor(lBookObject.get("Author").toString());
			lBook.setPublisher(lBookObject.get("Publisher").toString());
			lBook.setCover(lBookObject.get("Cover").toString());
			lBook.setPages(Integer
					.parseInt(lBookObject.get("Pages").toString()));
			lBook.setIsbn10(lBookObject.get("ISBN10").toString());
			lBook.setIsbn13(lBookObject.get("ISBN13").toString());

			BytesRef payload = null;

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(lBook);
			out.close();
			payload = new BytesRef(bos.toByteArray());
			Set<BytesRef> context = new HashSet<BytesRef>();
			context.add(new BytesRef(lBook.getPublisher()));
			lSuggester.add(new BytesRef(lBook.getTitle()), context, (long) 0,
					payload);

		}
		lSuggester.refresh();
	}

	private static void getSuggestions(String term) throws Exception {
		List<LookupResult> lResults = lSuggester.lookup(term, 10000, true,
				false);

		for (LookupResult result : lResults) {
			Book book = getBook(result);
			System.out.println(book.getAuthor());
		}

	}

	private static Book getBook(LookupResult result) {
		BytesRef payload = result.payload;
		Book lBook = null;
		if (payload != null) {
			ByteArrayInputStream bos = new ByteArrayInputStream(payload.bytes);
			try {
				ObjectInputStream in = new ObjectInputStream(bos);
				lBook = (Book) in.readObject();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return lBook;
		} else {
			return null;
		}
	}

	public static void main(String args[]) throws Exception {
		prepareSuggestIndex();
		getSuggestions("Under");
	}
	
	

}
