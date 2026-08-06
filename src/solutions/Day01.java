package src.solutions;

import src.meta.DayTemplate;

import java.util.Scanner;

public class Day01 implements DayTemplate {

    @Override
    public String[] fullSolve(Scanner in) {
        long[] sums = scan(slurp(in));
        return new String[]{sums[0] + "", sums[1] + ""};
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
        long[] sums = scan(slurp(in));
        return (part1 ? sums[0] : sums[1]) + "";
    }

    /** Reads the entire remaining input in one shot; empty input yields "". */
    private static String slurp(Scanner in) {
        in.useDelimiter("\\A");
        return in.hasNext() ? in.next() : "";
    }

    /**
     * Single forward scan over the whole input, computing both parts at once.
     * Literal digits feed the part-1 and part-2 first/last registers; spelled
     * digits (one..nine, matched by direct char lookahead so overlaps such as
     * "twone" fall out of scanning every position) feed part 2 only. A line
     * with no match contributes 10 * (-1) + (-1) = -11 to the affected part,
     * matching the previous per-line implementation. Trailing whitespace-only
     * lines are ignored, mirroring Scanner#hasNext-gated line reading; CR, LF
     * and CRLF terminators are all accepted.
     */
    private static long[] scan(String input) {
        char[] chars = input.toCharArray();
        int n = chars.length;
        int limit = n;
        while (limit > 0 && Character.isWhitespace(chars[limit - 1])) {
            limit--;
        }
        long sum1 = 0;
        long sum2 = 0;
        int first1 = -1;
        int last1 = -1;
        int first2 = -1;
        int last2 = -1;
        for (int i = 0; i < limit; i++) {
            char c = chars[i];
            if (c == '\n' || c == '\r') {
                sum1 += 10 * first1 + last1;
                sum2 += 10 * first2 + last2;
                first1 = last1 = first2 = last2 = -1;
                if (c == '\r' && i + 1 < limit && chars[i + 1] == '\n') {
                    i++;
                }
                continue;
            }
            if (c >= '0' && c <= '9') {
                int digit = c - '0';
                if (first1 < 0) {
                    first1 = digit;
                }
                last1 = digit;
                if (first2 < 0) {
                    first2 = digit;
                }
                last2 = digit;
                continue;
            }
            // Word lookahead may safely read past `limit` (only whitespace
            // lives there) but never crosses a line: '\n'/'\r' match no letter.
            int word = switch (c) {
                case 'o' -> i + 2 < n && chars[i + 1] == 'n' && chars[i + 2] == 'e' ? 1 : -1;
                case 't' -> i + 2 < n && chars[i + 1] == 'w' && chars[i + 2] == 'o' ? 2
                        : i + 4 < n && chars[i + 1] == 'h' && chars[i + 2] == 'r'
                                && chars[i + 3] == 'e' && chars[i + 4] == 'e' ? 3 : -1;
                case 'f' -> i + 3 < n && chars[i + 1] == 'o' && chars[i + 2] == 'u' && chars[i + 3] == 'r' ? 4
                        : i + 3 < n && chars[i + 1] == 'i' && chars[i + 2] == 'v' && chars[i + 3] == 'e' ? 5 : -1;
                case 's' -> i + 2 < n && chars[i + 1] == 'i' && chars[i + 2] == 'x' ? 6
                        : i + 4 < n && chars[i + 1] == 'e' && chars[i + 2] == 'v'
                                && chars[i + 3] == 'e' && chars[i + 4] == 'n' ? 7 : -1;
                case 'e' -> i + 4 < n && chars[i + 1] == 'i' && chars[i + 2] == 'g'
                        && chars[i + 3] == 'h' && chars[i + 4] == 't' ? 8 : -1;
                case 'n' -> i + 3 < n && chars[i + 1] == 'i' && chars[i + 2] == 'n' && chars[i + 3] == 'e' ? 9 : -1;
                default -> -1;
            };
            if (word >= 0) {
                if (first2 < 0) {
                    first2 = word;
                }
                last2 = word;
            }
        }
        if (limit > 0) {
            sum1 += 10 * first1 + last1;
            sum2 += 10 * first2 + last2;
        }
        return new long[]{sum1, sum2};
    }
}
