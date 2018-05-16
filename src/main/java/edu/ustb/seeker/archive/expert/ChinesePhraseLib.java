package edu.ustb.seeker.archive.expert;

import edu.ustb.seeker.model.data.ChineseToken;
import edu.ustb.seeker.model.data.SchemaField;
import edu.ustb.seeker.model.data.SemanticPhrase;

public interface ChinesePhraseLib {
    public boolean isStopPhrase(ChineseToken u);
    public SemanticPhrase getSemanticType(ChineseToken u);
    public double similarityOf(ChineseToken u, ChineseToken v);
}
