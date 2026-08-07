package src.solutions;

import src.meta.DayTemplate;

import java.math.BigInteger;
import java.util.Scanner;

public class Day02 implements DayTemplate {

    static final String RED = "red";
    static final String GREEN = "green";
    static final String BLUE = "blue";

    @Override
    public String[] fullSolve(Scanner in) {
        return analyze(in);
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
        String[] answers = analyze(in);
        return answers[part1 ? 0 : 1];
    }

    private String[] analyze(Scanner in) {
        in.useDelimiter("\\A");
        String input = in.hasNext() ? in.next() : "";
        BigInteger possibleIdSum = BigInteger.ZERO;
        BigInteger powerSum = BigInteger.ZERO;
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
            if (!blank(input, lineStart, trimmedEnd)) {
                Game game = parseGame(input, lineStart, trimmedEnd);
                if (game.red.compareTo(BigInteger.valueOf(12)) <= 0
                        && game.green.compareTo(BigInteger.valueOf(13)) <= 0
                        && game.blue.compareTo(BigInteger.valueOf(14)) <= 0) {
                    possibleIdSum = possibleIdSum.add(game.id);
                }
                powerSum = powerSum.add(game.red.multiply(game.green).multiply(game.blue));
            }
            lineStart = lineEnd + 1;
        }
        return new String[]{possibleIdSum.toString(), powerSum.toString()};
    }

    private Game parseGame(String input, int from, int end) {
        int index = skipWhitespace(input, from, end);
        if (index + 4 > end || !input.regionMatches(index, "Game", 0, 4)) {
            throw new IllegalArgumentException("Expected Game prefix");
        }
        index = skipWhitespace(input, index + 4, end);
        Number id = parseNumber(input, index, end);
        index = skipWhitespace(input, id.end, end);
        if (index >= end || input.charAt(index++) != ':') {
            throw new IllegalArgumentException("Expected ':' after game ID");
        }

        BigInteger red = BigInteger.ZERO;
        BigInteger green = BigInteger.ZERO;
        BigInteger blue = BigInteger.ZERO;
        while (true) {
            index = skipWhitespace(input, index, end);
            if (index == end) {
                return new Game(id.value, red, green, blue);
            }
            char separator = input.charAt(index);
            if (separator == ',' || separator == ';') {
                index = skipWhitespace(input, index + 1, end);
            }
            Number count = parseNumber(input, index, end);
            index = skipWhitespace(input, count.end, end);
            int colorStart = index;
            while (index < end && Character.isLetter(input.charAt(index))) {
                index++;
            }
            int colorLength = index - colorStart;
            if (colorLength == 0) {
                throw new IllegalArgumentException("Expected cube color");
            }
            if (colorLength == 3 && input.regionMatches(colorStart, RED, 0, 3)) {
                red = red.max(count.value);
            } else if (colorLength == 5 && input.regionMatches(colorStart, GREEN, 0, 5)) {
                green = green.max(count.value);
            } else if (colorLength == 4 && input.regionMatches(colorStart, BLUE, 0, 4)) {
                blue = blue.max(count.value);
            } else {
                throw new IllegalArgumentException("Unknown cube color");
            }
            index = skipWhitespace(input, index, end);
            if (index < end && input.charAt(index) != ',' && input.charAt(index) != ';') {
                throw new IllegalArgumentException("Expected draw separator");
            }
        }
    }

    private boolean blank(String input, int from, int end) {
        for (int index = from; index < end; index++) {
            if (!Character.isWhitespace(input.charAt(index))) {
                return false;
            }
        }
        return true;
    }

    private int skipWhitespace(String input, int index, int end) {
        while (index < end && Character.isWhitespace(input.charAt(index))) {
            index++;
        }
        return index;
    }

    private Number parseNumber(String input, int index, int end) {
        int start = index;
        while (index < end && input.charAt(index) >= '0' && input.charAt(index) <= '9') {
            index++;
        }
        if (start == index) {
            throw new IllegalArgumentException("Expected nonnegative integer");
        }
        return new Number(new BigInteger(input.substring(start, index)), index);
    }

    private record Game(BigInteger id, BigInteger red, BigInteger green, BigInteger blue) {}

    private record Number(BigInteger value, int end) {}
}
