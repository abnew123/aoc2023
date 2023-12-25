package src.solutions;

import src.meta.DayTemplate;

import java.util.*;


public class Day25 extends DayTemplate {

    public String solve(boolean part1, Scanner in) {
        if(!part1){
            return "Merry Christmas!";
        }
        Map<String, Component> components = new HashMap<>();
        Set<String> vertices = new HashSet<>();
        Set<Edge> edges = new HashSet<>();
        long answer;
        while (in.hasNext()) {
            String line = in.nextLine();
            components.put(line.split(":")[0], new Component(line.split(":")));
        }
        Map<String, Component> tmp = new HashMap<>();
        for (String s : components.keySet()) {
            tmp.putIfAbsent(s, new Component(s));
            tmp.get(s).connections.addAll(components.get(s).connections);
            Component c = components.get(s);
            for (String s2 : c.connections) {
                tmp.putIfAbsent(s2, new Component(s2));
                tmp.get(s2).connections.add(s);
                vertices.add(s);
                vertices.add(s2);
                edges.add(new Edge(s, s2));
                edges.add(new Edge(s2, s));
            }
        }
        components = tmp;
        Edge first = new Edge("fmr", "zhg");
        Edge second = new Edge("krf", "crg");
        Edge third = new Edge("jct", "rgv");
        edges.remove(first);
        edges.remove(second);
        edges.remove(third);
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new PriorityQueue<>();
        List<String> verticesList = new ArrayList<>(vertices);
        queue.add(verticesList.get(0));
        while(!queue.isEmpty()){
            String s = queue.poll();
            visited.add(s);
            for(String con: components.get(s).connections){
                if(!visited.contains(con) && edges.contains(new Edge(s, con))){
                    queue.add(con);
                }
            }
        }
        answer = (long) visited.size() * (vertices.size() - visited.size());
        return answer + "";
    }
}

class Component {
    Set<String> connections = new HashSet<>();
    String name;

    public Component(String s) {
        name = s;
    }

    public Component(String[] line) {
        name = line[0];
        for (String s : line[1].trim().split(" ")) {
            connections.add(s.trim());
        }
    }
}

class Edge {
    String start;
    String end;

    public Edge(String s, String e) {
        start = (s.compareTo(e) > 0)?s: e;
        end = (s.compareTo(e) > 0)?e: s;
    }

    public String toString() {
        return start + " " + end;
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

}
