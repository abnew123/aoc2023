package src.solutions;

import src.meta.DayTemplate;

import java.util.Arrays;
import java.util.Scanner;

public class Day11 implements DayTemplate {

    @Override
    public String[] fullSolve(Scanner in) {
        Distances distances = analyze(in);
        return new String[]{
                (distances.base + distances.emptyCrossings) + "",
                (distances.base + 999999L * distances.emptyCrossings) + ""
        };
    }

    /**
     * Main solving method.
     *
     * @param part1 The solver will solve part 1 if param is set to true.
     * @param in    The solver will read data from this Scanner.
     * @return Returns answer in string format.
     */
    public String solve(boolean part1, Scanner in) {
        Distances distances = analyze(in);
        long expansion = part1 ? 1 : 999999L;
        return (distances.base + expansion * distances.emptyCrossings) + "";
    }

    private Distances analyze(Scanner in) {
        in.useDelimiter("\\A");
        String input = in.hasNext() ? in.next() : "";
        int[] rowCounts = new int[64];
        int[] columnCounts = null;
        int rows = 0;
        int galaxies = 0;
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
            if (columnCounts == null) {
                columnCounts = new int[width];
            } else if (width != columnCounts.length) {
                throw new IllegalArgumentException("Galaxy map must be rectangular");
            }
            if (rows == rowCounts.length) {
                rowCounts = Arrays.copyOf(rowCounts, rowCounts.length * 2);
            }
            for (int column = 0; column < width; column++) {
                if (input.charAt(start + column) == '#') {
                    rowCounts[rows]++;
                    columnCounts[column]++;
                    galaxies++;
                }
            }
            rows++;
        }
        if (rows == 0) {
            return new Distances(0, 0);
        }
        long base = axisDistance(rowCounts, rows) + axisDistance(columnCounts, columnCounts.length);
        long emptyCrossings = emptyCrossings(rowCounts, rows, galaxies)
                + emptyCrossings(columnCounts, columnCounts.length, galaxies);
        return new Distances(base, emptyCrossings);
    }

    private long axisDistance(int[] counts, int limit) {
        long distance = 0;
        long seen = 0;
        long positionSum = 0;
        for (int position = 0; position < limit; position++) {
            int count = counts[position];
            distance += (long) count * (seen * position - positionSum);
            seen += count;
            positionSum += (long) count * position;
        }
        return distance;
    }

    private long emptyCrossings(int[] counts, int limit, int galaxies) {
        long crossings = 0;
        long seen = 0;
        for (int position = 0; position < limit; position++) {
            int count = counts[position];
            if (count == 0) {
                crossings += seen * (galaxies - seen);
            }
            seen += count;
        }
        return crossings;
    }

    private record Distances(long base, long emptyCrossings) {
    }
}
