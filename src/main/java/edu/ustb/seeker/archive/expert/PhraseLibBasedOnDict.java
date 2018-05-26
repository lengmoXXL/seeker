package edu.ustb.seeker.archive.expert;

import edu.ustb.seeker.model.data.*;
import edu.ustb.seeker.service.LuceneContact;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.util.*;

public class PhraseLibBasedOnDict implements ChinesePhraseLib {
    private class State {
        private String string;
        private int distance;

        State(String string, int distance) {
            this.string = string;
            this.distance = distance;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }
    }
    private class TokenSimilarEdge {
        private String from, to;
        private TokenSimilarEdge next;

        public TokenSimilarEdge(String from, String to, TokenSimilarEdge next) {
            this.from = from;
            this.to = to;
            this.next = next;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public TokenSimilarEdge getNext() {
            return next;
        }

        public void setNext(TokenSimilarEdge next) {
            this.next = next;
        }
    }
    private class TokenSimilarGraph {
        List<TokenSimilarEdge> es;
        Map<String, TokenSimilarEdge> head;

        TokenSimilarGraph() {
            es = new ArrayList<TokenSimilarEdge>();
            head = new HashMap<String, TokenSimilarEdge>();
        }

        public void addEdge(String from, String to) {
            if (!head.containsKey(from)) {
                head.put(from, null);
            }
            TokenSimilarEdge e = new TokenSimilarEdge(from, to, head.get(from));
            es.add(e);
            head.put(from, e);
        }

        public int distanceOf(String u, String v) {
            Queue<State> q = new LinkedList<State>();
            Map<String, Boolean> vis = new HashMap<String, Boolean>();

            q.add(new State(u, 0));
            vis.put(u, true);
            while (!q.isEmpty()) {
                State p = q.poll();
                if (p.getString().equals(v)) {
                    return p.getDistance();
                }
                if (!head.containsKey(p.getString())) {
                    continue;
                }
                for (TokenSimilarEdge e = head.get(p.getString()); e != null; e = e.getNext()) {
                    if (!vis.containsKey(e.getTo())) {
                        vis.put(e.getTo(), true);
                        q.add(new State(e.getTo(), p.getDistance()+1));
                    }
                }
            }
            return -1;
        }
    }

    private TokenSimilarGraph g;
    private HashSet<String> stopDict;
    private LuceneContact luceneContact;
    private static Map<String, HashSet<String>> importantDict;
    private static final double alpha = 0.8;

    private ChineseGrammar chineseGrammar;

    public PhraseLibBasedOnDict(ChineseGrammar chineseGrammar) throws IOException {
        g = new TokenSimilarGraph();
        stopDict = new HashSet<String>();

        File synoFile = new File("settings/syno.txt");
        Scanner synoScan = new Scanner(synoFile);
        while (synoScan.hasNext()) {
            String line = synoScan.nextLine();
            String[] words = line.split(",");
            g.addEdge(words[0], words[1]);
            g.addEdge(words[1], words[0]);
        }
        synoScan.close();

        File stopFile = new File("settings/stop_phrase.txt");
        Scanner stopScan = new Scanner(stopFile);
        while (stopScan.hasNext()) {
            String phrase = stopScan.nextLine();
            stopDict.add(phrase);
        }
        stopScan.close();

        File importantFile = new File("settings/important_phrase.properties");
        Properties importantProp = new Properties();
        importantProp.load(new InputStreamReader(new BufferedInputStream(new FileInputStream(importantFile)), "gbk"));
        importantDict = new HashMap<String, HashSet<String>>();


        Set<Object> keys = importantProp.keySet();
        for (Object key: keys) {
            HashSet<String> words = new HashSet<String>();
            String strList = (String) importantProp.get(key);
            List<String> lists = Arrays.asList(strList.split(","));
            for (String word: lists) {
                words.add(word);
            }
            importantDict.put(key.toString(), words);
        }

        this.chineseGrammar = chineseGrammar;

        this.luceneContact = new LuceneContact("luceneData/schemas");
    }

    private void travel(DependentTreeNode v, List<ChineseToken> toAdd) {
        if (!isStopPhrase(v.getToken()))
            toAdd.add(v.getToken());
        for (DependentTreeNode u: v.getChildren()) {
            travel(u, toAdd);
        }
    }

    @Override
    public SemanticNode parseSemanticNode(ChineseSentence sentence) {
        DependentTreeNode root = sentence.getDependentTree().getRoot();
        List<ChineseToken> tokens = sentence.getTokens();
        if (root.getChildren().size() == 0) {
            return new SemanticNode();
        }
        List<ChineseToken> field = new ArrayList<>();
        List<ChineseToken> value = new ArrayList<>();
        SemanticPhrase operation = null;
        DependentTreeNode centerNode = root.getChildren().get(0);
        if (getSemanticType(centerNode.getToken()).isOperation()) {
            operation = getSemanticType(centerNode.getToken());
            for (DependentTreeNode ch: centerNode.getChildren()) {
                if (ch.getRelShortName().contains("neg")) {
                    operation.flip();
                } else if (ch.getRelShortName().contains("top")) {
                    travel(ch, field);
                } else if (ch.getRelShortName().contains("subj")) {
                    travel(ch, field);
                }
            }
        } else {
            for (ChineseToken token: sentence.getTokens()) {
                if (!isStopPhrase(token)) {
                    field.add(token);
                }
            }
        }
        for (ChineseToken token: tokens) {
            if (!field.contains(token)) {
                value.add(token);
            }
        }
        return new SemanticNode(operation, field, value);
    }

    private Schema extractData(JSONObject document, String prefix) {
        Schema ret = new Schema();
        Set<String> keys = document.keySet();
        for (String key: keys) {
            Object jsonObj = document.get(key);
            if (jsonObj instanceof JSONObject) {
                ret.append(extractData((JSONObject) jsonObj, prefix + key));
            } else if (jsonObj instanceof JSONArray) {
                for (Object obj: (JSONArray) jsonObj) {
                    if (obj instanceof String) {
                        ret.append(new SchemaField(prefix + key, SchemaField.STRING));
                    } else if (obj instanceof Double) {
                        ret.append(new SchemaField(prefix + key, SchemaField.NUMBER));
                    } else if (obj instanceof Integer) {
                        ret.append(new SchemaField(prefix + key, SchemaField.NUMBER));
                    } else if (obj instanceof JSONObject) {
                        ret.append(extractData((JSONObject)obj, prefix));
                    }
                }
            } else if (jsonObj instanceof String) {
                ret.append(new SchemaField(prefix + key, SchemaField.STRING));
            } else if (jsonObj instanceof Double) {
                ret.append(new SchemaField(prefix + key, SchemaField.NUMBER));
            } else if (jsonObj instanceof Integer) {
                ret.append(new SchemaField(prefix + key, SchemaField.NUMBER));
            } else if (jsonObj instanceof Long) {
                ret.append(new SchemaField(prefix + key, SchemaField.NUMBER));
            }
        }
        return ret;
    }

    private List<Schema> extractData(String data) {
        Object jsonObj = JSONValue.parse(data);
        List<Schema> schemas = new ArrayList<>();
        if (jsonObj instanceof JSONArray) {
            JSONArray documents = (JSONArray) jsonObj;
            for (Object obj: documents) {
                schemas.add(extractData((JSONObject) obj, ""));
            }
        } else {
            JSONObject document = (JSONObject) jsonObj;
            schemas.add(extractData(document, ""));
        }
        return schemas;
    }

    @Override
    public void updateSchemas(String data) throws IOException {
        List<Schema> schemas = extractData(data);
        for (Schema schema: schemas) {
            if (!luceneContact.exist(schema)) {
                luceneContact.addSchema(schema);
            }
        }
    }

    @Override
    public boolean isStopPhrase(ChineseToken u) {
        return stopDict.contains(u.getValue());
    }

    @Override
    public boolean isStopPhrases(List<ChineseToken> tokens) {
        for (ChineseToken token: tokens) {
            if (!isStopPhrase(token)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public double similarityOf(ChineseToken u, ChineseToken v) {
        int distance  = g.distanceOf(u.getValue(), v.getValue());
        if (distance < 0) return 0;
        return Math.pow(alpha, distance);
    }

    @Override
    public double similarityOf(SemanticNode sn, SchemaField sf) {
        List<String> fieldTokens = new ArrayList<>();
        for (String subField: sf.getFieldName().split("\\.")) {
            for (String token: chineseGrammar.segmentStringList(subField)) {
                fieldTokens.add(token);
            }
        }
        MaxCostMaxFlow flowG = new MaxCostMaxFlow();
        int leftNum = fieldTokens.size();
        int rightNum = sn.getField().size();
        flowG.setVertex(leftNum+rightNum + 2, leftNum+rightNum, leftNum+rightNum+1);
        for (int i = 0; i < fieldTokens.size(); i++) {
            flowG.addEdge(leftNum+rightNum, i, 1, 0);
        }
        for (int i = 0; i < sn.getField().size(); i++) {
            flowG.addEdge(i+leftNum, leftNum+rightNum+1, 1, 0);
        }
        for (int i = 0; i < fieldTokens.size(); i++) {
            for (int j = 0; j < sn.getField().size(); j++) {
                flowG.addEdge(i, j+leftNum, 1, similarityOf(new ChineseToken(fieldTokens.get(i)), sn.getField().get(j)));
            }
        }
        return flowG.execute();
    }

    @Override
    public SemanticPhrase getSemanticType(ChineseToken u) {
        String test = u.getValue();
        for (Map.Entry<String, HashSet<String>> entry: importantDict.entrySet()) {
            if (entry.getValue().contains(test)) {
                return new SemanticPhrase(entry.getKey());
            }
        }
        return new SemanticPhrase("Other");
    }

    @Override
    public boolean isNumber(ChineseToken u) {
        return u.getNer() == nerTag.NUMBER;
    }

    @Override
    public double parser2Number(ChineseToken u) {
        if (isNumeric(u.getValue())) {
            return Double.parseDouble(u.getValue());
        } else {
            return (double)chineseNumber2Int(u.getValue());
        }
    }

    public boolean isNumeric(String str) {
        boolean f = false;
        for (int i = 0; i < str.length(); i++) {
            int chr = str.charAt(i);
            if (chr < '0' || chr > '9') {
                return false;
            }
            if (chr == '.') {
                if (f) {
                    f = false;
                } else {
                    return false;
                }
                continue;
            }
        }
        return true;
    }

    public int chineseNumber2Int(String str) {
        int result = 0;
        int temp = 1;//存放一个单位的数字如：十万
        int alpha = 0;
        int count = 0;//判断是否有chArr
        char[] cnArr = new char[]{'一','二','三','四','五','六','七','八','九'};
        char[] chArr = new char[]{'十','百','千','万','亿'};
        for (int i = 0; i < str.length(); i++) {
            boolean b = true;//判断是否是chArr
            char c = str.charAt(i);
            for (int j = 0; j < cnArr.length; j++) {//非单位，即数字
                if (cnArr[j] == c) {
                    if(0 != count){//添加下一个单位之前，先把上一个单位值添加到结果中
                        result += temp;
                        temp = 1;
                        count = 0;
                    }
                    // 下标+1，就是对应的值
                    temp = j + 1;
                    b = false;
                    break;
                }
            }
            if ('0' <= c && c <= '9') {
                alpha = alpha * 10 + c - '0';
                b = false;
            }
            if(b){//单位{'十','百','千','万','亿'}
                temp = alpha;
                alpha = 0;
                for (int j = 0; j < chArr.length; j++) {
                    if (chArr[j] == c) {
                        switch (j) {
                            case 0:
                                temp *= 10;
                                break;
                            case 1:
                                temp *= 100;
                                break;
                            case 2:
                                temp *= 1000;
                                break;
                            case 3:
                                temp *= 10000;
                                break;
                            case 4:
                                temp *= 100000000;
                                break;
                            default:
                                break;
                        }
                        count++;
                    }
                }
            }
            if (i == str.length() - 1) {//遍历到最后一个字符
                result += temp;
            }
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        ChineseGrammar cg = new ChineseGrammar();
        PhraseLibBasedOnDict cp = new PhraseLibBasedOnDict(cg);
        String json = "{\"河流名称\": \"长江\",\"河流长度(米)\": 6397000,\"河流流经地区\": [\"青海省\", \"西藏省\", \"四川省\", \"云南省\", \"重庆市\", \"湖北省\", \"湖南省\", \"江西省\", \"安徽省\", \"江苏省\", \"上海市\"] }";
        cp.extractData(json);
//        System.out.println(cp.getSemanticType(new ChineseToken("之间")));
    }
}
