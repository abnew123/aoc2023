package src.solutions;

import src.meta.DayTemplate;

import java.util.*;



public class Day25 implements DayTemplate {


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

    Map<String, List<String>> parse(Scanner in){
        Map<String, List<String>> graph = new HashMap<>();
        while (in.hasNext()) {
            String[] line = in.nextLine().split(":");
            String src = line[0];
            String[] tars = line[1].trim().split(" ");
            
            
            graph.putIfAbsent(src, new ArrayList<>());
            
            
            for (String tar : tars) {
                graph.putIfAbsent(tar, new ArrayList<>());
                graph.get(src).add(tar);
                graph.get(tar).add(src);
            }
        }
        return graph;
    }

    String bfs(String node, Map<String, List<String>> graph) {
        String ret = "";
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(node);
        while (!queue.isEmpty()) {
            ret = queue.poll();
            for (String nbr : graph.get(ret)) {
                if (!visited.contains(nbr)) {
                    queue.add(nbr);
                    visited.add(nbr);
                }
            }
        }
        return ret;
    }

    int getSize(String start, String end, Map<String, List<String>> graph) {
        int res = 0;
        Queue<Route> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        Set<Edge> usedEdges = new HashSet<>();
        for (int i = 0; i < 4; i++) {
            res = 0;
            queue.add(new Route(start));
            visited.add(start);
            while (!queue.isEmpty()) {
                res++;
                Route cur = queue.poll();
                if (cur.vertex.equals(end)) {
                    usedEdges.addAll(cur.edges);
                    break;
                }
                for (String nbr : graph.get(cur.vertex)) {
                    Edge e = new Edge(cur.vertex, nbr);
                    if (!visited.contains(nbr) && !usedEdges.contains(e)) {
                        visited.add(nbr);
                        queue.add(new Route(nbr, cur));
                    }
                }
            }
            visited = new HashSet<>();
            queue = new LinkedList<>();
        }

        return res;
    }

}

class Route {
    String vertex;
    List<Edge> edges;

    public Route(String start) {
        vertex = start;
        edges = new ArrayList<>();
    }

    public Route(String next, Route cur) {
        vertex = next;
        edges = new ArrayList<>(cur.edges);
        edges.add(new Edge(cur.vertex, next));
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

    public boolean equals(Object o) {
        if (o instanceof Edge edge) {
            return start.equals(edge.start) && end.equals(edge.end);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(start, end);
    }
}
