package src.solutions;

import src.meta.DayTemplate;

import java.util.*;


public class Day25 extends DayTemplate {

    public String solve(boolean part1, Scanner in) {
        if (!part1) {
            return "Merry Christmas!";
        }
        Map<String, List<String>> graph = new HashMap<>();
        List<Edge> edges = new ArrayList<>();
        while (in.hasNext()) {
            String[] line = in.nextLine().split(":");
            graph.putIfAbsent(line[0], new ArrayList<>());
            for (String s : line[1].trim().split(" ")) {
                graph.putIfAbsent(s, new ArrayList<>());
                graph.get(line[0]).add(s);
                graph.get(s).add(line[0]);
                edges.add(new Edge(line[0], s));
            }
        }
        Map<Edge, Integer> frequencies = new HashMap<>();
        for (Edge edge : edges) {
            frequencies.put(edge, 0);
        }
        getMostUsedEdges(graph, frequencies);
        for (Edge e : frequencies.keySet()) {
            edges.get(edges.indexOf(e)).used = frequencies.get(e);
        }
        Collections.sort(edges);
        edges.subList(0, 3).clear();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        List<String> verticesList = new ArrayList<>(graph.keySet());
        queue.add(verticesList.get(0));
        while (!queue.isEmpty()) {
            String s = queue.poll();
            visited.add(s);
            for (String con : graph.get(s)) {
                if (!visited.contains(con) && edges.contains(new Edge(s, con))) {
                    queue.add(con);
                }
            }
        }
        return visited.size() * (graph.keySet().size() - visited.size()) + "";
    }

    public void getMostUsedEdges(Map<String, List<String>> graph, Map<Edge, Integer> frequencies) {
        for (String vertex : graph.keySet()) {
            Set<String> visited = new HashSet<>();
            visited.add(vertex);
            List<Route> routes = new ArrayList<>();
            routes.add(new Route(vertex));
            while (!routes.isEmpty()) {
                List<Route> routeTmp = new ArrayList<>();
                for (Route r : routes) {
                    if (r.edges.size() > 10) {
                        for (Edge e : r.edges) {
                            frequencies.put(e, frequencies.get(e) + 1);
                        }
                    }
                    for (String con : graph.get(r.vertex)) {
                        if (!visited.contains(con)) {
                            Route tmp = new Route(con);
                            tmp.edges.addAll(r.edges);
                            tmp.edges.add(new Edge(r.vertex, con));
                            routeTmp.add(tmp);
                            visited.add(con);
                        }
                    }
                }
                routes = routeTmp;
            }
        }
    }
}

class Route {
    String vertex;
    List<Edge> edges;

    public Route(String start) {
        vertex = start;
        edges = new ArrayList<>();
    }

}

class Edge implements Comparable<Edge> {
    String start;
    String end;
    int used = 0;

    public Edge(String s, String e) {
        start = (s.compareTo(e) > 0) ? s : e;
        end = (s.compareTo(e) > 0) ? e : s;
    }

    @Override
    public boolean equals(Object o) {
        Edge edge = (Edge) o;
        return start.equals(edge.start) && end.equals(edge.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public int compareTo(Edge o) {
        return o.used - used;
    }
}
