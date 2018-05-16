package edu.ustb.seeker.archive.expert;

import edu.ustb.seeker.model.data.ChineseToken;
import edu.ustb.seeker.model.data.SemanticPhrase;

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
    private static Map<String, HashSet<String>> importantDict;
    private static final double alpha = 0.8;

    public PhraseLibBasedOnDict() throws IOException {
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
    }

    @Override
    public boolean isStopPhrase(ChineseToken u) {
        return stopDict.contains(u.getValue());
    }

    @Override
    public double similarityOf(ChineseToken u, ChineseToken v) {
        int distance  = g.distanceOf(u.getValue(), v.getValue());
        if (distance < 0) return 0;
        return Math.pow(alpha, distance);
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

    public static void main(String[] args) throws IOException {
        ChinesePhraseLib cp = new PhraseLibBasedOnDict();
        System.out.println(cp.getSemanticType(new ChineseToken("之间")));
    }
}
