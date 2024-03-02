package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day20 implements DayTemplate {

    protected static final String BROADCASTER = "broadcaster";

    /**
     * Main solving method.
     *
     * @param part1 The solver will solve part 1 if param is set to true.
     *              The solver will solve part 2 if param is set to false.
     * @param in    The solver will read data from this Scanner.
     * @return Returns answer in string format.
     */
    public String solve(boolean part1, Scanner in) {
        long answer;
        Map<String, Module> modules = buildModules(in);
        String rxInput = findInputAndInitializeConjunctions(modules);
        if (part1) {
            answer = part1(modules);
        } else {
            answer = part2(modules, rxInput);

        }
        return answer + "";
    }

    private Map<String, Module> buildModules(Scanner in) {
        Map<String, Module> modules = new HashMap<>();
        while (in.hasNext()) {
            String[] line = in.nextLine().split("->|,");
            if (line[0].contains("%")) {
                modules.put(line[0].substring(1).trim(), new FlipFlop(line));
            }
            if (line[0].contains("&")) {
                modules.put(line[0].substring(1).trim(), new Conjunction(line));
            }
            if (line[0].startsWith(BROADCASTER)) {
                modules.put(line[0].trim(), new Broadcaster(line));
            }
        }
        return modules;
    }

    private String findInputAndInitializeConjunctions(Map<String, Module> modules) {
        String rxInput = null;
        for (Map.Entry<String, Module> entry : modules.entrySet()) {
            List<String> targets = entry.getValue().getTargets();
            for (String t : targets) {
                if (t.equals("rx")) {
                    rxInput = entry.getKey();
                }
                if (modules.get(t) instanceof Conjunction conjunction) {
                    conjunction.inputs.add(entry.getKey());
                }
            }
        }
        return rxInput;
    }

    private long part1(Map<String, Module> modules) {
        long answer;
        long highPulses = 0;
        long lowPulses = 0;
        for (int i = 0; i < 1000; i++) {
            List<Pulse> pulses = new ArrayList<>();
            pulses.add(new Pulse(false, BROADCASTER, "button"));
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
        return answer;
    }

    private long part2(Map<String, Module> modules, String rxInput) {
        long answer = 1;
        List<String> allInputs = findAllInputs(modules, rxInput);

        List<Integer> recordedSuccesses = new ArrayList<>();
        int totalRecorded = allInputs.size();
        int recordedSoFar = 0;
        while (recordedSuccesses.size() < totalRecorded) {
            recordedSuccesses.add(0);
        }
        for (int i = 1; (i < 10000 && recordedSoFar < totalRecorded); i++) {
            List<Pulse> pulses = new ArrayList<>();
            pulses.add(new Pulse(false, BROADCASTER, "button"));
            int index = 0;
            while (pulses.size() > index) {
                for (int j = 0; j < allInputs.size(); j++) {
                    if (pulses.get(index).input.equals(allInputs.get(j)) && pulses.get(index).high && recordedSuccesses.get(j) == 0) {
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
        return answer;
    }

    private List<String> findAllInputs(Map<String, Module> modules, String rxInput){
        List<String> allInputs = new ArrayList<>();
        for (Map.Entry<String, Module> entry : modules.entrySet()) {
            List<String> targets = entry.getValue().getTargets();
            for (String t : targets) {
                if (t.equals(rxInput)) {
                    allInputs.add(entry.getKey());
                }
            }
        }
        return allInputs;
    }
}

abstract class Module {
    String name;
    List<String> targets = new ArrayList<>();

    protected Module(String[] line, String n) {
        for (int i = 1; i < line.length; i++) {
            targets.add(line[i].trim());
        }
        name = n;
    }

    public abstract List<Pulse> sendPulse(Map<String, Module> modules, Pulse p);

    public List<String> getTargets() {
        return targets;
    }
}

class Broadcaster extends Module {

    public Broadcaster(String[] line) {
        super(line, Day20.BROADCASTER);
    }

    @Override
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

    @Override
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

    @Override
    public List<Pulse> sendPulse(Map<String, Module> modules, Pulse p) {
        List<Pulse> ret = new ArrayList<>();
        if (lastPulses.isEmpty()) {
            for (int i = 0; i < inputs.size(); i++) {
                lastPulses.add(false);
            }
        }
        lastPulses.set(inputs.indexOf(p.input), p.high);
        boolean on = false;
        for (Boolean b : lastPulses) {
            if (Boolean.FALSE.equals(b)) {
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