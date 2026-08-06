package src.solutions;

import src.meta.DayTemplate;

import java.util.Scanner;

/**
 * Hot Springs: counts spring arrangements with a bottom-up table DP over
 * (pattern position, groups placed), iterated as rolling primitive long[] rows
 * restricted to the feasible "wiggle" band. Each input line is parsed once into
 * persistent primitive buffers, counted for part 1 as-is, unfolded x5 in place
 * ('?' joins), and counted again for part 2 — a single streaming pass with zero
 * steady-state allocation per line beyond the Scanner's own line String.
 */
public class Day12 implements DayTemplate {

    /** Pattern bytes plus trailing '.' sentinel; sized for the unfolded record. */
    private byte[] pattern = new byte[256];
    /** Damage-group lengths; sized for the unfolded (x5) group list. */
    private int[] groups = new int[64];
    /** prefix[i] = number of non-'.' chars in pattern[0..i). */
    private int[] prefix = new int[257];
    /** Two rolling DP rows, reused across lines and parts. */
    private long[] rowA = new long[256];
    private long[] rowB = new long[256];

    @Override
    public String[] fullSolve(Scanner in) {
        long[] answers = process(in, true, true);
        return new String[]{String.valueOf(answers[0]), String.valueOf(answers[1])};
    }

    public String solve(boolean part1, Scanner in) {
        long[] answers = process(in, part1, !part1);
        return String.valueOf(part1 ? answers[0] : answers[1]);
    }

    /** Streams the input once, accumulating whichever parts are requested. */
    private long[] process(Scanner in, boolean doPart1, boolean doPart2) {
        long answer1 = 0;
        long answer2 = 0;
        while (in.hasNext()) {
            String line = in.nextLine();
            int len = line.length();

            int n = 0;
            while (n < len && line.charAt(n) != ' ') {
                n++;
            }
            ensureCapacity(5 * n + 5, 5 * ((len - n) / 2 + 1));

            byte[] pat = pattern;
            for (int i = 0; i < n; i++) {
                pat[i] = (byte) line.charAt(i);
            }

            int[] gs = groups;
            int g = 0;
            int value = 0;
            for (int i = n + 1; i < len; i++) {
                char c = line.charAt(i);
                if (c >= '0' && c <= '9') {
                    value = value * 10 + (c - '0');
                } else if (c == ',') {
                    gs[g++] = value;
                    value = 0;
                }
            }
            gs[g++] = value;

            if (doPart1) {
                pat[n] = '.';
                answer1 += count(n + 1, g);
            }
            if (doPart2) {
                for (int copy = 1; copy < 5; copy++) {
                    int base = copy * (n + 1);
                    pat[base - 1] = '?';
                    System.arraycopy(pat, 0, pat, base, n);
                    System.arraycopy(gs, 0, gs, copy * g, g);
                }
                int unfolded = 5 * n + 4;
                pat[unfolded] = '.';
                answer2 += count(unfolded + 1, 5 * g);
            }
        }
        return new long[]{answer1, answer2};
    }

    /**
     * Counts arrangements for pattern[0..m) (last char is the '.' sentinel)
     * against groups[0..groupCount). Row r holds, per band offset, the number
     * of ways to place groups 0..r with every '#' up to the current frontier
     * covered; '.'/'?'-as-operational carries counts forward along the row
     * (reset when an uncovered '#' appears at the frontier), and '?'/'#'-as-
     * damaged placements are admitted via the prefix-sum feasibility test
     * (no '.' inside the span, preceding boundary not '#').
     */
    private long count(int m, int groupCount) {
        byte[] pat = pattern;
        int[] gs = groups;
        int total = 0;
        for (int i = 0; i < groupCount; i++) {
            total += gs[i];
        }
        int wiggle = m - total - groupCount + 1;
        if (wiggle <= 0) {
            return 0;
        }

        int[] pre = prefix;
        int nonOperational = 0;
        pre[0] = 0;
        for (int i = 0; i < m; i++) {
            if (pat[i] != '.') {
                nonOperational++;
            }
            pre[i + 1] = nonOperational;
        }

        long[] previous = rowA;
        long[] current = rowB;

        long sum = 0;
        int first = gs[0];
        boolean noHashesToLeft = true;
        for (int i = 0; i < wiggle; i++) {
            if (pat[i + first] == '#') {
                sum = 0;
            } else if (noHashesToLeft && pre[i + first] - pre[i] == first) {
                sum++;
            }
            previous[i] = sum;
            noHashesToLeft &= pat[i] != '#';
        }

        int start = first + 1;
        for (int r = 1; r < groupCount; r++) {
            sum = 0;
            int length = gs[r];
            for (int offset = 0; offset < wiggle; offset++) {
                int j = start + offset;
                if (pat[j + length] == '#') {
                    sum = 0;
                } else if (previous[offset] > 0 && pat[j - 1] != '#'
                        && pre[j + length] - pre[j] == length) {
                    sum += previous[offset];
                }
                current[offset] = sum;
            }
            long[] swap = previous;
            previous = current;
            current = swap;
            start += length + 1;
        }
        return sum;
    }

    /** Grows the persistent buffers; steady state performs no allocation. */
    private void ensureCapacity(int patternLength, int groupCapacity) {
        if (pattern.length < patternLength) {
            int size = Math.max(patternLength, pattern.length * 2);
            pattern = new byte[size];
            prefix = new int[size + 1];
            rowA = new long[size];
            rowB = new long[size];
        }
        if (groups.length < groupCapacity) {
            groups = new int[Math.max(groupCapacity, groups.length * 2)];
        }
    }
}
