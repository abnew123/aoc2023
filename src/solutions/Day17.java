package src.solutions;

import src.meta.DayTemplate;
import java.util.Scanner;

/**
 * Uninformed cost-layered forward search (plain dial) over axis-collapsed
 * states, betting the incumbent A*'s guidance does not pay for itself on a
 * cold JVM.
 *
 * State = cell * 2 + axis of the run that arrived there (after a run you must
 * turn, so only the axis matters). A pop expands every legal run length lo..hi
 * in both signs of the perpendicular axis with one cumulative-cost sweep.
 * Keys are plain path costs processed in nondecreasing layers from a
 * power-of-two ring of int stacks (ring covers the max single-run weight,
 * 9 * hi), so a mask replaces both heap ordering and modulo. There is no
 * heuristic pass, no lower-bound array, and no key arithmetic beyond the
 * path cost itself; the first goal pop is exact and ends the part.
 *
 * Input is drained from the Scanner in one shot (single anchor-delimited
 * token, the cheapest cold drain measured) and scanned manually instead of
 * per-line regex matching, which dominates cold parse cost.
 */
public class Day17 implements DayTemplate {

    private static final int INF = 1_000_000_000;

    private int width;
    private int height;
    private byte[] heat;

    @Override
    public String[] fullSolve(Scanner in) {
        parse(in);
        return new String[]{String.valueOf(solveOne(1, 3)), String.valueOf(solveOne(4, 10))};
    }

    public String solve(boolean part1, Scanner in) {
        parse(in);
        return String.valueOf(part1 ? solveOne(1, 3) : solveOne(4, 10));
    }

    private void parse(Scanner in) {
        in.useDelimiter("\\A");
        String all = in.hasNext() ? in.next() : "";
        int length = all.length();
        int lineEnd = all.indexOf('\n');
        if (lineEnd < 0) {
            lineEnd = length;
        }
        int w = lineEnd > 0 && all.charAt(lineEnd - 1) == '\r' ? lineEnd - 1 : lineEnd;
        int stride = lineEnd + 1;
        int h = (length + stride) / stride;
        while ((h - 1) * stride >= length || all.charAt((h - 1) * stride) < '0') {
            h--;
        }
        width = w;
        height = h;
        byte[] grid = new byte[w * h];
        for (int y = 0; y < h; y++) {
            int rowStart = y * stride;
            int rowBase = y * w;
            for (int x = 0; x < w; x++) {
                grid[rowBase + x] = (byte) (all.charAt(rowStart + x) - '0');
            }
        }
        heat = grid;
    }

    private int solveOne(int lo, int hi) {
        int w = width;
        int h = height;
        int cells = w * h;
        int goal = cells - 1;
        int[] best = new int[cells * 2];
        java.util.Arrays.fill(best, INF);

        int ringSize = Integer.highestOneBit(9 * hi) << 1;
        int mask = ringSize - 1;
        int[][] buckets = new int[ringSize][];
        int[] sizes = new int[ringSize];
        buckets[0] = new int[64];
        best[0] = 0;
        best[1] = 0;
        buckets[0][0] = 0;
        buckets[0][1] = 1;
        sizes[0] = 2;
        int pending = 2;

        byte[] cost = heat;
        for (int key = 0; pending > 0; key++) {
            int bucket = key & mask;
            while (true) {
                int size = sizes[bucket];
                if (size == 0) {
                    break;
                }
                sizes[bucket] = size - 1;
                int state = buckets[bucket][size - 1];
                pending--;
                if (best[state] != key) {
                    continue;
                }
                int cell = state >> 1;
                if (cell == goal) {
                    return key;
                }
                int x = cell % w;
                int y = cell / w;
                // Perpendicular axis: axis 0 = arrived horizontally (or start),
                // so move vertically next; axis 1 = arrived vertically.
                if ((state & 1) == 0) {
                    int span = y;
                    int limit = hi < span ? hi : span;
                    int path = key;
                    for (int length = 1, next = cell - w; length <= limit; length++, next -= w) {
                        path += cost[next];
                        if (length >= lo) {
                            int nextState = (next << 1) | 1;
                            if (path < best[nextState]) {
                                best[nextState] = path;
                                int slot = path & mask;
                                int[] target = buckets[slot];
                                int targetSize = sizes[slot];
                                if (target == null) {
                                    target = new int[64];
                                    buckets[slot] = target;
                                } else if (targetSize == target.length) {
                                    target = java.util.Arrays.copyOf(target, targetSize << 1);
                                    buckets[slot] = target;
                                }
                                target[targetSize] = nextState;
                                sizes[slot] = targetSize + 1;
                                pending++;
                            }
                        }
                    }
                    span = h - 1 - y;
                    limit = hi < span ? hi : span;
                    path = key;
                    for (int length = 1, next = cell + w; length <= limit; length++, next += w) {
                        path += cost[next];
                        if (length >= lo) {
                            int nextState = (next << 1) | 1;
                            if (path < best[nextState]) {
                                best[nextState] = path;
                                int slot = path & mask;
                                int[] target = buckets[slot];
                                int targetSize = sizes[slot];
                                if (target == null) {
                                    target = new int[64];
                                    buckets[slot] = target;
                                } else if (targetSize == target.length) {
                                    target = java.util.Arrays.copyOf(target, targetSize << 1);
                                    buckets[slot] = target;
                                }
                                target[targetSize] = nextState;
                                sizes[slot] = targetSize + 1;
                                pending++;
                            }
                        }
                    }
                } else {
                    int span = x;
                    int limit = hi < span ? hi : span;
                    int path = key;
                    for (int length = 1, next = cell - 1; length <= limit; length++, next--) {
                        path += cost[next];
                        if (length >= lo) {
                            int nextState = next << 1;
                            if (path < best[nextState]) {
                                best[nextState] = path;
                                int slot = path & mask;
                                int[] target = buckets[slot];
                                int targetSize = sizes[slot];
                                if (target == null) {
                                    target = new int[64];
                                    buckets[slot] = target;
                                } else if (targetSize == target.length) {
                                    target = java.util.Arrays.copyOf(target, targetSize << 1);
                                    buckets[slot] = target;
                                }
                                target[targetSize] = nextState;
                                sizes[slot] = targetSize + 1;
                                pending++;
                            }
                        }
                    }
                    span = w - 1 - x;
                    limit = hi < span ? hi : span;
                    path = key;
                    for (int length = 1, next = cell + 1; length <= limit; length++, next++) {
                        path += cost[next];
                        if (length >= lo) {
                            int nextState = next << 1;
                            if (path < best[nextState]) {
                                best[nextState] = path;
                                int slot = path & mask;
                                int[] target = buckets[slot];
                                int targetSize = sizes[slot];
                                if (target == null) {
                                    target = new int[64];
                                    buckets[slot] = target;
                                } else if (targetSize == target.length) {
                                    target = java.util.Arrays.copyOf(target, targetSize << 1);
                                    buckets[slot] = target;
                                }
                                target[targetSize] = nextState;
                                sizes[slot] = targetSize + 1;
                                pending++;
                            }
                        }
                    }
                }
            }
        }
        return INF;
    }
}
