package edu.ustb.seeker.model.data;

public enum nerTag {
    PERSON("PERSON"), GPE("GPE"), NUMBER("NUMBER"), MISC("MISC"), ORGANIZATION("ORGANIZATION"), O("O");

    private String value;
    nerTag(String value) {
        this.value = value;
    }
}
