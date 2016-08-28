package websearch;

/**
 * @author Sri Venkata Subramaniyan Arunagiri
 */

import java.util.Scanner;


public class MainProgram {
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		
		String URL = "https://en.wikibooks.org/wiki/Java_Programming";
		
		Crawler_PTag crawler = new Crawler_PTag("index_venkat_new.dat"); // This is the index file being created
	    crawler.crawler(URL);    
		
		QuerySearch queryEngine = new QuerySearch("index_venkat_new.dat");
		scanner.useDelimiter("\n");
		while(true) {
			System.out.print("Enter Query: ");
			queryEngine.query(scanner.next());
		}
	}
}
