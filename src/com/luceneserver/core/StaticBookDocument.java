package com.luceneserver.core;

import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.SortedNumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.util.BytesRef;

public class StaticBookDocument {
	static TextField sTFTitle;
	static TextField sTFCover;
	static TextField sTFAuthor;
	static TextField sTFPublisher;
	static StoredField sSFTitle;
	static StoredField sSFCover;
	static StoredField sSFAuthor;
	static StoredField sSFPublisher;
	static SortedDocValuesField sSDVTitle;
	static SortedDocValuesField sSDVCover;
	static SortedDocValuesField sSDVAuthor;
	static SortedDocValuesField sSDVPublisher;
	static LongPoint sLPPages;
	static LongPoint sLPISBN10;
	static LongPoint sLPISBN13;
	static StoredField sSFPages;
	static StoredField sSFISBN10;
	static StoredField sSFISBN13;
	static SortedNumericDocValuesField sSNDVPages;
	static SortedNumericDocValuesField sSNDVISBN10;
	static SortedNumericDocValuesField sSNDVISBN13;
	
	static String lFieldName = "2";

	static Document doc = new Document();

	static {
		sTFTitle = new TextField("title", "ni", Field.Store.NO);
		sTFCover = new TextField("cover", "ni", Field.Store.NO);
		sTFAuthor = new TextField("author", "ni", Field.Store.NO);
		sTFPublisher = new TextField("publisher", "ni", Field.Store.NO);

		sSFTitle = new StoredField("title", "ni");
		sSFCover = new StoredField("cover", "ni");
		sSFAuthor = new StoredField("author", "ni");
		sSFPublisher = new StoredField("publisher", "ni");

		sSDVTitle = new SortedDocValuesField("title", new BytesRef("ni"));
		sSDVCover = new SortedDocValuesField("cover", new BytesRef("ni"));
		sSDVAuthor = new SortedDocValuesField("author", new BytesRef("ni"));
		sSDVPublisher = new SortedDocValuesField("publisher", new BytesRef("ni"));

		sLPPages = new LongPoint("pages", 1L);
		sLPISBN10 = new LongPoint("isbn10", 1L);
		sLPISBN13 = new LongPoint("isbn13", 1L);

		sSFPages = new StoredField("pages", 1L);
		sSFISBN10 = new StoredField("isbn10", 1L);
		sSFISBN13 = new StoredField("isbn13", 1L);

		sSNDVPages = new SortedNumericDocValuesField("pages", 1L);
		sSNDVISBN10 = new SortedNumericDocValuesField("isbn10", 1L);
		sSNDVISBN13 = new SortedNumericDocValuesField("isbn13", 1L);
		
//		doc.add(sTFTitle);
//		doc.add(sTFCover);
//		doc.add(sTFAuthor);
//		doc.add(sTFPublisher);
//		doc.add(sSFTitle);
//		doc.add(sSFCover);
//		doc.add(sSFAuthor);
//		doc.add(sSFPublisher);
//		doc.add(sSDVTitle);
//		doc.add(sSDVCover);
//		doc.add(sSDVAuthor);
//		doc.add(sSDVPublisher);
//		doc.add(sLPPages);
//		doc.add(sLPISBN10);
//		doc.add(sLPISBN13);
//		doc.add(sSFPages);
//		doc.add(sSFISBN10);
//		doc.add(sSFISBN13);
//		doc.add(sSNDVPages);
//		doc.add(sSNDVISBN10);
//		doc.add(sSNDVISBN13);

	}

	public static Document getBookdocument(String pTitle, long pPages, long pISBN10, long pISBN13, String pCover,
			String pAuthor, String pPublisher) {

//		List<IndexableField> lFields = doc.getFields();
//		for(IndexableField lField: lFields) {
//			
//			lFieldName = lField.name();
//			
//			if(lField instanceof TextField) {
//				if(lFieldName.equals("title")) {
//					((Field) lField).setStringValue(pTitle);
//				}
//				else if(lFieldName.equals("cover")) {
//					((Field) lField).setStringValue(pCover);
//				}
//				else if(lFieldName.equals("author")) {
//					((Field) lField).setStringValue(pAuthor);
//				}
//				else if(lFieldName.equals("publisher")) {
//					((Field) lField).setStringValue(pPublisher);
//				}
//			}
//			if(lField instanceof StoredField) {
//				if(lFieldName.equals("title")) {
//					((Field) lField).setStringValue(pTitle);
//				}
//				else if(lFieldName.equals("cover")) {
//					((Field) lField).setStringValue(pCover);
//				}
//				else if(lFieldName.equals("author")) {
//					((Field) lField).setStringValue(pAuthor);
//				}
//				else if(lFieldName.equals("publisher")) {
//					((Field) lField).setStringValue(pPublisher);
//				}
//			}
//			if(lField instanceof SortedDocValuesField) {
//				if(lFieldName.equals("title")) {
//					((Field) lField).setBytesValue(new BytesRef(pTitle));
//				}
//				else if(lFieldName.equals("cover")) {
//					((Field) lField).setBytesValue(new BytesRef(pCover));
//				}
//				else if(lFieldName.equals("author")) {
//					((Field) lField).setBytesValue(new BytesRef(pAuthor));
//				}
//				else if(lFieldName.equals("publisher")) {
//					((Field) lField).setBytesValue(new BytesRef(pPublisher));
//				}
//			}
//			if(lField instanceof LongPoint) {
//				if(lFieldName.equals("pages")) {
//					((Field) lField).setLongValue(pPages);
//				}
//				else if(lFieldName.equals("isbn10")) {
//					((Field) lField).setLongValue(pISBN10);
//				}
//				else if(lFieldName.equals("isbn13")) {
//					((Field) lField).setLongValue(pISBN13);
//				}
//			}
//			if(lField instanceof StoredField) {
//				if(lFieldName.equals("pages")) {
//					((Field) lField).setLongValue(pPages);
//				}
//				else if(lFieldName.equals("isbn10")) {
//					((Field) lField).setLongValue(pISBN10);
//				}
//				else if(lFieldName.equals("isbn13")) {
//					((Field) lField).setLongValue(pISBN13);
//				}
//			}
//			if(lField instanceof SortedNumericDocValuesField) {
//				if(lFieldName.equals("pages")) {
//					((Field) lField).setLongValue(pPages);
//				}
//				else if(lFieldName.equals("isbn10")) {
//					((Field) lField).setLongValue(pISBN10);
//				}
//				else if(lFieldName.equals("isbn13")) {
//					((Field) lField).setLongValue(pISBN13);
//				}
//			}
//		}
//		
		
		
		
//		doc.removeFields("title");
//		doc.removeFields("pages");
//		doc.removeFields("isbn10");
//		doc.removeFields("isbn13");
//		doc.removeFields("cover");
//		doc.removeFields("author");
//		doc.removeFields("publisher");
		
		sTFTitle.setStringValue(pTitle);
		sTFCover.setStringValue(pCover);
		sTFAuthor.setStringValue(pAuthor);
		sTFPublisher.setStringValue(pPublisher);

		sSFTitle.setStringValue(pTitle);
		sSFCover.setStringValue(pCover);
		sSFAuthor.setStringValue(pAuthor);
		sSFPublisher.setStringValue(pPublisher);

		sSDVTitle.setBytesValue(new BytesRef(pTitle));
		sSDVCover.setBytesValue(new BytesRef(pCover));
		sSDVAuthor.setBytesValue(new BytesRef(pAuthor));
		sSDVPublisher.setBytesValue(new BytesRef(pPublisher));

		sLPPages.setLongValue(pPages);
		sLPISBN10.setLongValue(pISBN10);
		sLPISBN13.setLongValue(pISBN13);

		sSFPages.setLongValue(pPages);
		sSFISBN10.setLongValue(pISBN10);
		sSFISBN13.setLongValue(pISBN13);

		sSNDVPages.setLongValue(pPages);
		sSNDVISBN10.setLongValue(pISBN10);
		sSNDVISBN13.setLongValue(pISBN13);
		
		doc = new Document();
		
		doc.add(sTFTitle);
		doc.add(sTFCover);
		doc.add(sTFAuthor);
		doc.add(sTFPublisher);
		doc.add(sSFTitle);
		doc.add(sSFCover);
		doc.add(sSFAuthor);
		doc.add(sSFPublisher);
		doc.add(sSDVTitle);
		doc.add(sSDVCover);
		doc.add(sSDVAuthor);
		doc.add(sSDVPublisher);
		doc.add(sLPPages);
		doc.add(sLPISBN10);
		doc.add(sLPISBN13);
		doc.add(sSFPages);
		doc.add(sSFISBN10);
		doc.add(sSFISBN13);
		doc.add(sSNDVPages);
		doc.add(sSNDVISBN10);
		doc.add(sSNDVISBN13);
		
		

		return doc;
	}

}
