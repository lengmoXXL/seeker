package edu.ustb.seeker.archive.expert;

import edu.ustb.seeker.model.data.ChineseToken;
import edu.ustb.seeker.archive.hownet.WordSimilarity;

import java.io.IOException;

public class PhraseLibExtendedHowNet extends PhraseLibBasedOnDict {
    public PhraseLibExtendedHowNet(ChineseGrammar chineseGrammar) throws IOException {
        super(chineseGrammar);
    }

    @Override
    public double similarityOf(ChineseToken u, ChineseToken v) {
        return WordSimilarity.simWord(u.getValue(), v.getValue());
    }
}
