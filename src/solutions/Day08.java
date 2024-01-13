package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day08 implements DayTemplate {

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
        List<String> currSteps = new ArrayList<>();
        Map<String, Step> map = new HashMap<>();
        String[] instructions = in.nextLine().split("");
        in.nextLine();
        while (in.hasNext()) {
            Step step = new Step(in.nextLine());
            map.put(step.name, step);
            if (step.name.substring(2).equals("A") && (step.name.equals("AAA") || !part1)) {
                currSteps.add(step.name);
            }
        }
        int index = 0;
        int[] loops = new int[currSteps.size()];
        while (!check(currSteps, index, loops, part1)) {
            for (int i = 0; i < currSteps.size(); i++) {
                Step step = map.get(currSteps.get(i));
                if (instructions[index % instructions.length].equals("L")) {
                    currSteps.set(i, step.left);
                } else {
                    currSteps.set(i, step.right);
                }
            }
            index++;
        }
        answer = lcm(loops);
        return answer + "";
    }

    private boolean check(List<String> currSteps, int index, int[] loops, boolean part1) {
        boolean ret = true;
        for (int i = 0; i < currSteps.size(); i++) {
            if (loops[i] != 0) {
                continue;
            }
            if ((currSteps.get(i).substring(2).equals("Z") && !part1) || currSteps.get(i).equals("ZZZ")) {
                loops[i] = index;
            } else {
                ret = false;
            }
        }
        return ret;
    }

    private long lcm(int[] loops) {
        long lcm = 1;
        for (int loop : loops) {
            lcm = lcm * loop / gcd(lcm, loop);
        }
        return lcm;
    }

    private long gcd(long a, long b) {
        return (b == 0) ? a : gcd(b, a % b);
    }
}

class Step {
    String left;
    String right;
    String name;

    public Step(String line) {
        left = line.split("[\\(,]")[1].trim();
        right = line.split("[,\\)]")[1].trim();
        name = line.split("=")[0].trim();
    }
}