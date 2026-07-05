package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day25 implements DayTemplate {
    public String solve(boolean part1, Scanner in) {
        if (!part1) {
            return "Merry Christmas!";
        }
        Map<String, List<String>> g = new HashMap<>();
        while (in.hasNextLine()) {
            String[] a = in.nextLine().split(":? ");
            g.putIfAbsent(a[0], new ArrayList<>());
            for (int i = 1; i < a.length; i++) {
                g.putIfAbsent(a[i], new ArrayList<>());
                g.get(a[0]).add(a[i]);
                g.get(a[i]).add(a[0]);
            }
        }
        String a = far(g.keySet().iterator().next(), g), b = far(a, g);
        int n = cut(a, b, g);
        return "" + n * (g.size() - n);
    }

    String far(String start, Map<String, List<String>> g) {
        String last = start;
        Queue<String> q = new ArrayDeque<>();
        Set<String> seen = new HashSet<>();
        q.add(start);
        seen.add(start);
        while (!q.isEmpty()) {
            last = q.remove();
            for (String n : g.get(last)) {
                if (seen.add(n)) {
                    q.add(n);
                }
            }
        }
        return last;
    }

    int cut(String start, String end, Map<String, List<String>> g) {
        Set<String> used = new HashSet<>();
        int size = 0;
        for (int pass = 0; pass < 4; pass++) {
            Queue<String> q = new ArrayDeque<>();
            Map<String, String> parent = new HashMap<>();
            q.add(start);
            parent.put(start, "");
            size = 0;
            while (!q.isEmpty()) {
                String v = q.remove();
                size++;
                if (v.equals(end)) {
                    for (String x = end; !x.equals(start); x = parent.get(x)) {
                        used.add(edge(x, parent.get(x)));
                    }
                    break;
                }
                for (String n : g.get(v)) {
                    if (!parent.containsKey(n) && !used.contains(edge(v, n))) {
                        parent.put(n, v);
                        q.add(n);
                    }
                }
            }
        }
        return size;
    }

    String edge(String a, String b) {
        return a.compareTo(b) < 0 ? a + b : b + a;
    }
}
