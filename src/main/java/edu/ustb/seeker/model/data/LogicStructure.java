package edu.ustb.seeker.model.data;

import edu.ustb.seeker.archive.expert.ChineseGrammar;
import edu.ustb.seeker.archive.expert.ChinesePhraseLib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LogicStructure {
    private LogicNode root;
    private List<LogicNode> ln;
    private List<SemanticNode> ss;

    private ChineseGrammar chineseGrammar;
    private ChinesePhraseLib chinesePhraseLib;

    public LogicNode getRoot() {
        return root;
    }

    public void setRoot(LogicNode root) {
        this.root = root;
    }

    public LogicStructure(ChineseGrammar chineseGrammar, ChinesePhraseLib chinesePhraseLib) {
        ln = new ArrayList<LogicNode>();
        ss = new ArrayList<SemanticNode>();
        root = new LogicNode(LogicNode.OR);
        this.chineseGrammar = chineseGrammar;
        this.chinesePhraseLib = chinesePhraseLib;
    }

    public void add(List<ChineseSentence> sents) throws IOException {
        LogicNode node = new LogicNode(LogicNode.AND);
        root.add(node);
        ln.add(node);
        SemanticNode pre = null;
        List<ChineseToken> state = null;
        for (ChineseSentence s: sents) {
            SemanticNode st = chinesePhraseLib.parseSemanticNode(s);
            if (chinesePhraseLib.isStopPhrases(st.getValue())) {
                state = st.getField();
                continue;
            }

            if (st.isNoSub()) {
                if (pre != null) {
                    List<ChineseToken> t = new ArrayList<>();
                    t.addAll(pre.getField());
                    st.setField(t);
                } else {
                    continue;
                }
            }

            if (state != null && state.size() > 0) {
                List<ChineseToken> t = new ArrayList<>();
                t.addAll(st.getField());
                t.addAll(state);
                st.setField(t);
            }

            LogicNode curNode = new LogicNode(LogicNode.END);
            curNode.setContent(st);
            ln.add(curNode);
            ss.add(st);
            node.add(curNode);

            pre = st;
        }
    }

    public List<SemanticNode> getSemanticNodes() {
        return this.ss;
    }

//    public List<Map<SubStructure, SchemaField>> findMatch() throws IOException {
//        SchemaFinder sf = new SchemaFinder();
//        List<Schema> schemas = sf.allSchemas();
//        List<Map<SubStructure, SchemaField>> ret = new ArrayList<>();
//        for (Schema schema: schemas) {
//            Map<SubStructure, SchemaField> tmp = new HashMap<>();
//            boolean ok = true;
//            for (SubStructure sItem: ss) {
//                double score = 0;
//                SchemaField maxField = null;
//                for ( SchemaField sfItem: schema.getFields() ) {
//                    double updateScore = SchemaFinder.similarityOf(sfItem, sItem);
//                    if (updateScore > score) {
//                        score = updateScore;
//                        maxField = sfItem;
//                    }
//                }
//                if (maxField != null) {
//                    tmp.put(sItem, maxField);
//                } else {
//                    ok = false;
//                }
//            }
//            if (ok)
//                ret.add(tmp);
//        }
//        return ret;
//    }

//    public JSONObject toMongo(Map<SubStructure, SchemaField> map) {
//        return subToMongo(map, root);
//    }
//
//    private JSONObject subToMongo(Map<SubStructure, SchemaField> map, LogicNode v) {
//        if (v.state == LogicNode.END) {
//            JSONObject item = new JSONObject();
//            SchemaField sf = map.get(v.getContent());
//            item.put(sf.getFieldName(), v.getContent().toMongo(sf.getFieldType()));
//            return item;
//        }
//
//        if (v.getChildNum() == 1) {
//            return subToMongo(map, v.getChild().get(0));
//        }
//
//        JSONArray ar = new JSONArray();
//        for (LogicNode ch: v.getChild()) {
//            ar.add(subToMongo(map, ch));
//        }
//        JSONObject ret = new JSONObject();
//        String key = v.getState() == LogicNode.AND ? "$and": "$or";
//        ret.put(key, ar);
//        return ret;
//    }
}
