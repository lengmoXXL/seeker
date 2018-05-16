package edu.ustb.seeker.model.data;

import java.util.ArrayList;
import java.util.List;

public class Schema {

    private List<SchemaField> fields;

    public List<SchemaField> getFields() {
        return fields;
    }

    public void setFields(List<SchemaField> fields) {
        this.fields = fields;
    }

    Schema() {
        fields = new ArrayList<>();
    }

    public Schema(List<String> names, List<Integer> types) {
        fields = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            fields.add(new SchemaField(names.get(i), types.get(i)));
        }
    }

    @Override
    public String toString() {
        String ret = "";
        for (int i = 0; i < fields.size(); i++) {
            ret += fields.get(i).toString() + "\n";
        }
        return ret;
    }
}
