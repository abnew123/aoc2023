package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day03 implements DayTemplate {
    public String solve(boolean part1, Scanner in) {
        List<String> g = new ArrayList<>();
        while (in.hasNextLine()) {
            g.add(in.nextLine());
        }
        long ans = 0;
        Map<Integer, List<Integer>> gears = new HashMap<>();
        int rows = g.size(), cols = g.get(0).length();
        for (int r = 0; r < rows; r++) {
            String s = g.get(r);
            for (int c = 0; c < cols; c++) {
                if (!Character.isDigit(s.charAt(c))) {
                    continue;
                }
                int start = c, n = 0;
                while (c < cols && Character.isDigit(s.charAt(c))) {
                    n = 10 * n + s.charAt(c++) - '0';
                }
                boolean ok = false;
                Set<Integer> near = new HashSet<>();
                for (int rr = Math.max(0, r - 1); rr <= Math.min(rows - 1, r + 1); rr++) {
                    for (int cc = Math.max(0, start - 1); cc <= Math.min(cols - 1, c); cc++) {
                        char ch = g.get(rr).charAt(cc);
                        if (!Character.isDigit(ch) && ch != '.') {
                            ok = true;
                        }
                        if (ch == '*') {
                            near.add(rr * cols + cc);
                        }
                    }
                }
                if (part1 && ok) {
                    ans += n;
                }
                for (int gear : near) {
                    gears.computeIfAbsent(gear, k -> new ArrayList<>()).add(n);
                }
                c--;
            }
        }
        if (!part1) {
            for (List<Integer> a : gears.values()) {
                if (a.size() == 2) {
                    ans += a.get(0) * a.get(1);
                }
            }
        }
        return "" + ans;
    }
}
