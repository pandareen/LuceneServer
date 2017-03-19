package com.luceneserver.suggest;

import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class AutoCompletionAnalyzer extends Analyzer {

	private static final int MAX_TOKEN_LENGTH = StandardAnalyzer.DEFAULT_MAX_TOKEN_LENGTH;

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {

		StandardTokenizer tokenStream = new StandardTokenizer();
		tokenStream.setMaxTokenLength(MAX_TOKEN_LENGTH);

		TokenStream result = new StandardFilter(tokenStream);
		result = new LowerCaseFilter(result);
		result = new org.apache.lucene.analysis.StopFilter(result,
				StopFilter.makeStopSet((List<?>) StopAnalyzer.ENGLISH_STOP_WORDS_SET));
		result = new EdgeNGramTokenFilter(result, 1, 20);

		return new TokenStreamComponents(tokenStream, result);
	}
}