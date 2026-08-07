package src.solutions;

import src.meta.DayTemplate;

import java.math.BigInteger;
import java.util.Scanner;

public class Day18 implements DayTemplate {

    @Override
    public String[] fullSolve(Scanner in) {
        return solve(in, true, true);
    }

    public String solve(boolean part1, Scanner in) {
        String[] answers = solve(in, part1, !part1);
        return part1 ? answers[0] : answers[1];
    }

    private String[] solve(Scanner in, boolean literal, boolean encoded) {
        in.useDelimiter("\\A");
        String input = in.hasNext() ? in.next() : "";
        Lagoon first = literal ? new Lagoon() : null;
        Lagoon second = encoded ? new Lagoon() : null;
        int offset = 0;
        int length = input.length();
        while (offset < length) {
            int lineStart = offset;
            while (offset < length && input.charAt(offset) != '\n'
                    && input.charAt(offset) != '\r') {
                offset++;
            }
            int lineEnd = offset;
            if (offset < length) {
                char ending = input.charAt(offset++);
                if (ending == '\r' && offset < length && input.charAt(offset) == '\n') {
                    offset++;
                }
            }
            int directionStart = skipWhitespace(input, lineStart, lineEnd);
            if (directionStart >= lineEnd) {
                continue;
            }
            int afterDirection = directionStart + 1;
            if (afterDirection >= lineEnd || !Character.isWhitespace(input.charAt(afterDirection))) {
                throw malformed();
            }
            int distanceStart = skipWhitespace(input, afterDirection, lineEnd);
            int distanceEnd = distanceStart;
            while (distanceEnd < lineEnd && !Character.isWhitespace(input.charAt(distanceEnd))) {
                distanceEnd++;
            }
            if (distanceStart == distanceEnd) {
                throw malformed();
            }
            if (literal) {
                first.move(literalDirection(input.charAt(directionStart)),
                        decimal(input, distanceStart, distanceEnd));
            }
            if (encoded) {
                int hash = -1;
                for (int i = distanceEnd; i < lineEnd; i++) {
                    if (input.charAt(i) == '#') {
                        hash = i;
                        break;
                    }
                }
                int close = -1;
                if (hash >= 0) {
                    for (int i = hash + 1; i < lineEnd; i++) {
                        if (input.charAt(i) == ')') {
                            close = i;
                            break;
                        }
                    }
                }
                if (hash < 0 || close - hash != 7) {
                    throw malformed();
                }
                int direction = encodedDirection(input.charAt(close - 1));
                second.move(direction, hexDistance(input, hash + 1, close - 1));
            }
        }
        return new String[]{literal ? first.finish().toString() : null,
                encoded ? second.finish().toString() : null};
    }

    private int skipWhitespace(String input, int index, int limit) {
        while (index < limit && Character.isWhitespace(input.charAt(index))) {
            index++;
        }
        return index;
    }

    private BigInteger decimal(String input, int start, int end) {
        if (end - start <= 18) {
            long value = 0;
            boolean digitsOnly = start < end;
            for (int i = start; i < end; i++) {
                char digit = input.charAt(i);
                if (digit < '0' || digit > '9') {
                    digitsOnly = false;
                    break;
                }
                value = value * 10 + (digit - '0');
            }
            if (digitsOnly) {
                return BigInteger.valueOf(value);
            }
        }
        try {
            return new BigInteger(input.substring(start, end));
        } catch (NumberFormatException invalid) {
            throw malformed();
        }
    }

    private BigInteger hexDistance(String input, int start, int end) {
        long value = 0;
        for (int i = start; i < end; i++) {
            int digit = Character.digit(input.charAt(i), 16);
            if (digit < 0) {
                return new BigInteger(input.substring(start, end), 16);
            }
            value = (value << 4) | digit;
        }
        return BigInteger.valueOf(value);
    }

    private int literalDirection(char direction) {
        return switch (direction) {
            case 'R' -> 0;
            case 'D' -> 1;
            case 'L' -> 2;
            case 'U' -> 3;
            default -> throw new IllegalArgumentException("Unknown dig direction: " + direction);
        };
    }

    private int encodedDirection(char direction) {
        if (direction < '0' || direction > '3') {
            throw new IllegalArgumentException("Unknown encoded dig direction: " + direction);
        }
        return direction - '0';
    }

    private IllegalArgumentException malformed() {
        return new IllegalArgumentException("Malformed dig instruction");
    }

    private static class Lagoon {
        private BigInteger x = BigInteger.ZERO;
        private BigInteger y = BigInteger.ZERO;
        private BigInteger twiceArea = BigInteger.ZERO;
        private BigInteger boundary = BigInteger.ZERO;

        private void move(int direction, BigInteger distance) {
            if (distance.signum() < 0) {
                throw new IllegalArgumentException("Dig distance must be nonnegative");
            }
            BigInteger nextX = x;
            BigInteger nextY = y;
            if (direction == 0) {
                nextX = x.add(distance);
            } else if (direction == 1) {
                nextY = y.subtract(distance);
            } else if (direction == 2) {
                nextX = x.subtract(distance);
            } else {
                nextY = y.add(distance);
            }
            twiceArea = twiceArea.add(x.multiply(nextY).subtract(nextX.multiply(y)));
            boundary = boundary.add(distance);
            x = nextX;
            y = nextY;
        }

        private BigInteger finish() {
            if (x.signum() != 0 || y.signum() != 0) {
                throw new IllegalArgumentException("Dig path must return to its start");
            }
            BigInteger[] halves = twiceArea.abs().add(boundary).divideAndRemainder(BigInteger.TWO);
            if (halves[1].signum() != 0) {
                throw new IllegalArgumentException("Lagoon area is not integral");
            }
            return halves[0].add(BigInteger.ONE);
        }
    }
}
