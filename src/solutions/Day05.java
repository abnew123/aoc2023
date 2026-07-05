package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day05 implements DayTemplate {
    long[] seeds;
    List<long[][]> maps = new ArrayList<>();

    public String solve(boolean part1, Scanner in) {
        String[] s = in.nextLine().split(" ");
        seeds = new long[s.length - 1];
        for (int i = 1; i < s.length; i++) seeds[i - 1] = Long.parseLong(s[i]);
        List<long[]> cur = new ArrayList<>();
        while (in.hasNextLine()) {
            String line = in.nextLine();
            if (line.contains("map")) {
                if (!cur.isEmpty()) maps.add(cur.toArray(long[][]::new));
                cur.clear();
            } else if (!line.isEmpty()) {
                cur.add(Arrays.stream(line.split(" ")).mapToLong(Long::parseLong).toArray());
            }
        }
        maps.add(cur.toArray(long[][]::new));
        return "" + (part1 ? one() : two());
    }

    long one() {
        long best = Long.MAX_VALUE;
        for (long seed : seeds) {
            long v = seed;
            for (long[][] m : maps) v = conv(v, m)[0];
            best = Math.min(best, v);
        }
        return best;
    }

    long two() {
        long best = Long.MAX_VALUE;
        for (int i = 0; i < seeds.length; i += 2) {
            for (long seed = seeds[i]; seed < seeds[i] + seeds[i + 1];) {
                long v = seed, skip = 10_000_000_000L;
                for (long[][] m : maps) {
                    long[] c = conv(v, m);
                    v = c[0];
                    skip = Math.min(skip, c[1]);
                }
                best = Math.min(best, v);
                seed += skip + 1;
            }
        }
        return best;
    }

    long[] conv(long v, long[][] map) {
        long next = 10_000_000_000L;
        for (long[] r : map) {
            if (r[1] > v) next = Math.min(next, r[1] - v - 1);
            if (r[1] <= v && v < r[1] + r[2]) return new long[]{r[0] + v - r[1], r[2] - (v - r[1]) - 1};
        }
        return new long[]{v, next == 10_000_000_000L ? 0 : next};
    }
}
