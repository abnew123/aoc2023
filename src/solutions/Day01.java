package src.solutions;

import src.meta.DayTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day01 implements DayTemplate {

    /**
     * Main solving method.
     *
     * @param part1 The solver will solve part 1 if param is set to true.
     *              The solver will solve part 2 if param is set to false.
     * @param in    The solver will read data from this Scanner.
     * @return Returns answer in string format.
     */
    public String solve(boolean part1, Scanner in) {
        int answer = 0;
        List<String> lines = new ArrayList<>();
        while (in.hasNext()) {
            lines.add(in.nextLine());
        }
        for (String line : lines) {
            answer += 10 * helper(line, true, part1);
            answer += helper(line, false, part1);
        }
        return answer + "";
    }

    private int helper(String line, boolean forwards, boolean part1) {
        String[] digits = new String[]{"one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};
        char[] chars = line.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int index = forwards ? i : (chars.length - i - 1);
            if (!part1) {
                for (int j = 0; j < digits.length; j++) {
                    if (index + digits[j].length() > line.length()) {
                        continue;
                    }
                    String substring = line.substring(index, index + digits[j].length());
                    if (substring.contains(digits[j])) {
                        return j + 1;
                    }
                }
            }
            char ch = chars[index];
            if (Character.isDigit(ch)) {
                return Character.digit(ch, 10);
            }
        }
        return -1;
    }
}
