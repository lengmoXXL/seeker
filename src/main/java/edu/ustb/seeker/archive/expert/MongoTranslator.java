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
            if (maxField == null || score < 0.5)
                return null;
            else {
                ret.put(sn, maxField);
            }
        }
        return ret;
    }

    public Schema findMaxSchema(LogicStructure logicStructure, List<Schema> schemas) {
        Schema ret = null;
        double maxScore = 0;
        for (Schema schema: schemas) {
            boolean check = false;
            double scoreSum = 0;
            for ( SemanticNode sn: logicStructure.getSemanticNodes()) {
                double score = 0;
                SchemaField maxField = null;
                for (SchemaField sf: schema.getFields()) {
                    double updateScore = chinesePhraseLib.similarityOf(sn, sf);
                    if (updateScore > score) {
                        score = updateScore;
                        maxField = sf;
                    }
                }
                if (maxField == null || score < 0.5) {
                    check = true;
                    break;
                } else {
                    scoreSum += score;
                }
            }
            if (!check) {
                if (scoreSum > maxScore) {
                    maxScore = scoreSum;
                    ret = schema;
                }
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
            for (String sentence: subInput.split("；")) {
                sentences.add(chineseGrammar.parseSentence(sentence));
            }
            for (ChineseSentence sentence: sentences) {
                chinesePhraseLib.annotateSemanticType(sentence);
            }
            logicStructure.add(sentences);
        }

        Set<String> translations = new HashSet<>();
        List<Schema> schemas = new ArrayList<>();
        LuceneContact luceneContact = new LuceneContact("luceneData/schemas");
//        for (Schema s: luceneContact.allSchemas()) {
//            if (findMatch(logicStructure, s) != null) {
//                schemas.add(s);
//            }
//        }
//        for (Schema s: schemas) {
//            translations.add(toMongo(logicStructure, s).toJSONString());
//        }

        translations.add(toMongo(logicStructure, findMaxSchema(logicStructure, luceneContact.allSchemas())).toJSONString());

        List<String> ret = new ArrayList<>();
        for (String string: translations) {
            ret.add(string);
        }
        return ret;
    }

    public static void main(String[] args) throws IOException {
        ChineseGrammar cg = new ChineseGrammar();
        ChinesePhraseLib cpl = new PhraseLibExtendedHowNet(cg);
        MongoTranslator translator = new MongoTranslatorExtendRules(cg, cpl);
        String input[] = {
                "河流的名称是长江。",
                "河流的长度是6397000米。",
                "河流流经的地区包括青海省，西藏藏族自治区，云南省和重庆市。",
                "行政区缩写是豫。",
                "行政区人口大于8000万。",
                "行政区人口在1000万到8000万之间。",
                "行政区面积小于等于200000。",
                "行政区包含的城市有洛阳市。",
                "行政区边界的省份有陕西省。",
                "行政区包含的山包括白云山。",
                "行政区人口小于1000万；面积大于10万。",
                "行政区省会是郑州市；包含城市有三门峡市，周口市和商丘市。",
                "缩写为湘。",
                "省会城市是长沙市。",
                "边界省份包含重庆市和湖北省；人口小于9000万。行政区缩写市苏。省份名称市河南省。",
                "人口在10万以上；边界省份有河北省，辽宁省和天津市。",
                "行政区面积在100000到300000之间；人口小于等于3000万。",
                "河流流经地区包含青海省和江西省；河流长度小于900万米。",
                "行政区类型是自治区。",
                "行政区名称是内蒙古自治区；包含的山有大兴安岭。",
                "河流名字是海河。",
                "河流流经地区有河南省，湖北省，江苏省和山东省。",
                "河流的长度小于200万米；流经地区包含河北省和山西省。",
                "行政区面积小于187000；人口大于90591300。",
                "行政区面积大于187000；小于200000。",
                "河流名称是黄河；河流长度大于100000米；小于200000米。",
                "河流长度大于100000米；小于200000米；河流名称是黄河。",
                "行政区人口在8000万到20000万之间；小于14000万。边界省份有山东省。囊括的山脉有白云山。",
                "行政区类型是省；缩写是粤；行政区人口大于100万；小于1000万。行政区类型是自治区；面积超过10000万。行政区类型是直辖市。",
                "河流长度大于1000万；小于2000万。河流流经的地区包含安徽省；长度小于1000万；小于100万。",
                "行政区包含的城市有开封市；有信阳市；有周口市。行政区的人口超过1000万。行政区包含的山有嵩山和伏牛山。"
        };
        for (int i = 0; i < input.length; i++) {
            System.out.println(translator.translate(input[i]));
        }
    }
}
