package src.solutions;

import src.meta.DayTemplate;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Scanner;

public class Day09 implements DayTemplate {

    @Override
    public String[] fullSolve(Scanner in) {
        Answers answers = analyze(in);
        return new String[]{answers.next(), answers.previous()};
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
        Answers answers = analyze(in);
        return part1 ? answers.next() : answers.previous();
    }

    private Answers analyze(Scanner in) {
        in.useDelimiter("\\A");
        String input = in.hasNext() ? in.next() : "";
        ExactTotal nextTotal = new ExactTotal();
        ExactTotal previousTotal = new ExactTotal();
        long[] scratch = new long[32];
        int length = input.length();
        int lineStart = 0;
        while (lineStart < length) {
            int lineEnd = lineStart;
            while (lineEnd < length && input.charAt(lineEnd) != '\n') {
                lineEnd++;
            }
            int trimmedEnd = lineEnd;
            if (trimmedEnd > lineStart && input.charAt(trimmedEnd - 1) == '\r') {
                trimmedEnd--;
            }
            try {
                LongLine values = parseLongs(input, lineStart, trimmedEnd, scratch);
                scratch = values.values();
                if (values.size() > 0) {
                    LongAnswers answers = extrapolate(values.values(), values.size());
                    nextTotal.add(answers.next());
                    previousTotal.add(answers.previous());
                }
            } catch (ArithmeticException | NumberFormatException e) {
                BigAnswers answers = extrapolate(parseBigIntegers(input, lineStart, trimmedEnd));
                nextTotal.add(answers.next());
                previousTotal.add(answers.previous());
            }
            lineStart = lineEnd + 1;
        }
        return new Answers(nextTotal.toString(), previousTotal.toString());
    }

    private LongLine parseLongs(String line, int from, int end, long[] values) {
        int size = 0;
        int i = from;
        while (i < end) {
            while (i < end && Character.isWhitespace(line.charAt(i))) {
                i++;
            }
            if (i == end) {
                break;
            }
            int start = i;
            while (i < end && !Character.isWhitespace(line.charAt(i))) {
                i++;
            }
            if (size == values.length) {
                values = Arrays.copyOf(values, values.length * 2);
            }
            values[size++] = Long.parseLong(line, start, i, 10);
        }
        return new LongLine(values, size);
    }

    private BigInteger[] parseBigIntegers(String line, int from, int end) {
        BigInteger[] values = new BigInteger[32];
        int size = 0;
        int i = from;
        while (i < end) {
            while (i < end && Character.isWhitespace(line.charAt(i))) {
                i++;
            }
            if (i == end) {
                break;
            }
            int start = i;
            while (i < end && !Character.isWhitespace(line.charAt(i))) {
                i++;
            }
            if (size == values.length) {
                values = Arrays.copyOf(values, values.length * 2);
            }
            values[size++] = new BigInteger(line.substring(start, i));
        }
        return Arrays.copyOf(values, size);
    }

    private LongAnswers extrapolate(long[] values, int size) {
        long next = 0;
        long previous = 0;
        boolean addFirst = true;
        while (size > 0) {
            next = Math.addExact(next, values[size - 1]);
            previous = addFirst
                    ? Math.addExact(previous, values[0])
                    : Math.subtractExact(previous, values[0]);
            addFirst = !addFirst;

            boolean allZero = true;
            for (int i = 0; i + 1 < size; i++) {
                values[i] = Math.subtractExact(values[i + 1], values[i]);
                allZero &= values[i] == 0;
            }
            size--;
            if (allZero) {
                break;
            }
        }
        return new LongAnswers(next, previous);
    }

    private BigAnswers extrapolate(BigInteger[] values) {
        BigInteger next = BigInteger.ZERO;
        BigInteger previous = BigInteger.ZERO;
        boolean addFirst = true;
        int size = values.length;
        while (size > 0) {
            next = next.add(values[size - 1]);
            previous = addFirst ? previous.add(values[0]) : previous.subtract(values[0]);
            addFirst = !addFirst;

            boolean allZero = true;
            for (int i = 0; i + 1 < size; i++) {
                values[i] = values[i + 1].subtract(values[i]);
                allZero &= values[i].signum() == 0;
            }
            size--;
            if (allZero) {
                break;
            }
        }
        return new BigAnswers(next, previous);
    }

    private record Answers(String next, String previous) {}

    private record LongLine(long[] values, int size) {}

    private record LongAnswers(long next, long previous) {}

    private record BigAnswers(BigInteger next, BigInteger previous) {}

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

        private void add(BigInteger value) {
            if (big == null) {
                big = BigInteger.valueOf(small);
            }
            big = big.add(value);
        }

        @Override
        public String toString() {
            return big == null ? Long.toString(small) : big.toString();
        }
    }
}
