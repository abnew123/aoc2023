package src.solutions;

import src.meta.DayTemplate;
import java.util.*;

public class Day17 implements DayTemplate {

    private static final int AXIS_COUNT = 2;
    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;
    private static final int INF = 1_000_000_000;

    @Override
    public String[] fullSolve(Scanner in) {
        Grid grid = parse(in);
        int[] lowerBound = goalLowerBounds(grid);
        int answer1 = solve(grid, lowerBound, 1, 3);
        return new String[]{answer1 + "", solve(grid, lowerBound, 4, 10) + ""};
    }

    public String solve(boolean part1, Scanner in) {
        Grid grid = parse(in);
        return solve(grid, goalLowerBounds(grid), part1 ? 1 : 4, part1 ? 3 : 10) + "";
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

    /**
     * Exact cost of an unconstrained (run-length-free) walk from each cell to the goal,
     * via backward Dijkstra on a Dial bucket queue. Entering a cell costs its heat loss,
     * so relaxing a popped cell into a neighbour uses the popped cell's heat as weight.
     * Every crucible path is also an unconstrained walk, so this is an admissible and
     * consistent A* heuristic, and it is independent of the run-length limits, letting
     * both parts share a single pass.
     */
    private int[] goalLowerBounds(Grid grid) {
        int[] lowerBound = new int[grid.width * grid.height];
        Arrays.fill(lowerBound, INF);
        int goal = cellIndex(grid.width - 1, grid.height - 1, grid.height);
        lowerBound[goal] = 0;
        BucketQueue queue = new BucketQueue(grid.maxHeatLoss, 0);
        queue.add(0, goal);
        while (!queue.isEmpty()) {
            long entry = queue.poll();
            int distance = entryCost(entry);
            int cell = entryState(entry);
            if (distance != lowerBound[cell]) {
                continue;
            }
            int x = cell / grid.height;
            int y = cell % grid.height;
            int candidate = distance + grid.heatLoss[cell];
            if (x > 0) {
                relax(lowerBound, queue, cell - grid.height, candidate);
            }
            if (x < grid.width - 1) {
                relax(lowerBound, queue, cell + grid.height, candidate);
            }
            if (y > 0) {
                relax(lowerBound, queue, cell - 1, candidate);
            }
            if (y < grid.height - 1) {
                relax(lowerBound, queue, cell + 1, candidate);
            }
        }
        return lowerBound;
    }

    private static void relax(int[] lowerBound, BucketQueue queue, int cell, int candidate) {
        if (candidate < lowerBound[cell]) {
            lowerBound[cell] = candidate;
            queue.add(candidate, cell);
        }
    }

    /**
     * A* over axis-collapsed states (cell x horizontal/vertical), expanding every legal
     * run length in one prefix-summed sweep. Queue keys are path + lowerBound; with a
     * consistent heuristic the polled keys never decrease and consecutive keys differ by
     * at most run weight + reverse run weight, i.e. 2 * maxHeatLoss * max, so a Dial
     * bucket ring of that span replaces a binary heap.
     */
    private int solve(Grid grid, int[] lowerBound, int min, int max) {
        int stateCount = grid.width * grid.height * AXIS_COUNT;
        int[] best = new int[stateCount];
        Arrays.fill(best, INF);

        int startCell = cellIndex(0, 0, grid.height);
        int startKey = lowerBound[startCell];
        BucketQueue queue = new BucketQueue(2 * grid.maxHeatLoss * max, startKey);
        int horizontalStart = stateIndex(0, 0, HORIZONTAL, grid.height);
        int verticalStart = stateIndex(0, 0, VERTICAL, grid.height);
        best[horizontalStart] = 0;
        best[verticalStart] = 0;
        queue.add(startKey, horizontalStart);
        queue.add(startKey, verticalStart);

        while (!queue.isEmpty()) {
            long entry = queue.poll();
            int key = entryCost(entry);
            int state = entryState(entry);
            int axis = state % AXIS_COUNT;
            int cell = state / AXIS_COUNT;
            int path = key - lowerBound[cell];
            if (path != best[state]) {
                continue;
            }

            int x = cell / grid.height;
            int y = cell % grid.height;

            if (x == grid.width - 1 && y == grid.height - 1) {
                return path;
            }

            for (int step = -1; step <= 1; step += 2) {
                int dx = axis == HORIZONTAL ? step : 0;
                int dy = axis == VERTICAL ? step : 0;
                int nextPath = path;
                for (int length = 1; length <= max; length++) {
                    int nextX = x + length * dx;
                    int nextY = y + length * dy;
                    if (isOutOfBounds(nextX, nextY, grid)) {
                        break;
                    }

                    int nextCell = cellIndex(nextX, nextY, grid.height);
                    nextPath += grid.heatLoss[nextCell];
                    if (length < min) {
                        continue;
                    }

                    int nextState = nextCell * AXIS_COUNT + (axis ^ 1);
                    if (nextPath < best[nextState]) {
                        best[nextState] = nextPath;
                        queue.add(nextPath + lowerBound[nextCell], nextState);
                    }
                }
            }
        }
        return INF;
    }

    private int stateIndex(int x, int y, int axis, int height) {
        return cellIndex(x, y, height) * AXIS_COUNT + axis;
    }

    private static int cellIndex(int x, int y, int height) {
        return x * height + y;
    }

    private static boolean isOutOfBounds(int x, int y, Grid grid) {
        return x < 0 || y < 0 || x >= grid.width || y >= grid.height;
    }

    // Queue entries sort by key first; the low 32 bits hold the flat state index.
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

        BucketQueue(int maxKeyStep, int initialKey) {
            buckets = new int[maxKeyStep + 1][];
            bucketSizes = new int[buckets.length];
            currentCost = initialKey;
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
