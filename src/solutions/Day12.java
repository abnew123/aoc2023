package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day12 implements DayTemplate {
    Map<String, Long> memo;

    public String solve(boolean part1, Scanner in) {
        long ans = 0;
        while (in.hasNextLine()) {
            String[] p = in.nextLine().split(" ");
            String s = p[0];
            int[] g = Arrays.stream(p[1].split(",")).mapToInt(Integer::parseInt).toArray();
            if (!part1) {
                s = String.join("?", Collections.nCopies(5, s));
                int[] old = g;
                g = new int[old.length * 5];
                for (int i = 0; i < 5; i++) System.arraycopy(old, 0, g, i * old.length, old.length);
            }
            memo = new HashMap<>();
            ans += count(s, g, 0, 0, 0);
        }
        return "" + ans;
    }

    long count(String s, int[] g, int i, int group, int run) {
        String key = i + "," + group + "," + run;
        if (memo.containsKey(key)) return memo.get(key);
        if (i == s.length()) {
            return group == g.length && run == 0 || group == g.length - 1 && run == g[group] ? 1 : 0;
        }
        long ans = 0;
        char c = s.charAt(i);
        if (c != '#') {
            if (run == 0) ans += count(s, g, i + 1, group, 0);
            else if (group < g.length && run == g[group]) ans += count(s, g, i + 1, group + 1, 0);
        }
        if (c != '.' && group < g.length && run < g[group]) ans += count(s, g, i + 1, group, run + 1);
        memo.put(key, ans);
        return ans;
    }
}
