package lucenex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.codecs.simpletext.SimpleTextCodec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class QueryIndex {
	public static void main(String[] args) throws IOException, ParseException {
		try {
			Directory directory = FSDirectory.open(Paths.get("target/index")); 
			IndexReader indexReader = DirectoryReader.open(directory); 
			IndexSearcher searcher = new IndexSearcher(indexReader); 

			System.out.print("Inserisci una query: ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String queryString = reader.readLine();

            //Query query = new TermQuery(new Term("content", queryString));
            
    		String[] terms = queryString.split(" ");
    		PhraseQuery.Builder builder = new PhraseQuery.Builder();
    		for(String term: terms) {
    			builder.add(new Term("content",term));
    		}
    		PhraseQuery phraseQuery = builder.build();
			
    		TopDocs hits = searcher.search(phraseQuery, 10);
			for(int i = 0; i< hits.scoreDocs.length; i++) {
				ScoreDoc scoreDoc = hits.scoreDocs[i];
				Document doc = searcher.doc(scoreDoc.doc);
				System.out.println("doc"+scoreDoc.doc + ":"+ doc.get("name") + " (" + scoreDoc.score +")");
			}
			directory.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
