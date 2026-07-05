package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day20 implements DayTemplate {

    static String B = "broadcaster";
    static int OUT = -1;
    static int BC = 0;
    static int FF = 1;
    static int CJ = 2;


    
    public String solve(boolean part1, Scanner in) {
        Network net = new Network(readLines(in));
        long ans = part1 ? part1(net) : part2(net, net.rxInput);
        return ans + "";
    }

    static List<String> readLines(Scanner in) {
        List<String> lines = new ArrayList<>();
        while (in.hasNextLine()) {
            lines.add(in.nextLine());
        }
        return lines;
    }

    long part1(Network net) {
        long hi = 0;
        long lo = 0;
        for (int i = 0; i < 1000; i++) {
            net.clearQueue();
            net.enqueue(false, net.broadcaster, net.button);
            while (net.hasPulse()) {
                Pulse pulse = net.nextPulse();
                if (pulse.high) {
                    hi++;
                } else {
                    lo++;
                }
                net.sendPulse(pulse);
            }
        }
        return hi * lo;
    }

    long part2(Network net, int rxInput) {
        long ans = 1;
        int[] allInputs = net.inputsTo(rxInput);
        int[] seenOk = new int[allInputs.length];
        int seenN = 0;
        for (int i = 1; i < 10000 && seenN < allInputs.length; i++) {
            net.clearQueue();
            net.enqueue(false, net.broadcaster, net.button);
            while (net.hasPulse()) {
                Pulse pulse = net.nextPulse();
                if (pulse.high) {
                    for (int j = 0; j < allInputs.length; j++) {
                        if (pulse.input == allInputs[j] && seenOk[j] == 0) {
                            seenOk[j] = i;
                            seenN++;
                        }
                    }
                }
                net.sendPulse(pulse);
            }
        }
        for (int success : seenOk) {
            ans *= success;
        }
        return ans;
    }

    static class Network {
        Map<String, Integer> ids = new HashMap<>();
        List<Integer> tl = new ArrayList<>();
        List<int[]> tarList = new ArrayList<>();

        int button;
        int broadcaster;
        int rxInput = -1;

        int[] types;
        int[][] tars;
        int[][] inputs;
        boolean[] flip;
        boolean[][] mem;

        int[] qi = new int[256];
        int[] qt = new int[256];
        boolean[] qh = new boolean[256];
        int head;
        int tail;

        Network(List<String> lines) {
            button = id("button");
            broadcaster = id(B);
            for (String line : lines) {
                String[] sides = line.split(" -> ");
                String rawName = sides[0];
                int type;
                String name;
                if (rawName.charAt(0) == '%') {
                    type = FF;
                    name = rawName.substring(1);
                } else if (rawName.charAt(0) == '&') {
                    type = CJ;
                    name = rawName.substring(1);
                } else {
                    type = BC;
                    name = rawName;
                }

                int src = id(name);
                tl.set(src, type);

                String[] tarNames = sides[1].split(", ");
                int[] tarIds = new int[tarNames.length];
                for (int i = 0; i < tarNames.length; i++) {
                    int tar = id(tarNames[i]);
                    tarIds[i] = tar;
                    if (tarNames[i].equals("rx")) {
                        rxInput = src;
                    }
                }
                tarList.set(src, tarIds);
            }

            int size = tl.size();
            types = new int[size];
            tars = new int[size][];
            ArrayList<ArrayList<Integer>> inLists = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                types[i] = tl.get(i);
                tars[i] = tarList.get(i);
                inLists.add(new ArrayList<>());
            }
            for (int src = 0; src < size; src++) {
                for (int tar : tars[src]) {
                    if (types[tar] == CJ) {
                        inLists.get(tar).add(src);
                    }
                }
            }

            inputs = new int[size][];
            mem = new boolean[size][];
            for (int i = 0; i < size; i++) {
                inputs[i] = inLists.get(i).stream().mapToInt(Integer::intValue).toArray();
                if (types[i] == CJ) {
                    mem[i] = new boolean[inputs[i].length];
                }
            }
            flip = new boolean[size];
        }

        int id(String name) {
            Integer existing = ids.get(name);
            if (existing != null) {
                return existing;
            }
            int id = ids.size();
            ids.put(name, id);
            tl.add(OUT);
            tarList.add(new int[0]);
            return id;
        }

        int[] inputsTo(int module) {
            int count = 0;
            for (int src = 0; src < tars.length; src++) {
                for (int tar : tars[src]) {
                    if (tar == module) {
                        count++;
                    }
                }
            }
            int[] res = new int[count];
            int index = 0;
            for (int src = 0; src < tars.length; src++) {
                for (int tar : tars[src]) {
                    if (tar == module) {
                        res[index++] = src;
                    }
                }
            }
            return res;
        }

        void clearQueue() {
            head = 0;
            tail = 0;
        }

        boolean hasPulse() {
            return head < tail;
        }

        Pulse nextPulse() {
            return new Pulse(qh[head], qt[head], qi[head++]);
        }

        void enqueue(boolean high, int tar, int input) {
            if (tail == qt.length) {
                int newLength = qt.length * 2;
                qt = Arrays.copyOf(qt, newLength);
                qi = Arrays.copyOf(qi, newLength);
                qh = Arrays.copyOf(qh, newLength);
            }
            qh[tail] = high;
            qt[tail] = tar;
            qi[tail] = input;
            tail++;
        }

        void sendPulse(Pulse pulse) {
            int type = types[pulse.tar];
            if (type == OUT) {
                return;
            }
            if (type == BC) {
                sendToTargets(false, pulse.tar);
            } else if (type == FF) {
                if (!pulse.high) {
                    flip[pulse.tar] = !flip[pulse.tar];
                    sendToTargets(flip[pulse.tar], pulse.tar);
                }
            } else {
                boolean[] memory = mem[pulse.tar];
                int[] ins0 = inputs[pulse.tar];
                for (int i = 0; i < ins0.length; i++) {
                    if (ins0[i] == pulse.input) {
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
                sendToTargets(!allHigh, pulse.tar);
            }
        }

        void sendToTargets(boolean high, int src) {
            for (int tar : tars[src]) {
                enqueue(high, tar, src);
            }
        }
    }

    record Pulse(boolean high, int tar, int input) {
    }
}
