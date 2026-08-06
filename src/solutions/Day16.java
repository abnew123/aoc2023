package src.solutions;

import src.meta.DayTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Strategy: splitter-graph condensation.
 *
 * Between split events the beam dynamics are deterministic and injective
 * (mirrors, empty cells and pointy-end splitter pass-throughs are all
 * bijective per cell), so every beam run is a straight-line trace that ends by
 * exiting the grid, arriving at a perpendicular splitter (a "split node"), or
 * closing a cycle back to its own initial state. Both perpendicular arrival
 * directions at a splitter produce the identical pair of outgoing beams, so
 * split nodes are canonical per splitter cell. The split nodes form a graph
 * with out-degree at most 2; condensing it with Tarjan's SCC algorithm yields
 * a DAG over which the full energized cell-set of every split node is memoized
 * once as a bitset (segments of all runs leaving the SCC's members, unioned
 * with the successor SCCs' bitsets). Each of the ~4*n edge starts then costs
 * only one short deterministic trace plus an O(1) memo lookup, instead of a
 * full reachability closure per start.
 */
public class Day16 implements DayTemplate {

    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;
    private static final int SPLIT = -1;

    private static final int[] DX = {-1, 1, 0, 0};
    private static final int[] DY = {0, 0, -1, 1};

    // NEXT[tileType][incomingDir] -> outgoing dir, or SPLIT.
    // tile types: 0='.', 1='/', 2='\\', 3='|', 4='-'
    private static final int[][] NEXT = {
            {LEFT, RIGHT, UP, DOWN},
            {DOWN, UP, RIGHT, LEFT},
            {UP, DOWN, LEFT, RIGHT},
            {SPLIT, SPLIT, UP, DOWN},
            {LEFT, RIGHT, SPLIT, SPLIT},
    };

    private int rows;
    private int cols;
    private int cells;
    private int gridWords;
    private byte[] tile;

    // Split-node graph: one node per splitter cell.
    private int[] splitId;    // cell -> node id, or -1
    private int[] splitCell;  // node id -> cell
    private int[] succ0;      // node id -> split node reached by first branch, or -1
    private int[] succ1;      // node id -> split node reached by second branch, or -1

    // CSR segments: cells energized by branch b of node v are
    // seg[segStart[2 * v + b] .. segStart[2 * v + b + 1]).
    private int[] segStart;
    private int[] seg;
    private int segLen;

    // Condensation memo.
    private int[] sccId;      // node id -> SCC id (assigned in Tarjan pop order)
    private long[][] sccBits; // SCC id -> energized cell bitset
    private int[] sccCnt;     // SCC id -> popcount of sccBits

    // Per-start scratch.
    private int[] pathStamp;
    private int[] pathBuf;
    private int stamp;

    @Override
    public String[] fullSolve(Scanner in) {
        generateGraph(in);
        int answer1 = tryFromLocation(-1, 0, RIGHT);
        int answer2 = runFromAllPoints();
        return new String[]{answer1 + "", answer2 + ""};
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
        generateGraph(in);
        int answer = part1 ? tryFromLocation(-1, 0, RIGHT) : runFromAllPoints();
        return answer + "";
    }

    private void generateGraph(Scanner in) {
        List<String> lines = new ArrayList<>();
        while (in.hasNextLine()) {
            lines.add(in.nextLine());
        }

        rows = lines.size();
        cols = lines.get(0).length();
        cells = rows * cols;
        gridWords = (cells + 63) >> 6;

        tile = new byte[cells];
        splitId = new int[cells];
        Arrays.fill(splitId, -1);
        int nodeCount = 0;
        for (int y = 0; y < rows; y++) {
            String line = lines.get(y);
            int base = y * cols;
            for (int x = 0; x < cols; x++) {
                byte t = switch (line.charAt(x)) {
                    case '/' -> 1;
                    case '\\' -> 2;
                    case '|' -> 3;
                    case '-' -> 4;
                    default -> 0;
                };
                tile[base + x] = t;
                if (t >= 3) {
                    splitId[base + x] = nodeCount++;
                }
            }
        }

        splitCell = new int[nodeCount];
        for (int cell = 0; cell < cells; cell++) {
            if (splitId[cell] >= 0) {
                splitCell[splitId[cell]] = cell;
            }
        }

        traceAllBranches(nodeCount);
        int sccCount = condense(nodeCount);
        fillMemo(nodeCount, sccCount);

        pathStamp = new int[cells];
        pathBuf = new int[cells];
        stamp = 0;
    }

    /** Traces both outgoing beams of every split node, recording segments and successors. */
    private void traceAllBranches(int nodeCount) {
        succ0 = new int[nodeCount];
        succ1 = new int[nodeCount];
        segStart = new int[2 * nodeCount + 1];
        seg = new int[Math.max(64, 4 * nodeCount)];
        segLen = 0;

        for (int v = 0; v < nodeCount; v++) {
            int cell = splitCell[v];
            boolean vertical = tile[cell] == 3;
            segStart[2 * v] = segLen;
            succ0[v] = traceBranch(cell, vertical ? UP : LEFT);
            segStart[2 * v + 1] = segLen;
            succ1[v] = traceBranch(cell, vertical ? DOWN : RIGHT);
        }
        segStart[2 * nodeCount] = segLen;
    }

    /**
     * Follows one deterministic beam from {@code fromCell} heading {@code dir},
     * recording every energized cell (including {@code fromCell} itself) into
     * the shared segment buffer. Ends on grid exit, on arrival at a split node
     * (returned), or on closing a cycle back to the run's first state — the
     * deterministic step map is injective, so the first repeated state of any
     * run is necessarily its initial state, making an O(1) check sufficient.
     */
    private int traceBranch(int fromCell, int dir) {
        record(fromCell);
        int x = fromCell % cols;
        int y = fromCell / cols;
        int s0 = -1;
        while (true) {
            x += DX[dir];
            y += DY[dir];
            if (x < 0 || y < 0 || x >= cols || y >= rows) {
                return -1;
            }
            int cell = y * cols + x;
            int state = (cell << 2) | dir;
            if (state == s0) {
                return -1;
            }
            if (s0 == -1) {
                s0 = state;
            }
            record(cell);
            int next = NEXT[tile[cell]][dir];
            if (next == SPLIT) {
                return splitId[cell];
            }
            dir = next;
        }
    }

    private void record(int cell) {
        if (segLen == seg.length) {
            seg = Arrays.copyOf(seg, seg.length * 2);
        }
        seg[segLen++] = cell;
    }

    /** Iterative Tarjan; SCC ids are assigned in pop order (successor SCCs first). */
    private int condense(int nodeCount) {
        sccId = new int[nodeCount];
        int[] index = new int[nodeCount];
        int[] low = new int[nodeCount];
        int[] tarjanStack = new int[nodeCount];
        boolean[] onStack = new boolean[nodeCount];
        int[] dfsNode = new int[nodeCount + 1];
        int[] dfsEdge = new int[nodeCount + 1];
        int tsp = 0;
        int idx = 0;
        int sccCount = 0;

        for (int root = 0; root < nodeCount; root++) {
            if (index[root] != 0) {
                continue;
            }
            int sp = 0;
            dfsNode[0] = root;
            dfsEdge[0] = 0;
            index[root] = low[root] = ++idx;
            tarjanStack[tsp++] = root;
            onStack[root] = true;

            while (sp >= 0) {
                int v = dfsNode[sp];
                int e = dfsEdge[sp];
                if (e < 2) {
                    dfsEdge[sp] = e + 1;
                    int w = (e == 0) ? succ0[v] : succ1[v];
                    if (w >= 0) {
                        if (index[w] == 0) {
                            sp++;
                            dfsNode[sp] = w;
                            dfsEdge[sp] = 0;
                            index[w] = low[w] = ++idx;
                            tarjanStack[tsp++] = w;
                            onStack[w] = true;
                        } else if (onStack[w] && index[w] < low[v]) {
                            low[v] = index[w];
                        }
                    }
                } else {
                    sp--;
                    if (sp >= 0 && low[v] < low[dfsNode[sp]]) {
                        low[dfsNode[sp]] = low[v];
                    }
                    if (low[v] == index[v]) {
                        int w;
                        do {
                            w = tarjanStack[--tsp];
                            onStack[w] = false;
                            sccId[w] = sccCount;
                        } while (w != v);
                        sccCount++;
                    }
                }
            }
        }
        return sccCount;
    }

    /**
     * Builds each SCC's full energized bitset in pop order: union of every
     * member's branch segments and of all successor SCCs' bitsets (already
     * complete, since Tarjan pops successors first).
     */
    private void fillMemo(int nodeCount, int sccCount) {
        int[] sccOffset = new int[sccCount + 1];
        for (int v = 0; v < nodeCount; v++) {
            sccOffset[sccId[v] + 1]++;
        }
        for (int c = 0; c < sccCount; c++) {
            sccOffset[c + 1] += sccOffset[c];
        }
        int[] sccNodes = new int[nodeCount];
        int[] cursor = Arrays.copyOf(sccOffset, sccCount);
        for (int v = 0; v < nodeCount; v++) {
            sccNodes[cursor[sccId[v]]++] = v;
        }

        sccBits = new long[sccCount][];
        sccCnt = new int[sccCount];
        for (int c = 0; c < sccCount; c++) {
            long[] bits = new long[gridWords];
            for (int i = sccOffset[c]; i < sccOffset[c + 1]; i++) {
                int v = sccNodes[i];
                int w0 = succ0[v];
                int w1 = succ1[v];
                int c0 = (w0 >= 0) ? sccId[w0] : -1;
                int c1 = (w1 >= 0) ? sccId[w1] : -1;
                if (c0 >= 0 && c0 != c) {
                    or(bits, sccBits[c0]);
                }
                if (c1 >= 0 && c1 != c && c1 != c0) {
                    or(bits, sccBits[c1]);
                }
                for (int j = segStart[2 * v]; j < segStart[2 * v + 2]; j++) {
                    int cell = seg[j];
                    bits[cell >> 6] |= 1L << (cell & 63);
                }
            }
            int count = 0;
            for (long word : bits) {
                count += Long.bitCount(word);
            }
            sccBits[c] = bits;
            sccCnt[c] = count;
        }
    }

    private static void or(long[] into, long[] from) {
        for (int i = 0; i < into.length; i++) {
            into[i] |= from[i];
        }
    }

    private int runFromAllPoints() {
        int currBest = 0;
        for (int y = 0; y < rows; y++) {
            currBest = Math.max(currBest, tryFromLocation(-1, y, RIGHT));
            currBest = Math.max(currBest, tryFromLocation(cols, y, LEFT));
        }
        for (int x = 0; x < cols; x++) {
            currBest = Math.max(currBest, tryFromLocation(x, -1, DOWN));
            currBest = Math.max(currBest, tryFromLocation(x, rows, UP));
        }
        return currBest;
    }

    /**
     * Energized-cell count for one start: trace the deterministic prefix,
     * de-duplicating its cells with a stamp; on reaching a split node, the
     * answer is the memoized SCC popcount plus the prefix cells not already in
     * the memo bitset.
     */
    private int tryFromLocation(int startX, int startY, int startDir) {
        int currentStamp = ++stamp;
        int unique = 0;
        int x = startX;
        int y = startY;
        int dir = startDir;
        int s0 = -1;
        int target = -1;
        while (true) {
            x += DX[dir];
            y += DY[dir];
            if (x < 0 || y < 0 || x >= cols || y >= rows) {
                break;
            }
            int cell = y * cols + x;
            int state = (cell << 2) | dir;
            if (state == s0) {
                break;
            }
            if (s0 == -1) {
                s0 = state;
            }
            if (pathStamp[cell] != currentStamp) {
                pathStamp[cell] = currentStamp;
                pathBuf[unique++] = cell;
            }
            int next = NEXT[tile[cell]][dir];
            if (next == SPLIT) {
                target = splitId[cell];
                break;
            }
            dir = next;
        }
        if (target < 0) {
            return unique;
        }
        int c = sccId[target];
        long[] bits = sccBits[c];
        int answer = sccCnt[c];
        for (int i = 0; i < unique; i++) {
            int cell = pathBuf[i];
            if ((bits[cell >> 6] & (1L << (cell & 63))) == 0) {
                answer++;
            }
        }
        return answer;
    }
}
