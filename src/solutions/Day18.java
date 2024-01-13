package src.solutions;

import src.meta.DayTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Day18 extends DayTemplate {

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
        List<Instruction> instructions = new ArrayList<>();
        int max = 0;
        while (in.hasNext()) {
            String line = in.nextLine();
            instructions.add(new Instruction(line, part1));
            max += instructions.get(instructions.size() - 1).distance;
        }
        List<Long> xVals = new ArrayList<>();
        List<Long> yVals = new ArrayList<>();
        long x = 0;
        long y = 0;
        int[] xs = new int[]{1, 0, -1, 0};
        int[] ys = new int[]{0, -1, 0, 1};
        for (Instruction instruction : instructions) {
            x += instruction.distance * xs[instruction.direction];
            y += instruction.distance * ys[instruction.direction];
            xVals.add(x);
            yVals.add(y);
        }
        for (int i = 0; i < xVals.size(); i++) {
            answer -= xVals.get(i) * yVals.get((i + 1) % xVals.size());
            answer += xVals.get((i + 1) % xVals.size()) * yVals.get(i);
        }
        answer /= 2;
        answer += max / 2 + 1;
        return answer + "";
    }
}

class Instruction {
    int distance;
    int direction;

    public Instruction(String line, boolean part1) {
        Map<String, Integer> map = Map.of("R", 0, "D", 1, "L", 2, "U", 3);
        if (part1) {
            distance = Integer.parseInt(line.split(" ")[1]);
            direction = map.get(line.split(" ")[0]);
        } else {
            String hex = line.split("[#)]")[1];
            direction = Integer.parseInt(hex.substring(hex.length() - 1));
            distance = Integer.parseInt(hex.substring(0, hex.length() - 1), 16);
        }
    }
}