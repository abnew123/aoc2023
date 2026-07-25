package src.solutions;

import src.meta.DayTemplate;
import src.objects.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day21 implements DayTemplate {

    int[][] grid;

    /**
     * Main solving method.
     *
     * @param part1 The solver will solve part 1 if param is set to true.
     *              The solver will solve part 2 if param is set to false.
     * @param in    The solver will read data from this Scanner.
     * @return Returns answer in string format.
     */
    public String solve(boolean part1, Scanner in) {
        long answer = 0;
        List<Coordinate> reachablePoints = new ArrayList<>();
        reachablePoints.add(buildGridAndGetStart(in));
        walk(reachablePoints, 0, part1 ? 64 : grid.length);
        if (part1) {
            answer = solvePart1();
        }
        if (!part1) {
            answer = solvePart2();
        }
        return answer + "";
    }

    /**
     * Solves both parts of the day.
     *
     * @param in The solver will read data from this Scanner.
     * @return Returns answer as a string array, with part 1 as index 0 and part 2 as index 1
     */
    @Override
    public String[] fullSolve(Scanner in) {
        List<Coordinate> reachablePoints = new ArrayList<>();
        reachablePoints.add(buildGridAndGetStart(in));
        int part2Steps = grid.length;
        String answer1;
        String answer2;
        if (part2Steps >= 64) {
            // The flood fill writes each cell's shortest distance from the start, so
            // walking further never changes a distance that was already written.
            // solvePart1() only counts cells with a distance of at most 64, which makes
            // the deeper walk that part 2 needs safe to share with part 1.
            walk(reachablePoints, 0, part2Steps);
            answer1 = solvePart1() + "";
            answer2 = solvePart2() + "";
        } else {
            // solvePart2() counts every cell the walk reached, so it has to be read
            // before the walk is extended past grid.length for part 1.
            reachablePoints = walk(reachablePoints, 0, part2Steps);
            answer2 = solvePart2() + "";
            walk(reachablePoints, part2Steps, 64);
            answer1 = solvePart1() + "";
        }
        return new String[]{answer1, answer2};
    }

    /**
     * Flood fills the grid, stamping each newly reached cell with the step count at
     * which it was reached. Resumable: pass the frontier and step count of a previous
     * call to continue the same walk.
     *
     * @return the frontier after the last step.
     */
    private List<Coordinate> walk(List<Coordinate> reachablePoints, int index, int steps) {
        int[] xs = new int[]{-1, 1, 0, 0};
        int[] ys = new int[]{0, 0, -1, 1};
        while (index < steps) {
            index++;
            List<Coordinate> tmp2 = new ArrayList<>();
            for (Coordinate c : reachablePoints) {
                for (int i = 0; i < 4; i++) {
                    int newx = c.x + xs[i];
                    int newy = c.y + ys[i];
                    if (newx >= 0 && newy >= 0 && newx < grid.length && newy < grid[0].length && grid[newx][newy] == 9999) {
                        Coordinate candidate = new Coordinate(newx, newy);
                        tmp2.add(candidate);
                        grid[newx][newy] = index;
                    }
                }
            }
            reachablePoints = tmp2;
        }
        return reachablePoints;
    }

    private Coordinate buildGridAndGetStart(Scanner in){
        List<String[]> tmp = new ArrayList<>();
        while (in.hasNext()) {
            String line = in.nextLine();
            tmp.add(line.split(""));
        }
        int x = 0;
        int y = 0;
        grid = new int[tmp.get(0).length][tmp.size()];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (tmp.get(j)[i].equals("S")) {
                    x = i;
                    y = j;
                    grid[i][j] = 0;
                } else {
                    grid[i][j] = tmp.get(j)[i].equals("#") ? -1 : 9999;
                }
            }
        }
        return new Coordinate(x,y);
    }

    private long solvePart1(){
        long answer = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] % 2 == 0 && grid[i][j] <= 64) {
                    answer++;
                }
            }
        }
        return answer;
    }

    private long solvePart2(){
        long[] evenOddLarge = new long[4];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] != -1 && grid[i][j] != 9999) {
                    evenOddLarge[(i + j) % 2]++;
                    if(grid[i][j] > grid.length / 2){
                        evenOddLarge[(i + j) % 2 + 2]++;
                    }
                }
            }
        }
        long size = 26501365 / grid.length;
        return ((size + 1) * (size + 1) * evenOddLarge[1]) + (size * size * evenOddLarge[0]) - ((size + 1) * evenOddLarge[3]) + (size * evenOddLarge[2]);
    }
}
