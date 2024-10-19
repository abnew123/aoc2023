package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day12 implements DayTemplate {
    int[] possibleCount;
    List<String> conditionRecords;
    List<List<Integer>> vals;

    @Override
    public String[] fullSolve(Scanner in) {
        parse(in);
        long answer1 = 0;
        long answer2 = 0;
        for (int i = 0; i < conditionRecords.size(); i++) {
            possibleCount = precomputePossible(conditionRecords.get(i) + ".");
            answer1 += solveOne(conditionRecords.get(i) + ".", vals.get(i));
        }
        List<String> newRecords = new ArrayList<>();
        List<List<Integer>> newGroups = new ArrayList<>();
        for (int i = 0; i < conditionRecords.size(); i++) {
            String a = conditionRecords.get(i);
            List<Integer> b = vals.get(i);
            newRecords.add(a + "?" + a + "?" + a + "?" + a + "?" + a);
            List<Integer> tmp = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                tmp.addAll(b);
            }
            newGroups.add(tmp);
        }
        conditionRecords = newRecords;
        vals = newGroups;
        for (int i = 0; i < conditionRecords.size(); i++) {
            possibleCount = precomputePossible(conditionRecords.get(i) + ".");
            answer2 += solveOne(conditionRecords.get(i) + ".", vals.get(i));
        }
        return new String[]{answer1 + "", answer2 + ""};
    }

    public String solve(boolean part1, Scanner in) {
        long answer = 0;
        parse(in);
        if (!part1) {
            List<String> newRecords = new ArrayList<>();
            List<List<Integer>> newGroups = new ArrayList<>();
            for (int i = 0; i < conditionRecords.size(); i++) {
                String a = conditionRecords.get(i);
                List<Integer> b = vals.get(i);
                newRecords.add(a + "?" + a + "?" + a + "?" + a + "?" + a);
                List<Integer> tmp = new ArrayList<>();
                for (int j = 0; j < 5; j++) {
                    tmp.addAll(b);
                }
                newGroups.add(tmp);
            }
            conditionRecords = newRecords;
            vals = newGroups;
        }
        for (int i = 0; i < conditionRecords.size(); i++) {
            possibleCount = precomputePossible(conditionRecords.get(i) + ".");
            answer += solveOne(conditionRecords.get(i) + ".", vals.get(i));
        }
        return answer + "";
    }

    private void parse(Scanner in){
        conditionRecords = new ArrayList<>();
        vals = new ArrayList<>();
        while (in.hasNext()) {
            List<Integer> tmp = new ArrayList<>();
            String line = in.nextLine();
            conditionRecords.add(line.split(" ")[0]);
            for (String v : line.split(" ")[1].split(",")) {
                tmp.add(Integer.parseInt(v));
            }
            vals.add(tmp);
        }
    }

    private long solveOne(String conditionRecord, List<Integer> groups) {
        int totalSprings = 0;
        for (Integer group : groups) {
            totalSprings += group;
        }
        int wiggle = conditionRecord.length() - totalSprings - groups.size() + 1;
        long[][] dp = new long[conditionRecord.length()][groups.size()];
        boolean noHashesToLeft = true;
        long sum = 0;
        for (int i = 0; i < wiggle; i++) {
            if (conditionRecord.charAt(i + groups.get(0)) == '#') {
                sum = 0;
            } else {
                if (noHashesToLeft && (possibleCount[i + groups.get(0)] - possibleCount[i]) == groups.get(0)) {
                    sum++;
                }
            }
            dp[i + groups.get(0)][0] = sum;
            noHashesToLeft &= (conditionRecord.charAt(i) != '#');
        }

        int start = groups.get(0) + 1;
        for (int i = 1; i < groups.size(); i++) {
            sum = 0;
            for (int j = start; j < start + wiggle; j++) {
                if (conditionRecord.charAt(j + groups.get(i)) == '#') {
                    sum = 0;
                } else {
                    if (dp[j - 1][i - 1] > 0 && (conditionRecord.charAt(j - 1) != '#') && (possibleCount[j + groups.get(i)] - possibleCount[j]) == groups.get(i)) {
                        sum += dp[j - 1][i - 1];
                    }
                }
                dp[j + groups.get(i)][i] = sum;
            }
            start += groups.get(i) + 1;
        }
        return sum;
    }

    private int[] precomputePossible(String conditionRecord) {
        int[] counts = new int[conditionRecord.length() + 1];
        int count = 0;
        for (int i = 0; i < conditionRecord.length(); i++) {
            if (conditionRecord.charAt(i) == '#' || conditionRecord.charAt(i) == '?') {
                count++;
            }
            counts[i + 1] = count;
        }
        return counts;
    }
}
