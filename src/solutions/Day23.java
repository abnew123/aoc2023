package src.solutions;

import src.meta.DayTemplate;
import src.objects.Coordinate;

import java.util.*;

//Possible optimization, add row of # below and above to remove need for boundary checks

public class Day23 extends DayTemplate {

    public String solve(boolean part1, Scanner in) {

        long answer = 0;
        List<String[]> tmp = new ArrayList<>();
        while (in.hasNext()) {
            String line = in.nextLine();
            tmp.add(line.split(""));
        }
        int[][] grid = new int[tmp.get(0).length][tmp.size()];
        Map<String, Integer> gridBuilder = Map.of(".", 1, "#", 2, ">", 3, "v", 4, "<", 5, "^", 6);
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = gridBuilder.get(tmp.get(j)[i]);
                if (grid[i][j] > 2 && !part1) {
                    grid[i][j] = 1;
                }
            }
        }
        Path path = new Path();
        List<Path> paths = new ArrayList<>();
        for (int i = 0; i < grid.length; i++) {
            if (grid[i][0] == 1) {
                path.path.add(new Coordinate(i, 0));
                path.latest = new Coordinate(i, 0);
            }
        }

        int[] xs = new int[]{1, 0, -1, 0};
        int[] ys = new int[]{0, 1, 0, -1};
        Set<Coordinate> intersections = new HashSet<>();
        Map<Coordinate, Integer> labels = new HashMap<>();
        int index = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                int neighbors = 0;
                if (grid[i][j] != 2) {
                    for (int k = 0; k < 4; k++) {
                        Coordinate next = new Coordinate(i + xs[k], j + ys[k]);
                        if (next.x >= 0 && next.y >= 0 && next.x < grid.length && next.y < grid[0].length) {
                            if (grid[next.x][next.y] != 2) {
                                neighbors++;
                            }
                        }
                    }
                }
                if (neighbors == 1 || neighbors > 2) {
                    intersections.add(new Coordinate(i, j));
                    labels.put(new Coordinate(i,j), index);
                    index++;
                }
            }
        }
        int[][] adjacencyMatrix = new int[intersections.size()][intersections.size()];
        Map<Coordinate, Set<Coordinate>> neighbors = new HashMap<>();
        for(Coordinate intersection: intersections){
            bfs(intersection, adjacencyMatrix, neighbors, grid, labels);
        }
        Stack<Path> stack = new Stack<>();
        stack.push(path);
        while (stack.size() > 0) {
            Path p = stack.pop();
            Coordinate current = p.latest;
            if (current.y == grid[0].length - 1) {
                if (p.pathLength > answer) {
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
                newPath.pathLength += adjacencyMatrix[labels.get(current)][labels.get(next)];
                stack.push(newPath);
            }
        }
        return answer + "";
    }

    private void bfs(Coordinate start, int[][] adjacencyMatrix, Map<Coordinate, Set<Coordinate>> neighbors, int[][] grid, Map<Coordinate, Integer> labels){
        int[] xs = new int[]{1, 0, -1, 0};
        int[] ys = new int[]{0, 1, 0, -1};
        Queue<Coordinate> queue = new LinkedList<>();
        Set<Coordinate> visited = new HashSet<>();
        queue.add(start);
        neighbors.putIfAbsent(start, new HashSet<>());
        while(!queue.isEmpty()){
            Coordinate curr = queue.poll();
            for (int k = 0; k < 4; k++) {
                if(grid[curr.x][curr.y] > 2 && k != grid[curr.x][curr.y] - 3){
                    continue;
                }
                Coordinate next = new Coordinate(curr.x + xs[k], curr.y + ys[k]);
                if (next.x >= 0 && next.y >= 0 && next.x < grid.length && next.y < grid[0].length && !visited.contains(next)) {
                    next.weight = curr.weight + 1;
                    if(labels.containsKey(next)){
                        neighbors.get(start).add(next);
                        adjacencyMatrix[labels.get(start)][labels.get(next)] = next.weight;
                    }
                    else{
                        if (grid[next.x][next.y] != 2) {
                            queue.add(next);
                            visited.add(next);
                        }
                    }

                }
            }
        }
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