package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day14 extends DayTemplate {

    /**
     * Main solving method.
     *
     * @param part1 The solver will solve part 1 if param is set to true.
     *              The solver will solve part 2 if param is set to false.
     * @param in    The solver will read data from this Scanner.
     * @return Returns answer in string format.
     */
    public String solve(boolean part1, Scanner in) {
        List<String> tmp = new ArrayList<>();
        while (in.hasNext()) {
            tmp.add(in.nextLine());
        }
        Map<Integer, Integer> states = new HashMap<>();
        int[][] stones = new int[tmp.get(0).length()][tmp.size()];
        for (int i = 0; i < stones.length; i++) {
            for (int j = 0; j < stones[0].length; j++) {
                char s = tmp.get(j).charAt(i);
                if (s == '.') {
                    stones[i][j] = 0;
                }
                if (s == '#') {
                    stones[i][j] = 1;
                }
                if (s == 'O') {
                    stones[i][j] = 2;
                }
            }
        }
        if (part1) {
            shiftNorth(stones);
        } else {
            int index = 0;
            int offset = 0;
            while (index < 1000000000) {
                shiftNorth(stones);
                shiftWest(stones);
                shiftSouth(stones);
                shiftEast(stones);
                int tmpVal = getSupportLoad(stones);
                index++;
                if (states.containsKey(Arrays.deepHashCode(stones) + tmpVal)) {
                    int cycle = index - states.get(Arrays.deepHashCode(stones)+tmpVal);
                    offset = (1000000000 - index) % cycle;
                    break;
                } else {
                    states.put(Arrays.deepHashCode(stones) + tmpVal, index);
                }
            }
            for (int i = 0; i < offset; i++) {
                shiftNorth(stones);
                shiftWest(stones);
                shiftSouth(stones);
                shiftEast(stones);
            }

        }
        return getSupportLoad(stones) + "";
    }

    private void shiftNorth(int[][] stones) {
        boolean moved = true;
        while (moved) {
            moved = false;
            for (int i = 0; i < stones[0].length; i++) {
                for (int j = 1; j < stones.length; j++) {
                    if (stones[i][j] == 2 && stones[i][j - 1] == 0) {
                        stones[i][j] = 0;
                        stones[i][j - 1] = 2;
                        moved = true;
                    }
                }
            }
        }
    }

    private void shiftSouth(int[][] stones) {
        boolean moved = true;
        while (moved) {
            moved = false;
            for (int i = 0; i < stones[0].length; i++) {
                for (int j = 0; j < stones.length - 1; j++) {
                    if (stones[i][j] == 2 && stones[i][j + 1] == 0) {
                        stones[i][j] = 0;
                        stones[i][j + 1] = 2;
                        moved = true;
                    }
                }
            }
        }
    }

    private void shiftWest(int[][] stones) {
        boolean moved = true;
        while (moved) {
            moved = false;
            for (int i = 1; i < stones[0].length; i++) {
                for (int j = 0; j < stones.length; j++) {
                    if (stones[i][j] == 2 && stones[i - 1][j] == 0) {
                        stones[i][j] = 0;
                        stones[i - 1][j] = 2;
                        moved = true;
                    }
                }
            }
        }
    }

    private void shiftEast(int[][] stones) {
        boolean moved = true;
        while (moved) {
            moved = false;
            for (int i = 0; i < stones[0].length - 1; i++) {
                for (int j = 0; j < stones.length; j++) {
                    if (stones[i][j] == 2 && stones[i + 1][j] == 0) {
                        stones[i][j] = 0;
                        stones[i + 1][j] = 2;
                        moved = true;
                    }
                }
            }
        }
    }

    private int getSupportLoad(int[][] stones) {
        int answer = 0;
        for (int i = 0; i < stones[0].length; i++) {
            for (int j = 0; j < stones.length; j++) {
                if (stones[i][j] == 2) {
                    answer += stones.length - j;
                }
            }
        }
        return answer;
    }
}
