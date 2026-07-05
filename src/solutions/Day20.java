package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day20 implements DayTemplate {

    protected static final String BROADCASTER = "broadcaster";

    Map<String, Module> modules2 = new HashMap<>();

    @Override
    public String[] fullSolve(Scanner in) {
        Map<String, Module> modules = buildModules(in);
        String rxInput = findInputAndInitializeConjunctions(modules);
        findInputAndInitializeConjunctions(modules2);
        return new String[]{part1(modules) + "", part2(modules2, rxInput) + ""};
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
            modules.put(moduleName(line), createModule(line));
            modules2.put(moduleName(line), createModule(line));
        }
        return modules;
    }

    private String moduleName(String[] line) {
        if (line[0].startsWith(BROADCASTER)) {
            return line[0].trim();
        }
        return line[0].substring(1).trim();
    }

    private Module createModule(String[] line) {
        if (line[0].contains("%")) {
            return new FlipFlop(line);
        }
        if (line[0].contains("&")) {
            return new Conjunction(line);
        }
        return new Broadcaster(line);
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
            List<Pulse> pulses = initialButtonPress();
            int index = 0;
            while (pulses.size() > index) {
                sendQueuedPulse(modules, pulses, index);
                index++;
            }
            for (Pulse pulse : pulses) {
                if (pulse.high) {
                    highPulses++;
                } else {
                    lowPulses++;
                }
            }
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
            List<Pulse> pulses = initialButtonPress();
            int index = 0;
            while (pulses.size() > index) {
                if (recordFirstHighPulse(pulses.get(index), allInputs, recordedSuccesses, i)) {
                    recordedSoFar++;
                }
                sendQueuedPulse(modules, pulses, index);
                index++;
            }
        }
        for (Integer success : recordedSuccesses) {
            answer *= success;
        }
        return answer;
    }

    private List<Pulse> initialButtonPress() {
        List<Pulse> pulses = new ArrayList<>();
        pulses.add(new Pulse(false, BROADCASTER, "button"));
        return pulses;
    }

    private void sendQueuedPulse(Map<String, Module> modules, List<Pulse> pulses, int index) {
        Pulse pulse = pulses.get(index);
        Module module = modules.get(pulse.target);
        if (module != null) {
            pulses.addAll(module.sendPulse(modules, pulse));
        }
    }

    private boolean recordFirstHighPulse(Pulse pulse, List<String> allInputs, List<Integer> recordedSuccesses, int buttonPress) {
        for (int j = 0; j < allInputs.size(); j++) {
            if (pulse.input.equals(allInputs.get(j)) && pulse.high && recordedSuccesses.get(j) == 0) {
                recordedSuccesses.set(j, buttonPress);
                return true;
            }
        }
        return false;
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
