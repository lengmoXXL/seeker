package edu.ustb.seeker.service;

import org.bson.BsonArray;
import org.bson.Document;

import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.util.ArrayList;
import java.util.List;

public class MongoContact {
    private MongoClientURI url;
    private MongoClient cli;
    private MongoDatabase db;
    public MongoContact(String url) {
        this.url = new MongoClientURI(url);
        this.cli = MongoClients.create(url);
    }

    public void setDatabase(String database) {
        this.db = cli.getDatabase(database);
    }

    public List<String> showCollections(String database) {
        this.setDatabase(database);
        return this.showCollections();
    }

    public List<String> showCollections() {
        List<String> ret = new ArrayList<String>();
        for (String collectionName: this.db.listCollectionNames()) {
            ret.add(collectionName);
        }
        return ret;
    }

    public void insertOne(String json, String collectionName) {
        MongoCollection<Document> col = this.db.getCollection(collectionName);
        col.insertOne(Document.parse(json));
    }

    public void insert(String json, String collectionName) throws ParseException {
        MongoCollection<Document> col = this.db.getCollection(collectionName);
        JSONParser jsonParser = new JSONParser();
        Object docsObj = jsonParser.parse(json);
        if (docsObj instanceof JSONObject) {
            col.insertOne(Document.parse(docsObj.toString()));
        } else {
            JSONArray jsonArray = (JSONArray) docsObj;
            List<Document> documents = new ArrayList<>();
            for (Object docObj: jsonArray) {
                documents.add(Document.parse(docObj.toString()));
            }
            col.insertMany(documents);
        }
    }

    public List<Document> find(String collectionName) {
        MongoCollection<Document> col = this.db.getCollection(collectionName);
        List<Document> ret = new ArrayList<Document>();
        for (Document doc: col.find()) {
            ret.add(doc);
        }
        return ret;
    }

    public List<Document> find(String queryJson, String collectionName) {
        MongoCollection<Document> col = this.db.getCollection(collectionName);
        List<Document> ret = new ArrayList<Document>();
        for (Document doc: col.find(Document.parse(queryJson))) {
            ret.add(doc);
        }
        return ret;
    }

    public List<Document> find(String queryJson, String collectionName, int from, int size) {
        MongoCollection<Document> col = this.db.getCollection(collectionName);
        List<Document> ret = new ArrayList<Document>();
        int currentPosition = 0;
        for (Document doc: col.find(Document.parse(queryJson))) {
            if (from <= currentPosition && currentPosition < from + size)
                ret.add(doc);
            currentPosition++;
        }
        return ret;
    }

    public long count(String queryJson, String collectionName) {
        MongoCollection<Document> col = this.db.getCollection(collectionName);
        return col.count(Document.parse(queryJson));
    }

    public static void main(String[] args) {
        MongoContact ms = new MongoContact("mongodb://seeker:seeker@192.168.56.129:27017/?authSource=seeker");
        ms.setDatabase("seeker");
        System.out.println(ms.showCollections());
        ms.insertOne("{'name': 'lzy', 'say': 'hello'}", "test");
        System.out.println(ms.find("test"));
    }
}
