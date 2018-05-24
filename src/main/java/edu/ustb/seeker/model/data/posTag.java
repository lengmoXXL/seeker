package edu.ustb.seeker.model.data;

public enum posTag {
    AD("AD"),
    CC("CC"), CD("CD"), CS("CS"),
    DEV("DEV"), DT("DT"), DEG("DEG"), DEC("DEC"),
    LC("LC"),
    JJ("JJ"),
    M("M"), MSP("MSP"),
    NT("NT"), NR("NR"), NN("NN"),
    PU("PU"), PN("PN"), P("P"),
    VV("VV"), VC("VC"), VA("VA"), VE("VE"),
    SP("SP"),
    O("O");

    private String value;
    posTag(String value) {
        this.value = value;
    }
}
