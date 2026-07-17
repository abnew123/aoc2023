package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day08 implements DayTemplate {

    @Override
    public String[] fullSolve(Scanner in) {
        Network network = parse(in);
        return new String[]{
                stepsToEnds(network, new int[]{network.aaa}, true) + "",
                stepsToEnds(network, network.starts, false) + ""
        };
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
        Network network = parse(in);
        int[] starts = part1 ? new int[]{network.aaa} : network.starts;
        return stepsToEnds(network, starts, part1) + "";
    }

    private Network parse(Scanner in) {
        char[] instructions = in.nextLine().toCharArray();
        in.nextLine();
        List<String> lines = new ArrayList<>();
        Map<String, Integer> ids = new HashMap<>();
        while (in.hasNext()) {
            String line = in.nextLine();
            lines.add(line);
            id(line.substring(0, 3), ids);
        }
        int[] left = new int[ids.size()];
        int[] right = new int[ids.size()];
        int[] starts = new int[ids.size()];
        int startCount = 0;
        int aaa = -1;
        int zzz = -1;
        boolean[] zEnds = new boolean[ids.size()];
        for (String line : lines) {
            int source = ids.get(line.substring(0, 3));
            String leftName = line.substring(7, 10);
            String rightName = line.substring(12, 15);
            left[source] = id(leftName, ids);
            right[source] = id(rightName, ids);
            if (line.charAt(2) == 'A') {
                starts[startCount++] = source;
            }
            if (line.charAt(2) == 'Z') {
                zEnds[source] = true;
            }
            if (line.startsWith("AAA")) {
                aaa = source;
            }
            if (line.startsWith("ZZZ")) {
                zzz = source;
            }
        }
        return new Network(instructions, left, right, Arrays.copyOf(starts, startCount), aaa, zzz, zEnds);
    }

    private int id(String name, Map<String, Integer> ids) {
        Integer existing = ids.get(name);
        if (existing != null) {
            return existing;
        }
        int next = ids.size();
        ids.put(name, next);
        return next;
    }

    private long stepsToEnds(Network network, int[] starts, boolean part1) {
        int index = 0;
        int[] currSteps = Arrays.copyOf(starts, starts.length);
        int[] loops = new int[currSteps.length];
        while (!check(currSteps, index, loops, part1, network)) {
            boolean goLeft = network.instructions[index % network.instructions.length] == 'L';
            int[] next = goLeft ? network.left : network.right;
            for (int i = 0; i < currSteps.length; i++) {
                currSteps[i] = next[currSteps[i]];
            }
            index++;
        }
        return lcm(loops);
    }

    private boolean check(int[] currSteps, int index, int[] loops, boolean part1, Network network) {
        boolean ret = true;
        for (int i = 0; i < currSteps.length; i++) {
            if (loops[i] != 0) {
                continue;
            }
            if ((!part1 && network.zEnds[currSteps[i]]) || currSteps[i] == network.zzz) {
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

    private record Network(char[] instructions, int[] left, int[] right, int[] starts, int aaa, int zzz, boolean[] zEnds) {
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
