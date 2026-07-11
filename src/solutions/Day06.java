package src.solutions;

import src.meta.DayTemplate;

import java.util.Scanner;

public class Day06 implements DayTemplate {

    @Override
    public String[] fullSolve(Scanner in) {
        RaceInput races = parse(in, true);
        return new String[]{part1(races) + "", countWins(races.combinedTime, races.combinedDistance) + ""};
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
        RaceInput races = parse(in, !part1);
        return (part1 ? part1(races) : countWins(races.combinedTime, races.combinedDistance)) + "";
    }

    private long part1(RaceInput races) {
        long answer = 1;
        for (int i = 0; i < races.times.length; i++) {
            answer = Math.multiplyExact(answer, countWins(races.times[i], races.distances[i]));
        }
        return answer;
    }

    private long countWins(long time, long distance) {
        if (time < 0) {
            throw new IllegalArgumentException("Negative race time");
        }
        if (distance < 0) {
            return Math.addExact(time, 1);
        }
        long half = time / 2;
        if (!wins(half, time, distance)) {
            return 0;
        }
        long low = 0;
        long high = half;
        while (low < high) {
            long middle = low + (high - low) / 2;
            if (wins(middle, time, distance)) {
                high = middle;
            } else {
                low = middle + 1;
            }
        }
        return time - 2 * low + 1;
    }

    private boolean wins(long hold, long time, long distance) {
        long remaining = time - hold;
        return hold > 0 && remaining > 0 && hold > distance / remaining;
    }

    private RaceInput parse(Scanner in, boolean combine) {
        Values times = parseLine(in.nextLine(), combine);
        Values distances = parseLine(in.nextLine(), combine);
        if (times.values.length != distances.values.length) {
            throw new IllegalArgumentException("Race count mismatch");
        }
        return new RaceInput(times.values, distances.values, times.combined, distances.combined);
    }

    private Values parseLine(String line, boolean combine) {
        int colon = line.indexOf(':');
        if (colon < 0) {
            throw new IllegalArgumentException("Missing race label separator");
        }
        long[] values = new long[4];
        int count = 0;
        long current = 0;
        long combined = 0;
        boolean hasDigit = false;
        for (int index = colon + 1; index < line.length(); index++) {
            char c = line.charAt(index);
            if (c >= '0' && c <= '9') {
                int digit = c - '0';
                current = Math.addExact(Math.multiplyExact(current, 10), digit);
                if (combine) {
                    combined = Math.addExact(Math.multiplyExact(combined, 10), digit);
                }
                hasDigit = true;
            } else if (Character.isWhitespace(c)) {
                if (hasDigit) {
                    if (count == values.length) {
                        long[] grown = new long[values.length * 2];
                        System.arraycopy(values, 0, grown, 0, values.length);
                        values = grown;
                    }
                    values[count++] = current;
                    current = 0;
                    hasDigit = false;
                }
            } else {
                throw new IllegalArgumentException("Unexpected race character: " + c);
            }
        }
        if (hasDigit) {
            if (count == values.length) {
                long[] grown = new long[values.length * 2];
                System.arraycopy(values, 0, grown, 0, values.length);
                values = grown;
            }
            values[count++] = current;
        }
        long[] exact = new long[count];
        System.arraycopy(values, 0, exact, 0, count);
        return new Values(exact, combined);
    }

    private static class Values {
        private final long[] values;
        private final long combined;

        private Values(long[] values, long combined) {
            this.values = values;
            this.combined = combined;
        }
    }

    private static class RaceInput {
        private final long[] times;
        private final long[] distances;
        private final long combinedTime;
        private final long combinedDistance;

        private RaceInput(long[] times, long[] distances, long combinedTime, long combinedDistance) {
            this.times = times;
            this.distances = distances;
            this.combinedTime = combinedTime;
            this.combinedDistance = combinedDistance;
        }
    }
}
