package com.luceneserver.analyzers.custom;

import java.io.IOException;
import java.util.LinkedList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.AttributeSource.State;

public class CustomAnalyzer1 extends Analyzer {

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		
		Tokenizer source = new KeywordTokenizer();
		
		TokenStream result = new LowerCaseFilter(source);
		result = new EdgeNGramTokenFilter(result, 1, 50);
		return new TokenStreamComponents(source, result);
	}

}

class SampleTokenStream extends TokenStream
{
	private final LinkedList<String> extraTokens = new LinkedList<String>();
	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	private final PositionIncrementAttribute posIncAtt = addAttribute(PositionIncrementAttribute.class);
	private State savedState;
	@Override
	public boolean incrementToken() throws IOException {
		if (!extraTokens.isEmpty()) {
	        // Do we not loose/overwrite the current termAtt token here? (*)
			restoreState(savedState);
			posIncAtt.setPositionIncrement(0);
			termAtt.setEmpty().append(extraTokens.remove());
	        return true;
	    }
	    return false;
	}
	
}
