package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day17 extends DayTemplate {

    public String solve(boolean part1, Scanner in) {
        long answer = 1000000000;
        List<String[]> tmp = new ArrayList<>();
        while (in.hasNext()) {
            String[] line = in.nextLine().split("");
            tmp.add(line);
        }
        int[][] grid = new int[tmp.get(0).length][tmp.size()];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = Integer.parseInt(tmp.get(j)[i]);
            }
        }
        if (part1) {
            Queue<Location> queue = new PriorityQueue<>();
            Set<Location> allSeen = new HashSet<>();
            queue.add(new Location(0, 0, 0, 0, 0));
            int[] xs = new int[]{-1, 0, 0, 1};
            int[] ys = new int[]{0, -1, 1, 0};
            while (queue.size() > 0) {
                Location c = queue.poll();
                allSeen.add(c);
                if (c.x == grid.length - 1 && c.y == grid[0].length - 1) {
                    return c.currBest + "";
                }
                for (int i = 1; i < 5; i++) {
                    if (i + c.direction == 5) {
                        continue;
                    }
                    int newx = c.x + xs[i - 1];
                    int newy = c.y + ys[i - 1];
                    if (newx >= 0 && newy >= 0 && newx < grid.length && newy < grid[0].length) {
                        int steps = 1;
                        if (c.direction == i) {
                            if (c.steps >= 3) {
                                continue;
                            }
                            steps = c.steps + 1;
                        }
                        Location tmp2 = new Location(newx, newy, i, steps, c.currBest + grid[newx][newy]);
                        if (!allSeen.contains(tmp2) && !queue.contains(tmp2)) {
                            queue.add(tmp2);
                        }
                    }
                }
            }
        }
        else{
            Queue<Location> queue = new PriorityQueue<>();
            Set<Location> allSeen = new HashSet<>();
            queue.add(new Location(0, 0, 0, 0, 0));
            int[] xs = new int[]{-1, 0, 0, 1};
            int[] ys = new int[]{0, -1, 1, 0};
            while (queue.size() > 0) {
                Location c = queue.poll();
                allSeen.add(c);
                if (c.x == grid.length - 1 && c.y == grid[0].length - 1 && c.steps >=4) {
                    return c.currBest + "";
                }
                for (int i = 1; i < 5; i++) {
                    if (i + c.direction == 5) {
                        continue;
                    }
                    int newx = c.x + xs[i - 1];
                    int newy = c.y + ys[i - 1];
                    if (newx >= 0 && newy >= 0 && newx < grid.length && newy < grid[0].length) {
                        int steps = 1;
                        if (c.direction == i) {
                            if (c.steps >= 10) {
                                continue;
                            }
                            steps = c.steps + 1;
                        }
                        else{
                            if(c.steps < 4 && c.direction != 0){
                                continue;
                            }
                        }
                        Location tmp2 = new Location(newx, newy, i, steps, c.currBest + grid[newx][newy]);
                        if (!allSeen.contains(tmp2) && !queue.contains(tmp2)) {
                            queue.add(tmp2);
                        }
                    }
                }
            }
        }
        return answer + "";
    }


}

class Location implements Comparable<Location> {
    int x;
    int y;
    int direction;
    int steps;
    int currBest;

    public Location(int x, int y, int direction, int steps, int currBest) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.steps = steps;
        this.currBest = currBest;
    }

    @Override
    public int compareTo(Location o) {
        return currBest - o.currBest;
    }

    @Override
    public String toString() {
        return x + " " + y + " " + direction + " " + steps + " " + currBest;
    }

    @Override
    public boolean equals(Object other) {
        Location o = (Location) other;
        return o.x == x && o.y == y && o.direction == direction && o.steps == steps;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, direction, steps);
    }
}