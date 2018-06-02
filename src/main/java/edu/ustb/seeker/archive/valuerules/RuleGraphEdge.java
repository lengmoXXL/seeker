package edu.ustb.seeker.archive.valuerules;

import edu.ustb.seeker.model.data.ChineseToken;
import edu.ustb.seeker.model.data.SchemaField;

public class RuleGraphEdge {
    public static final int EMPTY = 0;
    public static final int WORD = 1;
    public static final int SEMANTIC = 2;
    public static final int NER = 3;
    public static final int ALL = 4;

    public static final int LEFT = 5;
    public static final int RIGHT = 6;
    public static final int UNKNOWN = -1;

    private RuleGraphNode from, to;

    private int type;
    private String tag;
    private int transformType;
    private String extractVariableName;

    private int getTypeBy(String t) {
        t = t.trim();
        if (t.length() <= 0) return EMPTY;
        if (t.charAt(0) == '\"') return WORD;
        if (t.charAt(0) == '{') return SEMANTIC;
        if (t.charAt(0) == '[') return NER;
        if (t.charAt(0) == '?') return ALL;
        if (t.charAt(0) == '<') return LEFT;
        if (t.charAt(0) == '>') return RIGHT;
        return UNKNOWN;
    }

    public RuleGraphEdge(String t, RuleGraphNode from, RuleGraphNode to) {
        this.from = from;
        this.to = to;
        type = getTypeBy(t);

        if (type == EMPTY) return;
        if (type == WORD || type == SEMANTIC || type == NER) this.tag = t.substring(1, t.length()-1);
        if (type == ALL) return;
        if (type == LEFT) return;
        if (type == RIGHT) {
            String[] splits = t.substring(1, t.length()).split("/");
            extractVariableName = "";
            if (splits.length > 1) {
                extractVariableName = splits[0];
                transformType = SchemaField.getType(splits[1]);
            } else {
                transformType = SchemaField.getType(splits[0]);
            }
        }
    }

    public boolean same(String t) {
        if (this.type != getTypeBy(t)) return false;
        if (type == WORD || type == SEMANTIC || type == NER)
            return this.tag == t.substring(1, t.length()-1);
        if (type == RIGHT) {
            String[] splits = t.substring(1, t.length()).split("/");
            if (splits.length > 1) {
                return this.extractVariableName == splits[0]
                    && this.transformType == SchemaField.getType(splits[1]);
            } else {
                return this.transformType == SchemaField.getType(splits[0]);
            }
        }

        return true;
    }

    public boolean match(ChineseToken token) {
        switch (type) {
            case EMPTY:
                return false;
            case ALL:
                if ("，。、：！@#￥%……&*（）".contains(token.getValue())) return false;
                if (token.getNer().equals("NUMBER")) return false;
                if (!token.getSemanticPhrase().equals("Other")) return false;
                return true;
            case WORD:
                return token.getValue().equals(tag);
            case SEMANTIC:
                return token.getSemanticPhrase().equals(tag);
            case NER:
                return token.getNer().equals(tag);
            default:
                return false;
        }
    }

    public RuleGraphNode getTo() {
        return this.to;
    }

    public int getType() {
        return type;
    }

    public int getTransformType() {
        return transformType;
    }

    public String getExtractVariableName() {
        return extractVariableName;
    }
}
