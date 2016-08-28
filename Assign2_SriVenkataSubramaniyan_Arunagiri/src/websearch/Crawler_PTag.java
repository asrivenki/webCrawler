package websearch;

/**
 * @author Sri Venkata Subramaniyan Arunagiri.
 */

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.util.Version;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

public class Crawler_PTag {

	private  HashMap<String, Integer> doneUrls;
	
	private Directory directory;
	 IndexWriterConfig iwc;
	private IndexWriter indexwriter;
	 Analyzer analyzer;
	

	public Crawler_PTag() {
		doneUrls = new HashMap<String, Integer>();
		directory = null;
	}

	public Crawler_PTag(String indexPath) {
		doneUrls = new HashMap<String, Integer>();
		directory = null;
		analyzer = null;
		iwc = null;
		try {
			directory = FSDirectory.open(new File(indexPath));
			analyzer = new StandardAnalyzer(Version.LUCENE_40);
			iwc = new IndexWriterConfig(Version.LUCENE_40, analyzer);
			iwc.setOpenMode(OpenMode.CREATE/*_OR_APPEND*/);
			
			indexwriter = new IndexWriter(directory, iwc);
			
			
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	public void indexDocument(String text, String title, String url) {
		Document doc = new Document();
		Field url_Field = new StringField("url", url, Field.Store.YES);
		doc.add(url_Field);
		
		Field title_Field = new TextField("title", title, Field.Store.YES);
		doc.add(title_Field);
		
		Field text_Field = new TextField("text", text, Field.Store.YES);
		doc.add(text_Field);
		if(indexwriter.getConfig().getOpenMode() == OpenMode.CREATE) {
			System.out.println("Adding the Paragraph to the index from URL:   " + url);
			try {
				indexwriter.addDocument(doc);
			} catch (IOException e) {
				System.out.println(e);
			}
		}
		
		
	}
	
	public  int crawler(String startUrl) {
		
		HashSet<String> nextUrls = new HashSet<String>();
		
		try {
			org.jsoup.nodes.Document doc = Jsoup.connect(startUrl).get();
			
				Elements links = doc.select("a[href]");
			for(Element l: links) {
				System.out.println(l.attr("href"));
				String CompleteUrl = constructFullUrl(l.attr("href"),startUrl);
				
				if (CompleteUrl != null)
				{
					crawlGivenUrl(CompleteUrl);
					nextUrls.add(CompleteUrl);
				}
			}
			
			
		} catch (MalformedURLException me) {
			System.out.println(me);
		} catch (IOException e) {
			System.out.println(e);
		}
		
		
		try {
			indexwriter.close();
		} catch (IOException e) {
			System.out.println(e);
		}
		return 0;
	}
	
	
	
	
	
	
	private String constructFullUrl(String href, String url) throws MalformedURLException {
		if(href.startsWith("mailto://")) {
			return null;
		}
		if(href.startsWith("http://") || href.startsWith("https://") || href.startsWith("www:")) {
			return href;
		}
		
		if(href.contains("#")) {
			href = href.substring(0, href.indexOf("#"));
		}
		URL CompleteUrl = new URL(new URL(url), href);
		String fullURL_String = CompleteUrl.toString();
		if(fullURL_String.endsWith("/")) {
			fullURL_String = fullURL_String.substring(0, fullURL_String.length()-1);
		}
		return fullURL_String.toString();
	}

	public void crawlGivenUrl(String url) {
		if(doneUrls.containsKey(url)) {
			return;
		}
		doneUrls.put(url, 1);
		//HashSet<String> nextUrls = new HashSet<String>();
		try {
			org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
			
			Elements links = doc.select("p");
			for(Element l: links) {
			
				indexDocument(l.text(), doc.title(), url);
			}
			
			
			
			
			
		} catch (MalformedURLException me) {
			System.out.println(me);
		} catch (IOException e) {
			System.out.println(e);
		}
		
	}
	
	
}
