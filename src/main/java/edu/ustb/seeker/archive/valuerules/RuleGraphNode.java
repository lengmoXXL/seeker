package edu.ustb.seeker.archive.valuerules;

import edu.ustb.seeker.archive.expert.ChinesePhraseLib;
import edu.ustb.seeker.model.data.ChineseToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class RuleGraphNode {
    public static final int ACCEPT = 1;
    public static final int PATH = 0;
    public static final int REJECT = -1;
    private static ChinesePhraseLib chinesePhraseLib;
    public static void initChinesePhraseLib(ChinesePhraseLib chinesePhraseLib) {
        RuleGraphNode.chinesePhraseLib = chinesePhraseLib;
    }

    private List<RuleGraphEdge> edges;
    private int state;

    public RuleGraphNode() {
        edges = new ArrayList<>();
        state = PATH;
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

    public void extend(List<ChineseToken> tokens, TravelState travelState, Queue<TravelState> queue) {
        for (RuleGraphEdge e: edges) {
            switch (e.getType()) {
                case RuleGraphEdge.WORD:
                case RuleGraphEdge.SEMANTIC:
                case RuleGraphEdge.NER:
                case RuleGraphEdge.ALL:
                    if (travelState.getWordIndex() < tokens.size() && e.match(tokens.get(travelState.getWordIndex()))) {
                        Stack<Integer> sameStack = new Stack<>();
                        for (Object x: travelState.getStack().toArray()) {
                            sameStack.push((int)x);
                        }
                        queue.add(new TravelState(e.getTo(),
                                travelState.getWordIndex()+1,
                                new ExtractVariablePool(travelState.getVariablePool()),
                                sameStack));
                    }
                    break;
                case RuleGraphEdge.EMPTY:
                    Stack<Integer> sameStack = new Stack<>();
                    for (Object x: travelState.getStack().toArray()) {
                        sameStack.push((int)x);
                    }
                    queue.add(new TravelState(e.getTo(),
                            travelState.getWordIndex(),
                            new ExtractVariablePool(travelState.getVariablePool()),
                            sameStack));

                    break;
                case RuleGraphEdge.LEFT:
                    Stack<Integer> addStack = new Stack<>();
                    for (Object x: travelState.getStack().toArray()) {
                        addStack.push((int)x);
                    }
                    addStack.push(travelState.getWordIndex());
                    queue.add(new TravelState(e.getTo(),
                            travelState.getWordIndex(),
                            new ExtractVariablePool(travelState.getVariablePool()),
                            addStack));
                    break;
                case RuleGraphEdge.RIGHT:
                    Stack<Integer> delStack = new Stack<>();
                    for (Object x: travelState.getStack().toArray()) {
                        delStack.push((int)x);
                    }
                    int previous = delStack.pop();
                    ExtractVariablePool extractVariablePool = new ExtractVariablePool(travelState.getVariablePool());
                    String concatTokens = "";
                    for (int i = previous; i < travelState.getWordIndex(); i++)
                        concatTokens += tokens.get(i).getValue();
                    extractVariablePool.add(e.getExtractVariableName(),
                            chinesePhraseLib.parse(concatTokens, e.getTransformType()));
                    queue.add(new TravelState(e.getTo(),
                            travelState.getWordIndex(),
                            extractVariablePool,
                            delStack));
                    break;
                default:
                    break;
            }
        }
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isAccept() {
        return this.state == ACCEPT;
    }
}
