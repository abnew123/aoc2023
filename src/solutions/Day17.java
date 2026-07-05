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
        for (int y = 0; y < height; y++) {
            String line = lines.get(y);
            for (int x = 0; x < width; x++) {
                heatLoss[cellIndex(x, y, height)] = line.charAt(x) - '0';
            }
        }
        return new Grid(heatLoss, width, height);
    }

    private int solve(Grid grid, int min, int max) {
        int stateCount = grid.width * grid.height * DIRECTION_COUNT;
        int[] best = new int[stateCount];
        Arrays.fill(best, INF);

        int startState = stateIndex(0, 0, 0, grid.height);
        best[startState] = 0;

        LongMinHeap queue = new LongMinHeap(stateCount);
        queue.add(queueEntry(0, startState));

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
                        queue.add(queueEntry(nextPath, nextState));
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

    private record Grid(int[] heatLoss, int width, int height) {
    }

    private static class LongMinHeap {
        private long[] values;
        private int size;

        LongMinHeap(int stateCount) {
            values = new long[Math.max(16, Math.min(stateCount, 1024))];
        }

        boolean isEmpty() {
            return size == 0;
        }

        void add(long value) {
            if (size == values.length) {
                values = Arrays.copyOf(values, values.length * 2);
            }
            int index = size++;
            while (index > 0) {
                int parent = (index - 1) >>> 1;
                long parentValue = values[parent];
                if (parentValue <= value) {
                    break;
                }
                values[index] = parentValue;
                index = parent;
            }
            values[index] = value;
        }

        long poll() {
            long result = values[0];
            long value = values[--size];
            if (size > 0) {
                int index = 0;
                int half = size >>> 1;
                while (index < half) {
                    int child = (index << 1) + 1;
                    long childValue = values[child];
                    int right = child + 1;
                    if (right < size && values[right] < childValue) {
                        child = right;
                        childValue = values[right];
                    }
                    if (value <= childValue) {
                        break;
                    }
                    values[index] = childValue;
                    index = child;
                }
                values[index] = value;
            }
            return result;
        }
    }
}
