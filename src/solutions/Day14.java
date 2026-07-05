package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day14 implements DayTemplate {

    static byte EMPTY = 0;
    static byte BLOCK = 1;
    static byte ROUND = 2;

    byte[][] stones;
    int rows;
    int cols;

    
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

    void parse(Scanner in) {
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

    void cycle() {
        shiftNorth();
        shiftWest();
        shiftSouth();
        shiftEast();
    }

    void shiftNorth() {
        for (int c = 0; c < cols; c++) {
            int tar = 0;
            for (int r = 0; r < rows; r++) {
                if (stones[r][c] == BLOCK) {
                    tar = r + 1;
                } else if (stones[r][c] == ROUND) {
                    stones[r][c] = EMPTY;
                    stones[tar++][c] = ROUND;
                }
            }
        }
    }

    void shiftSouth() {
        for (int c = 0; c < cols; c++) {
            int tar = rows - 1;
            for (int r = rows - 1; r >= 0; r--) {
                if (stones[r][c] == BLOCK) {
                    tar = r - 1;
                } else if (stones[r][c] == ROUND) {
                    stones[r][c] = EMPTY;
                    stones[tar--][c] = ROUND;
                }
            }
        }
    }

    void shiftWest() {
        for (int r = 0; r < rows; r++) {
            int tar = 0;
            for (int c = 0; c < cols; c++) {
                if (stones[r][c] == BLOCK) {
                    tar = c + 1;
                } else if (stones[r][c] == ROUND) {
                    stones[r][c] = EMPTY;
                    stones[r][tar++] = ROUND;
                }
            }
        }
    }

    void shiftEast() {
        for (int r = 0; r < rows; r++) {
            int tar = cols - 1;
            for (int c = cols - 1; c >= 0; c--) {
                if (stones[r][c] == BLOCK) {
                    tar = c - 1;
                } else if (stones[r][c] == ROUND) {
                    stones[r][c] = EMPTY;
                    stones[r][tar--] = ROUND;
                }
            }
        }
    }

    int getSupportLoad() {
        int ans = 0;
        for (int r = 0; r < rows; r++) {
            int rowLoad = rows - r;
            for (int c = 0; c < cols; c++) {
                if (stones[r][c] == ROUND) {
                    ans += rowLoad;
                }
            }
        }
        return ans;
    }

    int gridHash() {
        int hash = 1;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                hash = 31 * hash + stones[r][c];
            }
        }
        return hash;
    }
}
