package com.luceneserver.analyzers.custom;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class ContainsAnalyzer extends Analyzer {

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		Tokenizer source = new StandardTokenizer();
		TokenStream results = new LowerCaseFilter(source);
		//results = new NGramTokenFilter(results, 3, 1000);
		results = new ShingleFilter(results, 2, 1000);
		return new TokenStreamComponents(source, results);
	}

}
