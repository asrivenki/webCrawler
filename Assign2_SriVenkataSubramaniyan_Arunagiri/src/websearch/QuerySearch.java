package websearch;

/**
 * @author Sri Venkata Subramaniyan Arunagiri
 */

import java.io.File;
import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Collector;


public class QuerySearch {
	private Analyzer QueryAnalyzer;
	private QueryParser queryParser;
	private IndexReader reader;
	private IndexSearcher searcher;
	
	
	public QuerySearch(String indexPath) {
		reader = null;
		searcher = null;
		QueryAnalyzer = null;
		queryParser = null;
		
		try {
			QueryAnalyzer = new StandardAnalyzer(Version.LUCENE_40);
			queryParser = new QueryParser(Version.LUCENE_40, "text", QueryAnalyzer);
			reader = DirectoryReader.open(FSDirectory.open(new File(indexPath)));
			searcher = new IndexSearcher(reader);
			
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	public void query(String queryString) {
		Query query;
		try {
			TopDocs results = null;
			query = queryParser.parse(queryString);
			results = searcher.search(query, 10);
			ScoreDoc[] hits = results.scoreDocs;   // This will score the documents based on the index file.
			System.out.println(" Fetching " + hits.length + " results:");
			for(ScoreDoc h : hits) {
				Document document = searcher.doc(h.doc);
				
				System.out.println(document.getField("url").stringValue());
				System.out.println(document.getField("title").stringValue());
				System.out.println(document.getField("text").stringValue());
				System.out.println(h);
				
				System.out.println();
			}
		} catch (ParseException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}
