package src.solutions;

import src.meta.DayTemplate;

import java.util.Scanner;

public class Day01 implements DayTemplate {

    @Override
    public String[] fullSolve(Scanner in){
        long answer1 = 0;
        long answer2 = 0;
        while (in.hasNext()) {
            long values = scanBoth(in.nextLine());
            answer1 += (int) (values >> 32);
            answer2 += (int) values;
        }
        return new String[]{answer1 + "", answer2+ ""};
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
        long answer = 0;
        while (in.hasNext()) {
            answer += scanLine(in.nextLine(), !part1);
        }
        return answer + "";
    }

    private long scanBoth(String line) {
        int firstNumeric = -1;
        int lastNumeric = -1;
        int firstToken = -1;
        int lastToken = -1;
        for (int index = 0; index < line.length(); index++) {
            char current = line.charAt(index);
            int numeric = Character.isDigit(current) ? Character.digit(current, 10) : -1;
            if (numeric >= 0) {
                if (firstNumeric < 0) {
                    firstNumeric = numeric;
                }
                lastNumeric = numeric;
            }
            int token = numeric >= 0 ? numeric : wordAt(line, index);
            if (token >= 0) {
                if (firstToken < 0) {
                    firstToken = token;
                }
                lastToken = token;
            }
        }
        int part1 = 10 * firstNumeric + lastNumeric;
        int part2 = 10 * firstToken + lastToken;
        return ((long) part1 << 32) | (part2 & 0xffffffffL);
    }

    private int scanLine(String line, boolean includeWords) {
        int first = -1;
        int last = -1;
        for (int index = 0; index < line.length(); index++) {
            char current = line.charAt(index);
            int value = Character.isDigit(current) ? Character.digit(current, 10) : -1;
            if (value < 0 && includeWords) {
                value = wordAt(line, index);
            }
            if (value >= 0) {
                if (first < 0) {
                    first = value;
                }
                last = value;
            }
        }
        return 10 * first + last;
    }

    private int wordAt(String line, int index) {
        return switch (line.charAt(index)) {
            case 'o' -> line.startsWith("one", index) ? 1 : -1;
            case 't' -> line.startsWith("two", index) ? 2
                    : line.startsWith("three", index) ? 3 : -1;
            case 'f' -> line.startsWith("four", index) ? 4
                    : line.startsWith("five", index) ? 5 : -1;
            case 's' -> line.startsWith("six", index) ? 6
                    : line.startsWith("seven", index) ? 7 : -1;
            case 'e' -> line.startsWith("eight", index) ? 8 : -1;
            case 'n' -> line.startsWith("nine", index) ? 9 : -1;
            default -> -1;
        };
    }
}
