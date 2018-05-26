package edu.ustb.seeker.archive.expert;

import edu.ustb.seeker.model.data.*;
import edu.ustb.seeker.service.LuceneContact;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.*;

public class MongoTranslator {
    private ChineseGrammar chineseGrammar;
    private ChinesePhraseLib chinesePhraseLib;

    public MongoTranslator(ChineseGrammar chineseGrammar, ChinesePhraseLib chinesePhraseLib) {
        this.chineseGrammar = chineseGrammar;
        this.chinesePhraseLib = chinesePhraseLib;
    }

    public Map<SemanticNode, SchemaField> findMatch(LogicStructure logicStructure, Schema schema) {
        Map<SemanticNode, SchemaField> ret = new HashMap<>();
        for (SemanticNode sn: logicStructure.getSemanticNodes()) {
            double score = 0;
            SchemaField maxField = null;
            for ( SchemaField sf: schema.getFields()) {
                double updateScore = chinesePhraseLib.similarityOf(sn, sf);
                if (updateScore > score) {
                    score = updateScore;
                    maxField = sf;
                }
            }
            if (maxField == null)
                return null;
            else {
                ret.put(sn, maxField);
            }
        }
        return ret;
    }

    public JSONObject toMongo(LogicStructure ls, Schema schema) {
        Map<SemanticNode, SchemaField> map = findMatch(ls, schema);
        if (map == null) return new JSONObject();
        return toMongo(ls.getRoot(), map);
    }

    public JSONObject toMongo(LogicNode v, Map<SemanticNode, SchemaField> map) {
        if (v.getState() == LogicNode.END) {
            SchemaField sf = map.get(v.getContent());
            JSONObject ret = new JSONObject();
            ret.put(sf.getFieldName(), toMongo(v.getContent(), sf.getFieldType()));
            return ret;
        }
        if (v.getChildNum() == 1) {
            return toMongo(v.getChild().get(0), map);
        }

        JSONArray array = new JSONArray();
        for (LogicNode ch: v.getChild()) {
            array.add(toMongo(ch, map));
        }
        JSONObject ret = new JSONObject();
        ret.put(v.getState() == LogicNode.AND ? "$and": "$or", array);
        return ret;
    }

    public Object toMongo(SemanticNode v, int type) {
        switch(type) {
            case SchemaField.STRING:
                String ret = "";
                for (ChineseToken token: v.getValue()) {
                    ret += token.getValue();
                }
                return ret;
            case SchemaField.NUMBER:
                List<Double> num = new ArrayList<>();
                SemanticPhrase ty = v.getOperation();
                for (ChineseToken token: v.getValue()) {
                    if (chinesePhraseLib.getSemanticType(token).isOperation()) {
                        ty.combine(chinesePhraseLib.getSemanticType(token));
                    }
                    if (chinesePhraseLib.isNumber(token)) {
                        num.add(chinesePhraseLib.parser2Number(token));
                    }
                    if (chinesePhraseLib.getSemanticType(token).isRange()) {
                        ty = new SemanticPhrase("Range");
                    }
                }
                JSONObject res = new JSONObject();
                if (ty.getState() == SemanticPhrase.Range) {
                    Collections.sort(num);
                    res.put("$gte", num.get(0));
                    res.put("$lte", num.get(1));
                } else if (ty.getState() == SemanticPhrase.Gt) {
                    res.put("$gt", num.get(0));
                } else if (ty.getState() == SemanticPhrase.Lt) {
                    res.put("$lt", num.get(0));
                } else if (ty.getState() == SemanticPhrase.Gte) {
                    res.put("$gte", num.get(0));
                } else if (ty.getState() == SemanticPhrase.Lte) {
                    res.put("$lte", num.get(0));
                } else if (ty.getState() == SemanticPhrase.Equ) {
                    return num.get(0);
                }
                return res;
        }
        return null;
    }

    public List<String> translate(String input) throws IOException {
        LogicStructure logicStructure = new LogicStructure(chineseGrammar, chinesePhraseLib);
        for (String subInput: input.split("。")) {
            List<ChineseSentence> sentences = new ArrayList<>();
            for (String sentence: subInput.split("，")) {
                sentences.add(chineseGrammar.parseSentence(sentence));
            }
            logicStructure.add(sentences);
        }

        Set<String> translations = new HashSet<>();
        List<Schema> schemas = new ArrayList<>();
        LuceneContact luceneContact = new LuceneContact("luceneData/schemas");
        for (Schema s: luceneContact.allSchemas()) {
            if (findMatch(logicStructure, s) != null) {
                schemas.add(s);
            }
        }
        for (Schema s: schemas) {
            translations.add(toMongo(logicStructure, s).toJSONString());
        }

        List<String> ret = new ArrayList<>();
        for (String string: translations) {
            ret.add(string);
        }
        return ret;
    }

    public static void main(String[] args) throws IOException {
        ChineseGrammar cg = new ChineseGrammar();
        ChinesePhraseLib cpl = new PhraseLibBasedOnDict(cg);
        MongoTranslator translator = new MongoTranslator(cg, cpl);
        String input = "河流的长度小于1万米。";
        System.out.println(translator.translate(input));
    }
}
