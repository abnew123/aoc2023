package src.solutions;

import src.meta.DayTemplate;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Day 22: Sand Slabs.
 *
 * Bricks settle through a per-cell height map (top height + top brick per
 * ground cell), and both answers then come from the dominator tree of the
 * support DAG instead of one cascade walk per brick.
 *
 * Settling in ascending original height numbers every supporter before the
 * brick it supports, so settle order is a topological order of the support
 * DAG. Over a DAG in topological order the immediate dominator of a node is
 * the nearest common ancestor of its supporters in the dominator tree built
 * so far, so a single pass with pairwise NCA intersection (walking the
 * larger id up its idom chain) builds the whole tree while the bricks drop.
 *
 * Disintegrating brick B makes brick F fall exactly when every path from the
 * ground to F passes through B, i.e. when B strictly dominates F. Therefore
 * part 2 is the sum over bricks of (dominator-tree depth - 1), counting the
 * virtual ground root as depth 0, and part 1 counts the bricks that are
 * nobody's immediate dominator (a brick strictly dominates some brick if and
 * only if it is the immediate dominator of some brick).
 */
public class Day22 implements DayTemplate {

    @Override
    public String[] fullSolve(Scanner in) {
        long[] answers = solveBoth(in);
        return new String[]{answers[0] + "", answers[1] + ""};
    }

    @Override
    public String solve(boolean part1, Scanner in) {
        long[] answers = solveBoth(in);
        return (part1 ? answers[0] : answers[1]) + "";
    }

    private static long[] solveBoth(Scanner in) {
        String text = in.findWithinHorizon("(?s).*", 0);
        int length = text == null ? 0 : text.length();

        int n = 0;
        for (int i = 0; i < length; i++) {
            if (text.charAt(i) == '~') {
                n++;
            }
        }
        if (n == 0) {
            return new long[]{0, 0};
        }

        int[] xLo = new int[n];
        int[] yLo = new int[n];
        int[] zLo = new int[n];
        int[] xHi = new int[n];
        int[] yHi = new int[n];
        int[] zHi = new int[n];
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        int[] values = new int[6];
        int valueCount = 0;
        int count = 0;
        int index = 0;
        while (index < length && count < n) {
            char c = text.charAt(index);
            if (c == '-' || (c >= '0' && c <= '9')) {
                int sign = 1;
                if (c == '-') {
                    sign = -1;
                    index++;
                }
                int value = 0;
                boolean any = false;
                while (index < length) {
                    c = text.charAt(index);
                    if (c < '0' || c > '9') {
                        break;
                    }
                    value = value * 10 + (c - '0');
                    any = true;
                    index++;
                }
                if (any) {
                    values[valueCount++] = sign * value;
                    if (valueCount == 6) {
                        valueCount = 0;
                        int x0 = Math.min(values[0], values[3]);
                        int y0 = Math.min(values[1], values[4]);
                        xLo[count] = x0;
                        yLo[count] = y0;
                        zLo[count] = Math.min(values[2], values[5]);
                        xHi[count] = Math.max(values[0], values[3]);
                        yHi[count] = Math.max(values[1], values[4]);
                        zHi[count] = Math.max(values[2], values[5]);
                        if (x0 < minX) {
                            minX = x0;
                        }
                        if (xHi[count] > maxX) {
                            maxX = xHi[count];
                        }
                        if (y0 < minY) {
                            minY = y0;
                        }
                        if (yHi[count] > maxY) {
                            maxY = yHi[count];
                        }
                        count++;
                    }
                }
            } else {
                index++;
            }
        }
        n = count;
        if (n == 0) {
            return new long[]{0, 0};
        }

        long[] order = new long[n];
        for (int i = 0; i < n; i++) {
            order[i] = ((long) zLo[i] << 32) | i;
        }
        Arrays.sort(order);

        int gridHeight = maxY - minY + 1;
        int cells = (maxX - minX + 1) * gridHeight;
        int[] topHeight = new int[cells];
        int[] topBrick = new int[cells];
        Arrays.fill(topBrick, -1);

        int[] idom = new int[n];
        int[] depth = new int[n];
        boolean[] fellsSomething = new boolean[n];
        long chainSum = 0;

        for (int id = 0; id < n; id++) {
            int brick = (int) order[id];
            int x0 = xLo[brick] - minX;
            int x1 = xHi[brick] - minX;
            int y0 = yLo[brick] - minY;
            int y1 = yHi[brick] - minY;

            int support = 0;
            for (int x = x0; x <= x1; x++) {
                int base = x * gridHeight;
                for (int y = y0; y <= y1; y++) {
                    int h = topHeight[base + y];
                    if (h > support) {
                        support = h;
                    }
                }
            }

            int dom = -2;
            if (support > 0) {
                int last = -1;
                for (int x = x0; x <= x1; x++) {
                    int base = x * gridHeight;
                    for (int y = y0; y <= y1; y++) {
                        int cell = base + y;
                        if (topHeight[cell] != support) {
                            continue;
                        }
                        int s = topBrick[cell];
                        if (s == last) {
                            continue;
                        }
                        last = s;
                        if (dom == -2) {
                            dom = s;
                        } else if (dom != s) {
                            int a = dom;
                            int b = s;
                            while (a != b) {
                                while (a > b) {
                                    a = idom[a];
                                }
                                while (b > a) {
                                    b = idom[b];
                                }
                            }
                            dom = a;
                        }
                    }
                }
            }
            if (dom == -2) {
                dom = -1;
            }

            idom[id] = dom;
            int d = dom < 0 ? 1 : depth[dom] + 1;
            depth[id] = d;
            chainSum += d - 1;
            if (dom >= 0) {
                fellsSomething[dom] = true;
            }

            int newTop = support + zHi[brick] - zLo[brick] + 1;
            for (int x = x0; x <= x1; x++) {
                int base = x * gridHeight;
                for (int y = y0; y <= y1; y++) {
                    topHeight[base + y] = newTop;
                    topBrick[base + y] = id;
                }
            }
        }

        long safe = 0;
        for (int id = 0; id < n; id++) {
            if (!fellsSomething[id]) {
                safe++;
            }
        }
        return new long[]{safe, chainSum};
    }
}
