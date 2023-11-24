package src.algorithms;
import java.util.*;

class BFS {
    private Map<Integer, List<Integer>> graph;

    public BFS(Map<Integer, List<Integer>> graph) {
        this.graph = graph;
    }

    public boolean hasPath(int start, int end) {
        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            int current = queue.poll();

            if (current == end) {
                return true;
            }

            for (int neighbor : graph.getOrDefault(current, Collections.emptyList())) {
                if (!visited.contains(neighbor)) {
                    queue.add(neighbor);
                    visited.add(neighbor);
                }
            }
        }

        return false;
    }
}
