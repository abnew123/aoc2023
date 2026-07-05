package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day22 implements DayTemplate {

    int maxX = 0;
    int maxY = 0;
    int maxZ = 0;

    int[] xs = new int[]{1, 0, 0};
    int[] ys = new int[]{0, 1, 0};
    int[] zs = new int[]{0, 0, 1};

    int[][][] grid;

    List<Brick> bricks = new ArrayList<>();

    @Override
    public String[] fullSolve(Scanner in) {
        parse(in);
        grid = initializeGrid();
        assignBricksToGrid();
        simulateBricks();
        addDependencies();
        int[][] dependencies = dependencyArrays(true);
        int[][] dependents = dependencyArrays(false);
        int[] hardDependencies = generateHardDependencies();
        long answer1 = part1(hardDependencies);
        long answer2 = part2(dependencies, dependents);
        return new String[]{answer1 + "", answer2 + ""};
    }

    public String solve(boolean part1, Scanner in) {
        long answer;
        parse(in);
        grid = initializeGrid();
        assignBricksToGrid();
        simulateBricks();
        addDependencies();
        if(part1){
            int[] hardDependencies = generateHardDependencies();
            answer = part1(hardDependencies);
        }
        else{
            answer = part2(dependencyArrays(true), dependencyArrays(false));
        }

        return answer + "";
    }

    private long part1(int[] hardDependencies){
        long answer = 0;
        for (int hardDependency : hardDependencies) {
            if (hardDependency == 0) {
                answer++;
            }
        }
        return answer;
    }

    private long part2(int[][] dependencies, int[][] dependents){
        long answer = 0;
        int[] deadStamp = new int[bricks.size()];
        int[] queue = new int[bricks.size() * 4];
        for (int i = 0; i < bricks.size(); i++) {
            int stamp = i + 1;
            int fallen = 1;
            int head = 0;
            int tail = 0;
            deadStamp[i] = stamp;
            for (int dependent : dependents[i]) {
                queue[tail++] = dependent;
            }
            while (head < tail) {
                int brick = queue[head++];
                if (deadStamp[brick] == stamp) {
                    continue;
                }
                boolean allDependenciesFallen = true;
                for (int dependency : dependencies[brick]) {
                    if (deadStamp[dependency] != stamp) {
                        allDependenciesFallen = false;
                        break;
                    }
                }
                if (allDependenciesFallen) {
                    deadStamp[brick] = stamp;
                    fallen++;
                    for (int dependent : dependents[brick]) {
                        if (tail == queue.length) {
                            queue = Arrays.copyOf(queue, queue.length * 2);
                        }
                        queue[tail++] = dependent;
                    }
                }
            }
            answer += fallen - 1;
        }
        return answer;
    }

    private void parse(Scanner in){
        while (in.hasNext()) {
            String line = in.nextLine();
            Brick b = new Brick(line);
            bricks.add(b);
            maxX = Math.max(maxX, b.x + b.size);
            maxY = Math.max(maxY, b.y + b.size);
            maxZ = Math.max(maxZ, b.z + b.size);
        }
    }

    private int[][][] initializeGrid(){
        grid = new int[maxX][maxY][maxZ];
        int rows = grid.length;
        int cols = grid[0].length;
        int depth = grid[0][0].length;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                for (int k = 0; k < depth; k++) {
                    grid[i][j][k] = -1;
                }
            }
        }
        return grid;
    }

    private void assignBricksToGrid(){
        Collections.sort(bricks);
        Collections.reverse(bricks);
        for (int i = 0; i < bricks.size(); i++) {
            bricks.get(i).id = i;
        }
        for (Brick b : bricks) {
            for (int i = 0; i < b.size; i++) {
                grid[b.x + i * xs[b.dir]][b.y + i * ys[b.dir]][b.z + i * zs[b.dir]] = b.id;
            }
        }
    }

    private void simulateBricks(){
        for (Brick b : bricks) {
            boolean supported = b.z == 1;
            while (!supported) {
                supported = supported(b);
                if (!supported) {
                    for (int k = 0; k < b.size; k++) {
                        grid[b.x + k * xs[b.dir]][b.y + k * ys[b.dir]][b.z + k * zs[b.dir] - 1] = b.id;
                        grid[b.x + k * xs[b.dir]][b.y + k * ys[b.dir]][b.z + k * zs[b.dir]] = -1;
                    }
                    b.z = b.z - 1;
                }
            }
        }
    }

    private boolean supported(Brick b){
        boolean supported = b.z == 1;
        if (b.dir == 2) {
            if (grid[b.x][b.y][b.z - 1] != -1) {
                supported = true;
            }
        } else {
            for (int k = 0; k < b.size; k++) {
                if (grid[b.x + k * xs[b.dir]][b.y + k * ys[b.dir]][b.z - 1] != -1) {
                    supported = true;
                    break;
                }
            }
        }
        return supported;
    }
    private void addDependencies(){
        for (Brick b : bricks) {
            if (b.dir != 2) {
                for (int k = 0; k < b.size; k++) {
                    if (grid[b.x + k * xs[b.dir]][b.y + k * ys[b.dir]][b.z + 1] != -1) {
                        bricks.get(grid[b.x + k * xs[b.dir]][b.y + k * ys[b.dir]][b.z + 1]).dependencies.add(b.id);
                        bricks.get(b.id).dependents.add(grid[b.x + k * xs[b.dir]][b.y + k * ys[b.dir]][b.z + 1]);
                    }
                }
            } else {
                if (grid[b.x][b.y][b.z + b.size] != -1) {
                    bricks.get(grid[b.x][b.y][b.z + b.size]).dependencies.add(b.id);
                    bricks.get(b.id).dependents.add(grid[b.x][b.y][b.z + b.size]);
                }
            }
        }
    }

    private int[] generateHardDependencies(){
        int[] hardDependencies = new int[bricks.size()];
        for (Brick b : bricks) {
            if (b.dependencies.size() == 1) {
                for (Integer i : b.dependencies) {
                    hardDependencies[i]++;
                }
            }
        }
        return hardDependencies;
    }

    private int[][] dependencyArrays(boolean dependency) {
        int[][] arrays = new int[bricks.size()][];
        for (int i = 0; i < bricks.size(); i++) {
            Set<Integer> source = dependency ? bricks.get(i).dependencies : bricks.get(i).dependents;
            arrays[i] = new int[source.size()];
            int index = 0;
            for (int brick : source) {
                arrays[i][index++] = brick;
            }
        }
        return arrays;
    }
}

class Brick implements Comparable<Brick> {
    int id;
    int x;
    int y;
    int z;
    int size;
    int dir;
    Set<Integer> dependents = new HashSet<>();
    Set<Integer> dependencies = new HashSet<>();

    public Brick(String line) {
        String[] ends = line.split("~");
        int x1 = Integer.parseInt(ends[0].split(",")[0]);
        int y1 = Integer.parseInt(ends[0].split(",")[1]);
        int z1 = Integer.parseInt(ends[0].split(",")[2]);
        int x2 = Integer.parseInt(ends[1].split(",")[0]);
        int y2 = Integer.parseInt(ends[1].split(",")[1]);
        int z2 = Integer.parseInt(ends[1].split(",")[2]);
        x = Math.min(x1, x2);
        y = Math.min(y1, y2);
        z = Math.min(z1, z2);
        if (x1 != x2) {
            dir = 0;
            size = Math.abs(x1 - x2);
        }
        if (y1 != y2) {
            dir = 1;
            size = Math.abs(y1 - y2);
        }
        if (z1 != z2) {
            dir = 2;
            size = Math.abs(z1 - z2);
        }
        size++;
    }

    @Override
    public int compareTo(Brick o) {
        return o.z - z;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Brick o) {
            return o.z == z;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(z);
    }
}
