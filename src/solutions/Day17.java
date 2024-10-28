package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day17 implements DayTemplate {

    int[][][] debug;

    int[] xs = new int[]{-1, 0, 0, 1};
    int[] ys = new int[]{0, -1, 1, 0};

    int[][] turns = new int[][] {new int[]{1,2,3,4}, new int[]{2,3}, new int[]{1,4}, new int[]{1,4}, new int[]{2,3}};

    @Override
    public String[] fullSolve(Scanner in) {
        int[][] grid = parse(in);
        initializeDebug(grid);
        long answer1 = solve(grid,1,3);
        initializeDebug(grid);
        return new String[]{answer1 + "", solve(grid,4,10) + ""};
    }

    public String solve(boolean part1, Scanner in) {
        int[][] grid = parse(in);
        initializeDebug(grid);
        return solve(grid, part1 ? 1 : 4, part1 ? 3 : 10) + "";
    }

    private void initializeDebug(int[][] grid){
        debug = new int[grid.length][grid[0].length][5];
        for (int i = 0; i < debug.length; i++) {
            for (int j = 0; j < debug[0].length; j++) {
                for (int k = 0; k < debug[0][0].length; k++) {
                    debug[i][j][k] = 999999;
                }
            }
        }
    }

    private int[][] parse(Scanner in){
        List<String[]> tmp = new ArrayList<>();
        while (in.hasNext()) {
            tmp.add(in.nextLine().split(""));
        }
        int[][] grid = new int[tmp.getFirst().length][tmp.size()];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = Integer.parseInt(tmp.get(j)[i]);
            }
        }
        return grid;
    }

    private long solve(int[][] grid, int min, int max) {
        long answer = 999999;
        Queue<Location> queue = new PriorityQueue<>();
        Set<Location> allSeen = new HashSet<>();
        queue.add(new Location(0, 0, 0, 0));
        while (!queue.isEmpty()) {
            Location c = queue.poll();
            allSeen.add(c);
            if (c.x == grid.length - 1 && c.y == grid[0].length - 1) {
                answer = Math.min(answer, c.currBest);
            }
            for (int k = 0; k < turns[c.direction].length; k++) {
                int i = turns[c.direction][k];
                int path = c.currBest;
                for (int j = 1; j <= max; j++) {
                    int newx = c.x + j * xs[i - 1];
                    int newy = c.y + j * ys[i - 1];
                    if (newx < 0 || newy < 0 || newx >= grid.length || newy >= grid[0].length) {
                        break;
                    }
                    path += grid[newx][newy];
                    if (j >= min && path < debug[newx][newy][i]) {
                        debug[newx][newy][i] = path;
                        Location tmp2 = new Location(newx, newy, i, path);
                        if (!allSeen.contains(tmp2)) {
                            queue.add(tmp2);
                        }
                    }
                }
            }
        }
        return answer;
    }

}

class Location implements Comparable<Location> {
    int x;
    int y;
    int direction;
    int currBest;

    public Location(int x, int y, int direction, int currBest) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.currBest = currBest;
    }

    @Override
    public int compareTo(Location o) {
        return currBest - o.currBest;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Location o) {
            return o.x == x && o.y == y && o.direction == direction;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, direction);
    }
}