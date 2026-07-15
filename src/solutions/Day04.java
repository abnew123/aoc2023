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
        MatchParser parser = new MatchParser();
        int[] matches = new int[256];
        int cardCount = 0;
        BigInteger points = BigInteger.ZERO;
        while (in.hasNextLine()) {
            String line = in.nextLine();
            if (line.isBlank()) {
                continue;
            }
            if (cardCount == matches.length) {
                matches = Arrays.copyOf(matches, matches.length * 2);
            }
            int count = parser.count(line);
            matches[cardCount++] = count;
            if (count > 0) {
                points = points.add(BigInteger.ONE.shiftLeft(count - 1));
            }
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

    private void addChange(BigInteger[] changes, int index, BigInteger amount) {
        changes[index] = changes[index] == null ? amount : changes[index].add(amount);
    }

    private static final class MatchParser {
        private int[] winningSpans = new int[48];
        private int winningCount;

        private int count(String line) {
            int colon = line.indexOf(':');
            int separator = colon < 0 ? -1 : line.indexOf('|', colon + 1);
            if (colon < 0 || separator < 0 || line.indexOf('|', separator + 1) >= 0) {
                throw malformed(line);
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
                index = skipWhitespace(line, index, line.length());
                if (index == line.length()) {
                    return matches;
                }
                int end = numberEnd(line, index, line.length());
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
                throw malformed(line);
            }
            return index;
        }

        private IllegalArgumentException malformed(String line) {
            return new IllegalArgumentException("Malformed scratchcard: " + line);
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
