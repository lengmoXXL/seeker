package edu.ustb.seeker.archive.valuerules;

import edu.ustb.seeker.archive.expert.ChinesePhraseLib;
import edu.ustb.seeker.archive.expert.PhraseLibExtendedHowNet;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtractVariablePool {
    Map<String, List<Object>> map;

    public ExtractVariablePool() {
        map = new HashMap<>();
    }

    public ExtractVariablePool(ExtractVariablePool extractVariablePool) {
        map = new HashMap<>();
        for (Map.Entry<String, List<Object>> entry: extractVariablePool.getMap().entrySet()) {
            Object[] objects = entry.getValue().toArray();
            List<Object> values = new ArrayList<>();
            for (Object obj: objects) values.add(obj);
            map.put(entry.getKey(), values);
        }
    }

    public void add(String name, Object obj) {
        List<Object> toAdd;
        if (map.containsKey(name)) {
            toAdd = map.get(name);
        } else {
            toAdd = new ArrayList<>();
        }
        toAdd.add(obj);
        map.put(name, toAdd);
    }

    public JSONObject toJSONObject() {
        JSONObject ret = new JSONObject();
        for (Map.Entry<String, List<Object>> entry: map.entrySet()) {
            String key = entry.getKey();
            List<Object> values = entry.getValue();
            if (values.size() == 1) {
                ret.put(key, values.get(0));
            } else {
                ret.put(key, values);
            }
        }
        return ret;
    }

    public Map<String, List<Object>> getMap() {
        return map;
    }
}
