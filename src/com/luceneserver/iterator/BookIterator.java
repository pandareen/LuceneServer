package com.luceneserver.iterator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.lucene.search.suggest.InputIterator;
import org.apache.lucene.util.BytesRef;

import com.luceneserver.pojo.Book;

public class BookIterator implements InputIterator {

	Iterator<Book> mBookIterator = null;
	Book mCurrentBook = null;
	
	public BookIterator(Iterator<Book> bookItr) {
		mBookIterator = bookItr;
	}

	@Override
	public BytesRef next() throws IOException {
		if (mBookIterator.hasNext()) {
			mCurrentBook = mBookIterator.next();
			try {
				return new BytesRef(mCurrentBook.getTitle().getBytes("UTF-8"));
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return null;
	}

	@Override
	public Set<BytesRef> contexts() {
		Set<BytesRef> publishers = null;
		try {
			publishers = new HashSet<BytesRef>();
			publishers.add(new BytesRef(mCurrentBook.getPublisher()));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return publishers;
	}

	@Override
	public boolean hasContexts() {
		return true;
	}

	@Override
	public boolean hasPayloads() {
		return true;
	}

	@Override
	public BytesRef payload() {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(mCurrentBook);
			out.close();
			return new BytesRef(bos.toByteArray());
		} catch (IOException e) {
			throw new Error("couldnt decode payload");
		}

	}

	@Override
	public long weight() {
		return 0;
	}

}
