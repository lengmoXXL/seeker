package edu.ustb.seeker.model.data;

public class SchemaField {
    static public final int STRING = 1;
    static public final int NUMBER  = 2;

    private String fieldName;
    private int fieldType;

    public SchemaField(String name, int type) {
        this.fieldName = name;
        this.fieldType = type;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public int getFieldType() {
        return fieldType;
    }

    public void setFieldType(int fieldType) {
        this.fieldType = fieldType;
    }

    @Override
    public String toString() {
        return fieldName + ":" + fieldType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof  SchemaField) {
            SchemaField schemaField = (SchemaField)obj;
            if (fieldName.equals(schemaField.fieldName) &&
                    fieldType == schemaField.fieldType) {
                return true;
            }
        }
        return super.equals(obj);
    }
}
