package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day14 implements DayTemplate {

    Map<Integer, Integer> states;
    int[][] stones;

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
        return run(part1, stones);
    }

    /**
     * Solves both parts of the day.
     *
     * @param in The solver will read data from this Scanner.
     * @return Returns answer as a string array, with part 1 as index 0 and part 2 as index 1
     */
    @Override
    public String[] fullSolve(Scanner in) {
        parse(in);
        int[][] parsed = stones;
        // run() tilts the grid in place, so each part gets its own copy of the
        // parsed layout. Without the copies part 2 would start from a grid that
        // part 1 had already tilted north.
        String answer1 = run(true, copyGrid(parsed));
        String answer2 = run(false, copyGrid(parsed));
        return new String[]{answer1, answer2};
    }

    private int[][] copyGrid(int[][] original) {
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = Arrays.copyOf(original[i], original[i].length);
        }
        return copy;
    }

    private String run(boolean part1, int[][] stones) {
        states = new HashMap<>();
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
                    int cycle = index - states.get(Arrays.deepHashCode(stones) + tmpVal);
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

    private void parse(Scanner in){
        List<String> tmp = new ArrayList<>();
        while (in.hasNext()) {
            tmp.add(in.nextLine());
        }
        stones = new int[tmp.get(0).length()][tmp.size()];
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
    }

    private void shiftNorth(int[][] stones) {
        for (int i = 0; i < stones.length; i++) {
            int lastObstacle = -1;
            for (int j = 0; j < stones[0].length; j++) {
                if (stones[i][j] == 1) {
                    lastObstacle = j;
                    continue;
                }
                if (stones[i][j] == 2) {
                    stones[i][j] = 0;
                    stones[i][lastObstacle + 1] = 2;
                    lastObstacle++;
                }
            }
        }
    }

    private void shiftSouth(int[][] stones) {
        for (int i = 0; i < stones.length; i++) {
            int lastObstacle = -1;
            for (int j = 0; j < stones[0].length; j++) {
                if (stones[i][stones.length - 1-j] == 1) {
                    lastObstacle = j;
                    continue;
                }
                if (stones[i][stones.length - 1-j] == 2) {
                    stones[i][stones.length - 1-j] = 0;
                    stones[i][stones.length - 1-(lastObstacle + 1)] = 2;
                    lastObstacle++;
                }
            }
        }
    }

    private void shiftWest(int[][] stones) {
        for (int i = 0; i < stones.length; i++) {
            int lastObstacle = -1;
            for (int j = 0; j < stones[0].length; j++) {
                if (stones[j][i] == 1) {
                    lastObstacle = j;
                    continue;
                }
                if (stones[j][i] == 2) {
                    stones[j][i] = 0;
                    stones[lastObstacle + 1][i] = 2;
                    lastObstacle++;
                }
            }
        }
    }

    private void shiftEast(int[][] stones) {
        for (int i = 0; i < stones.length; i++) {
            int lastObstacle = -1;
            for (int j = 0; j < stones[0].length; j++) {
                if (stones[stones.length - 1-j][i] == 1) {
                    lastObstacle = j;
                    continue;
                }
                if (stones[stones.length - 1-j][i] == 2) {
                    stones[stones.length - 1-j][i] = 0;
                    stones[stones.length - 1-(lastObstacle + 1)][i] = 2;
                    lastObstacle++;
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
