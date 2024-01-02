package src.solutions;

import src.meta.DayTemplate;
import src.objects.Coordinate;

import java.util.*;

public class Day16 extends DayTemplate {

    static int[][] dirChart;

    static int[] xs = new int[]{-1, 1, 0, 0};
    static int[] ys = new int[]{0, 0, -1, 1};

    static Set<Coordinate> pointsOfInterest;

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
        pointsOfInterest = new HashSet<>();
        dirChart = new int[6][4];
        for (int i = 0; i < dirChart.length; i++) {
            for (int j = 0; j < dirChart[0].length; j++) {
                if (i == 0) {
                    dirChart[i][j] = 3 - j;
                }
                if (i == 1) {
                    dirChart[i][j] = (j + 2) % 4;
                }
                if (i == 2) {
                    if (j >= 2) {
                        dirChart[i][j] = j;
                    } else {
                        dirChart[i][j] = 2;
                    }
                }
                if (i == 3) {
                    if (j < 2) {
                        dirChart[i][j] = j;
                    } else {
                        dirChart[i][j] = 0;
                    }
                }
                if (i == 4) {
                    if (j >= 2) {
                        dirChart[i][j] = j;
                    } else {
                        dirChart[i][j] = 3;
                    }
                }
                if (i == 5) {
                    if (j < 2) {
                        dirChart[i][j] = j;
                    } else {
                        dirChart[i][j] = 1;
                    }
                }
            }
        }
        int[][] graph = new int[tmpGraph.size()][tmpGraph.get(0).length];
        Map<String, Integer> graphConvert = Map.of(".", 1, "/", 2, "\\", 3, "|", 4, "-", 5);
        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph[0].length; j++) {
                graph[i][j] = graphConvert.get(tmpGraph.get(j)[i]);
                if (graph[i][j] != 1) {
                    pointsOfInterest.add(new Coordinate(i, j));
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
            beams = oneCycle(graph, energized, beams, seen);
        }
        return totalEnergized(energized);
    }

    private int totalEnergized(int[][] energized) {
        int answer = 0;
        for (int i = 0; i < energized.length; i++) {
            for (int j = 0; j < energized[0].length; j++) {
                if (energized[i][j] > 0) {
                    answer++;
                }
            }
        }
        return answer;
    }

    private Set<Beam> oneCycle(int[][] graph, int[][] energized, Set<Beam> beams, Set<Beam> seen) {
        Set<Beam> newBeams = new HashSet<>();
        for (Beam beam : beams) {
            int dir = beam.direction;
            int newx = beam.x + xs[dir];
            int newy = beam.y + ys[dir];
            while (!pointsOfInterest.contains(new Coordinate(newx, newy)) && inBounds(newx, newy, graph)) {
                energized[newx][newy]++;
                newx += xs[dir];
                newy += ys[dir];
            }
            if (!inBounds(newx, newy, graph)) {
                continue;
            }
            energized[newx][newy]++;
            int str = graph[newx][newy];
            if (str < 4) {
                newBeams.add(new Beam(newx, newy, dirChart[str - 2][dir]));
            } else {
                newBeams.add(new Beam(newx, newy, dirChart[str - 2][dir]));
                newBeams.add(new Beam(newx, newy, dirChart[str][dir]));
            }
        }
        newBeams.removeAll(seen);
        seen.addAll(newBeams);
        return newBeams;
    }

    public boolean inBounds(int x, int y, int[][] graph) {
        return x >= 0 && y >= 0 && x < graph.length && y < graph.length;
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