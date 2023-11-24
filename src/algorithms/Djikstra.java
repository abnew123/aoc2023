package src.algorithms;
import java.util.*;

class Dijkstra {
    private Map<Integer, List<Node>> graph;

    public Dijkstra(Map<Integer, List<Node>> graph) {
        this.graph = graph;
    }

    public int shortestPath(int start, int end) {
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(node -> node.distance));
        Map<Integer, Integer> distances = new HashMap<>();

        queue.add(new Node(start, 0));
        distances.put(start, 0);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            if (current.node == end) {
                return distances.get(end);
            }

            for (Node neighbor : graph.getOrDefault(current.node, Collections.emptyList())) {
                int newDistance = current.distance + neighbor.distance;

                if (!distances.containsKey(neighbor.node) || newDistance < distances.get(neighbor.node)) {
                    distances.put(neighbor.node, newDistance);
                    queue.add(new Node(neighbor.node, newDistance));
                }
            }
        }

        return -1; // If there is no path from start to end
    }

    private static class Node {
        int node;
        int distance;

        public Node(int node, int distance) {
            this.node = node;
            this.distance = distance;
        }
    }
}
