package src.solutions;

import src.meta.DayTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day16 implements DayTemplate {

    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;

    private static final int[] DX = {-1, 1, 0, 0};
    private static final int[] DY = {0, 0, -1, 1};

    private char[][] grid;
    private int rows;
    private int cols;
    private int[] seen;
    private int[] energized;
    private int stamp = 1;

    @Override
    public String[] fullSolve(Scanner in) {
        generateGraph(in);
        int[] traversalStack = newTraversalStack();
        int answer1 = tryFromLocation(-1, 0, RIGHT, traversalStack);
        int answer2 = runFromAllPoints(traversalStack);
        return new String[]{answer1 + "", answer2 + ""};
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
        generateGraph(in);
        int[] traversalStack = newTraversalStack();
        int answer = part1
                ? tryFromLocation(-1, 0, RIGHT, traversalStack)
                : runFromAllPoints(traversalStack);
        return answer + "";
    }

    private void generateGraph(Scanner in) {
        List<String> lines = new ArrayList<>();
        while (in.hasNextLine()) {
            lines.add(in.nextLine());
        }

        rows = lines.size();
        cols = lines.get(0).length();
        grid = new char[rows][cols];
        for (int y = 0; y < rows; y++) {
            grid[y] = lines.get(y).toCharArray();
        }

        int stateCount = rows * cols * 4;
        seen = new int[stateCount];
        energized = new int[rows * cols];
        stamp = 1;
    }

    private int[] newTraversalStack() {
        return new int[seen.length + 4];
    }

    private int runFromAllPoints(int[] traversalStack) {
        int currBest = 0;
        for (int y = 0; y < rows; y++) {
            currBest = Math.max(currBest, tryFromLocation(-1, y, RIGHT, traversalStack));
            currBest = Math.max(currBest, tryFromLocation(cols, y, LEFT, traversalStack));
        }
        for (int x = 0; x < cols; x++) {
            currBest = Math.max(currBest, tryFromLocation(x, -1, DOWN, traversalStack));
            currBest = Math.max(currBest, tryFromLocation(x, rows, UP, traversalStack));
        }
        return currBest;
    }

    private int tryFromLocation(int startX, int startY, int startDir, int[] traversalStack) {
        int currentStamp = nextStamp();
        int energizedCount = 0;
        int x = startX;
        int y = startY;
        int dir = startDir;

        int stackSize = 0;
        while (true) {
            x += DX[dir];
            y += DY[dir];
            if (x < 0 || y < 0 || x >= cols || y >= rows) {
                if (stackSize == 0) {
                    return energizedCount;
                }
                int packed = traversalStack[--stackSize];
                dir = packed & 3;
                int pos = packed >> 2;
                x = pos % cols;
                y = pos / cols;
                continue;
            }

            int cell = y * cols + x;
            int state = (cell << 2) | dir;
            if (seen[state] == currentStamp) {
                if (stackSize == 0) {
                    return energizedCount;
                }
                int packed = traversalStack[--stackSize];
                dir = packed & 3;
                int pos = packed >> 2;
                x = pos % cols;
                y = pos / cols;
                continue;
            }
            seen[state] = currentStamp;

            if (energized[cell] != currentStamp) {
                energized[cell] = currentStamp;
                energizedCount++;
            }

            char tile = grid[y][x];
            if (tile == '/') {
                dir = slashDirection(dir);
            } else if (tile == '\\') {
                dir = backslashDirection(dir);
            } else if (tile == '|' && (dir == LEFT || dir == RIGHT)) {
                traversalStack[stackSize++] = (cell << 2) | DOWN;
                dir = UP;
            } else if (tile == '-' && (dir == UP || dir == DOWN)) {
                traversalStack[stackSize++] = (cell << 2) | RIGHT;
                dir = LEFT;
            }
        }
    }

    private int nextStamp() {
        if (stamp == Integer.MAX_VALUE) {
            seen = new int[seen.length];
            energized = new int[energized.length];
            stamp = 1;
        }
        return stamp++;
    }

    private int slashDirection(int dir) {
        return switch (dir) {
            case LEFT -> DOWN;
            case RIGHT -> UP;
            case UP -> RIGHT;
            default -> LEFT;
        };
    }

    private int backslashDirection(int dir) {
        return switch (dir) {
            case LEFT -> UP;
            case RIGHT -> DOWN;
            case UP -> LEFT;
            default -> RIGHT;
        };
    }
}
