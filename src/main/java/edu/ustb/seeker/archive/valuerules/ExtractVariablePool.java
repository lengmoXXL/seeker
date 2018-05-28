package edu.ustb.seeker.archive.valuerules;

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

    public void add(String name, Object obj) {
        List<Object> toAdd;
        if (map.containsKey(name)) {
            toAdd = map.get(name);
        } else {
            toAdd = new ArrayList<>();
        }
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
}
