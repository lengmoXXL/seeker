package edu.ustb.seeker.archive.valuerules;

import java.util.Stack;

public class TravelState {
    private RuleGraphNode currentNode;
    private int wordIndex;
    private ExtractVariablePool variablePool;
    private Stack<Integer> stack;

    public TravelState(RuleGraphNode currentNode, int wordIndex, ExtractVariablePool variablePool, Stack<Integer> stack) {
        this.currentNode = currentNode;
        this.wordIndex = wordIndex;
        this.variablePool = variablePool;
        this.stack = stack;
    }

    public TravelState(TravelState travelState) {
        this(travelState.getCurrentNode(), travelState.getWordIndex(), travelState.getVariablePool(), travelState.getStack());
    }

    public RuleGraphNode getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(RuleGraphNode currentNode) {
        this.currentNode = currentNode;
    }

    public int getWordIndex() {
        return wordIndex;
    }

    public void setWordIndex(int wordIndex) {
        this.wordIndex = wordIndex;
    }

    public ExtractVariablePool getVariablePool() {
        return variablePool;
    }

    public void setVariablePool(ExtractVariablePool variablePool) {
        this.variablePool = variablePool;
    }

    public Stack<Integer> getStack() {
        return stack;
    }

    public void setStack(Stack<Integer> stack) {
        this.stack = stack;
    }
}