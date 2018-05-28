package edu.ustb.seeker.archive.valuerules;

import java.util.ArrayList;
import java.util.List;

public class RuleGraphNode {
    private List<RuleGraphEdge> edges;

    public RuleGraphNode() {
        edges = new ArrayList<>();
    }

    public void addEdge(RuleGraphEdge edge) {
        edges.add(edge);
    }

    public RuleGraphNode travel(String t, boolean wireCard) {
        for (RuleGraphEdge edge: edges) {
            if (edge.same(t)) {
                if (wireCard) {
                    if (edge.getTo() == this) {
                        return this;
                    }
                } else {
                    return edge.getTo();
                }
            }
        }
        return null;
    }
}
