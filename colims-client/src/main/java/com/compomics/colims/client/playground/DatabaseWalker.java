package com.compomics.colims.client.playground;

import java.util.LinkedList;
import java.util.Set;

/**
 * Created by Davy Maddelein on 19/08/2015.
 */
public class DatabaseWalker {

    private Heuristic heuristic;
    private State initState;

    private Frontier frontier;
    private Set<State> exploredStates;
    private Benchmark benchmark;
    protected boolean cycleDetection;

    public DatabaseWalker(State initState,Heuristic heuristic){
        this.initState = initState;
        this.heuristic = heuristic;

    }

    protected Node run() {
        Node initialNode = new Node(initState);
        Node solution = null;
        int currentCostBound = heuristic.compute(initState);

        while (solution == null) {
            exploredStates.clear();
            exploredStates.add(initState);
            solution = depthFirstSearch(initialNode, currentCostBound);
            currentCostBound += 2;
        }

        return solution;
    }

    private Node depthFirstSearch(Node currentNode, int currentCostBound) {
        State state = currentNode.getState();
        if (state.isGoal()) {
            return currentNode;
        }
        benchmark.increaseCycleCount();

        for (StateChange stateChange : state.getPossibleStateChanges()) {
            State followState = stateChange.getFollowState();
            int cost = currentNode.getCost() + stateChange.getCost();

            if (cost+heuristic.compute(followState) <= currentCostBound) {
                if (!cycleDetection || !exploredStates.contains(followState)) {
                    Node nextNode = new Node(followState, currentNode);
                    nextNode.setCost(cost);
                    exploredStates.add(followState);

                    Node possibleSolution = depthFirstSearch(nextNode, currentCostBound);
                    if (possibleSolution != null) {
                        return possibleSolution;
                    }
                }
            }
        }
        return null;
    }


    private interface Heuristic {

        public int compute(State state);

    }


    private interface State {

        LinkedList<StateChange> getPossibleStateChanges();
        boolean isGoal();

        boolean equals(Object obj);
        int hashCode();
        String toString();
    }

    private static class StateChange {

        private State initialState, followState;
        private int cost;

        public StateChange(State initialState, State followState) {
            this.initialState = initialState;
            this.followState = followState;
        }

        public StateChange(State initialState, State followState, int cost) {
            this(initialState, followState);
            this.cost = cost;
        }

        public int getCost() {
            return cost;
        }

        public State getFollowState() {
            return followState;
        }

        public State getInitialState() {
            return initialState;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }

        public void setFollowState(State followState) {
            this.followState = followState;
        }

        public void setInitialState(State initialState) {
            this.initialState = initialState;
        }
    }

    private class Node{
        private Node parent;
        private State state;
        private int cost;

        public Node(State state) {
            this.state = state;
        }

        public Node(State state, Node parent) {
            this.parent = parent;
            this.state = state;
        }

        public int getCost() {
            return cost;
        }

        public Node getParent() {
            return parent;
        }

        public State getState() {
            return state;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        public void setState(State state) {
            this.state = state;
        }

        @Override
        public boolean equals(Object obj) {
            return (obj instanceof Node) && ((Node)obj).state.equals(state);
        }

        @Override
        public int hashCode() {
            return state.hashCode();
        }
    }


    public class Benchmark {
        private long startTime;
        private long elapsedTime;
        private long cycles;

        public void startTimeMeasurement() {
            startTime = System.nanoTime();
        }

        public void stopTimeMeasurement() {
            elapsedTime = System.nanoTime() - startTime;
        }

        public long getElapsedTime() {
            return elapsedTime;
        }

        public void increaseCycleCount() {
            cycles++;
        }

        public long getCycleCount() {
            return cycles;
        }

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();
            s.append("Cycles in loop: ");
            s.append(cycles);
            s.append("\n");
            s.append("Time elapsed: ");
            s.append(elapsedTime/1000/1000);
            s.append(" ms");
            return s.toString();
        }
    }

    public interface Frontier {

        public Node remove();
        public void add(Node node);
        public boolean isEmpty();

    }
}
