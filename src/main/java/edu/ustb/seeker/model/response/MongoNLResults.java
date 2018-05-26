package edu.ustb.seeker.model.response;

import org.bson.Document;

import java.util.List;

public class MongoNLResults extends MongoFindResults {
    List<String> translations;

    public MongoNLResults(String status, List<String> translations, long total, List<Document> results) {
        super(status, total, results);
        this.translations = translations;
    }

    public List<String> getTranslations() {
        return translations;
    }
}
