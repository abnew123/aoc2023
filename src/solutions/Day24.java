package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day24 implements DayTemplate {
    public String solve(boolean part1, Scanner in) {
        List<long[]> h = new ArrayList<>();
        int[] min = new int[3], max = new int[3];
        while (in.hasNextLine()) {
            long[] a = Arrays.stream(in.nextLine().split("@|,")).map(String::trim).mapToLong(Long::parseLong).toArray();
            h.add(a);
            for (int i = 0; i < 3; i++) {
                min[i] = Math.min(min[i], (int) a[i + 3]);
                max[i] = Math.max(max[i], (int) a[i + 3]);
            }
        }
        return "" + (part1 ? xy(h) : rock(h, min, max));
    }

    long xy(List<long[]> h) {
        long ans = 0;
        double lo = 200000000000000.0, hi = 400000000000000.0;
        for (int i = 0; i < h.size(); i++) for (int j = i + 1; j < h.size(); j++) {
            long[] a = h.get(i), b = h.get(j);
            long den = a[3] * b[4] - a[4] * b[3];
            long n1 = (b[0] - a[0]) * b[4] - (b[1] - a[1]) * b[3];
            long n2 = (a[0] - b[0]) * a[4] - (a[1] - b[1]) * a[3];
            if (den != 0 && n1 / den > 0 && n2 / den < 0) {
                double x = n1 / (double) den * a[3] + a[0], y = n1 / (double) den * a[4] + a[1];
                if (x >= lo && x <= hi && y >= lo && y <= hi) ans++;
            }
        }
        return ans;
    }

    long rock(List<long[]> h, int[] min, int[] max) {
        List<Integer>[] ok = new List[]{new ArrayList<>(), new ArrayList<>(), new ArrayList<>()};
        for (int axis = 0; axis < 3; axis++) {
            boolean[] bad = new boolean[max[axis] - min[axis] + 1];
            for (long[] a : h) for (long[] b : h) {
                if (a[axis + 3] > b[axis + 3] && a[axis] > b[axis]) {
                    for (long v = b[axis + 3]; v <= a[axis + 3]; v++) bad[(int) v - min[axis]] = true;
                }
            }
            for (int i = 0; i < bad.length; i++) if (!bad[i]) ok[axis].add(i + min[axis]);
        }
        for (int vx : ok[0]) for (int vy : ok[1]) for (int vz : ok[2]) {
            long[] p = hit(h, vx, vy, vz);
            if (p != null) return p[0] + p[1] + p[2];
        }
        return 0;
    }

    long[] hit(List<long[]> h, int vx, int vy, int vz) {
        long[] ans = null, a = h.get(0);
        for (int i = 1; i < h.size(); i++) {
            long[] b = h.get(i);
            long ax = a[3] - vx, ay = a[4] - vy, bx = b[3] - vx, by = b[4] - vy;
            long den = ax * by - ay * bx;
            if (den == 0) continue;
            long n1 = (b[0] - a[0]) * by - (b[1] - a[1]) * bx;
            long n2 = (a[0] - b[0]) * ay - (a[1] - b[1]) * ax;
            if (n1 / den < 0 || n2 / den > 0) return null;
            long[] p = {n1 / den * ax + a[0], n1 / den * ay + a[1], n1 / den * (a[5] - vz) + a[2]};
            if (ans == null) ans = p;
            else if (ans[0] != p[0] || ans[1] != p[1] || ans[2] != p[2]) return null;
        }
        return ans;
    }
}
