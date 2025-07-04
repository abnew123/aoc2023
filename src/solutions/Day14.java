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
        int rows = stones.length;
        int cols = stones[0].length;
        for (int i = 0; i < rows; i++) {
            int lastObstacle = -1;
            for (int j = 0; j < cols; j++) {
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
        int rows = stones.length;
        int cols = stones[0].length;
        for (int i = 0; i < rows; i++) {
            int lastObstacle = -1;
            for (int j = 0; j < cols; j++) {
                int rowIndex = cols - 1 - j;
                if (stones[i][rowIndex] == 1) {
                    lastObstacle = j;
                    continue;
                }
                if (stones[i][rowIndex] == 2) {
                    stones[i][rowIndex] = 0;
                    stones[i][cols - 1 - (lastObstacle + 1)] = 2;
                    lastObstacle++;
                }
            }
        }
    }

    private void shiftWest(int[][] stones) {
        int rows = stones.length;
        int cols = stones[0].length;
        for (int i = 0; i < cols; i++) {
            int lastObstacle = -1;
            for (int j = 0; j < rows; j++) {
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
        int rows = stones.length;
        int cols = stones[0].length;
        for (int i = 0; i < cols; i++) {
            int lastObstacle = -1;
            for (int j = 0; j < rows; j++) {
                int colIndex = rows - 1 - j;
                if (stones[colIndex][i] == 1) {
                    lastObstacle = j;
                    continue;
                }
                if (stones[colIndex][i] == 2) {
                    stones[colIndex][i] = 0;
                    stones[rows - 1 - (lastObstacle + 1)][i] = 2;
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
