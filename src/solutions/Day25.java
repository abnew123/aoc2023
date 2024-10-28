package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

/* Major inspiration drawn from the following rust solution
 * https://github.com/maneatingape/advent-of-code-rust/blob/main/src/year2023/day25.rs
 * Did not do the low level optimizations, but roughly followed the high level solution
 */

public class Day25 implements DayTemplate {

    @Override
    public String[] fullSolve(Scanner in) {
        Map<String, List<String>> graph = parse(in);
        List<String> verticesList = new ArrayList<>(graph.keySet());
        String start = bfs(verticesList.getFirst(), graph);
        String end = bfs(start, graph);
        int size = getSize(start, end, graph);
        return new String[]{size * (verticesList.size() - size) + "", "Merry Christmas!"};
    }

    public String solve(boolean part1, Scanner in) {
        if (!part1) {
            return "Merry Christmas!";
        }
        Map<String, List<String>> graph = parse(in);
        List<String> verticesList = new ArrayList<>(graph.keySet());
        String start = bfs(verticesList.getFirst(), graph);
        String end = bfs(start, graph);
        int size = getSize(start, end, graph);
        return size * (verticesList.size() - size) + "";
    }

    private Map<String, List<String>> parse(Scanner in){
        Map<String, List<String>> graph = new HashMap<>();
        while (in.hasNext()) {
            String[] line = in.nextLine().split(":");
            graph.putIfAbsent(line[0], new ArrayList<>());
            for (String s : line[1].trim().split(" ")) {
                graph.putIfAbsent(s, new ArrayList<>());
                graph.get(line[0]).add(s);
                graph.get(s).add(line[0]);
            }
        }
        return graph;
    }

    private String bfs(String node, Map<String, List<String>> graph) {
        String ret = "";
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(node);
        while (!queue.isEmpty()) {
            ret = queue.poll();
            for (String neighbor : graph.get(ret)) {
                if (!visited.contains(neighbor)) {
                    queue.add(neighbor);
                    visited.add(neighbor);
                }
            }
        }
        return ret;
    }

    private int getSize(String start, String end, Map<String, List<String>> graph) {
        int result = 0;
        Queue<Route> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        Set<Edge> usedEdges = new HashSet<>();
        for (int i = 0; i < 4; i++) {
            result = 0;
            queue.add(new Route(start));
            visited.add(start);
            while (!queue.isEmpty()) {
                result++;
                Route current = queue.poll();
                if (current.vertex.equals(end)) {
                    usedEdges.addAll(current.edges);
                    break;
                }
                for (String neighbor : graph.get(current.vertex)) {
                    Edge e = new Edge(current.vertex, neighbor);
                    if (!visited.contains(neighbor) && !usedEdges.contains(e)) {
                        visited.add(neighbor);
                        queue.add(new Route(neighbor, current));
                    }
                }
            }
            visited = new HashSet<>();
            queue = new LinkedList<>();
        }

        return result;
    }

}

class Route {
    String vertex;
    List<Edge> edges;

    public Route(String start) {
        vertex = start;
        edges = new ArrayList<>();
    }

    public Route(String next, Route current) {
        vertex = next;
        edges = new ArrayList<>(current.edges);
        edges.add(new Edge(current.vertex, next));
    }

    public String toString() {
        return vertex + ": " + edges;
    }

}

class Edge {
    String start;
    String end;

    public Edge(String s, String e) {
        start = (s.compareTo(e) > 0) ? s : e;
        end = (s.compareTo(e) > 0) ? e : s;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Edge edge) {
            return start.equals(edge.start) && end.equals(edge.end);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}
