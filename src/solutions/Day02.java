package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day02 implements DayTemplate {

    static final String RED = "red";
    static final String GREEN = "green";
    static final String BLUE = "blue";

    @Override
    public String[] fullSolve(Scanner in) {
        int answer1 = 0;
        int answer2 = 0;
        while (in.hasNext()) {
            String[] parts = in.nextLine().split("[:;]");
            int index = Integer.parseInt(parts[0].split(" ")[1]);
            Map<String, Integer> totals = computeTotals(parts);
            answer1 += part1Add(totals, index);
            answer2 += part2Add(totals);
        }
        return new String[]{answer1 + "", answer2+ ""};
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
        int answer = 0;
        while (in.hasNext()) {
            String[] parts = in.nextLine().split("[:;]");
            int index = Integer.parseInt(parts[0].split(" ")[1]);
            Map<String, Integer> totals = computeTotals(parts);
            answer += part1?part1Add(totals, index): part2Add(totals);
        }
        return answer + "";
    }

    public Map<String, Integer> computeTotals(String[] parts){
        Map<String, Integer> totals = new HashMap<>();
        for (int i = 1; i < parts.length; i++) {
            String[] draws = parts[i].split(",");
            for (String draw : draws) {
                draw = draw.trim();
                String color = draw.split(" ")[1];
                int number = Integer.parseInt(draw.split(" ")[0]);
                totals.putIfAbsent(color, 0);
                totals.put(color, Math.max(totals.get(color), number));
            }
        }
        return totals;
    }

    private int part1Add(Map<String, Integer> totals, int index){
        if (totals.get(RED) <= 12 && totals.get(GREEN) <= 13 && totals.get(BLUE) <= 14) {
            return index;
        }
        return 0;
    }

    private int part2Add(Map<String, Integer> totals){
        return totals.get(RED) * totals.get(GREEN) * totals.get(BLUE);
    }
}
