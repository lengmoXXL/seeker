package edu.ustb.seeker.model.data;

import edu.ustb.seeker.archive.expert.ChinesePhraseLib;

import java.util.ArrayList;
import java.util.List;

public class SemanticNode {
    private SemanticPhrase operation;
    List<ChineseToken> field;
    List<ChineseToken> value;

    public SemanticNode() {
        this.operation = null;
        this.field = null;
        this.value = null;
    }

    public SemanticNode (SemanticPhrase operation, List<ChineseToken> field, List<ChineseToken> value) {
        this.operation = operation;
        this.field = field;
        this.value = value;
    }

    public boolean isNoSub() {
        return field.size() == 0;
    }

    public boolean isNone() {
        if (operation == null && (field == null || field.size() == 0) && (value == null || value.size() == 0))
            return true;
        return false;
    }

    public SemanticPhrase getOperation() {
        return operation;
    }

    public List<ChineseToken> getField() {
        return field;
    }

    public void setField(List<ChineseToken> field) {
        this.field = field;
    }

    public List<ChineseToken> getValue() {
        return value;
    }

    public void setValue(List<ChineseToken> value) {
        this.value = value;
    }
}
