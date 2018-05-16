package edu.ustb.seeker.model.data;

public enum nerTag {
    PERSON("PERSON"), GPE("GPE"), NUMBER("NUMBER"), OTHER("0");

    private String value;
    private nerTag(String value) {
        this.value = value;
    }
}
