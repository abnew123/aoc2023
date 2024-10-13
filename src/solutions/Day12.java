package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day12 implements DayTemplate {
    int[] possibleCount;

    public String solve(boolean part1, Scanner in) {
        long answer = 0;
        List<String> conditionRecords = new ArrayList<>();
        List<int[]> vals = new ArrayList<>();
        String[] stringGroup;
        String line;
        while (in.hasNext()) {
            line = in.nextLine();
            conditionRecords.add(line.split(" ")[0]);
            stringGroup = line.split(" ")[1].split(",");
            int[] group = new int[stringGroup.length];
            for(int i = 0; i < stringGroup.length; i++){
                group[i] = Integer.parseInt(stringGroup[i]);
            }
            vals.add(group);
        }
        if (!part1) {
            List<String> newRecords = new ArrayList<>();
            List<int[]> newGroups = new ArrayList<>();
            for (int i = 0; i < conditionRecords.size(); i++) {
                String a = conditionRecords.get(i);
                int[] group = vals.get(i);
                newRecords.add(a + "?" + a + "?" + a + "?" + a + "?" + a);
                int[] newGroup = new int[group.length * 5];
                for (int j = 0; j < 5; j++) {
                    for(int k = 0; k < group.length; k++){
                        newGroup[j*group.length + k] = group[k];
                    }
                }
                newGroups.add(newGroup);
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

    private long solveOne(String conditionRecord, int[] groups) {
        int totalSprings = 0;
        for (Integer group : groups) {
            totalSprings += group;
        }
        int wiggle = conditionRecord.length() - totalSprings - groups.length + 1;
        long[][] dp = new long[conditionRecord.length()][groups.length];
        boolean noHashesToLeft = true;
        long sum = 0;
        for (int i = 0; i < wiggle; i++) {
            if (conditionRecord.charAt(i + groups[0]) == '#') {
                sum = 0;
            } else {
                if (noHashesToLeft && (possibleCount[i + groups[0]] - possibleCount[i]) == groups[0]) {
                    sum++;
                }
            }
            dp[i + groups[0]][0] = sum;
            noHashesToLeft &= (conditionRecord.charAt(i) != '#');
        }

        int start = groups[0] + 1;
        for (int i = 1; i < groups.length; i++) {
            sum = 0;
            for (int j = start; j < start + wiggle; j++) {
                if (conditionRecord.charAt(j + groups[i]) == '#') {
                    sum = 0;
                } else {
                    if (dp[j - 1][i - 1] > 0 && (conditionRecord.charAt(j - 1) != '#') && (possibleCount[j + groups[i]] - possibleCount[j]) == groups[i]) {
                        sum += dp[j - 1][i - 1];
                    }
                }
                dp[j + groups[i]][i] = sum;
            }
            start += groups[i] + 1;
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
