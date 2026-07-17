package src.solutions;

import src.meta.DayTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day15 implements DayTemplate {

    @Override
    public String[] fullSolve(Scanner in) {
        long[] answers = process(in.nextLine(), true, true);
        return new String[]{answers[0] + "", answers[1] + ""};
    }

    public String solve(boolean part1, Scanner in) {
        long[] answers = process(in.nextLine(), part1, !part1);
        return (part1 ? answers[0] : answers[1]) + "";
    }

    private long[] process(String sequence, boolean needPart1, boolean needPart2) {
        @SuppressWarnings("unchecked")
        List<Lens>[] boxes = needPart2 ? (List<Lens>[]) new List<?>[256] : null;
        long part1 = 0;
        int index = 0;

        while (index < sequence.length()) {
            int start = index;
            int stepHash = 0;
            int labelHash = 0;
            int operatorIndex = -1;
            char operator = 0;

            while (index < sequence.length() && sequence.charAt(index) != ',') {
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
            if (index < sequence.length()) {
                index++;
            }

            if (needPart1) {
                part1 += stepHash;
            }
            if (needPart2) {
                apply(sequence, start, operatorIndex, end, operator, labelHash, boxes);
            }
        }

        return new long[]{part1, needPart2 ? focusingPower(boxes) : 0};
    }

    private void apply(String sequence, int start, int operatorIndex, int end,
                       char operator, int boxIndex, List<Lens>[] boxes) {
        if (operatorIndex <= start) {
            throw new IllegalArgumentException("Invalid initialization step: "
                    + sequence.substring(start, end));
        }

        List<Lens> box = boxes[boxIndex];
        if (operator == '-') {
            if (operatorIndex + 1 != end) {
                throw new IllegalArgumentException("Invalid removal step: "
                        + sequence.substring(start, end));
            }
            if (box != null) {
                for (int slot = 0; slot < box.size(); slot++) {
                    if (matches(sequence, start, operatorIndex, box.get(slot).name)) {
                        box.remove(slot);
                        break;
                    }
                }
            }
            return;
        }

        if (operator != '=' || operatorIndex + 1 == end) {
            throw new IllegalArgumentException("Invalid assignment step: "
                    + sequence.substring(start, end));
        }
        int focalLength = 0;
        for (int i = operatorIndex + 1; i < end; i++) {
            char digit = sequence.charAt(i);
            if (digit < '0' || digit > '9') {
                throw new IllegalArgumentException("Invalid focal length: "
                        + sequence.substring(start, end));
            }
            focalLength = Math.addExact(Math.multiplyExact(focalLength, 10), digit - '0');
        }

        if (box == null) {
            box = new ArrayList<>();
            boxes[boxIndex] = box;
        }
        for (Lens lens : box) {
            if (matches(sequence, start, operatorIndex, lens.name)) {
                lens.length = focalLength;
                return;
            }
        }
        box.add(new Lens(focalLength, sequence.substring(start, operatorIndex)));
    }

    private boolean matches(String sequence, int start, int end, String label) {
        return label.length() == end - start
                && sequence.regionMatches(start, label, 0, label.length());
    }

    private long focusingPower(List<Lens>[] boxes) {
        long answer = 0;
        for (int box = 0; box < boxes.length; box++) {
            List<Lens> lenses = boxes[box];
            if (lenses == null) {
                continue;
            }
            for (int slot = 0; slot < lenses.size(); slot++) {
                answer += (long) (box + 1) * (slot + 1) * lenses.get(slot).length;
            }
        }
        return answer;
    }
}

class Lens {
    int length;
    String name;

    public Lens(int l, String n) {
        length = l;
        name = n;
    }
}
