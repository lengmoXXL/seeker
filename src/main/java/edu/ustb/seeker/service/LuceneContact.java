package edu.ustb.seeker.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import edu.ustb.seeker.model.data.Schema;
import edu.ustb.seeker.model.data.SchemaField;

public class LuceneContact {
    private Path libPath;
    private int maxReturnSchemas;

    public LuceneContact(String path) {
        libPath = Paths.get(path);
        maxReturnSchemas = 20;
    }

    public List<Schema> allSchemas() throws IOException {
        Directory dir = FSDirectory.open(libPath);
        DirectoryReader ir = DirectoryReader.open(dir);
        IndexSearcher is = new IndexSearcher(ir);

        Query query = new MatchAllDocsQuery();
        ScoreDoc[] hits = is.search(query, this.maxReturnSchemas, Sort.RELEVANCE).scoreDocs;

        List<Schema> ret = new ArrayList<>();
        for (int i = 0; i < hits.length; i++) {
            Document hitDoc = is.doc(hits[i].doc);
            List<String> names = new ArrayList<>();
            List<Integer> types = new ArrayList<>();
            for (IndexableField f: hitDoc.getFields("key")) {
                names.add(f.stringValue());
            }
            for (IndexableField f: hitDoc.getFields("type")) {
                if (f.stringValue().equals("string")) {
                    types.add(SchemaField.STRING);
                } else {
                    types.add(SchemaField.NUMBER);
                }
            }
            ret.add(new Schema(names, types));
        }
        ir.close();
        dir.close();
        return ret;
    }

    public void addWhatEver(String[] names, String[] types) throws IOException {
        Analyzer analyzer = new StandardAnalyzer();
        Directory dir = FSDirectory.open(this.libPath);

        IndexWriterConfig conf =new IndexWriterConfig(analyzer);
        IndexWriter iw = new IndexWriter(dir, conf);

        Document doc = new Document();
        for (int i = 0; i < names.length; i++) {
            doc.add(new Field("key", names[i], TextField.TYPE_STORED));
            doc.add(new Field("type", types[i], TextField.TYPE_STORED));
        }

        iw.addDocument(doc);
        iw.close();
    }


    public static void main(String[] args) throws IOException {
        String[] texts = {"类型", "名称", "缩写", "省会城市", "人口", "面积", "城市.名称", "城市.人口", "边界省份", "山.名称", "山.海拔"};
        String[] types = {"string", "string", "string", "string", "number", "number", "string", "number", "string", "string", "number"};
        LuceneContact lc = new LuceneContact("luceneData/schemas");
        lc.addWhatEver(texts, types);
        System.out.println(lc.allSchemas());
    }
}
