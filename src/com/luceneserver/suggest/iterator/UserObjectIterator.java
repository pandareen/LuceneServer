package com.luceneserver.suggest.iterator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Set;

import org.apache.lucene.search.suggest.InputIterator;
import org.apache.lucene.util.BytesRef;

import com.luceneserver.suggest.core.UserObject;

public class UserObjectIterator implements InputIterator {

	private Iterator<UserObject> userObjectIterator;
	private UserObject currentUserObject;

	public UserObjectIterator(Iterator<UserObject> userObjectIterator) {
		this.userObjectIterator = userObjectIterator;
	}

	@Override
	public BytesRef next() throws IOException {
		if (userObjectIterator.hasNext()) {
			currentUserObject = userObjectIterator.next();
			try {
				return new BytesRef(currentUserObject.getUsername().getBytes("UTF8"));
			} catch (UnsupportedEncodingException e) {
				throw new Error("Couldn't convert to UTF-8");
			}
		} else {
			return null;
		}
	}

	@Override
	public long weight() {
		return 0;
	}

	@Override
	public BytesRef payload() {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(currentUserObject);
			out.close();
			return new BytesRef(bos.toByteArray());
		} catch (IOException exception) {
			throw new Error("Well that's unfortunate.");
		}
	}

	@Override
	public boolean hasPayloads() {
		return true;
	}

	@Override
	public Set<BytesRef> contexts() {
		return null;
	}

	@Override
	public boolean hasContexts() {
		return false;
	}

}
