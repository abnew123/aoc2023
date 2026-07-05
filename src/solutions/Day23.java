package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day23 implements DayTemplate {
    static int[] DR = {0, 1, 0, -1}, DC = {1, 0, -1, 0};
    List<int[]>[] graph;
    char[][] grid;
    int rows, cols, end;

    public String solve(boolean part1, Scanner in) {
        List<String> lines = new ArrayList<>();
        while (in.hasNextLine()) lines.add(in.nextLine());
        rows = lines.size();
        cols = lines.get(0).length();
        grid = new char[rows][];
        for (int r = 0; r < rows; r++) grid[r] = lines.get(r).toCharArray();
        int startCell = cell(0, lines.get(0).indexOf('.'));
        int endCell = cell(rows - 1, lines.get(rows - 1).indexOf('.'));
        Map<Integer, Integer> id = new HashMap<>();
        for (int r = 0; r < rows; r++) for (int c = 0; c < cols; c++) {
            if (grid[r][c] != '#' && (cell(r, c) == startCell || cell(r, c) == endCell || degree(r, c) != 2)) {
                id.put(cell(r, c), id.size());
            }
        }
        graph = new List[id.size()];
        for (int i = 0; i < graph.length; i++) graph[i] = new ArrayList<>();
        end = id.get(endCell);
        for (int p : id.keySet()) {
            int from = id.get(p), r = p / cols, c = p % cols;
            for (int d : dirs(grid[r][c], part1)) addEdge(id, from, r, c, d, part1);
        }
        return "" + dfs(id.get(startCell), 1L << id.get(startCell));
    }

    void addEdge(Map<Integer, Integer> id, int from, int r, int c, int d, boolean part1) {
        int steps = 0;
        for (;;) {
            r += DR[d];
            c += DC[d];
            steps++;
            if (!ok(r, c)) return;
            Integer to = id.get(cell(r, c));
            if (to != null) {
                graph[from].add(new int[]{to, steps});
                return;
            }
            int next = -1;
            for (int nd : dirs(grid[r][c], part1)) {
                int nr = r + DR[nd], nc = c + DC[nd];
                if (ok(nr, nc) && nd != (d + 2) % 4) next = nd;
            }
            if (next < 0) return;
            d = next;
        }
    }

    int dfs(int at, long seen) {
        if (at == end) return 0;
        int best = -1_000_000;
        for (int[] e : graph[at]) {
            long bit = 1L << e[0];
            if ((seen & bit) == 0) best = Math.max(best, e[1] + dfs(e[0], seen | bit));
        }
        return best;
    }

    int[] dirs(char ch, boolean part1) {
        if (!part1) return new int[]{0, 1, 2, 3};
        return switch (ch) {
            case '>' -> new int[]{0};
            case 'v' -> new int[]{1};
            case '<' -> new int[]{2};
            case '^' -> new int[]{3};
            default -> new int[]{0, 1, 2, 3};
        };
    }

    int degree(int r, int c) {
        int n = 0;
        for (int d = 0; d < 4; d++) if (ok(r + DR[d], c + DC[d])) n++;
        return n;
    }

    boolean ok(int r, int c) {
        return r >= 0 && c >= 0 && r < rows && c < cols && grid[r][c] != '#';
    }

    int cell(int r, int c) {
        return r * cols + c;
    }
}
