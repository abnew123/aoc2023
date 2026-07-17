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
        Lagoon first = literal ? new Lagoon() : null;
        Lagoon second = encoded ? new Lagoon() : null;
        while (in.hasNextLine()) {
            String line = in.nextLine();
            if (line.isBlank()) {
                continue;
            }
            int directionStart = skipWhitespace(line, 0);
            if (directionStart >= line.length()) {
                continue;
            }
            int afterDirection = directionStart + 1;
            if (afterDirection >= line.length() || !Character.isWhitespace(line.charAt(afterDirection))) {
                throw malformed(line);
            }
            int distanceStart = skipWhitespace(line, afterDirection);
            int distanceEnd = distanceStart;
            while (distanceEnd < line.length() && !Character.isWhitespace(line.charAt(distanceEnd))) {
                distanceEnd++;
            }
            if (distanceStart == distanceEnd) {
                throw malformed(line);
            }
            if (literal) {
                first.move(literalDirection(line.charAt(directionStart)),
                        decimal(line, distanceStart, distanceEnd));
            }
            if (encoded) {
                int hash = line.indexOf('#', distanceEnd);
                int close = hash < 0 ? -1 : line.indexOf(')', hash + 1);
                if (hash < 0 || close - hash != 7) {
                    throw malformed(line);
                }
                int direction = encodedDirection(line.charAt(close - 1));
                BigInteger distance = new BigInteger(line.substring(hash + 1, close - 1), 16);
                second.move(direction, distance);
            }
        }
        return new String[]{literal ? first.finish().toString() : null,
                encoded ? second.finish().toString() : null};
    }

    private int skipWhitespace(String line, int index) {
        while (index < line.length() && Character.isWhitespace(line.charAt(index))) {
            index++;
        }
        return index;
    }

    private BigInteger decimal(String line, int start, int end) {
        try {
            return new BigInteger(line.substring(start, end));
        } catch (NumberFormatException invalid) {
            throw malformed(line);
        }
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

    private IllegalArgumentException malformed(String line) {
        return new IllegalArgumentException("Malformed dig instruction: " + line);
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
