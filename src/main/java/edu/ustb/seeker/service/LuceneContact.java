package edu.ustb.seeker.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
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
            List<String> types = new ArrayList<>();
            for (IndexableField f: hitDoc.getFields("type")) {
                String[] tmp = f.stringValue().split("@");
                names.add(tmp[0]);
                types.add(tmp[1]);
            }
            ret.add(new Schema(names, types));
        }
        ir.close();
        dir.close();
        return ret;
    }

    public void addSchema(Schema schema) throws IOException {
        Analyzer analyzer = new KeywordAnalyzer();
        Directory dir = FSDirectory.open(this.libPath);

        IndexWriterConfig conf =new IndexWriterConfig(analyzer);
        IndexWriter iw = new IndexWriter(dir, conf);

        Document doc = new Document();
        for (SchemaField schemaField: schema.getFields()) {
            String type = "OTHER";
            if (schemaField.getFieldType() == SchemaField.NUMBER) {
                type = "NUMBER";
            } else if (schemaField.getFieldType() == SchemaField.STRING) {
                type = "STRING";
            }
            doc.add(new Field("type", schemaField.getFieldName() + '@' + type, TextField.TYPE_STORED));
        }
        iw.addDocument(doc);
        iw.close();
    }

    public boolean exist(Schema schema) throws IOException {
        Directory dir = FSDirectory.open(libPath);
        DirectoryReader ir = DirectoryReader.open(dir);
        IndexSearcher is = new IndexSearcher(ir);

        List<TermQuery> termQueries = new ArrayList<>();
        for (SchemaField schemaField: schema.getFields()) {
            termQueries.add(new TermQuery(new Term("type", schemaField.getFieldName() + '@' + schemaField.getFieldTypeName())));
        }

        BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
        for (TermQuery termQuery: termQueries) {
            booleanQueryBuilder.add(termQuery, BooleanClause.Occur.MUST);
        }

        BooleanQuery booleanQuery = booleanQueryBuilder.build();
        ScoreDoc[] hits = is.search(booleanQuery, this.maxReturnSchemas, Sort.RELEVANCE).scoreDocs;

        ir.close();
        dir.close();
        boolean ret = hits.length > 0 ? true: false;
        return ret;
    }

    public void addWhatEver(String[] names, String[] types) throws IOException {
        Analyzer analyzer = new KeywordAnalyzer();
        Directory dir = FSDirectory.open(this.libPath);

        IndexWriterConfig conf =new IndexWriterConfig(analyzer);
        IndexWriter iw = new IndexWriter(dir, conf);

        Document doc = new Document();
        for (int i = 0; i < names.length; i++) {
            doc.add(new Field("type", names[i] + '@' + types[i], TextField.TYPE_STORED));
        }

        iw.addDocument(doc);
        iw.close();
    }


    public static void main(String[] args) throws IOException {
        String[] texts = {"河流名称", "河流长度(米)", "河流流经地区"};
        String[] types = {"STRING", "NUMBER", "STRING"};
        LuceneContact lc = new LuceneContact("luceneData/schemas");
//        lc.addWhatEver(texts, types);
        Schema schema = new Schema(texts, types);
//        lc.addSchema(schema);
        System.out.println(lc.exist(schema));
        System.out.println(lc.allSchemas());
    }
}
