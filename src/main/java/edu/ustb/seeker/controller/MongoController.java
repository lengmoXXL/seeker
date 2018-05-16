package edu.ustb.seeker.controller;

import edu.ustb.seeker.model.response.MongoFind;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ustb.seeker.service.MongoContact;
import edu.ustb.seeker.model.response.MongoResult;


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
    public MongoFind find(@RequestParam(value="collection", defaultValue = "test") String collectionName,
                          @RequestBody String string) {
        return new MongoFind(mc.count(string, collectionName),
                mc.find(string, collectionName));
    }
}
