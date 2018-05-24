package edu.ustb.seeker.controller;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;
import org.bson.Document;

import edu.ustb.seeker.service.MongoContact;
import edu.ustb.seeker.model.response.MongoResult;
import edu.ustb.seeker.model.response.MongoFindResults;


@RestController
@RequestMapping("/mongodb")
public class MongoController {
    private MongoContact mc;
    MongoController() {
        mc = new MongoContact("mongodb://seeker:seeker@192.168.56.129:27017/?authSource=seeker");
        mc.setDatabase("seeker");
    }

    @RequestMapping("/insertOne")
    public MongoResult insertOne(@RequestParam(value="collection", defaultValue = "test") String collectionName,
                                 @RequestBody String string) {
        mc.insertOne(string, collectionName);
        return new MongoResult("ok");
    }

    @RequestMapping("/find")
    public MongoFindResults find(@RequestParam(value="collection", defaultValue = "test") String collectionName,
                                 @RequestBody String string) {
        return new MongoFindResults("ok", mc.count(string, collectionName),
                mc.find(string, collectionName));
    }

    @RequestMapping(value = "/{collection}", method=RequestMethod.GET)
    public MongoResult find(@PathVariable String collection,
                            @RequestParam(value="query", defaultValue = "{}") String query,
                            @RequestParam(value="nlQuery", defaultValue = "") String nlQuery,
                            @RequestParam(value="from", defaultValue = "0") int from,
                            @RequestParam(value="size", defaultValue = "10") int size,
                            @RequestParam(value="returnDocuments", defaultValue = "true") boolean returnDocuments) {
        List<String> collections = mc.showCollections();
        if (collections.contains(collection)) {
            if (nlQuery.length() == 0) {
                return new MongoFindResults("ok",
                        mc.count(query, collection),
                        returnDocuments ? mc.find(query, collection, from, size) : new ArrayList<Document>());
            } else {
                return new MongoResult("Not Implemented yet.");
            }
        } else {
            return new MongoResult("No collection found.");
        }
    }

    @RequestMapping(value = "/{collection}", method=RequestMethod.POST)
    public MongoResult insert(@PathVariable String collection, @RequestBody String body) throws ParseException {
        mc.insert(body, collection);
        return new MongoResult("ok");
    }
}
