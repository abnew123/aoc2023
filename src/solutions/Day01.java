package src.solutions;

import src.meta.DayTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day01 implements DayTemplate {


    
    public String solve(boolean part1, Scanner in) {
        int ans = 0;
        List<String> lines = new ArrayList<>();
        while (in.hasNext()) {
            lines.add(in.nextLine());
        }
        for (String line : lines) {
            ans += 10 * findFirstDigit(line, true, part1);
            ans += findFirstDigit(line, false, part1);
        }
        return ans + "";
    }

    int findFirstDigit(String line, boolean forwards, boolean part1) {
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
