package src.solutions;

import src.meta.DayTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Segment-count solver. The board is materialized only once, during parsing.
 * Afterwards the state is the number of round rocks per segment (maximal run
 * of non-cube cells) along the axis of the most recent tilt; piled rocks
 * occupy a segment prefix, so the count vector determines the configuration
 * exactly. A tilt scatters one increment per rock into the orthogonal segment
 * structure through tables precomputed per direction, costing
 * O(rocks + segments) instead of O(cells). Cycle detection hashes the east
 * count vector, and loads are computed in closed form from count vectors.
 */
public class Day14 implements DayTemplate {
    private static final byte EMPTY = 0;
    private static final byte BLOCK = 1;
    private static final byte ROUND = 2;
    private static final long SPIN_CYCLES = 1_000_000_000L;

    @Override
    public String[] fullSolve(Scanner in) {
        Dish dish = parse(in);
        return new String[] {
                Long.toString(dish.northTiltLoad()),
                Long.toString(dish.loadAfterSpins(SPIN_CYCLES))
        };
    }

    @Override
    public String solve(boolean part1, Scanner in) {
        Dish dish = parse(in);
        return Long.toString(part1 ? dish.northTiltLoad() : dish.loadAfterSpins(SPIN_CYCLES));
    }

    public long loadAfterCycles(Scanner in, long cycles) {
        if (cycles < 0) {
            throw new IllegalArgumentException("Cycle count must be nonnegative");
        }
        return parse(in).loadAfterSpins(cycles);
    }

    private Dish parse(Scanner in) {
        in.useDelimiter("\\A");
        String input = in.hasNext() ? in.next() : "";
        byte[] cells = new byte[Math.min(Math.max(input.length(), 16), 4096)];
        int rows = 0;
        int columns = -1;
        int offset = 0;
        while (offset < input.length()) {
            int start = offset;
            while (offset < input.length() && input.charAt(offset) != '\n'
                    && input.charAt(offset) != '\r') {
                offset++;
            }
            int end = offset;
            if (offset < input.length()) {
                char ending = input.charAt(offset++);
                if (ending == '\r' && offset < input.length() && input.charAt(offset) == '\n') {
                    offset++;
                }
            }
            int width = end - start;
            if (width == 0) {
                continue;
            }
            if (columns < 0) {
                columns = width;
            } else if (columns != width) {
                throw new IllegalArgumentException("Grid rows must have equal widths");
            }
            long required = (long) (rows + 1) * columns;
            if (required > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Grid is too large");
            }
            if (required > cells.length) {
                cells = Arrays.copyOf(cells, Math.max((int) required, cells.length * 2));
            }
            for (int column = 0; column < columns; column++) {
                cells[rows * columns + column] = switch (input.charAt(start + column)) {
                    case '.' -> EMPTY;
                    case '#' -> BLOCK;
                    case 'O' -> ROUND;
                    default -> throw new IllegalArgumentException("Unexpected grid character");
                };
            }
            rows++;
        }
        if (rows == 0) {
            return new Dish(new byte[0], 0, 0);
        }
        return new Dish(cells, rows, columns);
    }

    private static final class Dish {
        private final int rows;
        private final long rawLoad;
        private final int colSegCount;
        private final int rowSegCount;
        private final int[] offCol;
        private final int[] offRow;
        private final int[] northTargets;
        private final int[] westTargets;
        private final int[] southTargets;
        private final int[] eastTargets;
        private final int[] colSegStartRow;
        private final int[] rowSegWeight;
        private final int[] initialColCounts;

        Dish(byte[] cells, int rows, int columns) {
            this.rows = rows;
            int cellCount = rows * columns;
            int blocks = 0;
            for (int i = 0; i < cellCount; i++) {
                if (cells[i] == BLOCK) {
                    blocks++;
                }
            }

            int[] rowSegOf = new int[cellCount];
            int rowSegLimit = blocks + rows + 1;
            int[] rowSegRow = new int[rowSegLimit];
            int[] rowSegStartCol = new int[rowSegLimit];
            int[] rowSegLen = new int[rowSegLimit];
            int rowSegs = 0;
            long rawLoadAcc = 0;
            for (int r = 0; r < rows; r++) {
                int current = -1;
                int base = r * columns;
                for (int c = 0; c < columns; c++) {
                    byte cell = cells[base + c];
                    if (cell == BLOCK) {
                        current = -1;
                        continue;
                    }
                    if (current < 0) {
                        current = rowSegs++;
                        rowSegRow[current] = r;
                        rowSegStartCol[current] = c;
                    }
                    rowSegLen[current]++;
                    rowSegOf[base + c] = current;
                    if (cell == ROUND) {
                        rawLoadAcc += rows - r;
                    }
                }
            }
            this.rawLoad = rawLoadAcc;

            int[] colSegOf = new int[cellCount];
            int colSegLimit = blocks + columns + 1;
            int[] colSegStart = new int[colSegLimit];
            int[] colSegColumn = new int[colSegLimit];
            int[] colSegLen = new int[colSegLimit];
            int[] initCounts = new int[colSegLimit];
            int colSegs = 0;
            for (int c = 0; c < columns; c++) {
                int current = -1;
                for (int r = 0; r < rows; r++) {
                    byte cell = cells[r * columns + c];
                    if (cell == BLOCK) {
                        current = -1;
                        continue;
                    }
                    if (current < 0) {
                        current = colSegs++;
                        colSegStart[current] = r;
                        colSegColumn[current] = c;
                    }
                    colSegLen[current]++;
                    colSegOf[r * columns + c] = current;
                    if (cell == ROUND) {
                        initCounts[current]++;
                    }
                }
            }

            this.rowSegCount = rowSegs;
            this.colSegCount = colSegs;
            this.offRow = prefixSums(rowSegLen, rowSegs);
            this.offCol = prefixSums(colSegLen, colSegs);
            int freeCells = offRow[rowSegs];

            this.northTargets = new int[freeCells];
            this.southTargets = new int[freeCells];
            for (int s = 0; s < colSegs; s++) {
                int column = colSegColumn[s];
                int startRow = colSegStart[s];
                int length = colSegLen[s];
                int base = offCol[s];
                int lastRow = startRow + length - 1;
                for (int k = 0; k < length; k++) {
                    northTargets[base + k] = rowSegOf[(startRow + k) * columns + column];
                    southTargets[base + k] = rowSegOf[(lastRow - k) * columns + column];
                }
            }
            this.westTargets = new int[freeCells];
            this.eastTargets = new int[freeCells];
            for (int s = 0; s < rowSegs; s++) {
                int rowBase = rowSegRow[s] * columns;
                int startCol = rowSegStartCol[s];
                int length = rowSegLen[s];
                int base = offRow[s];
                int lastCol = startCol + length - 1;
                for (int k = 0; k < length; k++) {
                    westTargets[base + k] = colSegOf[rowBase + startCol + k];
                    eastTargets[base + k] = colSegOf[rowBase + lastCol - k];
                }
            }

            this.colSegStartRow = Arrays.copyOf(colSegStart, colSegs);
            this.initialColCounts = Arrays.copyOf(initCounts, colSegs);
            int[] weights = new int[rowSegs];
            for (int s = 0; s < rowSegs; s++) {
                weights[s] = rows - rowSegRow[s];
            }
            this.rowSegWeight = weights;
        }

        private static int[] prefixSums(int[] lengths, int count) {
            int[] offsets = new int[count + 1];
            int total = 0;
            for (int s = 0; s < count; s++) {
                offsets[s] = total;
                total += lengths[s];
            }
            offsets[count] = total;
            return offsets;
        }

        long northTiltLoad() {
            long load = 0;
            for (int s = 0; s < colSegCount; s++) {
                long count = initialColCounts[s];
                if (count > 0) {
                    load += count * (rows - colSegStartRow[s]) - count * (count - 1) / 2;
                }
            }
            return load;
        }

        long loadAfterSpins(long totalSpins) {
            if (totalSpins == 0) {
                return rawLoad;
            }
            int[] colCounts = initialColCounts.clone();
            int[] rowCounts = new int[rowSegCount];
            Map<CountState, Long> seen = new HashMap<>(512);
            long[] loads = new long[256];
            long spins = 0;
            while (true) {
                if (spins > 0) {
                    scatter(eastTargets, offRow, rowCounts, colCounts);
                }
                scatter(northTargets, offCol, colCounts, rowCounts);
                scatter(westTargets, offRow, rowCounts, colCounts);
                scatter(southTargets, offCol, colCounts, rowCounts);
                spins++;
                long load = loadFromEastCounts(rowCounts);
                if (spins > loads.length) {
                    loads = Arrays.copyOf(loads, loads.length * 2);
                }
                loads[(int) (spins - 1)] = load;
                if (spins == totalSpins) {
                    return load;
                }
                Long previous = seen.putIfAbsent(new CountState(rowCounts.clone()), spins);
                if (previous != null) {
                    long period = spins - previous;
                    long index = previous + (totalSpins - previous) % period;
                    return loads[(int) (index - 1)];
                }
            }
        }

        private static void scatter(int[] targets, int[] offsets, int[] source, int[] destination) {
            Arrays.fill(destination, 0);
            for (int s = 0; s < source.length; s++) {
                int base = offsets[s];
                int end = base + source[s];
                for (int k = base; k < end; k++) {
                    destination[targets[k]]++;
                }
            }
        }

        private long loadFromEastCounts(int[] rowCounts) {
            long load = 0;
            for (int s = 0; s < rowSegCount; s++) {
                load += (long) rowCounts[s] * rowSegWeight[s];
            }
            return load;
        }
    }

    private static final class CountState {
        private final int[] counts;
        private final int hash;

        private CountState(int[] counts) {
            this.counts = counts;
            hash = Arrays.hashCode(counts);
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof CountState state && Arrays.equals(counts, state.counts);
        }
    }
}
