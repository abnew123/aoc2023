package src.solutions;

import src.meta.DayTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Day14 implements DayTemplate {
    private static final byte EMPTY = 0;
    private static final byte BLOCK = 1;
    private static final byte ROUND = 2;
    private static final long SPIN_CYCLES = 1_000_000_000L;

    @Override
    public String[] fullSolve(Scanner in) {
        Grid grid = parse(in);
        byte[] north = grid.cells().clone();
        tiltNorth(north, grid.rows(), grid.columns());
        return new String[] {
                Long.toString(load(north, grid.rows(), grid.columns())),
                Long.toString(loadAfterCycles(grid.cells(), grid.rows(), grid.columns(), SPIN_CYCLES))
        };
    }

    @Override
    public String solve(boolean part1, Scanner in) {
        Grid grid = parse(in);
        if (part1) {
            tiltNorth(grid.cells(), grid.rows(), grid.columns());
            return Long.toString(load(grid.cells(), grid.rows(), grid.columns()));
        }
        return Long.toString(loadAfterCycles(grid.cells(), grid.rows(), grid.columns(), SPIN_CYCLES));
    }

    public long loadAfterCycles(Scanner in, long cycles) {
        if (cycles < 0) {
            throw new IllegalArgumentException("Cycle count must be nonnegative");
        }
        Grid grid = parse(in);
        return loadAfterCycles(grid.cells(), grid.rows(), grid.columns(), cycles);
    }

    private long loadAfterCycles(byte[] cells, int rows, int columns, long cycles) {
        Map<GridState, Long> seen = new HashMap<>();
        seen.put(new GridState(cells.clone()), 0L);
        long completed = 0;
        while (completed < cycles) {
            spin(cells, rows, columns);
            completed++;
            GridState state = new GridState(cells.clone());
            Long previous = seen.putIfAbsent(state, completed);
            if (previous != null) {
                long remaining = (cycles - completed) % (completed - previous);
                while (remaining-- > 0) {
                    spin(cells, rows, columns);
                }
                break;
            }
        }
        return load(cells, rows, columns);
    }

    private Grid parse(Scanner in) {
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
            return new Grid(new byte[0], 0, 0);
        }
        return new Grid(Arrays.copyOf(cells, rows * columns), rows, columns);
    }

    private void spin(byte[] cells, int rows, int columns) {
        tiltNorth(cells, rows, columns);
        tiltWest(cells, rows, columns);
        tiltSouth(cells, rows, columns);
        tiltEast(cells, rows, columns);
    }

    private void tiltNorth(byte[] cells, int rows, int columns) {
        for (int column = 0; column < columns; column++) {
            int target = 0;
            for (int row = 0; row < rows; row++) {
                int index = row * columns + column;
                if (cells[index] == BLOCK) {
                    target = row + 1;
                } else if (cells[index] == ROUND) {
                    int destination = target++ * columns + column;
                    if (destination != index) {
                        cells[index] = EMPTY;
                        cells[destination] = ROUND;
                    }
                }
            }
        }
    }

    private void tiltSouth(byte[] cells, int rows, int columns) {
        for (int column = 0; column < columns; column++) {
            int target = rows - 1;
            for (int row = rows - 1; row >= 0; row--) {
                int index = row * columns + column;
                if (cells[index] == BLOCK) {
                    target = row - 1;
                } else if (cells[index] == ROUND) {
                    int destination = target-- * columns + column;
                    if (destination != index) {
                        cells[index] = EMPTY;
                        cells[destination] = ROUND;
                    }
                }
            }
        }
    }

    private void tiltWest(byte[] cells, int rows, int columns) {
        for (int row = 0; row < rows; row++) {
            int target = 0;
            int rowOffset = row * columns;
            for (int column = 0; column < columns; column++) {
                int index = rowOffset + column;
                if (cells[index] == BLOCK) {
                    target = column + 1;
                } else if (cells[index] == ROUND) {
                    int destination = rowOffset + target++;
                    if (destination != index) {
                        cells[index] = EMPTY;
                        cells[destination] = ROUND;
                    }
                }
            }
        }
    }

    private void tiltEast(byte[] cells, int rows, int columns) {
        for (int row = 0; row < rows; row++) {
            int target = columns - 1;
            int rowOffset = row * columns;
            for (int column = columns - 1; column >= 0; column--) {
                int index = rowOffset + column;
                if (cells[index] == BLOCK) {
                    target = column - 1;
                } else if (cells[index] == ROUND) {
                    int destination = rowOffset + target--;
                    if (destination != index) {
                        cells[index] = EMPTY;
                        cells[destination] = ROUND;
                    }
                }
            }
        }
    }

    private long load(byte[] cells, int rows, int columns) {
        long result = 0;
        for (int row = 0; row < rows; row++) {
            long weight = rows - row;
            int end = (row + 1) * columns;
            for (int index = row * columns; index < end; index++) {
                if (cells[index] == ROUND) {
                    result += weight;
                }
            }
        }
        return result;
    }

    private record Grid(byte[] cells, int rows, int columns) {}

    private static final class GridState {
        private final byte[] cells;
        private final int hash;

        private GridState(byte[] cells) {
            this.cells = cells;
            hash = Arrays.hashCode(cells);
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof GridState state && Arrays.equals(cells, state.cells);
        }
    }
}
