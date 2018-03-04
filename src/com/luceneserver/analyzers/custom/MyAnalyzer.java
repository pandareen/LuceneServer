package com.luceneserver.analyzers.custom;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.KeywordTokenizerFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterFilterFactory;
import org.apache.lucene.analysis.ngram.EdgeNGramFilterFactory;
import org.apache.lucene.analysis.standard.StandardFilterFactory;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;

public class MyAnalyzer extends Analyzer {

	public MyAnalyzer() {

	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		return new TokenStreamComponents(new WhitespaceTokenizer());
	}

	public static void main(String[] args) throws IOException {
		Map<String, String> CONSTANT_MAP = new HashMap<String, String>();

		CONSTANT_MAP.put("generateWordParts", "1");
		CONSTANT_MAP.put("generateNumberParts", "0");
		CONSTANT_MAP.put("catenateWords", "0");
		CONSTANT_MAP.put("catenateNumbers", "0");
		CONSTANT_MAP.put("catenateAll", "0");
		CONSTANT_MAP.put("splitOnCaseChange", "0");
		CONSTANT_MAP.put("splitOnNumerics", "1");
		CONSTANT_MAP.put("preserveOriginal", "0");
		CONSTANT_MAP.put("stemEnglishPossessive", "0");
		// text to tokenize
		final String text = "power-shot-n566-neo.txt-ehh";

		Map<String, String> lmapMinMaxGramSize = new HashMap<String, String>();
		lmapMinMaxGramSize.put("minGramSize", "3");
		lmapMinMaxGramSize.put("maxGramSize", "1000");
		Analyzer analyzer = CustomAnalyzer.builder().withTokenizer(KeywordTokenizerFactory.class)
				.addTokenFilter(LowerCaseFilterFactory.class)
				.addTokenFilter(WordDelimiterFilterFactory.class, CONSTANT_MAP)
				// .addTokenFilter(EdgeNGramFilterFactory.class,
				// lmapMinMaxGramSize)

				.build();

		// analyzer = new CustomAnalyzer1();
		TokenStream stream = analyzer.tokenStream("field", new StringReader(text));

		// get the CharTermAttribute from the TokenStream
		CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);
		KeywordAttribute keyAtt = stream.addAttribute(KeywordAttribute.class);

		try {
			stream.reset();
			// print all tokens until stream is exhausted
			while (stream.incrementToken()) {
				System.out.println(termAtt.toString());
			}
			stream.end();
		} finally {
			stream.close();
		}
	}
}