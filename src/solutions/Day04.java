package src.solutions;

import src.meta.DayTemplate;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Scanner;

public class Day04 implements DayTemplate {

    @Override
    public String[] fullSolve(Scanner in) {
        Answers answers = analyze(in);
        return new String[]{answers.part1.toString(), answers.part2.toString()};
    }

    @Override
    public String solve(boolean part1, Scanner in) {
        Answers answers = analyze(in);
        return (part1 ? answers.part1 : answers.part2).toString();
    }

    private Answers analyze(Scanner in) {
        in.useDelimiter("\\A");
        String input = in.hasNext() ? in.next() : "";
        MatchParser parser = new MatchParser();
        int[] matches = new int[256];
        int cardCount = 0;
        BigInteger points = BigInteger.ZERO;
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
                if (cardCount == matches.length) {
                    matches = Arrays.copyOf(matches, matches.length * 2);
                }
                int count = parser.count(input, lineStart, trimmedEnd);
                matches[cardCount++] = count;
                if (count > 0) {
                    points = points.add(BigInteger.ONE.shiftLeft(count - 1));
                }
            }
            lineStart = lineEnd + 1;
        }

        BigInteger[] changes = new BigInteger[cardCount + 1];
        BigInteger activeCopies = BigInteger.ZERO;
        BigInteger totalCards = BigInteger.ZERO;
        for (int card = 0; card < cardCount; card++) {
            if (changes[card] != null) {
                activeCopies = activeCopies.add(changes[card]);
            }
            BigInteger copies = activeCopies.add(BigInteger.ONE);
            totalCards = totalCards.add(copies);
            int end = Math.min(cardCount, card + 1 + matches[card]);
            if (card + 1 < end) {
                addChange(changes, card + 1, copies);
                addChange(changes, end, copies.negate());
            }
        }
        return new Answers(points, totalCards);
    }

    private static boolean blank(String input, int from, int end) {
        for (int index = from; index < end; index++) {
            if (!Character.isWhitespace(input.charAt(index))) {
                return false;
            }
        }
        return true;
    }

    private void addChange(BigInteger[] changes, int index, BigInteger amount) {
        changes[index] = changes[index] == null ? amount : changes[index].add(amount);
    }

    private static final class MatchParser {
        private int[] winningSpans = new int[48];
        private int winningCount;

        private int count(String line, int from, int lineEnd) {
            int colon = -1;
            for (int scan = from; scan < lineEnd; scan++) {
                if (line.charAt(scan) == ':') {
                    colon = scan;
                    break;
                }
            }
            int separator = -1;
            if (colon >= 0) {
                for (int scan = colon + 1; scan < lineEnd; scan++) {
                    if (line.charAt(scan) == '|') {
                        if (separator >= 0) {
                            throw malformed();
                        }
                        separator = scan;
                    }
                }
            }
            if (colon < 0 || separator < 0) {
                throw malformed();
            }

            winningCount = 0;
            int index = colon + 1;
            while (true) {
                index = skipWhitespace(line, index, separator);
                if (index == separator) {
                    break;
                }
                int end = numberEnd(line, index, separator);
                addWinning(line, index, end);
                index = end;
            }

            int matches = 0;
            index = separator + 1;
            while (true) {
                index = skipWhitespace(line, index, lineEnd);
                if (index == lineEnd) {
                    return matches;
                }
                int end = numberEnd(line, index, lineEnd);
                int normalizedStart = normalizedStart(line, index, end);
                int length = end - normalizedStart;
                int sign = normalizedSign(line, index, normalizedStart, end);
                for (int winning = 0; winning < winningCount; winning++) {
                    int spanIndex = winning * 3;
                    if (winningSpans[spanIndex] == sign
                            && winningSpans[spanIndex + 2] == length
                            && line.regionMatches(normalizedStart, line,
                            winningSpans[spanIndex + 1], length)) {
                        matches++;
                        break;
                    }
                }
                index = end;
            }
        }

        private void addWinning(String line, int start, int end) {
            if (winningCount * 3 == winningSpans.length) {
                winningSpans = Arrays.copyOf(winningSpans, winningSpans.length * 2);
            }
            int normalizedStart = normalizedStart(line, start, end);
            int spanIndex = winningCount * 3;
            winningSpans[spanIndex] = normalizedSign(line, start, normalizedStart, end);
            winningSpans[spanIndex + 1] = normalizedStart;
            winningSpans[spanIndex + 2] = end - normalizedStart;
            winningCount++;
        }

        private int normalizedStart(String line, int start, int end) {
            char first = line.charAt(start);
            if (first == '+' || first == '-') {
                start++;
            }
            while (start + 1 < end && line.charAt(start) == '0') {
                start++;
            }
            return start;
        }

        private int normalizedSign(String line, int tokenStart, int normalizedStart, int end) {
            if (normalizedStart + 1 == end && line.charAt(normalizedStart) == '0') {
                return 0;
            }
            return line.charAt(tokenStart) == '-' ? -1 : 1;
        }

        private int skipWhitespace(String line, int index, int end) {
            while (index < end && Character.isWhitespace(line.charAt(index))) {
                index++;
            }
            return index;
        }

        private int numberEnd(String line, int start, int limit) {
            int index = start;
            if (index < limit && (line.charAt(index) == '+' || line.charAt(index) == '-')) {
                index++;
            }
            int digitStart = index;
            while (index < limit) {
                char character = line.charAt(index);
                if (character < '0' || character > '9') {
                    break;
                }
                index++;
            }
            if (index == digitStart
                    || index < limit && !Character.isWhitespace(line.charAt(index))) {
                throw malformed();
            }
            return index;
        }

        private IllegalArgumentException malformed() {
            return new IllegalArgumentException("Malformed scratchcard");
        }
    }

    private static final class Answers {
        private final BigInteger part1;
        private final BigInteger part2;

        private Answers(BigInteger part1, BigInteger part2) {
            this.part1 = part1;
            this.part2 = part2;
        }
    }
}
