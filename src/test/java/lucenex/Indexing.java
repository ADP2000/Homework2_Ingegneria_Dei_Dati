package lucenex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilterFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.simpletext.SimpleTextCodec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexing {
	
	public static void main(String[] args) throws IOException {
		
		Map<String, Analyzer> perFieldAnalyzers = new HashMap<>();
		
		CharArraySet stopWord = new CharArraySet(Arrays.asList(".txt"), 
				true);
	
		Analyzer analyzerPerContent = CustomAnalyzer.builder()
                .withTokenizer(WhitespaceTokenizerFactory.class)
                .addTokenFilter(LowerCaseFilterFactory.class)
                .addTokenFilter(WordDelimiterGraphFilterFactory.class)
                //.addTokenFilter(StopFilterFactory.class,"in", "dei", "di", "il", "lo",
        		//		"la", "i", "gli", "le", "un", "una", "in", "con","su","per","tra","fra")
               .build();
		
//		Analyzer analyzerPerName= CustomAnalyzer.builder()
//                .withTokenizer(WhitespaceTokenizerFactory.class)
//                //.addTokenFilter(StopFilterFactory.class,".txt")
//				.addTokenFilter(LowerCaseFilterFactory.class)
//                .build();

		perFieldAnalyzers.put("name", new StandardAnalyzer(stopWord));
		perFieldAnalyzers.put("content", analyzerPerContent);
		
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
