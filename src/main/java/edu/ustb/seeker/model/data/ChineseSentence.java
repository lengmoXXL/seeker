package edu.ustb.seeker.model.data;

import edu.ustb.seeker.archive.expert.ChineseGrammar;
import edu.ustb.seeker.archive.expert.ChinesePhraseLib;

import java.util.List;

public class ChineseSentence {
    private List<ChineseToken> tokens;
    private DependentTree dependentTree;

    public ChineseSentence(List<ChineseToken> tokens, DependentTree dependentTree) {
        this.tokens = tokens;
        this.dependentTree = dependentTree;
    }

    public DependentTree getDependentTree() {
        return dependentTree;
    }

    public List<ChineseToken> getTokens() {
        return tokens;
    }
}
