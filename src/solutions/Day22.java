package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day22 implements DayTemplate {

    int minX = 0;
    int maxX = 0;
    int minY = 0;
    int maxY = 0;

    List<Brick> bricks = new ArrayList<>();

    @Override
    public String[] fullSolve(Scanner in) {
        parse(in);
        settleBricks();
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
        settleBricks();
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
        bricks.clear();
        minX = Integer.MAX_VALUE;
        maxX = Integer.MIN_VALUE;
        minY = Integer.MAX_VALUE;
        maxY = Integer.MIN_VALUE;
        while (in.hasNext()) {
            String line = in.nextLine();
            Brick b = new Brick(line);
            bricks.add(b);
            minX = Math.min(minX, b.x);
            maxX = Math.max(maxX, b.x + (b.dir == 0 ? b.size : 1));
            minY = Math.min(minY, b.y);
            maxY = Math.max(maxY, b.y + (b.dir == 1 ? b.size : 1));
        }
        if (bricks.isEmpty()) {
            minX = maxX = minY = maxY = 0;
        }
    }

    private void settleBricks() {
        bricks.sort(Comparator.comparingInt(brick -> brick.z));
        int surfaceHeight = maxY - minY;
        int[] topHeight = new int[(maxX - minX) * surfaceHeight];
        int[] topBrick = new int[topHeight.length];
        Arrays.fill(topBrick, -1);

        for (int id = 0; id < bricks.size(); id++) {
            Brick brick = bricks.get(id);
            brick.id = id;
            int footprintSize = brick.dir == 2 ? 1 : brick.size;
            int dx = brick.dir == 0 ? 1 : 0;
            int dy = brick.dir == 1 ? 1 : 0;
            int supportHeight = 0;
            for (int offset = 0; offset < footprintSize; offset++) {
                int cell = (brick.x + offset * dx - minX) * surfaceHeight
                        + brick.y + offset * dy - minY;
                supportHeight = Math.max(supportHeight, topHeight[cell]);
            }

            brick.z = supportHeight + 1;
            for (int offset = 0; offset < footprintSize; offset++) {
                int cell = (brick.x + offset * dx - minX) * surfaceHeight
                        + brick.y + offset * dy - minY;
                int supporter = topBrick[cell];
                if (topHeight[cell] == supportHeight && supporter >= 0
                        && brick.dependencies.add(supporter)) {
                    bricks.get(supporter).dependents.add(id);
                }
            }

            int newTop = brick.z + (brick.dir == 2 ? brick.size - 1 : 0);
            for (int offset = 0; offset < footprintSize; offset++) {
                int cell = (brick.x + offset * dx - minX) * surfaceHeight
                        + brick.y + offset * dy - minY;
                topHeight[cell] = newTop;
                topBrick[cell] = id;
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

class Brick {
    int id;
    int x;
    int y;
    int z;
    int size;
    int dir;
    Set<Integer> dependents = new HashSet<>();
    Set<Integer> dependencies = new HashSet<>();

    public Brick(String line) {
        int[] coordinates = new int[6];
        int coordinate = 0;
        int value = 0;
        int sign = 1;
        boolean reading = false;
        for (int index = 0; index <= line.length(); index++) {
            char c = index == line.length() ? ',' : line.charAt(index);
            if (c == '-') {
                sign = -1;
            } else if (c >= '0' && c <= '9') {
                value = value * 10 + c - '0';
                reading = true;
            } else if (reading) {
                coordinates[coordinate++] = sign * value;
                value = 0;
                sign = 1;
                reading = false;
            }
        }
        if (coordinate != coordinates.length) {
            throw new IllegalArgumentException("Expected six brick coordinates: " + line);
        }
        int x1 = coordinates[0];
        int y1 = coordinates[1];
        int z1 = coordinates[2];
        int x2 = coordinates[3];
        int y2 = coordinates[4];
        int z2 = coordinates[5];
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
}
