package edu.ustb.seeker.archive.valuerules;

import edu.ustb.seeker.model.data.ChineseToken;
import org.json.simple.JSONObject;

import java.util.*;

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
                boolean wireCard = (i+1<rule.size() && rule.get(i+1).equals("*"));
                if (wireCard) {
                    current.addEdge(new RuleGraphEdge("", current, leftBracelet));
                    current = leftBracelet;
                    i++;
                }
            } else {
                boolean wireCard = (i+1<rule.size() && rule.get(i+1).equals("*"));
                if (wireCard) i++;
                RuleGraphNode nextNode;
                if (wireCard) nextNode = current;
                else nextNode = new RuleGraphNode();
                current.addEdge(new RuleGraphEdge(x, current, nextNode));
                current = nextNode;
            }
        }
        current.setState(RuleGraphNode.ACCEPT);
    }

    public ExtractVariablePool mapping(List<ChineseToken> tokens) {
        Queue<TravelState> queue = new LinkedList<>();
        queue.add(new TravelState(getRoot(), 0, new ExtractVariablePool(), new Stack<Integer>()));
        while (!queue.isEmpty()) {
            TravelState travelState = queue.poll();
            if (travelState.getWordIndex() >= tokens.size() && travelState.getCurrentNode().isAccept()) {
                return travelState.getVariablePool();
            }
            travelState.getCurrentNode().extend(tokens, travelState, queue);
        }
        return null;
    }
}
