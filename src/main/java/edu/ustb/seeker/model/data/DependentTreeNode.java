package edu.ustb.seeker.model.data;

import java.util.ArrayList;
import java.util.List;

public class DependentTreeNode {
    private ChineseToken token;
    private DependentTreeNode fatherNode;
    private String relShortName;
    private List<DependentTreeNode> children;

    public DependentTreeNode(ChineseToken token) {
        this.token = token;
        children = new ArrayList<>();
    }

    public DependentTreeNode(ChineseToken token, String relShortName) {
        this.token = token;
        this.relShortName = relShortName;
        children = new ArrayList<>();
    }

    public void addChild(DependentTreeNode child) { children.add(child); }
    public List<DependentTreeNode> getChildren() { return this.children; }

    public String getRelShortName() {
        return this.relShortName;
    }

    public void setRelShortName(String relShortName) {
        this.relShortName = relShortName;
    }

    public void setFatherNode(DependentTreeNode fatherNode) {
        this.fatherNode = fatherNode;
    }

    public DependentTreeNode getFatherNode() {
        return this.fatherNode;
    }

    public ChineseToken getToken() {
        return this.token;
    }
}
