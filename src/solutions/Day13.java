package src.solutions;

import src.meta.DayTemplate;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Scanner;

public class Day13 implements DayTemplate {

    @Override
    public String[] fullSolve(Scanner in) {
        return analyze(readAll(in));
    }

    @Override
    public String solve(boolean part1, Scanner in) {
        return analyze(readAll(in))[part1 ? 0 : 1];
    }

    private String readAll(Scanner in) {
        in.useDelimiter("\\A");
        return in.hasNext() ? in.next() : "";
    }

    private String[] analyze(String input) {
        ExactTotal exact = new ExactTotal();
        ExactTotal smudged = new ExactTotal();
        char[] grid = new char[Math.min(Math.max(input.length(), 16), 4096)];
        int rows = 0;
        int columns = -1;
        int offset = 0;

        while (offset < input.length()) {
            int start = offset;
            while (offset < input.length()) {
                char c = input.charAt(offset);
                if (c == '\n' || c == '\r') {
                    break;
                }
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
                if (rows != 0) {
                    score(grid, rows, columns, exact, smudged);
                    rows = 0;
                    columns = -1;
                }
                continue;
            }
            if (columns < 0) {
                columns = width;
            } else if (width != columns) {
                throw new IllegalArgumentException("Pattern rows must have equal widths");
            }
            int required = (rows + 1) * columns;
            if (required > grid.length) {
                grid = Arrays.copyOf(grid, Math.max(required, grid.length * 2));
            }
            for (int column = 0; column < columns; column++) {
                char c = input.charAt(start + column);
                if (c != '.' && c != '#') {
                    throw new IllegalArgumentException("Unexpected pattern character " + c);
                }
                grid[rows * columns + column] = c;
            }
            rows++;
        }
        if (rows != 0) {
            score(grid, rows, columns, exact, smudged);
        }
        return new String[] {exact.toString(), smudged.toString()};
    }

    private void score(char[] grid, int rows, int columns,
                       ExactTotal exact, ExactTotal smudged) {
        for (int split = 1; split < columns; split++) {
            int mismatches = verticalMismatches(grid, rows, columns, split);
            if (mismatches == 0) {
                exact.add(split);
            } else if (mismatches == 1) {
                smudged.add(split);
            }
        }
        for (int split = 1; split < rows; split++) {
            int mismatches = horizontalMismatches(grid, rows, columns, split);
            if (mismatches == 0) {
                exact.add(100L * split);
            } else if (mismatches == 1) {
                smudged.add(100L * split);
            }
        }
    }

    private int verticalMismatches(char[] grid, int rows, int columns, int split) {
        int mismatches = 0;
        for (int left = split - 1, right = split;
             left >= 0 && right < columns; left--, right++) {
            for (int row = 0; row < rows; row++) {
                if (grid[row * columns + left] != grid[row * columns + right]
                        && ++mismatches > 1) {
                    return mismatches;
                }
            }
        }
        return mismatches;
    }

    private int horizontalMismatches(char[] grid, int rows, int columns, int split) {
        int mismatches = 0;
        for (int upper = split - 1, lower = split;
             upper >= 0 && lower < rows; upper--, lower++) {
            int upperOffset = upper * columns;
            int lowerOffset = lower * columns;
            for (int column = 0; column < columns; column++) {
                if (grid[upperOffset + column] != grid[lowerOffset + column]
                        && ++mismatches > 1) {
                    return mismatches;
                }
            }
        }
        return mismatches;
    }

    private static final class ExactTotal {
        private long small;
        private BigInteger big;

        private void add(long value) {
            if (big != null) {
                big = big.add(BigInteger.valueOf(value));
                return;
            }
            try {
                small = Math.addExact(small, value);
            } catch (ArithmeticException e) {
                big = BigInteger.valueOf(small).add(BigInteger.valueOf(value));
            }
        }

        @Override
        public String toString() {
            return big == null ? Long.toString(small) : big.toString();
        }
    }
}
