package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day19 extends DayTemplate {

    public static String[] allCategories = new String[]{"x", "m", "a", "s"};

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
        boolean workflow = true;
        Map<String, Workflow> workflows = new HashMap<>();
        List<Part> parts = new ArrayList<>();
        while (in.hasNext()) {
            String line = in.nextLine();
            if (line.equals("")) {
                workflow = false;
            } else {
                if (workflow) {
                    String[] pieces = line.split("[{}]");
                    workflows.put(pieces[0], new Workflow(pieces[1]));
                } else {
                    parts.add(new Part(line));
                }
            }
        }
        if (!part1) {
            List<SubRangeTuple> accepted = new ArrayList<>();
            SubRangeTuple tuple = new SubRangeTuple();
            Workflow w = workflows.get("in");
            w.getValid(accepted, workflows, tuple);
            for (SubRangeTuple t : accepted) {
                long rangeTotal = 1;
                for (String s : allCategories) {
                    rangeTotal *= t.upper.get(s) - t.lower.get(s) + 1;
                }
                answer += rangeTotal;
            }
        } else {
            List<Part> accepted = new ArrayList<>();
            for (Part p : parts) {
                Workflow w = workflows.get("in");
                String result;
                while (true) {
                    result = w.process(p);
                    if (result.equals("A")) {
                        accepted.add(p);
                        break;
                    }
                    if (result.equals("R")) {
                        break;
                    }
                    w = workflows.get(result);
                }
            }
            for (Part p : accepted) {
                answer += p.vals.get("x") + p.vals.get("m") + p.vals.get("a") + p.vals.get("s");
            }
        }
        return answer + "";
    }
}

class Workflow {
    List<Condition> conditions;
    String fin;

    public Workflow(String line) {
        conditions = new ArrayList<>();
        String[] pieces = line.split(",");
        for (int i = 0; i < pieces.length - 1; i++) {
            conditions.add(new Condition(pieces[i]));
        }
        fin = pieces[pieces.length - 1];
    }

    public String process(Part p) {
        String ret;
        for (Condition c : conditions) {
            ret = c.process(p);
            if (ret != null) {
                return ret;
            }
        }
        return fin;
    }

    public void getValid(List<SubRangeTuple> accepted, Map<String, Workflow> workflows, SubRangeTuple tuple) {
        for (Condition c : conditions) {
            tuple = c.addValid(accepted, workflows, tuple);
        }
        if (fin.equals("A") || fin.equals("R")) {
            if (fin.equals("A")) {
                accepted.add(tuple);
            }
        } else {
            workflows.get(fin).getValid(accepted, workflows, tuple);
        }
    }
}

class Condition {
    String var;
    boolean greater;
    int threshold;
    String ifYes;

    public Condition(String l) {
        greater = l.contains(">");
        var = l.split("[<>:]")[0];
        threshold = Integer.parseInt(l.split("[<>:]")[1]);
        ifYes = l.split("[<>:]")[2];
    }

    public String process(Part p) {
        int value = p.vals.get(var);
        if (greater) {
            return (value > threshold) ? ifYes : null;
        } else {
            return (value < threshold) ? ifYes : null;
        }
    }

    public SubRangeTuple addValid(List<SubRangeTuple> accepted, Map<String, Workflow> workflows, SubRangeTuple tuple) {
        SubRangeTuple newTuple = new SubRangeTuple(tuple);
        SubRangeTuple currTuple = new SubRangeTuple(tuple);
        if (greater) {
            if (currTuple.upper.get(var) <= threshold) {
                return newTuple;
            }
            newTuple.upper.put(var, threshold);
            currTuple.lower.put(var, threshold + 1);
            if (newTuple.lower.get(var) >= threshold) {
                newTuple.upper.put(var, newTuple.lower.get(var));
            }
        } else {
            if (currTuple.lower.get(var) >= threshold) {
                return newTuple;
            }
            newTuple.lower.put(var, threshold);
            currTuple.upper.put(var, threshold - 1);
            if (newTuple.upper.get(var) <= threshold) {
                newTuple.upper.put(var, newTuple.lower.get(var));
            }
        }
        if (ifYes.equals("A") || ifYes.equals("R")) {
            if (ifYes.equals("A")) {
                accepted.add(currTuple);
            }
        } else {
            Workflow w = workflows.get(ifYes);
            w.getValid(accepted, workflows, currTuple);
        }
        return newTuple;
    }
}

class Part {
    Map<String, Integer> vals;

    public Part(String line) {
        String[] pieces = line.split("[{}=,xmas]");
        vals = Map.of("x", Integer.parseInt(pieces[3]), "m", Integer.parseInt(pieces[6]), "a", Integer.parseInt(pieces[9]), "s", Integer.parseInt(pieces[12]));
    }
}

class SubRangeTuple {
    Map<String, Integer> upper = new HashMap<>();
    Map<String, Integer> lower = new HashMap<>();

    public SubRangeTuple() {
        for (String s : Day19.allCategories) {
            upper.put(s, 4000);
            lower.put(s, 1);
        }
    }

    public SubRangeTuple(SubRangeTuple tuple) {
        upper.putAll(tuple.upper);
        lower.putAll(tuple.lower);
    }
}