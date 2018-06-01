package edu.ustb.seeker.archive.expert;

import edu.ustb.seeker.model.data.*;

import java.io.IOException;
import java.util.List;

public interface ChinesePhraseLib {
    public boolean isStopPhrase(ChineseToken u);
    public boolean isStopPhrases(List<ChineseToken> tokens);

    public SemanticNode parseSemanticNode(ChineseSentence sentence);

    public SemanticPhrase getSemanticType(ChineseToken u);
    public void annotateSemanticType(ChineseSentence sentence);

    public Object parse(String string, int type);

    public void updateSchemas(String data) throws IOException;

    public double similarityOf(ChineseToken u, ChineseToken v);
    public double similarityOf(SemanticNode sn, SchemaField sf);

    public boolean isNumber(ChineseToken u);
    public double parser2Number(ChineseToken u);
}
