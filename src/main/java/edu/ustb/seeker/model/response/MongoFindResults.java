package edu.ustb.seeker.model.response;

import java.util.List;

import org.bson.Document;

public class MongoFindResults extends MongoResult {
    private long total;
    private List<Document> results;

    public MongoFindResults(String status, long total, List<Document> results) {
        super(status);
        this.total = total;
        this.results = results;
    }

    public long getTotal() {
        return total;
    }

    public List<Document> getResults() {
        return results;
    }
}
