package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day14 implements DayTemplate {

    private static final byte EMPTY = 0;
    private static final byte BLOCK = 1;
    private static final byte ROUND = 2;

    private byte[][] stones;
    private int rows;
    private int cols;

    /**
     * Main solving method.
     *
     * @param part1 The solver will solve part 1 if param is set to true.
     *              The solver will solve part 2 if param is set to false.
     * @param in    The solver will read data from this Scanner.
     * @return Returns answer in string format.
     */
    public String solve(boolean part1, Scanner in) {
        parse(in);
        if (part1) {
            shiftNorth();
        } else {
            Map<Integer, Integer> states = new HashMap<>();
            int index = 0;
            int offset = 0;
            while (index < 1000000000) {
                cycle();
                index++;
                int key = gridHash() * 31 + getSupportLoad();
                Integer previous = states.putIfAbsent(key, index);
                if (previous != null) {
                    int cycle = index - previous;
                    offset = (1000000000 - index) % cycle;
                    break;
                }
            }
            for (int i = 0; i < offset; i++) {
                cycle();
            }
        }
        return getSupportLoad() + "";
    }

    private void parse(Scanner in) {
        List<String> lines = new ArrayList<>();
        while (in.hasNextLine()) {
            lines.add(in.nextLine());
        }
        rows = lines.size();
        cols = lines.get(0).length();
        stones = new byte[rows][cols];
        for (int r = 0; r < rows; r++) {
            String line = lines.get(r);
            for (int c = 0; c < cols; c++) {
                char ch = line.charAt(c);
                stones[r][c] = ch == '#' ? BLOCK : ch == 'O' ? ROUND : EMPTY;
            }
        }
    }

    private void cycle() {
        shiftNorth();
        shiftWest();
        shiftSouth();
        shiftEast();
    }

    private void shiftNorth() {
        for (int c = 0; c < cols; c++) {
            int target = 0;
            for (int r = 0; r < rows; r++) {
                if (stones[r][c] == BLOCK) {
                    target = r + 1;
                } else if (stones[r][c] == ROUND) {
                    stones[r][c] = EMPTY;
                    stones[target++][c] = ROUND;
                }
            }
        }
    }

    private void shiftSouth() {
        for (int c = 0; c < cols; c++) {
            int target = rows - 1;
            for (int r = rows - 1; r >= 0; r--) {
                if (stones[r][c] == BLOCK) {
                    target = r - 1;
                } else if (stones[r][c] == ROUND) {
                    stones[r][c] = EMPTY;
                    stones[target--][c] = ROUND;
                }
            }
        }
    }

    private void shiftWest() {
        for (int r = 0; r < rows; r++) {
            int target = 0;
            for (int c = 0; c < cols; c++) {
                if (stones[r][c] == BLOCK) {
                    target = c + 1;
                } else if (stones[r][c] == ROUND) {
                    stones[r][c] = EMPTY;
                    stones[r][target++] = ROUND;
                }
            }
        }
    }

    private void shiftEast() {
        for (int r = 0; r < rows; r++) {
            int target = cols - 1;
            for (int c = cols - 1; c >= 0; c--) {
                if (stones[r][c] == BLOCK) {
                    target = c - 1;
                } else if (stones[r][c] == ROUND) {
                    stones[r][c] = EMPTY;
                    stones[r][target--] = ROUND;
                }
            }
        }
    }

    private int getSupportLoad() {
        int answer = 0;
        for (int r = 0; r < rows; r++) {
            int rowLoad = rows - r;
            for (int c = 0; c < cols; c++) {
                if (stones[r][c] == ROUND) {
                    answer += rowLoad;
                }
            }
        }
        return answer;
    }

    private int gridHash() {
        int hash = 1;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                hash = 31 * hash + stones[r][c];
            }
        }
        return hash;
    }
}
