package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day10 extends DayTemplate {

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
        int[][] distances;
        List<List<String>> grid = new ArrayList<>();
        while (in.hasNext()) {
            String line = in.nextLine();
            grid.add(Arrays.stream(line.split("")).toList());
        }
        distances = new int[grid.get(0).size() * 2 + 1][grid.size() * 2 + 1];
        Location curr = null;
        Location prev = null;
        for (int i = 0; i < distances.length; i++) {
            for (int j = 0; j < distances[0].length; j++) {
                if (i % 2 == 1 && j % 2 == 1 && grid.get(i / 2).get(j / 2).equals("S")) {
                    distances[i][j] = 1;
                    curr = new Location(j / 2, i / 2);
                } else {
                    distances[i][j] = 2;
                }
            }
        }
        List<Location> been = new ArrayList<>();
        while (!been.contains(curr)) {
            Location tmp = curr;
            answer += 1;
            String pipe = grid.get(curr.y).get(curr.x);
            if (prev == null) {
                curr = new Location(curr.x, curr.y + 1);
            } else {
                if (pipe.equals("|")) {
                    if (prev.equals(new Location(curr.x, curr.y + 1))) {
                        curr = new Location(curr.x, curr.y - 1);
                    } else {
                        curr = new Location(curr.x, curr.y + 1);
                    }
                }
                if (pipe.equals("-")) {
                    if (prev.equals(new Location(curr.x + 1, curr.y))) {
                        curr = new Location(curr.x - 1, curr.y);
                    } else {
                        curr = new Location(curr.x + 1, curr.y);
                    }
                }
                if (pipe.equals("L")) {
                    if (prev.equals(new Location(curr.x + 1, curr.y))) {
                        curr = new Location(curr.x, curr.y - 1);
                    } else {
                        curr = new Location(curr.x + 1, curr.y);
                    }
                }
                if (pipe.equals("J")) {
                    if (prev.equals(new Location(curr.x - 1, curr.y))) {
                        curr = new Location(curr.x, curr.y - 1);
                    } else {
                        curr = new Location(curr.x - 1, curr.y);
                    }
                }
                if (pipe.equals("7")) {
                    if (prev.equals(new Location(curr.x, curr.y + 1))) {
                        curr = new Location(curr.x - 1, curr.y);
                    } else {
                        curr = new Location(curr.x, curr.y + 1);
                    }
                }
                if (pipe.equals("F")) {
                    if (prev.equals(new Location(curr.x, curr.y + 1))) {
                        curr = new Location(curr.x + 1, curr.y);
                    } else {
                        curr = new Location(curr.x, curr.y + 1);
                    }
                }
            }
            been.add(prev);
            prev = tmp;
            distances[curr.x * 2 + 1][curr.y * 2 + 1] = 1;
            distances[curr.x + prev.x + 1][curr.y + prev.y + 1] = 1;
        }
        boolean check = true;
        distances[0][0] = 0;
        while (check) {
            check = false;
            for (int i = 0; i < distances.length; i++) {
                for (int j = 0; j < distances[i].length; j++) {
                    if (distances[i][j] == 0) {
                        if (i > 0 && distances[i - 1][j] == 2) {
                            distances[i - 1][j] = 0;
                            check = true;
                        }
                        if (i < distances.length - 1 && distances[i + 1][j] == 2) {
                            distances[i + 1][j] = 0;
                            check = true;
                        }
                        if (j > 0 && distances[i][j - 1] == 2) {
                            distances[i][j - 1] = 0;
                            check = true;
                        }
                        if (j < distances[i].length - 1 && distances[i][j + 1] == 2) {
                            distances[i][j + 1] = 0;
                            check = true;
                        }
                    }
                }
            }
        }
        if (!part1) {
            answer = 0;
            for (int i = 1; i < distances.length; i += 2) {
                for (int j = 1; j < distances[i].length; j += 2) {
                    if (distances[i][j] == 2) {
                        answer += 1;
                    }
                }
            }
        }
        if (part1) {
            answer /= 2;
        }
        return answer + "";
    }
}

class Location {
    int x;
    int y;

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return x == location.x && y == location.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}