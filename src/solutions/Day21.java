package src.solutions;

import src.meta.DayTemplate;
import src.objects.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day21 implements DayTemplate {

    int[][] grid;

    
    public String solve(boolean part1, Scanner in) {
        long ans = 0;
        List<Coordinate> reachablePoints = new ArrayList<>();
        reachablePoints.add(buildGridAndGetStart(in));
        int[] xs = new int[]{-1, 1, 0, 0};
        int[] ys = new int[]{0, 0, -1, 1};
        int index = 0;
        while (index < (part1 ? 64 : grid.length)) {
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
        if (part1) {
            ans = solvePart1();
        }
        if (!part1) {
            ans = solvePart2();
        }
        return ans + "";
    }

    Coordinate buildGridAndGetStart(Scanner in){
        List<String[]> tmp = new ArrayList<>();
        while (in.hasNext()) {
            String line = in.nextLine();
            tmp.add(line.split(""));
        }
        int x = 0;
        int y = 0;
        int rows = tmp.size();
        int cols = tmp.get(0).length;
        grid = new int[cols][rows];
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
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

    long solvePart1(){
        long ans = 0;
        int rows = grid.length;
        int cols = grid[0].length;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] % 2 == 0 && grid[i][j] <= 64) {
                    ans++;
                }
            }
        }
        return ans;
    }

    long solvePart2(){
        long[] evenOddLarge = new long[4];
        int rows = grid.length;
        int cols = grid[0].length;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] != -1 && grid[i][j] != 9999) {
                    evenOddLarge[(i + j) % 2]++;
                    if(grid[i][j] > rows / 2){
                        evenOddLarge[(i + j) % 2 + 2]++;
                    }
                }
            }
        }
        long size = 26501365 / rows;
        return ((size + 1) * (size + 1) * evenOddLarge[1]) + (size * size * evenOddLarge[0]) - ((size + 1) * evenOddLarge[3]) + (size * evenOddLarge[2]);
    }
}
