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

    public Schema() {
        fields = new ArrayList<>();
    }

    public Schema(String[] names, int[] types) {
        fields = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            fields.add(new SchemaField(names[i], types[i]));
        }
    }

    public Schema(String[] names, String[] types) {
        fields = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            int type = SchemaField.NUMBER;
            if (types[i].equals("NUMBER")) {
                type = SchemaField.NUMBER;
            } else if (types[i].equals("STRING")) {
                type = SchemaField.STRING;
            }
            fields.add(new SchemaField(names[i], type));
        }
    }

    public Schema(List<String> names, List<String> types) {
        fields = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            int type = SchemaField.NUMBER;
            if (types.get(i).equals("NUMBER")) {
                type = SchemaField.NUMBER;
            } else if (types.get(i).equals("STRING")) {
                type = SchemaField.STRING;
            }
            fields.add(new SchemaField(names.get(i), type));
        }
    }

    public void append(Schema schema) {
        for (SchemaField schemaField: schema.getFields()) {
            append(new SchemaField(schemaField.getFieldName(), schemaField.getFieldType()));
        }
    }

    public void append(SchemaField schemaField) {
        if (!fields.contains(schemaField))
            fields.add(schemaField);
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
