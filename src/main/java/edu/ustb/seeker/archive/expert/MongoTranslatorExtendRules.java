package edu.ustb.seeker.archive.expert;

import edu.ustb.seeker.archive.valuerules.ExtractorByRule;
import edu.ustb.seeker.model.data.SemanticNode;


import java.io.IOException;


public class MongoTranslatorExtendRules extends MongoTranslator {
    private ExtractorByRule extractorByRule;

    public MongoTranslatorExtendRules(ChineseGrammar chineseGrammar, ChinesePhraseLib chinesePhraseLib) throws IOException {
        super(chineseGrammar, chinesePhraseLib);
        extractorByRule = new ExtractorByRule(chinesePhraseLib);
    }

    @Override
    public Object toMongo(SemanticNode v, int type) {
        return extractorByRule.mapping(v.getValue(), type);
    }
}
