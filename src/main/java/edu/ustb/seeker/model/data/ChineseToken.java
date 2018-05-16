package edu.ustb.seeker.model.data;

import edu.ustb.seeker.archive.expert.ChinesePhraseLib;

public class ChineseToken {
    public static final int STOP = 1;

    private int index;
    private String value;

    private nerTag ner;
    private posTag pos;

    private ChinesePhraseLib phraseLib;

    public ChineseToken(int index, String value, nerTag ner, posTag pos, ChinesePhraseLib phraseLib) {
        this.index = index;
        this.value = value;
        this.ner = ner;
        this.pos = pos;
        this.phraseLib = phraseLib;
    }

    public ChineseToken(String value) {
        this.index = -1;
        this.value = value;
        this.ner = nerTag.OTHER;
        this.pos = posTag.OTHER;
        this.phraseLib = null;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public nerTag getNer() {
        return ner;
    }

    public void setNer(nerTag ner) {
        this.ner = ner;
    }

    public posTag getPos() {
        return pos;
    }

    public void setPos(posTag pos) {
        this.pos = pos;
    }

    public ChinesePhraseLib getPhraseLib() {
        return phraseLib;
    }

    public void setPhraseLib(ChinesePhraseLib phraseLib) {
        this.phraseLib = phraseLib;
    }
}
