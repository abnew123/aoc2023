package src.algorithms;

import java.util.*;

class DFS {
    private Map<Integer, List<Integer>> graph;

    public DFS(Map<Integer, List<Integer>> graph) {
        this.graph = graph;
    }

    public boolean hasPath(int start, int end) {
        Set<Integer> visited = new HashSet<>();
        return dfs(start, end, visited);
    }

    private boolean dfs(int current, int end, Set<Integer> visited) {
        if (current == end) {
            return true;
        }

        visited.add(current);

        for (int neighbor : graph.getOrDefault(current, Collections.emptyList())) {
            if (!visited.contains(neighbor)) {
                if (dfs(neighbor, end, visited)) {
                    return true;
                }
            }
        }

        return false;
    }
}
