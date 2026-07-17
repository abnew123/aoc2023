package src.solutions;

import src.meta.DayTemplate;
import java.util.*;

public class Day17 implements DayTemplate {

    private static final int DIRECTION_COUNT = 5;
    private static final int INF = 1_000_000_000;
    private static final int[] DX = new int[]{0, -1, 0, 0, 1};
    private static final int[] DY = new int[]{0, 0, -1, 1, 0};
    private static final int[][] TURNS = new int[][]{
            new int[]{1, 2, 3, 4},
            new int[]{2, 3},
            new int[]{1, 4},
            new int[]{1, 4},
            new int[]{2, 3}
    };

    @Override
    public String[] fullSolve(Scanner in) {
        Grid grid = parse(in);
        int answer1 = solve(grid, 1, 3);
        return new String[]{answer1 + "", solve(grid, 4, 10) + ""};
    }

    public String solve(boolean part1, Scanner in) {
        Grid grid = parse(in);
        return solve(grid, part1 ? 1 : 4, part1 ? 3 : 10) + "";
    }

    private Grid parse(Scanner in) {
        List<String> lines = new ArrayList<>();
        while (in.hasNextLine()) {
            String line = in.nextLine();
            if (!line.isEmpty()) {
                lines.add(line);
            }
        }
        int height = lines.size();
        int width = lines.getFirst().length();
        int[] heatLoss = new int[width * height];
        int maxHeatLoss = 0;
        for (int y = 0; y < height; y++) {
            String line = lines.get(y);
            for (int x = 0; x < width; x++) {
                int heat = line.charAt(x) - '0';
                heatLoss[cellIndex(x, y, height)] = heat;
                maxHeatLoss = Math.max(maxHeatLoss, heat);
            }
        }
        return new Grid(heatLoss, width, height, maxHeatLoss);
    }

    private int solve(Grid grid, int min, int max) {
        int stateCount = grid.width * grid.height * DIRECTION_COUNT;
        int[] best = new int[stateCount];
        Arrays.fill(best, INF);

        int startState = stateIndex(0, 0, 0, grid.height);
        best[startState] = 0;

        BucketQueue queue = new BucketQueue(grid.maxHeatLoss * max);
        queue.add(0, startState);

        while (!queue.isEmpty()) {
            long entry = queue.poll();
            int path = entryCost(entry);
            int state = entryState(entry);
            if (path != best[state]) {
                continue;
            }

            int direction = state % DIRECTION_COUNT;
            int cell = state / DIRECTION_COUNT;
            int x = cell / grid.height;
            int y = cell % grid.height;

            if (x == grid.width - 1 && y == grid.height - 1) {
                return path;
            }

            for (int turn : TURNS[direction]) {
                int nextPath = path;
                for (int length = 1; length <= max; length++) {
                    int nextX = x + length * DX[turn];
                    int nextY = y + length * DY[turn];
                    if (isOutOfBounds(nextX, nextY, grid)) {
                        break;
                    }

                    nextPath += grid.heatLoss[cellIndex(nextX, nextY, grid.height)];
                    if (length < min) {
                        continue;
                    }

                    int nextState = stateIndex(nextX, nextY, turn, grid.height);
                    if (nextPath < best[nextState]) {
                        best[nextState] = nextPath;
                        queue.add(nextPath, nextState);
                    }
                }
            }
        }
        return INF;
    }

    private int stateIndex(int x, int y, int direction, int height) {
        return cellIndex(x, y, height) * DIRECTION_COUNT + direction;
    }

    private static int cellIndex(int x, int y, int height) {
        return x * height + y;
    }

    private static boolean isOutOfBounds(int x, int y, Grid grid) {
        return x < 0 || y < 0 || x >= grid.width || y >= grid.height;
    }

    // Queue entries sort by heat loss first; the low 32 bits hold the flat state index.
    private static long queueEntry(int cost, int state) {
        return ((long) cost << Integer.SIZE) | (state & 0xffffffffL);
    }

    private static int entryCost(long entry) {
        return (int) (entry >>> Integer.SIZE);
    }

    private static int entryState(long entry) {
        return (int) entry;
    }

    private record Grid(int[] heatLoss, int width, int height, int maxHeatLoss) {
    }

    private static class BucketQueue {
        private final int[][] buckets;
        private final int[] bucketSizes;
        private int currentCost;
        private int size;

        BucketQueue(int maxEdgeCost) {
            buckets = new int[maxEdgeCost + 1][];
            bucketSizes = new int[buckets.length];
        }

        boolean isEmpty() {
            return size == 0;
        }

        void add(int cost, int state) {
            int bucket = cost % buckets.length;
            int bucketSize = bucketSizes[bucket];
            int[] values = buckets[bucket];
            if (values == null) {
                values = new int[16];
                buckets[bucket] = values;
            } else if (bucketSize == values.length) {
                values = Arrays.copyOf(values, values.length * 2);
                buckets[bucket] = values;
            }
            values[bucketSize] = state;
            bucketSizes[bucket] = bucketSize + 1;
            size++;
        }

        long poll() {
            int bucket = currentCost % buckets.length;
            while (bucketSizes[bucket] == 0) {
                currentCost++;
                bucket = currentCost % buckets.length;
            }
            int state = buckets[bucket][--bucketSizes[bucket]];
            size--;
            return queueEntry(currentCost, state);
        }
    }
}
