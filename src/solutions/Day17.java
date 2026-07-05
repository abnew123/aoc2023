package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day17 implements DayTemplate {
    static int[] DX = {0, -1, 0, 0, 1}, DY = {0, 0, -1, 1, 0};
    static int[][] T = {{1, 2, 3, 4}, {2, 3}, {1, 4}, {1, 4}, {2, 3}};

    public String solve(boolean part1, Scanner in) {
        List<String> lines = new ArrayList<>();
        while (in.hasNextLine()) lines.add(in.nextLine());
        int h = lines.size(), w = lines.get(0).length(), min = part1 ? 1 : 4, max = part1 ? 3 : 10;
        int[] heat = new int[w * h], dist = new int[w * h * 5];
        Arrays.fill(dist, 1_000_000_000);
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) heat[x * h + y] = lines.get(y).charAt(x) - '0';
        PriorityQueue<int[]> q = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        q.add(new int[]{0, 0});
        dist[0] = 0;
        while (!q.isEmpty()) {
            int[] e = q.poll();
            int cost = e[0], s = e[1], dir = s % 5, cell = s / 5, x = cell / h, y = cell % h;
            if (cost != dist[s]) continue;
            if (x == w - 1 && y == h - 1) return "" + cost;
            for (int turn : T[dir]) {
                int next = cost;
                for (int len = 1; len <= max; len++) {
                    int nx = x + len * DX[turn], ny = y + len * DY[turn];
                    if (nx < 0 || ny < 0 || nx >= w || ny >= h) break;
                    next += heat[nx * h + ny];
                    int ns = (nx * h + ny) * 5 + turn;
                    if (len >= min && next < dist[ns]) {
                        dist[ns] = next;
                        q.add(new int[]{next, ns});
                    }
                }
            }
        }
        return "";
    }
}
