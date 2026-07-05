package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day10 implements DayTemplate {
    static int[] DX = {0, 1, 0, -1}, DY = {-1, 0, 1, 0};
    char[][] grid;
    boolean[][] loop, big;
    int rows, cols, sx, sy;

    public String solve(boolean part1, Scanner in) {
        List<String> lines = new ArrayList<>();
        while (in.hasNextLine()) lines.add(in.nextLine());
        rows = lines.size();
        cols = lines.get(0).length();
        grid = new char[rows][];
        loop = new boolean[rows][cols];
        big = new boolean[rows * 2 + 1][cols * 2 + 1];
        for (int y = 0; y < rows; y++) {
            grid[y] = lines.get(y).toCharArray();
            for (int x = 0; x < cols; x++) if (grid[y][x] == 'S') {
                sx = x;
                sy = y;
            }
        }
        int px = sx, py = sy, x = sx, y = sy + 1, len = 1;
        mark(px, py, x, y);
        while (!loop[y][x]) {
            loop[y][x] = true;
            int[] d = dirs(grid[y][x]);
            int nx = x + d[0], ny = y + d[1];
            if (nx == px && ny == py) {
                nx = x + d[2];
                ny = y + d[3];
            }
            px = x; py = y; x = nx; y = ny; len++;
            mark(px, py, x, y);
        }
        return "" + (part1 ? len / 2 : inside());
    }

    void mark(int x1, int y1, int x2, int y2) {
        big[y2 * 2 + 1][x2 * 2 + 1] = big[y1 + y2 + 1][x1 + x2 + 1] = true;
    }

    int inside() {
        boolean[][] out = new boolean[big.length][big[0].length];
        int[] q = new int[out.length * out[0].length];
        int head = 0, tail = 1;
        out[0][0] = true;
        while (head < tail) {
            int p = q[head++], y = p / out[0].length, x = p % out[0].length;
            for (int d = 0; d < 4; d++) {
                int ny = y + DY[d], nx = x + DX[d];
                if (ny >= 0 && nx >= 0 && ny < out.length && nx < out[0].length && !out[ny][nx] && !big[ny][nx]) {
                    out[ny][nx] = true;
                    q[tail++] = ny * out[0].length + nx;
                }
            }
        }
        int ans = 0;
        for (int y = 0; y < rows; y++) for (int x = 0; x < cols; x++) {
            if (!loop[y][x] && !out[y * 2 + 1][x * 2 + 1]) ans++;
        }
        return ans;
    }

    int[] dirs(char c) {
        return switch (c) {
            case '|' -> new int[]{0, -1, 0, 1};
            case '-' -> new int[]{-1, 0, 1, 0};
            case 'L' -> new int[]{0, -1, 1, 0};
            case 'J' -> new int[]{0, -1, -1, 0};
            case '7' -> new int[]{-1, 0, 0, 1};
            default -> new int[]{1, 0, 0, 1};
        };
    }
}
