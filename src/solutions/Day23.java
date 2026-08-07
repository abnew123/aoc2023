package src.solutions;

import src.meta.DayTemplate;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Longest simple path through the trail maze.
 *
 * The maze is contracted to its junction graph: every open cell whose open-neighbour count is
 * not exactly 2 (dead ends, forks, plus the entrance and exit) becomes a node, and corridors
 * between nodes become weighted edges found by walking each corridor once. Node count adapts
 * to the input; adjacency lives in flat primitive arrays (max degree 4, one corridor per
 * direction). Both parts then run the same iterative longest-path DFS over int node ids with
 * a long visited bitmask (with a recursive boolean[] fallback if a graph ever exceeds 64
 * nodes).
 *
 * Part 1 walks corridors respecting slope arrows (a slope cell can only be exited in its
 * arrow direction, so a slope against the walk kills that corridor), yielding a directed
 * graph whose search space is tiny; no pruning is needed.
 *
 * Part 2 (slopes ignored, undirected) is where the work is, and the search is branch and
 * bound:
 *  - the forced entrance and exit corridors are collapsed into a constant, so the search
 *    runs between the first and last real junctions and stops the moment it reaches the
 *    target (any path through the exit-adjacent junction that does not finish there can
 *    never come back);
 *  - adjacency lists are sorted by descending edge weight so heavy paths are found early
 *    and the incumbent best rises fast;
 *  - before descending into a child, a bitmask flood fill over unvisited nodes checks the
 *    target is still reachable; this soundly kills every branch that has walled itself off,
 *    including all "walked backward along the perimeter" pockets;
 *  - an admissible upper bound prunes the rest: twice any completion's remaining length is
 *    at most the sum, over junctions still reachable, of their two heaviest edges into the
 *    reachable set (only the heaviest for the current node and the target, whose path
 *    degree is 1), so a branch is cut when len + cap/2 cannot beat the incumbent.
 */
public class Day23 implements DayTemplate {

    /** Directions: 0 = up, 1 = down, 2 = left, 3 = right (indices must match slopeDir). */
    private static final int[] DR = {-1, 1, 0, 0};
    private static final int[] DC = {0, 0, -1, 1};

    @Override
    public String[] fullSolve(Scanner in) {
        char[][] grid = parse(in);
        return new String[]{run(true, grid), run(false, grid)};
    }

    @Override
    public String solve(boolean part1, Scanner in) {
        return run(part1, parse(in));
    }

    private static char[][] parse(Scanner in) {
        in.useDelimiter("\\A");
        String input = in.hasNext() ? in.next() : "";
        char[][] rows = new char[16][];
        int count = 0;
        int offset = 0;
        while (offset < input.length()) {
            int start = offset;
            while (offset < input.length() && input.charAt(offset) != '\n'
                    && input.charAt(offset) != '\r') {
                offset++;
            }
            int end = offset;
            if (offset < input.length()) {
                char ending = input.charAt(offset++);
                if (ending == '\r' && offset < input.length() && input.charAt(offset) == '\n') {
                    offset++;
                }
            }
            int width = end - start;
            if (width == 0) {
                continue;
            }
            if (count == rows.length) {
                rows = Arrays.copyOf(rows, rows.length * 2);
            }
            char[] row = new char[width];
            for (int column = 0; column < width; column++) {
                row[column] = input.charAt(start + column);
            }
            rows[count++] = row;
        }
        return count == rows.length ? rows : Arrays.copyOf(rows, count);
    }

    private static int slopeDir(char c) {
        return switch (c) {
            case '^' -> 0;
            case 'v' -> 1;
            case '<' -> 2;
            case '>' -> 3;
            default -> -1;
        };
    }

    private static String run(boolean part1, char[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        // --- Junction detection: open cells whose open-neighbour count is 1 or > 2. ---
        int[][] id = new int[rows][cols];
        for (int[] row : id) {
            Arrays.fill(row, -1);
        }
        int count = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == '#') {
                    continue;
                }
                int open = 0;
                for (int k = 0; k < 4; k++) {
                    int ar = r + DR[k];
                    int ac = c + DC[k];
                    if (ar >= 0 && ac >= 0 && ar < rows && ac < cols && grid[ar][ac] != '#') {
                        open++;
                    }
                }
                if (open == 1 || open > 2) {
                    id[r][c] = count++;
                }
            }
        }
        int startCol = -1;
        int endCol = -1;
        for (int c = 0; c < cols; c++) {
            if (startCol < 0 && grid[0][c] != '#') {
                startCol = c;
            }
            if (endCol < 0 && grid[rows - 1][c] != '#') {
                endCol = c;
            }
        }
        // The entrance and exit are nodes even if a degenerate input gives them degree 2.
        if (id[0][startCol] < 0) {
            id[0][startCol] = count++;
        }
        if (id[rows - 1][endCol] < 0) {
            id[rows - 1][endCol] = count++;
        }
        int v = count;
        int[] nodeR = new int[v];
        int[] nodeC = new int[v];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int u = id[r][c];
                if (u >= 0) {
                    nodeR[u] = r;
                    nodeC[u] = c;
                }
            }
        }

        // --- Contraction: walk every corridor once per end; flat arrays, degree <= 4. ---
        int[] deg = new int[v];
        int[] nbr = new int[v << 2];
        int[] wt = new int[v << 2];
        for (int u = 0; u < v; u++) {
            for (int d = 0; d < 4; d++) {
                long packed = walk(grid, id, nodeR[u], nodeC[u], d, part1);
                if (packed >= 0) {
                    int other = (int) (packed >>> 32);
                    if (other != u) {
                        int slot = (u << 2) + deg[u]++;
                        nbr[slot] = other;
                        wt[slot] = (int) packed;
                    }
                }
            }
        }
        int start = id[0][startCol];
        int end = id[rows - 1][endCol];

        if (v > 64) {
            // Correctness fallback for exotic inputs; real inputs have ~36 junctions.
            int best = searchBig(start, end, new boolean[v], 0, deg, nbr, wt);
            return String.valueOf(Math.max(best, 0));
        }

        // Heaviest edges first: the incumbent rises fast, which powers the bound prune.
        for (int u = 0; u < v; u++) {
            int base = u << 2;
            for (int a = 1; a < deg[u]; a++) {
                int nw = wt[base + a];
                int nn = nbr[base + a];
                int b = a - 1;
                while (b >= 0 && wt[base + b] < nw) {
                    wt[base + b + 1] = wt[base + b];
                    nbr[base + b + 1] = nbr[base + b];
                    b--;
                }
                wt[base + b + 1] = nw;
                nbr[base + b + 1] = nn;
            }
        }
        long[] adjMask = new long[v];
        for (int u = 0; u < v; u++) {
            int base = u << 2;
            for (int j = 0; j < deg[u]; j++) {
                adjMask[u] |= 1L << nbr[base + j];
            }
        }

        int s = start;
        int t = end;
        int bonus = 0;
        long visited = 0;
        if (!part1) {
            // Collapse the forced corridors hanging off the entrance and the exit.
            if (deg[start] == 1) {
                visited |= 1L << start;
                bonus += wt[start << 2];
                s = nbr[start << 2];
            }
            if (s == end) {
                return String.valueOf(bonus);
            }
            if (deg[end] == 1) {
                visited |= 1L << end;
                bonus += wt[end << 2];
                t = nbr[end << 2];
            }
            if (s == t) {
                return String.valueOf(bonus);
            }
        }
        visited |= 1L << s;
        int best = search(s, t, visited, v, deg, nbr, wt, adjMask, !part1);
        return String.valueOf(best < 0 ? 0 : bonus + best);
    }

    /**
     * Walks the corridor leaving node cell (r, c) in direction d until the next node cell.
     * Returns (nodeId << 32) | steps, or -1 if the walk dies against a wall or, in part 1,
     * against a slope arrow pointing back along the walk.
     */
    private static long walk(char[][] grid, int[][] id, int r, int c, int d, boolean part1) {
        int rows = grid.length;
        int cols = grid[0].length;
        if (part1) {
            int s = slopeDir(grid[r][c]);
            if (s >= 0 && s != d) {
                return -1;
            }
        }
        int nr = r + DR[d];
        int nc = c + DC[d];
        if (nr < 0 || nc < 0 || nr >= rows || nc >= cols || grid[nr][nc] == '#') {
            return -1;
        }
        int pr = r;
        int pc = c;
        int len = 1;
        while (id[nr][nc] < 0) {
            int forced = part1 ? slopeDir(grid[nr][nc]) : -1;
            int tr = -1;
            int tc = -1;
            for (int k = 0; k < 4; k++) {
                if (forced >= 0 && k != forced) {
                    continue;
                }
                int ar = nr + DR[k];
                int ac = nc + DC[k];
                if ((ar == pr && ac == pc) || ar < 0 || ac < 0 || ar >= rows || ac >= cols
                        || grid[ar][ac] == '#') {
                    continue;
                }
                tr = ar;
                tc = ac;
            }
            if (tr < 0) {
                return -1;
            }
            pr = nr;
            pc = nc;
            nr = tr;
            nc = tc;
            len++;
        }
        return ((long) id[nr][nc] << 32) | len;
    }

    /**
     * Iterative longest simple path from s to t; the path ends on first arrival at t.
     * Returns -1 if t is unreachable. With prune set, applies the flood-fill reachability
     * check and the admissible remaining-length bound before descending into any child.
     */
    private static int search(int s, int t, long visited, int v, int[] deg, int[] nbr, int[] wt,
                              long[] adjMask, boolean prune) {
        int[] stackNode = new int[v + 1];
        int[] stackIter = new int[v + 1];
        int[] stackLen = new int[v + 1];
        stackNode[0] = s;
        int sp = 0;
        int best = -1;
        while (sp >= 0) {
            int u = stackNode[sp];
            int i = stackIter[sp]++;
            if (i >= deg[u]) {
                visited &= ~(1L << u);
                sp--;
                continue;
            }
            int slot = (u << 2) + i;
            int nxt = nbr[slot];
            if ((visited >>> nxt & 1L) != 0) {
                continue;
            }
            int len = stackLen[sp] + wt[slot];
            if (nxt == t) {
                if (len > best) {
                    best = len;
                }
                continue;
            }
            if (prune && pruned(nxt, t, len, best, visited, deg, nbr, wt, adjMask)) {
                continue;
            }
            sp++;
            stackNode[sp] = nxt;
            stackIter[sp] = 0;
            stackLen[sp] = len;
            visited |= 1L << nxt;
        }
        return best;
    }

    /**
     * True if the branch entering nxt with running length len provably cannot beat best.
     *
     * Flood fill: any completion nxt -> t is a path through unvisited nodes, so t must stay
     * reachable from nxt in the unvisited-induced subgraph.
     *
     * Bound: a completion visits distinct nodes inside the flooded set, contributing two
     * path edges per interior node and one at nxt and t; every such edge joins two nodes of
     * the flooded set, so it is counted from both ends. Summing each flooded node's two
     * heaviest edges into the flooded set (heaviest only for nxt and t) therefore counts at
     * least twice the best completion, giving remaining length <= cap / 2.
     */
    private static boolean pruned(int nxt, int t, int len, int best, long visited,
                                  int[] deg, int[] nbr, int[] wt, long[] adjMask) {
        long free = ~visited;
        long reach = 1L << nxt;
        long frontier = reach;
        while (frontier != 0) {
            long grow = 0;
            do {
                int x = Long.numberOfTrailingZeros(frontier);
                frontier &= frontier - 1;
                grow |= adjMask[x];
            } while (frontier != 0);
            frontier = grow & free & ~reach;
            reach |= frontier;
        }
        if ((reach >>> t & 1L) == 0) {
            return true;
        }
        int cap = 0;
        long m = reach;
        do {
            int x = Long.numberOfTrailingZeros(m);
            m &= m - 1;
            int base = x << 2;
            int dx = deg[x];
            int hi = 0;
            int lo = 0;
            for (int j = 0; j < dx; j++) {
                if ((reach >>> nbr[base + j] & 1L) != 0) {
                    int w = wt[base + j];
                    if (w > hi) {
                        lo = hi;
                        hi = w;
                    } else if (w > lo) {
                        lo = w;
                    }
                }
            }
            cap += (x == nxt || x == t) ? hi : hi + lo;
        } while (m != 0);
        return len + (cap >> 1) <= best;
    }

    /** Recursive fallback for junction graphs beyond 64 nodes; same path semantics. */
    private static int searchBig(int u, int t, boolean[] vis, int len,
                                 int[] deg, int[] nbr, int[] wt) {
        if (u == t) {
            return len;
        }
        vis[u] = true;
        int best = -1;
        int base = u << 2;
        for (int j = 0; j < deg[u]; j++) {
            int nxt = nbr[base + j];
            if (!vis[nxt]) {
                int r = searchBig(nxt, t, vis, len + wt[base + j], deg, nbr, wt);
                if (r > best) {
                    best = r;
                }
            }
        }
        vis[u] = false;
        return best;
    }
}
