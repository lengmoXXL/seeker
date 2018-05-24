package edu.ustb.seeker.model.data;

import java.util.ArrayList;
import java.util.List;

public class LogicNode {
    static public final int AND = 1;
    static public final int OR = 2;
    static public final int END = 0;

    private int state;
    private List<LogicNode> ch;

    private SemanticNode content;

    public LogicNode(int s) {
        this.state = s;
        ch = new ArrayList<>();
    }

    public List<LogicNode> getChild() {
        return this.ch;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getChildNum() {
        return this.ch.size();
    }

    public void add(LogicNode ln) {
        ch.add(ln);
    }

    public SemanticNode getContent() {
        return content;
    }

    public void setContent(SemanticNode content) {
        this.content = content;
    }
}