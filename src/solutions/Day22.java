package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day22 implements DayTemplate {
    List<int[]> bricks = new ArrayList<>();
    List<Set<Integer>> supports = new ArrayList<>(), restsOn = new ArrayList<>();

    public String solve(boolean part1, Scanner in) {
        while (in.hasNextLine()) {
            int[] a = Arrays.stream(in.nextLine().split("[~,]")).mapToInt(Integer::parseInt).toArray();
            for (int i = 0; i < 3; i++) if (a[i] > a[i + 3]) {
                int t = a[i]; a[i] = a[i + 3]; a[i + 3] = t;
            }
            bricks.add(a);
        }
        bricks.sort(Comparator.comparingInt(a -> a[2]));
        for (int i = 0; i < bricks.size(); i++) {
            supports.add(new HashSet<>());
            restsOn.add(new HashSet<>());
        }
        for (int i = 0; i < bricks.size(); i++) {
            int[] b = bricks.get(i);
            int floor = 0;
            for (int j = 0; j < i; j++) if (overlap(b, bricks.get(j))) {
                floor = Math.max(floor, bricks.get(j)[5]);
            }
            int fall = b[2] - floor - 1;
            b[2] -= fall;
            b[5] -= fall;
            for (int j = 0; j < i; j++) if (overlap(b, bricks.get(j)) && bricks.get(j)[5] == b[2] - 1) {
                restsOn.get(i).add(j);
                supports.get(j).add(i);
            }
        }
        long ans = 0;
        for (int i = 0; i < bricks.size(); i++) {
            ans += part1 ? safe(i) ? 1 : 0 : cascade(i);
        }
        return "" + ans;
    }

    boolean overlap(int[] a, int[] b) {
        return a[0] <= b[3] && b[0] <= a[3] && a[1] <= b[4] && b[1] <= a[4];
    }

    boolean safe(int i) {
        for (int x : supports.get(i)) if (restsOn.get(x).size() == 1) return false;
        return true;
    }

    int cascade(int start) {
        Set<Integer> dead = new HashSet<>(Set.of(start));
        for (boolean changed = true; changed;) {
            changed = false;
            for (int i = 0; i < bricks.size(); i++) {
                if (!dead.contains(i) && !restsOn.get(i).isEmpty() && dead.containsAll(restsOn.get(i))) {
                    dead.add(i);
                    changed = true;
                }
            }
        }
        return dead.size() - 1;
    }
}
