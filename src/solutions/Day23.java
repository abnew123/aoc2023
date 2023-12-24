package src.solutions;

import src.meta.DayTemplate;
import src.objects.Coordinate;

import java.util.*;

public class Day23 extends DayTemplate {

    public String solve(boolean part1, Scanner in) {

        long answer = 0;
        List<String[]> tmp = new ArrayList<>();
        while (in.hasNext()) {
            String line = in.nextLine();
            tmp.add(line.split(""));
        }
        int[][] grid = new int[tmp.get(0).length][tmp.size()];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (tmp.get(j)[i].equals(".")) {
                    grid[i][j] = 1;
                }
                if (tmp.get(j)[i].equals("#")) {
                    grid[i][j] = 2;
                }
                if (tmp.get(j)[i].equals(">")) {
                    grid[i][j] = 3;
                }
                if (tmp.get(j)[i].equals("v")) {
                    grid[i][j] = 4;
                }
                if (tmp.get(j)[i].equals("<")) {
                    grid[i][j] = 5;
                }
                if (tmp.get(j)[i].equals("^")) {
                    grid[i][j] = 6;
                }
                if (grid[i][j] > 2 && !part1) {
                    grid[i][j] = 1;
                }
            }
        }
        List<Path> paths = new ArrayList<>();
        for (int i = 0; i < grid.length; i++) {
            if (grid[i][0] == 1) {
                Path p = new Path();
                p.path.add(new Coordinate(i, 0));
                p.latest = new Coordinate(i, 0);
                paths.add(p);
            }
        }
        int cycles = 20000;
        int index = 0;

        int[] xs = new int[]{1, 0, -1, 0};
        int[] ys = new int[]{0, 1, 0, -1};
        if (part1) {
            while (index < cycles && paths.size() > 0) {
                List<Path> tmpPaths = new ArrayList<>();
                for (Path p : paths) {
                    Coordinate current = p.latest;
                    if (current.y == grid[0].length - 1) {
                        answer = Math.max(answer, p.path.size() - 1);
                    }
                    for (int i = 0; i < 4; i++) {
                        Coordinate next = new Coordinate(current.x + xs[i], current.y + ys[i]);
                        if (p.path.contains(next)) {
                            continue;
                        }
                        if (next.x >= 0 && next.y >= 0 && next.x < grid.length && next.y < grid[0].length) {
                            if (grid[next.x][next.y] >= 3) {
                                Coordinate slide = new Coordinate(next.x + xs[grid[next.x][next.y] - 3], next.y + ys[grid[next.x][next.y] - 3]);
                                if (!p.path.contains(slide)) {
                                    Path newPath = new Path(p);
                                    newPath.path.add(next);
                                    newPath.path.add(slide);
                                    newPath.latest = slide;
                                    tmpPaths.add(newPath);
                                }
                            }

                            if (grid[next.x][next.y] == 1) {
                                Path newPath = new Path(p);
                                newPath.path.add(next);
                                newPath.latest = next;
                                if(!tmpPaths.contains(newPath)){
                                    tmpPaths.add(newPath);
                                }
                            }
                        }
                    }

                }
                paths = tmpPaths;
                index++;
            }

        } else {
            Set<Coordinate> intersections = new HashSet<>();
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[0].length; j++) {
                    int neighbors = 0;
                    if (grid[i][j] == 1) {
                        for (int k = 0; k < 4; k++) {
                            Coordinate next = new Coordinate(i + xs[k], j + ys[k]);
                            if (next.x >= 0 && next.y >= 0 && next.x < grid.length && next.y < grid[0].length) {
                                if (grid[next.x][next.y] == 1) {
                                    neighbors++;
                                }
                            }
                        }
                    }
                    if (neighbors == 1 || neighbors > 2) {
                        intersections.add(new Coordinate(i, j));
                    }
                }
            }
            List<Coordinate> hubs = new ArrayList<>(intersections);
            int[][] adjacencyMatrix = new int[intersections.size()][intersections.size()];
            Map<Coordinate, Set<Coordinate>> neighbors = new HashMap<>();
            for (int i = 0; i < adjacencyMatrix.length; i++) {
                for (int j = 0; j < adjacencyMatrix[0].length; j++) {
                    adjacencyMatrix[i][j] = maxPath(grid, hubs.get(i), hubs.get(j), hubs, neighbors);
                }
            }
            Stack<Path> stack = new Stack<>();
            stack.push(paths.get(0));
            while (stack.size() > 0) {
                Path p = stack.pop();
                Coordinate current = p.latest;
                if (current.y == grid[0].length - 1) {
                    if(p.pathLength > answer){
                        answer = p.pathLength;
                    }
                    continue;
                }
                for (Coordinate next : neighbors.get(current)) {
                    if (p.path.contains(next)) {
                        continue;
                    }
                    Path newPath = new Path(p);
                    newPath.latest = next;
                    newPath.path.add(next);
                    newPath.pathLength += adjacencyMatrix[hubs.indexOf(next)][hubs.indexOf(current)];
                    stack.push(newPath);
                }
            }
        }

        return answer + "";
    }

    private int maxPath(int[][] grid, Coordinate start, Coordinate end, List<Coordinate> intersections, Map<Coordinate, Set<Coordinate>> neighbors) {
        if(start.equals(end)){
            return -1;
        }
        int ret = -1;
        Path init = new Path();
        init.latest = start;
        init.path.add(start);
        List<Path> paths = new ArrayList<>();
        paths.add(init);
        int[] xs = new int[]{1, 0, -1, 0};
        int[] ys = new int[]{0, 1, 0, -1};

        while (paths.size() > 0) {
            List<Path> tmpPaths = new ArrayList<>();
            for (Path p : paths) {
                Coordinate current = p.latest;
                if (current.equals(end)) {
                    ret = Math.max(ret, p.path.size() - 1);
                }
                for (int i = 0; i < 4; i++) {
                    Coordinate next = new Coordinate(current.x + xs[i], current.y + ys[i]);
                    if (p.path.contains(next) || (intersections.contains(next) && !next.equals(end))) {
                        continue;
                    }
                    if (next.x >= 0 && next.y >= 0 && next.x < grid.length && next.y < grid[0].length) {
                        if (grid[next.x][next.y] == 1) {
                            Path newPath = new Path(p);
                            newPath.path.add(next);
                            newPath.latest = next;
                            if (!tmpPaths.contains(newPath)) {
                                tmpPaths.add(newPath);
                            }
                        }
                    }
                }
            }
            paths = tmpPaths;
        }
        if (ret != -1) {
            neighbors.putIfAbsent(start, new HashSet<>());
            neighbors.putIfAbsent(end, new HashSet<>());
            neighbors.get(start).add(end);
            neighbors.get(end).add(start);
        }

        return ret;
    }
}

class Path {
    Set<Coordinate> path = new HashSet<>();
    Coordinate latest;
    int pathLength = 0;

    public Path(Path root) {
        path.addAll(root.path);
        pathLength = root.pathLength;
    }

    public Path() {

    }

    public String toString() {
        return "" + path.size();
    }

    public boolean equals(Object o) {
        return latest.equals(((Path) o).latest) && path.equals(((Path) o).path);
    }
}