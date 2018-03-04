package com.luceneserver.suggest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.suggest.Lookup;
import org.apache.lucene.search.suggest.analyzing.AnalyzingInfixSuggester;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import com.luceneserver.suggest.core.UserObject;
import com.luceneserver.suggest.iterator.UserObjectIterator;

public class SuggestUserMain {

	public static void main(String[] args) {
		AnalyzingInfixSuggester suggester = null;
		try {
			RAMDirectory index_dir = new RAMDirectory();
			Directory dir =  FSDirectory.open((new File("suggest_index").toPath()));
			StandardAnalyzer analyzer = new StandardAnalyzer();
			suggester = new AnalyzingInfixSuggester(dir, analyzer);
			
			// Create our list of products.
			List<UserObject> userObjectslist = getList();
			// Index the products with the suggester.
			suggester.build(new UserObjectIterator(userObjectslist.iterator()));
			List<Lookup.LookupResult> results;
			BooleanQuery.Builder contextFilter = new BooleanQuery.Builder();
			contextFilter.add(new MatchAllDocsQuery(), Occur.MUST);
			results = suggester.lookup("user5 ", contextFilter.build(), 100, true, true);
			// Do some example lookups.
			for (Lookup.LookupResult result : results) {
				System.out.println(result.key);
			}
			suggester.close();
		} catch (IOException e) {
			System.err.println("Error!");
		} finally {

		}

	}

	private static List<UserObject> getList() {
		List<UserObject> userObjectList = new ArrayList<UserObject>();
		for (int i = 0; i < 100000; i++) {
			UserObject tempUserObject = new UserObject();
			tempUserObject.setEmail("user" + i + "@mail.ru");
			tempUserObject.setFirstName("firstname" + i);
			tempUserObject.setLastName("lastname" + i);
			tempUserObject.setRepositoryCode((i % 5));
			tempUserObject.setStatus((i % 3));
			tempUserObject.setUsername("user" + i + " name" + (i%5));
			userObjectList.add(tempUserObject);
		}
		return userObjectList;
	}
	
	private static BooleanQuery getContextQuery()
	{
		
		BooleanQuery.Builder contextFilter = new BooleanQuery.Builder();
		
		
		
		return null;
		
	}

}
