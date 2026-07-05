package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day20 implements DayTemplate {

    private static final String BROADCASTER = "broadcaster";
    private static final int OUTPUT = -1;
    private static final int BROADCAST = 0;
    private static final int FLIP_FLOP = 1;
    private static final int CONJUNCTION = 2;

    @Override
    public String[] fullSolve(Scanner in) {
        List<String> lines = readLines(in);
        Network part1Network = new Network(lines);
        Network part2Network = new Network(lines);
        return new String[]{
                part1(part1Network) + "",
                part2(part2Network, part2Network.rxInput) + ""
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
        Network network = new Network(readLines(in));
        long answer = part1 ? part1(network) : part2(network, network.rxInput);
        return answer + "";
    }

    private static List<String> readLines(Scanner in) {
        List<String> lines = new ArrayList<>();
        while (in.hasNextLine()) {
            lines.add(in.nextLine());
        }
        return lines;
    }

    private long part1(Network network) {
        long highPulses = 0;
        long lowPulses = 0;
        for (int i = 0; i < 1000; i++) {
            network.clearQueue();
            network.enqueue(false, network.broadcaster, network.button);
            while (network.hasPulse()) {
                Pulse pulse = network.nextPulse();
                if (pulse.high) {
                    highPulses++;
                } else {
                    lowPulses++;
                }
                network.sendPulse(pulse);
            }
        }
        return highPulses * lowPulses;
    }

    private long part2(Network network, int rxInput) {
        long answer = 1;
        int[] allInputs = network.inputsTo(rxInput);
        int[] recordedSuccesses = new int[allInputs.length];
        int recordedSoFar = 0;
        for (int i = 1; i < 10000 && recordedSoFar < allInputs.length; i++) {
            network.clearQueue();
            network.enqueue(false, network.broadcaster, network.button);
            while (network.hasPulse()) {
                Pulse pulse = network.nextPulse();
                if (pulse.high) {
                    for (int j = 0; j < allInputs.length; j++) {
                        if (pulse.input == allInputs[j] && recordedSuccesses[j] == 0) {
                            recordedSuccesses[j] = i;
                            recordedSoFar++;
                        }
                    }
                }
                network.sendPulse(pulse);
            }
        }
        for (int success : recordedSuccesses) {
            answer *= success;
        }
        return answer;
    }

    private static final class Network {
        private final Map<String, Integer> ids = new HashMap<>();
        private final List<Integer> typeList = new ArrayList<>();
        private final List<int[]> targetList = new ArrayList<>();

        private int button;
        private int broadcaster;
        private int rxInput = -1;

        private int[] types;
        private int[][] targets;
        private int[][] inputs;
        private boolean[] flipFlopOn;
        private boolean[][] conjunctionMemory;

        private int[] queueInput = new int[256];
        private int[] queueTarget = new int[256];
        private boolean[] queueHigh = new boolean[256];
        private int head;
        private int tail;

        Network(List<String> lines) {
            button = id("button");
            broadcaster = id(BROADCASTER);
            for (String line : lines) {
                String[] sides = line.split(" -> ");
                String rawName = sides[0];
                int type;
                String name;
                if (rawName.charAt(0) == '%') {
                    type = FLIP_FLOP;
                    name = rawName.substring(1);
                } else if (rawName.charAt(0) == '&') {
                    type = CONJUNCTION;
                    name = rawName.substring(1);
                } else {
                    type = BROADCAST;
                    name = rawName;
                }

                int source = id(name);
                typeList.set(source, type);

                String[] targetNames = sides[1].split(", ");
                int[] targetIds = new int[targetNames.length];
                for (int i = 0; i < targetNames.length; i++) {
                    int target = id(targetNames[i]);
                    targetIds[i] = target;
                    if (targetNames[i].equals("rx")) {
                        rxInput = source;
                    }
                }
                targetList.set(source, targetIds);
            }

            int size = typeList.size();
            types = new int[size];
            targets = new int[size][];
            ArrayList<ArrayList<Integer>> inputLists = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                types[i] = typeList.get(i);
                targets[i] = targetList.get(i);
                inputLists.add(new ArrayList<>());
            }
            for (int source = 0; source < size; source++) {
                for (int target : targets[source]) {
                    if (types[target] == CONJUNCTION) {
                        inputLists.get(target).add(source);
                    }
                }
            }

            inputs = new int[size][];
            conjunctionMemory = new boolean[size][];
            for (int i = 0; i < size; i++) {
                inputs[i] = inputLists.get(i).stream().mapToInt(Integer::intValue).toArray();
                if (types[i] == CONJUNCTION) {
                    conjunctionMemory[i] = new boolean[inputs[i].length];
                }
            }
            flipFlopOn = new boolean[size];
        }

        private int id(String name) {
            Integer existing = ids.get(name);
            if (existing != null) {
                return existing;
            }
            int id = ids.size();
            ids.put(name, id);
            typeList.add(OUTPUT);
            targetList.add(new int[0]);
            return id;
        }

        private int[] inputsTo(int module) {
            int count = 0;
            for (int source = 0; source < targets.length; source++) {
                for (int target : targets[source]) {
                    if (target == module) {
                        count++;
                    }
                }
            }
            int[] result = new int[count];
            int index = 0;
            for (int source = 0; source < targets.length; source++) {
                for (int target : targets[source]) {
                    if (target == module) {
                        result[index++] = source;
                    }
                }
            }
            return result;
        }

        private void clearQueue() {
            head = 0;
            tail = 0;
        }

        private boolean hasPulse() {
            return head < tail;
        }

        private Pulse nextPulse() {
            return new Pulse(queueHigh[head], queueTarget[head], queueInput[head++]);
        }

        private void enqueue(boolean high, int target, int input) {
            if (tail == queueTarget.length) {
                int newLength = queueTarget.length * 2;
                queueTarget = Arrays.copyOf(queueTarget, newLength);
                queueInput = Arrays.copyOf(queueInput, newLength);
                queueHigh = Arrays.copyOf(queueHigh, newLength);
            }
            queueHigh[tail] = high;
            queueTarget[tail] = target;
            queueInput[tail] = input;
            tail++;
        }

        private void sendPulse(Pulse pulse) {
            int type = types[pulse.target];
            if (type == OUTPUT) {
                return;
            }
            if (type == BROADCAST) {
                sendToTargets(false, pulse.target);
            } else if (type == FLIP_FLOP) {
                if (!pulse.high) {
                    flipFlopOn[pulse.target] = !flipFlopOn[pulse.target];
                    sendToTargets(flipFlopOn[pulse.target], pulse.target);
                }
            } else {
                boolean[] memory = conjunctionMemory[pulse.target];
                int[] conjunctionInputs = inputs[pulse.target];
                for (int i = 0; i < conjunctionInputs.length; i++) {
                    if (conjunctionInputs[i] == pulse.input) {
                        memory[i] = pulse.high;
                        break;
                    }
                }

                boolean allHigh = true;
                for (boolean high : memory) {
                    if (!high) {
                        allHigh = false;
                        break;
                    }
                }
                sendToTargets(!allHigh, pulse.target);
            }
        }

        private void sendToTargets(boolean high, int source) {
            for (int target : targets[source]) {
                enqueue(high, target, source);
            }
        }
    }

    private record Pulse(boolean high, int target, int input) {
    }
}
