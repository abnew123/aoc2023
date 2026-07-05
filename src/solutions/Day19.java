package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day19 implements DayTemplate {
    Map<String, String[]> flow = new HashMap<>();
    List<int[]> parts = new ArrayList<>();

    public String solve(boolean part1, Scanner in) {
        for (boolean rules = true; in.hasNextLine();) {
            String line = in.nextLine();
            if (line.isEmpty()) rules = false;
            else if (rules) {
                String[] p = line.split("[{}]");
                flow.put(p[0], p[1].split(","));
            } else {
                parts.add(Arrays.stream(line.split("[{}=,xmas]+")).filter(s -> !s.isEmpty()).mapToInt(Integer::parseInt).toArray());
            }
        }
        if (!part1) return "" + count("in", new int[]{1, 1, 1, 1}, new int[]{4000, 4000, 4000, 4000});
        long ans = 0;
        for (int[] p : parts) if (accept(p)) ans += p[0] + p[1] + p[2] + p[3];
        return "" + ans;
    }

    boolean accept(int[] p) {
        String at = "in";
        while (!at.equals("A") && !at.equals("R")) {
            for (String r : flow.get(at)) {
                at = next(r, p);
                if (at != null) break;
            }
        }
        return at.equals("A");
    }

    String next(String r, int[] p) {
        if (!r.contains(":")) return r;
        String[] s = r.split("[<>:]");
        int i = "xmas".indexOf(r.charAt(0)), n = Integer.parseInt(s[1]);
        return r.charAt(1) == '>' ? p[i] > n ? s[2] : null : p[i] < n ? s[2] : null;
    }

    long count(String at, int[] lo, int[] hi) {
        if (at.equals("R")) return 0;
        if (at.equals("A")) {
            long v = 1;
            for (int i = 0; i < 4; i++) v *= hi[i] - lo[i] + 1L;
            return v;
        }
        long ans = 0;
        for (String r : flow.get(at)) {
            if (!r.contains(":")) return ans + count(r, lo, hi);
            String[] s = r.split("[<>:]");
            int i = "xmas".indexOf(r.charAt(0)), n = Integer.parseInt(s[1]);
            int[] yesLo = lo.clone(), yesHi = hi.clone();
            if (r.charAt(1) == '>') {
                yesLo[i] = Math.max(yesLo[i], n + 1);
                hi[i] = Math.min(hi[i], n);
            } else {
                yesHi[i] = Math.min(yesHi[i], n - 1);
                lo[i] = Math.max(lo[i], n);
            }
            if (yesLo[i] <= yesHi[i]) ans += count(s[2], yesLo, yesHi);
            if (lo[i] > hi[i]) return ans;
        }
        return ans;
    }
}
