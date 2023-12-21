package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day20 extends DayTemplate {

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
        Map<String, Module> modules = new HashMap<>();
        while (in.hasNext()) {
            String[] line = in.nextLine().split("->|,");
            if (line[0].contains("%")) {
                modules.put(line[0].substring(1).trim(), new FlipFlop(line));
            }
            if (line[0].contains("&")) {
                modules.put(line[0].substring(1).trim(), new Conjunction(line));
            }
            if (line[0].startsWith("broadcaster")) {
                modules.put(line[0].trim(), new Broadcaster(line));
            }
        }
        for (String s : modules.keySet()) {
            List<String> targets = modules.get(s).getTargets();
            for (String t : targets) {
                if (modules.get(t) instanceof Conjunction) {
                    ((Conjunction) modules.get(t)).inputs.add(s);
                }
            }
        }
        List<Pulse> pulses;
        if (part1) {
            long highPulses = 0;
            long lowPulses = 0;
            for (int i = 0; i < 1000; i++) {
                pulses = new ArrayList<>();
                pulses.add(new Pulse(false, "broadcaster", "button"));
                int index = 0;
                while (pulses.size() > index) {
                    Module m = modules.get(pulses.get(index).target);
                    if (m != null) {
                        pulses.addAll(m.sendPulse(modules, pulses.get(index)));
                    }
                    index++;
                }
                highPulses += pulses.stream().map(x -> x.high).filter(x -> x).count();
                lowPulses += pulses.stream().map(x -> x.high).filter(x -> !x).count();
            }
            answer = highPulses * lowPulses;
        } else {
            answer = 1;
            List<String> allInputs = new ArrayList<>();
            allInputs.add("vc");
            allInputs.add("tn");
            allInputs.add("hd");
            allInputs.add("jx");
            List<Integer> recordedSuccesses = new ArrayList<>();
            int totalRecorded = allInputs.size();
            int recordedSoFar = 0;
            while (recordedSuccesses.size() < totalRecorded) {
                recordedSuccesses.add(0);
            }
            for (int i = 1; (i < 10000 && recordedSoFar < totalRecorded); i++) {
                pulses = new ArrayList<>();
                pulses.add(new Pulse(false, "broadcaster", "button"));
                int index = 0;
                while (pulses.size() > index) {
                    for (int j = 0; j < allInputs.size(); j++) {
                        if (pulses.get(index).input.equals(allInputs.get(j)) && !pulses.get(index).high && recordedSuccesses.get(j) == 0) {
                            recordedSuccesses.set(j, i);
                            recordedSoFar++;
                        }
                    }
                    Module m = modules.get(pulses.get(index).target);
                    if (m != null) {
                        pulses.addAll(m.sendPulse(modules, pulses.get(index)));
                    }
                    index++;
                }
            }
            for (Integer success : recordedSuccesses) {
                answer *= success;
            }
        }
        return answer + "";
    }
}

class Module {

    String name;
    List<String> targets = new ArrayList<>();

    public Module(String[] line, String n) {
        for (int i = 1; i < line.length; i++) {
            targets.add(line[i].trim());
        }
        name = n;
    }

    public List<Pulse> sendPulse(Map<String, Module> modules, Pulse p) {
        return new ArrayList<>();
    }

    public List<String> getTargets() {
        return targets;
    }
}

class Broadcaster extends Module {

    public Broadcaster(String[] line) {
        super(line, "broadcaster");
    }

    public List<Pulse> sendPulse(Map<String, Module> modules, Pulse p) {
        List<Pulse> ret = new ArrayList<>();
        for (String target : targets) {
            ret.add(new Pulse(false, target, name));
        }
        return ret;
    }

}

class FlipFlop extends Module {

    boolean on;

    public FlipFlop(String[] line) {
        super(line, line[0].substring(1).trim());
        on = false;
    }

    public List<Pulse> sendPulse(Map<String, Module> modules, Pulse p) {
        List<Pulse> ret = new ArrayList<>();
        if (!p.high) {
            on = !on;
            for (String target : targets) {
                ret.add(new Pulse(on, target, name));
            }
        }
        return ret;
    }
}

class Conjunction extends Module {

    List<String> inputs = new ArrayList<>();
    List<Boolean> lastPulses = new ArrayList<>();

    public Conjunction(String[] line) {
        super(line, line[0].substring(1).trim());
    }

    public List<Pulse> sendPulse(Map<String, Module> modules, Pulse p) {
        List<Pulse> ret = new ArrayList<>();
        if (lastPulses.isEmpty()) {
            for (String ignored : inputs) {
                lastPulses.add(false);
            }
        }
        lastPulses.set(inputs.indexOf(p.input), p.high);
        boolean on = false;
        for (Boolean b : lastPulses) {
            if (!b) {
                on = true;
                break;
            }
        }
        for (String target : targets) {
            ret.add(new Pulse(on, target, name));
        }
        return ret;
    }
}

class Pulse {
    
    boolean high;
    String target;
    String input;

    public Pulse(boolean high, String target, String input) {
        this.high = high;
        this.target = target;
        this.input = input;
    }
}