package edu.ustb.seeker.model.data;

import edu.stanford.nlp.trees.TypedDependency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DependentTree {
    List<DependentTreeNode> nodes;

    public DependentTree(List<ChineseToken> tokens, Collection<TypedDependency> tdl) {
        nodes = new ArrayList<>();
        nodes.add(new DependentTreeNode(new ChineseToken("ROOT")));
        for (ChineseToken token: tokens) {
            nodes.add(new DependentTreeNode(token));
        }

        // td中的index与token中的index一一对应
        for (TypedDependency td: tdl) {
            nodes.get(td.dep().index()).setRelShortName(td.reln().getShortName());
            nodes.get(td.gov().index()).addChild(nodes.get(td.dep().index()));
            nodes.get(td.dep().index()).setFatherNode(nodes.get(td.gov().index()));
        }
    }

    public DependentTreeNode getRoot() { return nodes.get(0); }

}
