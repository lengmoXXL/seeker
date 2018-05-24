package edu.ustb.seeker.model.response;

public class MongoResult {
    private final String status;

    public MongoResult(String status) {
        this.status = status;
    }

    public String getResult() {
        return status;
    }
}
