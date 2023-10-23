package lucenex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Searching {
	
	public void execPhraseQuery()throws IOException, ParseException {
		try {
			Directory directory = FSDirectory.open(Paths.get("target/index")); 
			IndexReader indexReader = DirectoryReader.open(directory); 
			IndexSearcher searcher = new IndexSearcher(indexReader); 

			System.out.print("Inserisci il campo su cui vui svolgere la query (content or "
					+ "name): ");
			BufferedReader reader1 = new BufferedReader(new InputStreamReader(System.in));
			String field = reader1.readLine();

			System.out.print("Inserisci una query: ");
			BufferedReader reader2 = new BufferedReader(new InputStreamReader(System.in));
			String queryString = reader2.readLine();
			//Query query = new TermQuery(new Term("content", queryString));

			String[] terms = queryString.split(" ");
			PhraseQuery.Builder builder = new PhraseQuery.Builder();
			for(String term: terms) {
				builder.add(new Term(field,term));
			}
			PhraseQuery phraseQuery = builder.build();

			TopDocs hits = searcher.search(phraseQuery, 3);
			for(int i = 0; i< hits.scoreDocs.length; i++) {
				ScoreDoc scoreDoc = hits.scoreDocs[i];
				Document doc = searcher.doc(scoreDoc.doc);
				System.out.println("Il documento ritornato è il seguente: "+ doc.get("name"));
			}

			if(hits.scoreDocs.length == 0) {
				System.out.print("Non ci sono documenti che soddisfano la tua query");
			}

			directory.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void execBooleanQuery()throws IOException, ParseException {
		try {
			Directory directory = FSDirectory.open(Paths.get("target/index")); 
			IndexReader indexReader = DirectoryReader.open(directory); 
			IndexSearcher searcher = new IndexSearcher(indexReader); 

			System.out.print("Inserisci il campo su cui vui svolgere la query (content or "
					+ "name): ");
			BufferedReader reader1 = new BufferedReader(new InputStreamReader(System.in));
			String field = reader1.readLine();

			System.out.print("Inserisci una query: ");
			BufferedReader reader2 = new BufferedReader(new InputStreamReader(System.in));
			String queryString = reader2.readLine();

			String[] terms = queryString.split(" ");
			BooleanQuery.Builder builder = new BooleanQuery.Builder();
			for(String term: terms) {
				builder.add(new TermQuery(new Term(field, term)), Occur.SHOULD);
			}
			BooleanQuery booleanQuery = builder.build();



			TopDocs hits = searcher.search(booleanQuery, 3);
			for(int i = 0; i< hits.scoreDocs.length; i++) {
				ScoreDoc scoreDoc = hits.scoreDocs[i];
				Document doc = searcher.doc(scoreDoc.doc);
				System.out.println("Il documento ritornato è il seguente: "+ doc.get("name"));
			}

			if(hits.scoreDocs.length == 0) {
				System.out.print("Non ci sono documenti che soddisfano la tua query");
			}

			directory.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException, ParseException {
		Searching searching = new Searching();
		//searching.execPhraseQuery();
		searching.execBooleanQuery();
	}


}
