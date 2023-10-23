package lucenex;

import org.apache.lucene.document.Field;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilterFactory;
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
import org.apache.lucene.tests.analysis.TokenStreamToDot;

public class Indice {
	
	public static void main(String[] args) throws IOException {
		
		Map<String, Analyzer> perFieldAnalyzers = new HashMap<>();
		
		CharArraySet stopWord = new CharArraySet(Arrays.asList(".txt"), 
				true);
	
//		Analyzer analyzerPerContent = CustomAnalyzer.builder()
//                .withTokenizer(WhitespaceTokenizerFactory.class)
//                .addTokenFilter(LowerCaseFilterFactory.class)
//                .addTokenFilter(WordDelimiterGraphFilterFactory.class)
//                .addTokenFilter(StopFilterFactory.class,"in", "dei", "di", "il", "lo",
//        				"la", "i", "gli", "le", "un", "una", "in", "con","su","per","tra","fra")
//               .build();
		
//		Analyzer analyzerPerName= CustomAnalyzer.builder()
//                .withTokenizer(WhitespaceTokenizerFactory.class)
//                .addTokenFilter(StopFilterFactory.class,".txt")
//                .build();

		perFieldAnalyzers.put("name", new StandardAnalyzer(stopWord));
		perFieldAnalyzers.put("content", new WhitespaceAnalyzer());
		
		Analyzer analyzer = new PerFieldAnalyzerWrapper(new ItalianAnalyzer(), 
				perFieldAnalyzers);
		
		Directory directory = FSDirectory.open(Paths.get("target/index")); 
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setCodec(new SimpleTextCodec());
		IndexWriter writer = new IndexWriter(directory, config);
		

		File directoryPath = new File("C:\\Users\\antod\\OneDrive - Universita degli Studi Roma Tre\\Università\\Ingegneria de Dati\\Homework\\documents");
		File[] files = directoryPath.listFiles();


		for (File file : files) {
			if (file.isFile() && file.getName().endsWith(".txt")) {
				Document document = new Document();
				Field nameField = new StringField("name", file.getName(), Field.Store.YES);
				document.add(nameField);
				try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
					String line;
					while ((line = reader.readLine()) != null) {
						TextField contentField = new TextField("content", line, Field.Store.YES);
						document.add(contentField);
					}
				}
				writer.addDocument(document);

			}

		}
		writer.commit();
		writer.close();
	}
}
