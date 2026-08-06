package src.solutions;

import src.meta.DayTemplate;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Day 21: Step Counter.
 *
 * Flat-array rewrite. The whole input is slurped in one Scanner token and a
 * single array-queue BFS over the tile computes exact plot distances (no
 * boxing, no hashing). Part 1 counts cells with distance at most 64 and
 * matching parity. Part 2 (26501365 steps, the statement's parameter)
 * verifies the structural preconditions of the standard quadratic-growth
 * argument: square odd-sized grid, centered start, rock-free start row and
 * column, rock-free border, step count congruent to half the grid size
 * modulo the grid size, and no reachable pocket deeper than grid + half.
 * When they hold, the three diamond samples f(h), f(h + R), f(h + 2R) are
 * derived from the tile distance field and extrapolated with Newton's
 * forward-difference quadratic. Otherwise the solver falls back to a direct
 * breadth-first count over the virtually tiled plane (bitset visited set,
 * two flat frontier arrays), which keeps it correct for grids like the
 * official example that lack the open cross (16 plots at 6 steps).
 */
public class Day21 implements DayTemplate {

    private static final int PART1_STEPS = 64;
    private static final long PART2_STEPS = 26501365L;
    private static final int MAX_DIRECT_STEPS = 16000;

    private int rows;
    private int cols;
    private int startRow;
    private int startCol;
    private boolean[] rock;
    private int[] dist;

    @Override
    public String[] fullSolve(Scanner in) {
        parse(in);
        bfs();
        return new String[]{Long.toString(part1Count(PART1_STEPS)),
                Long.toString(part2Count(PART2_STEPS))};
    }

    /**
     * Main solving method.
     *
     * @param part1 The solver will solve part 1 if param is set to true.
     *              The solver will solve part 2 if param is set to false.
     * @param in    The solver will read data from this Scanner.
     * @return Returns answer in string format.
     */
    public String solve(boolean part1, Scanner in) {
        parse(in);
        bfs();
        return Long.toString(part1 ? part1Count(PART1_STEPS) : part2Count(PART2_STEPS));
    }

    void parse(Scanner in) {
        String raw = in.useDelimiter("\\A").next();
        int length = raw.length();
        int firstBreak = raw.indexOf('\n');
        if (firstBreak < 0) {
            firstBreak = length;
        }
        int width = firstBreak;
        if (width > 0 && raw.charAt(width - 1) == '\r') {
            width--;
        }
        if (width == 0) {
            throw new IllegalStateException("Empty grid");
        }
        cols = width;
        int lineCount = 0;
        for (int pos = 0; pos < length; ) {
            int eol = raw.indexOf('\n', pos);
            int end = eol < 0 ? length : eol;
            int len = end - pos;
            if (len > 0 && raw.charAt(end - 1) == '\r') {
                len--;
            }
            if (len > 0) {
                if (len != cols) {
                    throw new IllegalStateException("Ragged grid");
                }
                lineCount++;
            }
            pos = end + 1;
        }
        rows = lineCount;
        rock = new boolean[rows * cols];
        startRow = -1;
        startCol = -1;
        int row = 0;
        for (int pos = 0; pos < length; ) {
            int eol = raw.indexOf('\n', pos);
            int end = eol < 0 ? length : eol;
            int len = end - pos;
            if (len > 0 && raw.charAt(end - 1) == '\r') {
                len--;
            }
            if (len > 0) {
                int base = row * cols;
                for (int col = 0; col < len; col++) {
                    char ch = raw.charAt(pos + col);
                    if (ch == '#') {
                        rock[base + col] = true;
                    } else if (ch == 'S') {
                        startRow = row;
                        startCol = col;
                    }
                }
                row++;
            }
            pos = end + 1;
        }
        if (startRow < 0) {
            throw new IllegalStateException("Missing start");
        }
    }

    void bfs() {
        int c = cols;
        int n = rows * c;
        int[] d = new int[n];
        Arrays.fill(d, -1);
        int[] queue = new int[n];
        boolean[] blocked = rock;
        int head = 0;
        int tail = 0;
        int start = startRow * c + startCol;
        d[start] = 0;
        queue[tail++] = start;
        while (head < tail) {
            int cell = queue[head++];
            int next = d[cell] + 1;
            int col = cell % c;
            int left = cell - 1;
            if (col > 0 && !blocked[left] && d[left] < 0) {
                d[left] = next;
                queue[tail++] = left;
            }
            int right = cell + 1;
            if (col + 1 < c && !blocked[right] && d[right] < 0) {
                d[right] = next;
                queue[tail++] = right;
            }
            int up = cell - c;
            if (up >= 0 && !blocked[up] && d[up] < 0) {
                d[up] = next;
                queue[tail++] = up;
            }
            int down = cell + c;
            if (down < n && !blocked[down] && d[down] < 0) {
                d[down] = next;
                queue[tail++] = down;
            }
        }
        dist = d;
    }

    long part1Count(int steps) {
        int parity = steps & 1;
        long count = 0;
        for (int v : dist) {
            if (v >= 0 && v <= steps && (v & 1) == parity) {
                count++;
            }
        }
        return count;
    }

    long part2Count(long steps) {
        if (structured(steps)) {
            int half = rows / 2;
            int matchParity = half & 1;
            long a = 0;
            long b = 0;
            long aCorner = 0;
            long bCorner = 0;
            for (int v : dist) {
                if (v < 0) {
                    continue;
                }
                if ((v & 1) == matchParity) {
                    a++;
                    if (v > half) {
                        aCorner++;
                    }
                } else {
                    b++;
                    if (v > half) {
                        bCorner++;
                    }
                }
            }
            long s0 = a - aCorner;
            long s1 = 4 * a + b - 2 * aCorner + bCorner;
            long s2 = 9 * a + 4 * b - 3 * aCorner + 2 * bCorner;
            long k = (steps - half) / rows;
            return s0 + (s1 - s0) * k + (s2 - 2 * s1 + s0) * (k * (k - 1) / 2);
        }
        return directTiledCount(steps);
    }

    boolean structured(long steps) {
        if (rows != cols || (rows & 1) == 0) {
            return false;
        }
        int half = rows / 2;
        if (startRow != half || startCol != half || steps % rows != half) {
            return false;
        }
        int last = rows - 1;
        for (int i = 0; i < cols; i++) {
            if (rock[half * cols + i] || rock[i] || rock[last * cols + i]) {
                return false;
            }
        }
        for (int i = 0; i < rows; i++) {
            if (rock[i * cols + half] || rock[i * cols] || rock[i * cols + last]) {
                return false;
            }
        }
        int limit = rows + half;
        for (int v : dist) {
            if (v > limit) {
                return false;
            }
        }
        return true;
    }

    /**
     * Exact reachable-plot count on the infinite tiled plane, by plain BFS in
     * a virtual window of radius {@code steps} around the start. Used for
     * grids without the quadratic structure; feasible only for small step
     * counts.
     */
    long directTiledCount(long stepsLong) {
        if (stepsLong <= 0) {
            return stepsLong == 0 && !rock[startRow * cols + startCol] ? 1 : 0;
        }
        if (stepsLong > MAX_DIRECT_STEPS) {
            throw new IllegalStateException("Grid lacks quadratic structure and " + stepsLong
                    + " steps is too large for direct search");
        }
        int steps = (int) stepsLong;
        int span = 2 * steps + 1;
        int[] rowBase = new int[span];
        int value = Math.floorMod(startRow - steps, rows);
        for (int i = 0; i < span; i++) {
            rowBase[i] = value * cols;
            value++;
            if (value == rows) {
                value = 0;
            }
        }
        int[] colLookup = new int[span];
        value = Math.floorMod(startCol - steps, cols);
        for (int i = 0; i < span; i++) {
            colLookup[i] = value;
            value++;
            if (value == cols) {
                value = 0;
            }
        }
        long[] seen = new long[(int) ((((long) span * span) + 63) >> 6)];
        int[] cur = new int[64];
        int[] nxt = new int[64];
        int center = steps * span + steps;
        seen[center >>> 6] |= 1L << center;
        cur[0] = center;
        int curSize = 1;
        long even = 1;
        long odd = 0;
        boolean[] blocked = rock;
        for (int d = 1; d <= steps && curSize > 0; d++) {
            int nxtSize = 0;
            for (int i = 0; i < curSize; i++) {
                int cell = cur[i];
                int vr = cell / span;
                int vc = cell - vr * span;
                int base = rowBase[vr];
                int left = cell - 1;
                if ((seen[left >>> 6] & (1L << left)) == 0
                        && !blocked[base + colLookup[vc - 1]]) {
                    seen[left >>> 6] |= 1L << left;
                    if (nxtSize == nxt.length) {
                        nxt = Arrays.copyOf(nxt, nxt.length * 2);
                    }
                    nxt[nxtSize++] = left;
                }
                int right = cell + 1;
                if ((seen[right >>> 6] & (1L << right)) == 0
                        && !blocked[base + colLookup[vc + 1]]) {
                    seen[right >>> 6] |= 1L << right;
                    if (nxtSize == nxt.length) {
                        nxt = Arrays.copyOf(nxt, nxt.length * 2);
                    }
                    nxt[nxtSize++] = right;
                }
                int col = colLookup[vc];
                int up = cell - span;
                if ((seen[up >>> 6] & (1L << up)) == 0
                        && !blocked[rowBase[vr - 1] + col]) {
                    seen[up >>> 6] |= 1L << up;
                    if (nxtSize == nxt.length) {
                        nxt = Arrays.copyOf(nxt, nxt.length * 2);
                    }
                    nxt[nxtSize++] = up;
                }
                int down = cell + span;
                if ((seen[down >>> 6] & (1L << down)) == 0
                        && !blocked[rowBase[vr + 1] + col]) {
                    seen[down >>> 6] |= 1L << down;
                    if (nxtSize == nxt.length) {
                        nxt = Arrays.copyOf(nxt, nxt.length * 2);
                    }
                    nxt[nxtSize++] = down;
                }
            }
            if ((d & 1) == 0) {
                even += nxtSize;
            } else {
                odd += nxtSize;
            }
            int[] swap = cur;
            cur = nxt;
            nxt = swap;
            curSize = nxtSize;
        }
        return (steps & 1) == 0 ? even : odd;
    }
}
