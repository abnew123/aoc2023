package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day02 implements DayTemplate {

    static final String RED = "red";
    static final String GREEN = "green";
    static final String BLUE = "blue";


    
    public String solve(boolean part1, Scanner in) {
        int ans = 0;
        while (in.hasNext()) {
            String[] parts = in.nextLine().split("[:;]");
            int index = Integer.parseInt(parts[0].split(" ")[1]);
            Map<String, Integer> totals = computeTotals(parts);
            ans += part1?part1Add(totals, index): part2Add(totals);
        }
        return ans + "";
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

    int part1Add(Map<String, Integer> totals, int index){
        if (totals.get(RED) <= 12 && totals.get(GREEN) <= 13 && totals.get(BLUE) <= 14) {
            return index;
        }
        return 0;
    }

    int part2Add(Map<String, Integer> totals){
        return totals.get(RED) * totals.get(GREEN) * totals.get(BLUE);
    }
}
