package ru.vsu.cs.course1.graph;

import com.kitfox.svg.A;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class GraphAlgorithms {

    public static void findWays (Graph graph, int start, int finish, boolean[] visited, ArrayList<Integer> path, ArrayList<ArrayList<Integer>> listOfAllPaths) {
        visited[start] = true;
        path.add(start);

        if (start == finish) {
            ArrayList<Integer> pathToAdd = new ArrayList<>(path);
            listOfAllPaths.add(pathToAdd);

            for (int i = 0; i < path.size() - 1; i++) {
                System.out.print("[" + path.get(i) + ", " + path.get(i + 1) + "]" + " ");
            }
            System.out.println();
        } else {
            for (Integer v : graph.adjacencies(start)) {
                if (!visited[v]) {
                    findWays(graph, v, finish, visited, path, listOfAllPaths);
                }
            }
        }

        path.remove(path.size() - 1);
        visited[start] = false;
    }

    public static void findParallelWays (Graph graph, int start, int finish, boolean[] visited, ArrayList<Integer> path, ArrayList<ArrayList<Integer>> listOfAllPaths) {
        visited[start] = true;
        path.add(start);

        if ((start == finish) && (path.size() > 2)) {
            ArrayList<Integer> pathToAdd = new ArrayList<>(path);
            listOfAllPaths.add(pathToAdd);

            for (int i = 0; i < path.size() - 1; i++) {
                System.out.print("[" + path.get(i) + ", " + path.get(i + 1) + "]" + " ");
            }

            System.out.println();
        } else {
            for (Integer v : graph.adjacencies(start)) {
                if (!visited[v]) {
                    findParallelWays(graph, v, finish, visited, path, listOfAllPaths);
                }
            }
        }

        path.remove(path.size() - 1);
        visited[start] = false;
    }

    public static boolean isThisAnEdge (Graph graph, int start, int finish) {
        for (Integer v: graph.adjacencies(start)) {
            if (v == finish) {
                return true;
            }
        }

        return false;
    }

    /**
     * ?????????? ?? ??????????????, ?????????????????????????? ????????????????????
     * (?????????????????? ?????????????? ?????????? ????????????????)
     * @param graph ????????
     * @param from ??????????????, ?? ?????????????? ???????????????????? ??????????
     * @param visitor ????????????????????
     */
    public static void dfsRecursion(Graph graph, int from, Consumer<Integer> visitor) {
        boolean[] visited = new boolean[graph.vertexCount()];

        class Inner {
            void visit(Integer curr) {
                visitor.accept(curr);
                visited[curr] = true;
                for (Integer v : graph.adjacencies(curr)) {
                    if (!visited[v]) {
                        visit(v);
                    }
                }
            }
        }
        new Inner().visit(from);
    }

    /**
     * ?????????? ?? ??????????????, ?????????????????????????? ?? ?????????????? ??????????
     * (???? ???????????? "????????????????????"/????????????????????????, ??.??. "?? ??????????????" ?????????????????????? ???????????? "????????" ????????????, ?? ???? ?????? ??????????)
     * @param graph ????????
     * @param from ??????????????, ?? ?????????????? ???????????????????? ??????????
     * @param visitor ????????????????????
     */
    public static void dfs(Graph graph, int from, Consumer<Integer> visitor) {
        boolean[] visited = new boolean[graph.vertexCount()];
        Stack<Integer> stack = new Stack<Integer>();
        stack.push(from);
        visited[from] = true;
        while (!stack.empty()) {
            Integer curr = stack.pop();
            visitor.accept(curr);
            for (Integer v : graph.adjacencies(curr)) {
                if (!visited[v]) {
                    stack.push(v);
                    visited[v] = true;
                }
            }
        }
    }

    /**
     * ?????????? ?? ????????????, ?????????????????????????? ?? ?????????????? ??????????????
     * (?????????????????? ?????????????? ?????????? ????????????????)
     * @param graph ????????
     * @param from ??????????????, ?? ?????????????? ???????????????????? ??????????
     * @param visitor ????????????????????
     */
    public static void bfs(Graph graph, int from, Consumer<Integer> visitor) {
        boolean[] visited = new boolean[graph.vertexCount()];
        Queue<Integer> queue = new LinkedList<Integer>();
        queue.add(from);
        visited[from] = true;
        while (queue.size() > 0) {
            Integer curr = queue.remove();
            visitor.accept(curr);
            for (Integer v : graph.adjacencies(curr)) {
                if (!visited[v]) {
                    queue.add(v);
                    visited[v] = true;
                }
            }
        }
    }

    /**
     * ?????????? ?? ?????????????? ?? ???????? ??????????????????
     * (?????????????????? ?????????????? ?????????? ????????????????)
     * @param graph ????????
     * @param from ??????????????, ?? ?????????????? ???????????????????? ??????????
     * @return ????????????????
     */
    public static Iterable<Integer> dfs(Graph graph, int from) {
        return new Iterable<Integer>() {
            private Stack<Integer> stack = null;
            private boolean[] visited = null;

            @Override
            public Iterator<Integer> iterator() {
                stack = new Stack<>();
                stack.push(from);
                visited = new boolean[graph.vertexCount()];
                visited[from] = true;

                return new Iterator<Integer>() {
                    @Override
                    public boolean hasNext() {
                        return ! stack.isEmpty();
                    }

                    @Override
                    public Integer next() {
                        Integer result = stack.pop();
                        for (Integer adj : graph.adjacencies(result)) {
                            if (!visited[adj]) {
                                visited[adj] = true;
                                stack.add(adj);
                            }
                        }
                        return result;
                    }
                };
            }
        };
    }

    /**
     * ?????????? ?? ???????????? ?? ???????? ??????????????????
     * (?????????????????? ?????????????? ?????????? ????????????????)
     * @param from ??????????????, ?? ?????????????? ???????????????????? ??????????
     * @return ????????????????
     */
    public static Iterable<Integer> bfs(Graph graph, int from) {
        return new Iterable<Integer>() {
            private Queue<Integer> queue = null;
            private boolean[] visited = null;

            @Override
            public Iterator<Integer> iterator() {
                queue = new LinkedList<>();
                queue.add(from);
                visited = new boolean[graph.vertexCount()];
                visited[from] = true;

                return new Iterator<Integer>() {
                    @Override
                    public boolean hasNext() {
                        return ! queue.isEmpty();
                    }

                    @Override
                    public Integer next() {
                        Integer result = queue.remove();
                        for (Integer adj : graph.adjacencies(result)) {
                            if (!visited[adj]) {
                                visited[adj] = true;
                                queue.add(adj);
                            }
                        }
                        return result;
                    }
                };
            }
        };
    }


    /**
     * ???????????????? ????????????????
     * (???????????????????? ???????????????????? ?????? ???????????????????????? ?????????????? ???? n^2)
     */
    public static class MinDistanceSearchResult {
        public double d[];
        public int from[];
    }

    public static MinDistanceSearchResult dijkstra(WeightedGraph graph, int source, int target) {
        int n = graph.vertexCount();

        double[] d = new double[n];
        int[] from = new int[n];
        boolean[] found = new boolean[n];

        Arrays.fill(d, Double.MAX_VALUE);
        d[source] = 0;
        int prev = -1;
        for (int i = 0; i < n; i++) {
            // ???????? "??????????????????????" ?????????????? ?? ?????????????????????? d[i]
            // (?? ?????????? ???????????? ?????????????????? ?? ???????????????????????? ??????????????)
            int curr = -1;
            for (int j = 0; j < n; j++) {
                if (!found[j] && (curr < 0 || d[j] < d[curr])) {
                    curr = j;
                }
            }

            found[curr] = true;
            from[curr] = prev;
            if (curr == target) {
                break;
            }
            for (WeightedGraph.WeightedEdgeTo v : graph.adjacenciesWithWeights(curr)) {
                if (d[curr] + v.weight() < d[v.to()]) {
                    d[v.to()] = d[curr] + v.weight();
                    // ?? ?????????? ???????????? ???????? ???????? ???????????????? ?? ???????????????????????? ?????????????? ?????????????????? ?????? v.to()
                }
            }
        }

        // ?????????????????????? ????????????????????
        MinDistanceSearchResult result = new MinDistanceSearchResult();
        result.d = d;
        result.from = from;
        return result;
    }

    /**
     * ???????????????? ??????????????-??????????
     * O(n*m)
     */
    public static MinDistanceSearchResult belmanFord(WeightedGraph graph, int source) {
        int n = graph.vertexCount();

        double[] d = new double[n];
        int[] from = new int[n];

        Arrays.fill(d, Double.MAX_VALUE);
        d[source] = 0;
        for (int i = 0; i < n - 1; i++) {
            boolean changed = false;
            // ?????????? ???????? ???????????? (?? ???????????? ???????????????????? - ???????? ?? ??????????)
            for (int j = 0; j < n; j++) {
                for (WeightedGraph.WeightedEdgeTo v : graph.adjacenciesWithWeights(j)) {
                    if (d[v.to()] > d[j] + v.weight()) {
                        d[v.to()] = d[j] + v.weight();
                        from[v.to()] = j;
                        changed = true;
                    }
                }
            }
            if (!changed) {
                break;
            }
        }

        // ?????????????????????? ????????????????????
        MinDistanceSearchResult result = new MinDistanceSearchResult();
        result.d = d;
        result.from = from;
        return result;
    }
}
