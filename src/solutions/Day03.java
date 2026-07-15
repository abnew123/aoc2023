package src.solutions;

import src.meta.DayTemplate;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day03 implements DayTemplate {

    @Override
    public String[] fullSolve(Scanner in) {
        Answers answers = analyze(in);
        return new String[]{answers.partNumbers(), answers.gearRatios()};
    }

    /**
     * Main solving method.
     *
     * @param part1 The solver will solve part 1 if param is set to true.
     *              The solver will solve part 2 if param is set to false.
     * @param in    The solver will read data from this Scanner.
     * @return Returns answer in string format.
     */
    public String solve(boolean part1, Scanner in) {
        Answers answers = analyze(in);
        return part1 ? answers.partNumbers() : answers.gearRatios();
    }

    private Answers analyze(Scanner in) {
        List<String> rows = new ArrayList<>();
        while (in.hasNextLine()) {
            rows.add(in.nextLine());
        }

        Gear[][] gears = new Gear[rows.size()][];
        for (int row = 0; row < rows.size(); row++) {
            gears[row] = new Gear[rows.get(row).length()];
        }

        ExactTotal partNumbers = new ExactTotal();
        for (int row = 0; row < rows.size(); row++) {
            String line = rows.get(row);
            int column = 0;
            while (column < line.length()) {
                if (!isDigit(line.charAt(column))) {
                    column++;
                    continue;
                }
                int start = column++;
                while (column < line.length() && isDigit(line.charAt(column))) {
                    column++;
                }
                int end = column - 1;
                NumberValue value = parseNumber(line, start, column);
                boolean adjacentSymbol = false;

                if (row > 0) {
                    adjacentSymbol |= inspectRange(rows, gears, row - 1,
                            start - 1, end + 1, value);
                }
                adjacentSymbol |= inspectCell(rows, gears, row, start - 1, value);
                adjacentSymbol |= inspectCell(rows, gears, row, end + 1, value);
                if (row + 1 < rows.size()) {
                    adjacentSymbol |= inspectRange(rows, gears, row + 1,
                            start - 1, end + 1, value);
                }

                if (adjacentSymbol) {
                    partNumbers.add(value);
                }
            }
        }

        ExactTotal gearRatios = new ExactTotal();
        for (Gear[] row : gears) {
            for (Gear gear : row) {
                if (gear != null && gear.count == 2) {
                    gearRatios.addProduct(gear.first, gear.second);
                }
            }
        }
        return new Answers(partNumbers.toString(), gearRatios.toString());
    }

    private boolean inspectRange(List<String> rows, Gear[][] gears, int row,
                                 int from, int through, NumberValue value) {
        String line = rows.get(row);
        int start = Math.max(0, from);
        int end = Math.min(line.length() - 1, through);
        boolean symbol = false;
        for (int column = start; column <= end; column++) {
            symbol |= inspectCell(rows, gears, row, column, value);
        }
        return symbol;
    }

    private boolean inspectCell(List<String> rows, Gear[][] gears, int row,
                                int column, NumberValue value) {
        String line = rows.get(row);
        if (column < 0 || column >= line.length()) {
            return false;
        }
        char cell = line.charAt(column);
        if (cell == '*') {
            Gear gear = gears[row][column];
            if (gear == null) {
                gear = gears[row][column] = new Gear();
            }
            gear.add(value);
        }
        return cell != '.' && !isDigit(cell);
    }

    private NumberValue parseNumber(String line, int start, int end) {
        try {
            return new NumberValue(Long.parseLong(line, start, end, 10), null);
        } catch (NumberFormatException e) {
            return new NumberValue(0, new BigInteger(line.substring(start, end)));
        }
    }

    private boolean isDigit(char value) {
        return value >= '0' && value <= '9';
    }

    private record Answers(String partNumbers, String gearRatios) {}

    private record NumberValue(long small, BigInteger big) {}

    private static final class Gear {
        private int count;
        private NumberValue first;
        private NumberValue second;

        private void add(NumberValue value) {
            if (count == 0) {
                first = value;
            } else if (count == 1) {
                second = value;
            } else if (count == 2) {
                first = null;
                second = null;
            }
            count++;
        }
    }

    private static final class ExactTotal {
        private long small;
        private BigInteger big;

        private void add(NumberValue value) {
            if (value.big() == null) {
                add(value.small());
            } else {
                add(value.big());
            }
        }

        private void addProduct(NumberValue left, NumberValue right) {
            if (left.big() == null && right.big() == null) {
                try {
                    add(Math.multiplyExact(left.small(), right.small()));
                    return;
                } catch (ArithmeticException ignored) {
                    // Promote only the uncommon overflowing product.
                }
            }
            BigInteger leftValue = left.big() == null
                    ? BigInteger.valueOf(left.small()) : left.big();
            BigInteger rightValue = right.big() == null
                    ? BigInteger.valueOf(right.small()) : right.big();
            add(leftValue.multiply(rightValue));
        }

        private void add(long value) {
            if (big != null) {
                big = big.add(BigInteger.valueOf(value));
                return;
            }
            try {
                small = Math.addExact(small, value);
            } catch (ArithmeticException e) {
                big = BigInteger.valueOf(small).add(BigInteger.valueOf(value));
            }
        }

        private void add(BigInteger value) {
            if (big == null) {
                big = BigInteger.valueOf(small);
            }
            big = big.add(value);
        }

        @Override
        public String toString() {
            return big == null ? Long.toString(small) : big.toString();
        }
    }
}
