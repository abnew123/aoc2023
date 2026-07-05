package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day16 implements DayTemplate {
    static int[] DX = {1, 0, -1, 0}, DY = {0, 1, 0, -1};
    char[][] grid;
    int rows, cols;

    public String solve(boolean part1, Scanner in) {
        List<String> lines = new ArrayList<>();
        while (in.hasNextLine()) lines.add(in.nextLine());
        rows = lines.size();
        cols = lines.get(0).length();
        grid = new char[rows][];
        for (int i = 0; i < rows; i++) grid[i] = lines.get(i).toCharArray();
        int ans = part1 ? run(-1, 0, 0) : 0;
        if (!part1) {
            for (int r = 0; r < rows; r++) {
                ans = Math.max(ans, Math.max(run(-1, r, 0), run(cols, r, 2)));
            }
            for (int c = 0; c < cols; c++) {
                ans = Math.max(ans, Math.max(run(c, -1, 1), run(c, rows, 3)));
            }
        }
        return "" + ans;
    }

    int run(int x, int y, int d) {
        boolean[] lit = new boolean[rows * cols], seen = new boolean[rows * cols * 4];
        int ans = 0;
        Deque<int[]> q = new ArrayDeque<>();
        q.push(new int[]{x, y, d});
        while (!q.isEmpty()) {
            int[] b = q.pop();
            x = b[0] + DX[b[2]];
            y = b[1] + DY[b[2]];
            d = b[2];
            if (x < 0 || y < 0 || x >= cols || y >= rows) continue;
            int cell = y * cols + x, state = cell * 4 + d;
            if (seen[state]) continue;
            seen[state] = true;
            if (!lit[cell]) {
                lit[cell] = true;
                ans++;
            }
            char ch = grid[y][x];
            if (ch == '/') d = 3 - d;
            else if (ch == '\\') d ^= 1;
            else if (ch == '|' && d % 2 == 0) {
                q.push(new int[]{x, y, 1});
                d = 3;
            } else if (ch == '-' && d % 2 == 1) {
                q.push(new int[]{x, y, 0});
                d = 2;
            }
            q.push(new int[]{x, y, d});
        }
        return ans;
    }
}
