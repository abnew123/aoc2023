package src.solutions;

import src.meta.DayTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day10 implements DayTemplate {

    private static final int[] DX = {0, 1, 0, -1};
    private static final int[] DY = {-1, 0, 1, 0};
    private static final int[] BIT = {1, 2, 4, 8};

    @Override
    public String[] fullSolve(Scanner in) {
        Answers answers = analyze(in);
        return new String[]{Long.toString(answers.farthest()), Long.toString(answers.enclosed())};
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
        return Long.toString(part1 ? answers.farthest() : answers.enclosed());
    }

    private Answers analyze(Scanner in) {
        List<String> rows = new ArrayList<>();
        while (in.hasNextLine()) {
            rows.add(in.nextLine());
        }
        if (rows.isEmpty() || rows.get(0).isEmpty()) {
            throw new IllegalArgumentException("Pipe grid must not be empty");
        }
        int height = rows.size();
        int width = rows.get(0).length();
        int startX = -1;
        int startY = -1;
        for (int y = 0; y < height; y++) {
            String row = rows.get(y);
            if (row.length() != width) {
                throw new IllegalArgumentException("Pipe grid must be rectangular");
            }
            int found = row.indexOf('S');
            if (found >= 0) {
                if (startX >= 0 || row.indexOf('S', found + 1) >= 0) {
                    throw new IllegalArgumentException("Pipe grid must contain exactly one start");
                }
                startX = found;
                startY = y;
            }
        }
        if (startX < 0) {
            throw new IllegalArgumentException("Pipe grid is missing its start");
        }

        int startMask = 0;
        for (int direction = 0; direction < 4; direction++) {
            int x = startX + DX[direction];
            int y = startY + DY[direction];
            if (inside(x, y, width, height)
                    && (pipeMask(rows.get(y).charAt(x)) & BIT[(direction + 2) & 3]) != 0) {
                startMask |= BIT[direction];
            }
        }
        if (Integer.bitCount(startMask) != 2) {
            throw new IllegalArgumentException("Start must have exactly two reciprocal connections");
        }

        int firstDirection = Integer.numberOfTrailingZeros(startMask);
        int previousX = startX;
        int previousY = startY;
        int currentX = startX + DX[firstDirection];
        int currentY = startY + DY[firstDirection];
        long boundary = 1;
        long areaTwice = cross(startX, startY, currentX, currentY);
        long cellLimit = (long) width * height;

        while (currentX != startX || currentY != startY) {
            int mask = pipeMask(rows.get(currentY).charAt(currentX));
            if (Integer.bitCount(mask) != 2) {
                throw new IllegalArgumentException("Loop entered a non-pipe tile");
            }
            int incomingDirection = directionTo(currentX, currentY, previousX, previousY);
            int incomingBit = BIT[incomingDirection];
            if ((mask & incomingBit) == 0) {
                throw new IllegalArgumentException("Pipe connection is not reciprocal");
            }
            int outgoingBit = mask ^ incomingBit;
            int outgoingDirection = Integer.numberOfTrailingZeros(outgoingBit);
            int nextX = currentX + DX[outgoingDirection];
            int nextY = currentY + DY[outgoingDirection];
            if (!inside(nextX, nextY, width, height)) {
                throw new IllegalArgumentException("Pipe loop exits the grid");
            }
            int nextMask = nextX == startX && nextY == startY
                    ? startMask : pipeMask(rows.get(nextY).charAt(nextX));
            if ((nextMask & BIT[(outgoingDirection + 2) & 3]) == 0) {
                throw new IllegalArgumentException("Pipe connection is not reciprocal");
            }

            areaTwice += cross(currentX, currentY, nextX, nextY);
            boundary++;
            if (boundary > cellLimit) {
                throw new IllegalArgumentException("Pipe path does not form one simple loop");
            }
            previousX = currentX;
            previousY = currentY;
            currentX = nextX;
            currentY = nextY;
        }

        long interiorNumerator = Math.abs(areaTwice) - boundary + 2;
        if (interiorNumerator < 0 || (interiorNumerator & 1) != 0) {
            throw new IllegalArgumentException("Pipe path is not a simple lattice loop");
        }
        return new Answers(boundary / 2, interiorNumerator / 2);
    }

    private int pipeMask(char pipe) {
        return switch (pipe) {
            case '|' -> 1 | 4;
            case '-' -> 2 | 8;
            case 'L' -> 1 | 2;
            case 'J' -> 1 | 8;
            case '7' -> 4 | 8;
            case 'F' -> 2 | 4;
            default -> 0;
        };
    }

    private int directionTo(int fromX, int fromY, int toX, int toY) {
        for (int direction = 0; direction < 4; direction++) {
            if (fromX + DX[direction] == toX && fromY + DY[direction] == toY) {
                return direction;
            }
        }
        throw new IllegalArgumentException("Pipe path contains a non-adjacent step");
    }

    private boolean inside(int x, int y, int width, int height) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    private long cross(int x1, int y1, int x2, int y2) {
        return (long) x1 * y2 - (long) x2 * y1;
    }

    private record Answers(long farthest, long enclosed) {}
}
