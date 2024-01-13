package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day12 implements DayTemplate {
    List<Integer> groups;
    long[] hashesCount;
    long[] questionsCount;

    public String solve(boolean part1, Scanner in) {
        long answer = 0;
        List<String> conditionRecords = new ArrayList<>();
        List<List<Integer>> vals = new ArrayList<>();
        while (in.hasNext()) {
            List<Integer> tmp = new ArrayList<>();
            String line = in.nextLine();
            conditionRecords.add(line.split(" ")[0]);
            for (String v : line.split(" ")[1].split(",")) {
                tmp.add(Integer.parseInt(v));
            }
            vals.add(tmp);
        }
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
            groups = vals.get(i);
            hashesCount = precomputeCounts(conditionRecords.get(i), '#');
            questionsCount = precomputeCounts(conditionRecords.get(i), '?');
            Map<List<Integer>, Long> memo = new HashMap<>();
            answer += helper(conditionRecords.get(i), memo, 0, precomputeSums(groups));
        }
        return answer + "";
    }

    private long helper(String conditionRecord, Map<List<Integer>, Long> memo, int currGroup, int[] sums) {
        long answer = 0;
        if (groups.size() == currGroup) {
            if (conditionRecord.contains("#")) {
                return 0;
            }
            return 1;
        }
        if (conditionRecord.length() == 0) {
            return 0;
        }
        List<Integer> memoKey = Arrays.asList(currGroup, conditionRecord.length());
        if (memo.containsKey(memoKey)) {
            return memo.get(memoKey);
        }
        if (!check(groups, conditionRecord, currGroup, sums)) {
            memo.put(memoKey, 0L);
            return 0;
        }

        int current = groups.get(currGroup);
        int index = 0;

        if (conditionRecord.startsWith(".")) {
            while (index < conditionRecord.length() && conditionRecord.charAt(index) == '.') {
                index++;
            }
            answer = helper(conditionRecord.substring(index), memo, currGroup, sums);
        } else if (conditionRecord.startsWith("#")) {
            if (conditionRecord.length() == current) {
                if (conditionRecord.contains(".")) {
                    return 0;
                } else {
                    return 1;
                }
            }
            if (conditionRecord.substring(0, current).contains(".") || conditionRecord.charAt(current) == '#') {
                return 0;
            } else {
                answer = helper(conditionRecord.substring(current + 1), memo, currGroup + 1, sums);
            }
        } else if (conditionRecord.startsWith("?")) {
            String rec1 = "#" + conditionRecord.substring(1);
            answer = helper(rec1, memo, currGroup, sums) + helper(conditionRecord.substring(1), memo, currGroup, sums);
        }

        memo.put(memoKey, answer);
        return answer;
    }

    private boolean check(List<Integer> groups, String conditionRecord, int currGroup, int[] sums) {
        int sum = sums[currGroup];
        int sum2 = sum + groups.size() - 1 - currGroup;
        long hashes = (conditionRecord.charAt(0) == '#'?1:0) + hashesCount[hashesCount.length - conditionRecord.length()];
        long questions = (conditionRecord.charAt(0) == '?'?1:0) +questionsCount[questionsCount.length - conditionRecord.length()];
        return sum >= hashes && sum <= (hashes + questions) && conditionRecord.length() >= sum2;
    }

    private int[] precomputeSums(List<Integer> groups) {
        int n = groups.size();
        int[] sums = new int[n];
        sums[n - 1] = groups.get(n - 1);
        for (int i = n - 2; i >= 0; i--) {
            sums[i] = sums[i + 1] + groups.get(i);
        }
        return sums;
    }

    private long[] precomputeCounts(String conditionRecord, char target) {
        long[] counts = new long[conditionRecord.length() + 1];
        long count = 0;
        for (int i = conditionRecord.length() - 1; i >= 0; i--) {
            count += conditionRecord.charAt(i) == target ? 1 : 0;
            counts[i] = count;
        }
        return counts;
    }
}
