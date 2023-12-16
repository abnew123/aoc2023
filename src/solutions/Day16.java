package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day16 extends DayTemplate {

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
        List<String[]> tmpGraph = new ArrayList<>();
        while (in.hasNext()) {
            String[] line = in.nextLine().split("");
            tmpGraph.add(line);
        }
        int[][] graph = new int[tmpGraph.size()][tmpGraph.get(0).length];
        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph[0].length; j++) {
                String val = tmpGraph.get(j)[i];
                if (val.equals(".")) {
                    graph[i][j] = 1;
                }
                if (val.equals("/")) {
                    graph[i][j] = 2;
                }
                if (val.equals("\\")) {
                    graph[i][j] = 3;
                }
                if (val.equals("|")) {
                    graph[i][j] = 4;
                }
                if (val.equals("-")) {
                    graph[i][j] = 5;
                }
            }
        }
        if (part1) {
            answer = tryFromLocation(graph, -1, 0, 1);
        } else {
            long currBest = 0;
            for (int i = 0; i < graph.length; i++) {
                long result1 = tryFromLocation(graph, -1, i, 1);
                if (result1 > currBest) {
                    currBest = result1;
                }
                long result2 = tryFromLocation(graph, graph.length, i, 0);
                if (result2 > currBest) {
                    currBest = result2;
                }
                long result3 = tryFromLocation(graph, i, -1, 3);
                if (result3 > currBest) {
                    currBest = result3;
                }
                long result4 = tryFromLocation(graph, i, graph.length, 2);
                if (result4 > currBest) {
                    currBest = result4;
                }
            }
            answer = currBest;
        }

        return answer + "";
    }

    private long tryFromLocation(int[][] graph, int x, int y, int dir) {
        Set<Beam> beams = new HashSet<>();
        Set<Beam> seen = new HashSet<>();
        int[][] energized = new int[graph.length][graph[0].length];
        beams.add(new Beam(x, y, dir));
        while (beams.size() > 0) {
            beams = helper(graph, energized, beams, seen);
        }
        return totalEnergized(energized);
    }

    private long totalEnergized(int[][] energized) {
        long answer = 0;
        for (int i = 0; i < energized.length; i++) {
            for (int j = 0; j < energized[0].length; j++) {
                if (energized[i][j] > 0) {
                    answer++;
                }
            }
        }
        return answer;
    }

    private Set<Beam> helper(int[][] graph, int[][] energized, Set<Beam> beams, Set<Beam> seen) {
        Set<Beam> newBeams = new HashSet<>();
        int[] xs = new int[]{-1, 1, 0, 0};
        int[] ys = new int[]{0, 0, -1, 1};
        for (Beam beam : beams) {
            int x = beam.x;
            int y = beam.y;
            int dir = beam.direction;
            int newx = x + xs[dir];
            int newy = y + ys[dir];
            if (newx < 0 || newx >= graph.length || newy < 0 || newy >= graph[0].length) {
                continue;
            }
            energized[newx][newy]++;
            int str = graph[newx][newy];
            if (str == 1) {
                newBeams.add(new Beam(newx, newy, dir));
            }
            if (str == 2) {
                newBeams.add(new Beam(newx, newy, 3 - dir));
            }
            if (str == 3) {
                newBeams.add(new Beam(newx, newy, (dir + 2) % 4));
            }
            if (str == 4) {
                if (dir >= 2) {
                    newBeams.add(new Beam(newx, newy, dir));
                } else {
                    newBeams.add(new Beam(newx, newy, 2));
                    newBeams.add(new Beam(newx, newy, 3));
                }
            }
            if (str == 5) {
                if (dir < 2) {
                    newBeams.add(new Beam(newx, newy, dir));
                } else {
                    newBeams.add(new Beam(newx, newy, 0));
                    newBeams.add(new Beam(newx, newy, 1));
                }
            }
        }
        newBeams.removeAll(seen);
        seen.addAll(newBeams);
        return newBeams;
    }
}

class Beam {
    int x;
    int y;
    int direction;

    public Beam(int x, int y, int direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Beam beam = (Beam) o;
        return x == beam.x && y == beam.y && direction == beam.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, direction);
    }
}