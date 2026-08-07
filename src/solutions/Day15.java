package src.solutions;

import src.meta.DayTemplate;

import java.util.Arrays;
import java.util.Scanner;

public class Day15 implements DayTemplate {

    @Override
    public String[] fullSolve(Scanner in) {
        long[] answers = process(readAll(in), true, true);
        return new String[]{answers[0] + "", answers[1] + ""};
    }

    public String solve(boolean part1, Scanner in) {
        long[] answers = process(readAll(in), part1, !part1);
        return (part1 ? answers[0] : answers[1]) + "";
    }

    private String readAll(Scanner in) {
        in.useDelimiter("\\A");
        return in.hasNext() ? in.next() : "";
    }

    private long[] process(String sequence, boolean needPart1, boolean needPart2) {
        int limit = 0;
        while (limit < sequence.length()) {
            char c = sequence.charAt(limit);
            if (c == '\n' || c == '\r') {
                break;
            }
            limit++;
        }

        int[][] boxes = needPart2 ? new int[256][] : null;
        int[] boxCounts = needPart2 ? new int[256] : null;
        long part1 = 0;
        int index = 0;

        while (index < limit) {
            int start = index;
            int stepHash = 0;
            int labelHash = 0;
            int operatorIndex = -1;
            char operator = 0;

            while (index < limit && sequence.charAt(index) != ',') {
                char current = sequence.charAt(index);
                if (needPart1) {
                    stepHash = ((stepHash + current) * 17) & 255;
                }
                if (needPart2 && operatorIndex < 0) {
                    if (current == '-' || current == '=') {
                        operator = current;
                        operatorIndex = index;
                    } else {
                        labelHash = ((labelHash + current) * 17) & 255;
                    }
                }
                index++;
            }
            int end = index;
            if (index < limit) {
                index++;
            }

            if (needPart1) {
                part1 += stepHash;
            }
            if (needPart2) {
                apply(sequence, start, operatorIndex, end, operator, labelHash, boxes, boxCounts);
            }
        }

        return new long[]{part1, needPart2 ? focusingPower(boxes, boxCounts) : 0};
    }

    /**
     * Applies one initialization step. Each box stores its lenses as
     * (labelStart, labelLength, focalLength) triples whose label text lives in
     * the shared immutable sequence string.
     */
    private void apply(String sequence, int start, int operatorIndex, int end,
                       char operator, int boxIndex, int[][] boxes, int[] boxCounts) {
        if (operatorIndex <= start) {
            throw new IllegalArgumentException("Invalid initialization step");
        }

        int labelLength = operatorIndex - start;
        int[] box = boxes[boxIndex];
        int count = boxCounts[boxIndex];
        if (operator == '-') {
            if (operatorIndex + 1 != end) {
                throw new IllegalArgumentException("Invalid removal step");
            }
            for (int slot = 0; slot < count; slot++) {
                if (matches(sequence, start, labelLength, box[3 * slot], box[3 * slot + 1])) {
                    System.arraycopy(box, 3 * slot + 3, box, 3 * slot, 3 * (count - slot - 1));
                    boxCounts[boxIndex] = count - 1;
                    break;
                }
            }
            return;
        }

        if (operator != '=' || operatorIndex + 1 == end) {
            throw new IllegalArgumentException("Invalid assignment step");
        }
        int focalLength = 0;
        for (int i = operatorIndex + 1; i < end; i++) {
            char digit = sequence.charAt(i);
            if (digit < '0' || digit > '9') {
                throw new IllegalArgumentException("Invalid focal length");
            }
            focalLength = Math.addExact(Math.multiplyExact(focalLength, 10), digit - '0');
        }

        for (int slot = 0; slot < count; slot++) {
            if (matches(sequence, start, labelLength, box[3 * slot], box[3 * slot + 1])) {
                box[3 * slot + 2] = focalLength;
                return;
            }
        }
        if (box == null) {
            box = new int[12];
            boxes[boxIndex] = box;
        } else if (3 * count == box.length) {
            box = Arrays.copyOf(box, box.length * 2);
            boxes[boxIndex] = box;
        }
        box[3 * count] = start;
        box[3 * count + 1] = labelLength;
        box[3 * count + 2] = focalLength;
        boxCounts[boxIndex] = count + 1;
    }

    private boolean matches(String sequence, int start, int length,
                            int otherStart, int otherLength) {
        if (length != otherLength) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (sequence.charAt(start + i) != sequence.charAt(otherStart + i)) {
                return false;
            }
        }
        return true;
    }

    private long focusingPower(int[][] boxes, int[] boxCounts) {
        long answer = 0;
        for (int box = 0; box < boxes.length; box++) {
            int[] lenses = boxes[box];
            int count = boxCounts[box];
            for (int slot = 0; slot < count; slot++) {
                answer += (long) (box + 1) * (slot + 1) * lenses[3 * slot + 2];
            }
        }
        return answer;
    }
}
