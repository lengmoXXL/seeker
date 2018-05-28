package edu.ustb.seeker.archive.valuerules;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class RuleGraph {
    private List<RuleGraphNode> nodes;
    private List<RuleGraphEdge> edges;

    public RuleGraph() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();

        nodes.add(new RuleGraphNode());
    }

    public RuleGraphNode getRoot() {
        return nodes.get(0);
    }

    public void addRule(List<String> rule) {
        RuleGraphNode current = getRoot();
        Stack<RuleGraphNode> stack = new Stack<>();
        for (int i = 0; i < rule.size(); i++) {
            String x = rule.get(i);
            if (x.equals("(")) {
                stack.push(current);
            } else if (x.equals(")")) {
                RuleGraphNode leftBracelet = stack.pop();
                current.addEdge(new RuleGraphEdge("", current, leftBracelet));
            } else {
                boolean wireCard = (i+1<rule.size() && rule.get(i+1).equals("*"));
                if (wireCard) i++;
                RuleGraphNode nextNode = current.travel(x, wireCard);
                if (nextNode == null) {
                    if (wireCard) nextNode = current;
                    else nextNode = new RuleGraphNode();
                    current.addEdge(new RuleGraphEdge(x, current, nextNode));
                }
                current = nextNode;
            }
        }
    }
}
