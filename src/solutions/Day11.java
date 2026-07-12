package src.solutions;

import src.meta.DayTemplate;

import java.util.ArrayList;
import java.util.List;
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
        List<String> lines = new ArrayList<>();
        while (in.hasNextLine()) {
            lines.add(in.nextLine());
        }
        if (lines.isEmpty()) {
            return new Distances(0, 0);
        }

        int[] rowCounts = new int[lines.size()];
        int[] columnCounts = new int[lines.get(0).length()];
        int galaxies = 0;
        for (int row = 0; row < lines.size(); row++) {
            String line = lines.get(row);
            if (line.length() != columnCounts.length) {
                throw new IllegalArgumentException("Galaxy map must be rectangular");
            }
            for (int column = 0; column < line.length(); column++) {
                if (line.charAt(column) == '#') {
                    rowCounts[row]++;
                    columnCounts[column]++;
                    galaxies++;
                }
            }
        }

        long base = axisDistance(rowCounts) + axisDistance(columnCounts);
        long emptyCrossings = emptyCrossings(rowCounts, galaxies) + emptyCrossings(columnCounts, galaxies);
        return new Distances(base, emptyCrossings);
    }

    private long axisDistance(int[] counts) {
        long distance = 0;
        long seen = 0;
        long positionSum = 0;
        for (int position = 0; position < counts.length; position++) {
            int count = counts[position];
            distance += (long) count * (seen * position - positionSum);
            seen += count;
            positionSum += (long) count * position;
        }
        return distance;
    }

    private long emptyCrossings(int[] counts, int galaxies) {
        long crossings = 0;
        long seen = 0;
        for (int count : counts) {
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
