package src.solutions;

import src.meta.DayTemplate;

import java.util.Arrays;
import java.util.Scanner;

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
        in.useDelimiter("\\A");
        String input = in.hasNext() ? in.next() : "";
        int length = input.length();

        int lineStart = 0;
        int lineEnd = 0;
        while (lineEnd < length && input.charAt(lineEnd) != '\n') {
            lineEnd++;
        }
        int trimmedEnd = lineEnd;
        if (trimmedEnd > lineStart && input.charAt(trimmedEnd - 1) == '\r') {
            trimmedEnd--;
        }
        char[] instructions = new char[trimmedEnd - lineStart];
        for (int i = 0; i < instructions.length; i++) {
            instructions[i] = input.charAt(lineStart + i);
        }
        lineStart = lineEnd + 1;

        int[] lineStarts = new int[16];
        int lineCount = 0;
        NodeIds ids = new NodeIds();
        while (lineStart < length) {
            lineEnd = lineStart;
            while (lineEnd < length && input.charAt(lineEnd) != '\n') {
                lineEnd++;
            }
            trimmedEnd = lineEnd;
            if (trimmedEnd > lineStart && input.charAt(trimmedEnd - 1) == '\r') {
                trimmedEnd--;
            }
            if (!blank(input, lineStart, trimmedEnd)) {
                if (trimmedEnd - lineStart < 15) {
                    throw new IllegalArgumentException("Malformed node line");
                }
                if (lineCount == lineStarts.length) {
                    lineStarts = Arrays.copyOf(lineStarts, lineCount * 2);
                }
                lineStarts[lineCount++] = lineStart;
                ids.id(key(input, lineStart));
            }
            lineStart = lineEnd + 1;
        }

        int[] left = new int[ids.size];
        int[] right = new int[ids.size];
        int[] starts = new int[ids.size];
        int startCount = 0;
        int aaa = -1;
        int zzz = -1;
        boolean[] zEnds = new boolean[ids.size];
        for (int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
            int nameStart = lineStarts[lineIndex];
            int source = ids.id(key(input, nameStart));
            left[source] = ids.id(key(input, nameStart + 7));
            right[source] = ids.id(key(input, nameStart + 12));
            char third = input.charAt(nameStart + 2);
            if (third == 'A') {
                starts[startCount++] = source;
            }
            if (third == 'Z') {
                zEnds[source] = true;
            }
            if (input.charAt(nameStart) == 'A' && input.charAt(nameStart + 1) == 'A' && third == 'A') {
                aaa = source;
            }
            if (input.charAt(nameStart) == 'Z' && input.charAt(nameStart + 1) == 'Z' && third == 'Z') {
                zzz = source;
            }
        }
        return new Network(instructions, left, right, Arrays.copyOf(starts, startCount), aaa, zzz, zEnds);
    }

    private static long key(String input, int index) {
        return ((long) input.charAt(index) << 32)
                | ((long) input.charAt(index + 1) << 16)
                | input.charAt(index + 2);
    }

    private static boolean blank(String input, int from, int end) {
        for (int index = from; index < end; index++) {
            if (!Character.isWhitespace(input.charAt(index))) {
                return false;
            }
        }
        return true;
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

    private static final class NodeIds {
        private long[] keys = new long[64];
        private int[] values = new int[64];
        private int size;

        NodeIds() {
            Arrays.fill(values, -1);
        }

        int id(long key) {
            int mask = keys.length - 1;
            int slot = mix(key) & mask;
            while (true) {
                int existing = values[slot];
                if (existing < 0) {
                    keys[slot] = key;
                    values[slot] = size;
                    int assigned = size++;
                    if (size * 2 >= keys.length) {
                        grow();
                    }
                    return assigned;
                }
                if (keys[slot] == key) {
                    return existing;
                }
                slot = (slot + 1) & mask;
            }
        }

        private void grow() {
            long[] oldKeys = keys;
            int[] oldValues = values;
            keys = new long[oldKeys.length * 2];
            values = new int[oldValues.length * 2];
            Arrays.fill(values, -1);
            int mask = keys.length - 1;
            for (int i = 0; i < oldKeys.length; i++) {
                if (oldValues[i] < 0) {
                    continue;
                }
                int slot = mix(oldKeys[i]) & mask;
                while (values[slot] >= 0) {
                    slot = (slot + 1) & mask;
                }
                keys[slot] = oldKeys[i];
                values[slot] = oldValues[i];
            }
        }

        private static int mix(long key) {
            return (int) ((key * 0x9E3779B97F4A7C15L) >>> 32);
        }
    }
}
