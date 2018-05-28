package edu.ustb.seeker.model.data;

public class SchemaField {
    public static final int STRING = 1;
    public static final int NUMBER  = 2;
    public static final int OTHER = 3;

    public static int getType(String type) {
        if (type.equals("STRING")) {
            return STRING;
        } else if (type.equals("NUMBER")) {
            return NUMBER;
        } else {
            return OTHER;
        }
    }

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

    public String getFieldTypeName() {
        if (fieldType == NUMBER) {
            return "NUMBER";
        } else if (fieldType == STRING) {
            return "STRING";
        } else {
            return "OTHER";
        }
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
