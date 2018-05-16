package edu.ustb.seeker.model.data;

public enum posTag {
    NN("NN"), VV("VV"), NR("NR"), CC("CC"),
    OTHER("0");

    private String value;
    private posTag(String value) {
        this.value = value;
    }
}
