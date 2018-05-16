package edu.ustb.seeker.model.response;

import java.util.List;

import org.bson.Document;

public class MongoFind {
    private long num;
    private List<Document> results;

    public MongoFind(long num, List<Document> results) {
        this.num = num;
        this.results = results;
    }

    public long getNum() {
        return num;
    }

    public List<Document> getResults() {
        return results;
    }
}
