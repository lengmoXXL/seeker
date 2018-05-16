package edu.ustb.seeker.archive.expert;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MaxCostMaxFlow {
    private class Edge {
        private int from, to;
        private int flow;
        private double cost;
        private Edge nxt, rev;

        public void flowFlow(int flow) {
            setFlow(getFlow() - flow);
            rev.setFlow(rev.getFlow() + flow);
        }

        public Edge(int from, int to, int flow, double cost, Edge nxt) {
            this.from = from;
            this.to = to;
            this.flow = flow;
            this.cost = cost;
            this.nxt = nxt;
        }

        public int getFrom() {
            return from;
        }

        public void setFrom(int from) {
            this.from = from;
        }

        public int getTo() {
            return to;
        }

        public void setTo(int to) {
            this.to = to;
        }

        public int getFlow() {
            return flow;
        }

        public void setFlow(int flow) {
            this.flow = flow;
        }

        public double getCost() {
            return cost;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }

        public Edge getNxt() {
            return nxt;
        }

        public void setNxt(Edge nxt) {
            this.nxt = nxt;
        }

        public Edge getRev() {
            return rev;
        }

        public void setRev(Edge rev) {
            this.rev = rev;
        }
    }

    public static final int INF = 0x3f3f3f3f;
    public static final double EPS = 1e-8;

    private List<Edge> es;
    private List<Edge> head;
    private int num, s, t;

    public MaxCostMaxFlow() {
        es = new ArrayList<>();
        head = new ArrayList<>();
    }

    public void setVertex(int num, int s, int t) {
        this.s = s;
        this.t = t;
        this.num = num ;
        for (int i = 0; i < this.num; i++) {
            head.add(null);
        }
    }

    public void addEdge(int u, int v, int flow, double cost) {
        Edge e1 = new Edge(u, v, flow, - cost, head.get(u));
        Edge e2 = new Edge(v, u, 0, cost, head.get(v));
        e1.setRev(e2);
        e2.setRev(e1);
        es.add(e1);
        head.set(u, e1);
        es.add(e2);
        head.set(v, e2);
    }

    private boolean SPFA(int s, int t, double[] dis, boolean[] vis, Edge[] pre, Queue<Integer> q) {
        for (int i = 0; i < this.num; i++) {
            dis[i] = INF;
            vis[i] = false;
            pre[i] = null;
        }
        q.clear();

        q.offer(s);
        dis[s] = 0;
        vis[s] = true;
        while (!q.isEmpty()) {
            int f = q.poll();
            for (Edge e = head.get(f); e != null; e = e.getNxt()) {
                if (e.getFlow() == 0) continue;
                if (dis[e.getTo()] >= INF-EPS  || dis[e.getTo()] > dis[f] + e.getCost()) {
                    dis[e.getTo()] = dis[f] + e.getCost();
                    pre[e.getTo()] = e;
                    if (!vis[ e.getTo( ) ]) {
                        vis[ e.getTo() ] = true;
                        q.offer( e.getTo() );
                    }
                }
            }
            vis[f] = false;
        }

        if (dis[t] >= INF-EPS) return false;
        return true;
    }

    public double execute() {
        double[] dis = new double[this.num];
        boolean[] vis = new boolean[this.num];
        Edge[] pre = new Edge[this.num];
        Queue<Integer> q = new LinkedList<>();

        double ret = 0;
        while (SPFA(s, t, dis, vis, pre, q)) {
            int curV = t;
            int minFlow = -1;
            while (curV != s) {
                minFlow = minFlow < 0 ? pre[curV].getFlow(): Math.min(minFlow, pre[curV].getFlow());
                curV = pre[curV].getFrom();
            }

            curV = t;
            while (curV != s) {
                ret += pre[curV].getCost() * minFlow;
                pre[curV].flowFlow(minFlow);
                curV = pre[curV].getFrom();
            }
        }
        return -ret;
    }


    public static void main(String[] args) {
        MaxCostMaxFlow mcmf = new MaxCostMaxFlow();
        mcmf.setVertex(4, 0, 3);
        mcmf.addEdge(0, 1, 1, 1);
        mcmf.addEdge(0, 2, 1, 1);
        mcmf.addEdge(1, 3, 1, 1);
        mcmf.addEdge(2, 3, 1, 1);
        mcmf.addEdge(1, 3, 1, 2);
        System.out.println(mcmf.execute());
    }
}

