package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day12 extends DayTemplate {
    static List<Integer> groups;
    static int[] sums;
    static long[] hashesCount;
    static long[] questionsCount;

    public String solve(boolean part1, Scanner in) {
        long answer = 0;
        List<String> records = new ArrayList<>();
        List<List<Integer>> vals = new ArrayList<>();
        while (in.hasNext()) {
            List<Integer> tmp = new ArrayList<>();
            String line = in.nextLine();
            records.add(line.split(" ")[0]);
            for (String v : line.split(" ")[1].split(",")) {
                tmp.add(Integer.parseInt(v));
            }
            vals.add(tmp);
        }
        if (!part1) {
            List<String> newRecords = new ArrayList<>();
            List<List<Integer>> newGroups = new ArrayList<>();
            for (int i = 0; i < records.size(); i++) {
                String a = records.get(i);
                List<Integer> b = vals.get(i);
                newRecords.add(a + "?" + a + "?" + a + "?" + a + "?" + a);
                List<Integer> tmp = new ArrayList<>();
                for (int j = 0; j < 5; j++) {
                    tmp.addAll(b);
                }
                newGroups.add(tmp);
            }
            records = newRecords;
            vals = newGroups;
        }
        for (int i = 0; i < records.size(); i++) {
            groups = vals.get(i);
            sums = precomputeSums(groups);
            hashesCount = precomputeCounts(records.get(i), '#');
            questionsCount = precomputeCounts(records.get(i), '?');
            Map<List<Integer>, Long> memo = new HashMap<>();
            answer += helper(records.get(i), memo, 0);
        }
        return answer + "";
    }

    private long helper(String record, Map<List<Integer>, Long> memo, int currGroup) {
        long answer = 0;
        if (groups.size() == currGroup) {
            if (record.contains("#")) {
                return 0;
            }
            return 1;
        }
        if (record.length() == 0) {
            return 0;
        }
        List<Integer> memoKey = Arrays.asList(currGroup, record.length());
        if (memo.containsKey(memoKey)) {
            return memo.get(memoKey);
        }
        if (!check(groups, record, currGroup)) {
            memo.put(memoKey, 0L);
            return 0;
        }

        int current = groups.get(currGroup);
        int index = 0;

        if (record.startsWith(".")) {
            while (index < record.length() && record.charAt(index) == '.') {
                index++;
            }
            answer = helper(record.substring(index), memo, currGroup);
        } else if (record.startsWith("#")) {
            if (record.length() == current) {
                if (record.contains(".")) {
                    return 0;
                } else {
                    return 1;
                }
            }
            if (record.substring(0, current).contains(".") || record.charAt(current) == '#') {
                return 0;
            } else {
                answer = helper(record.substring(current + 1), memo, currGroup + 1);
            }
        } else if (record.startsWith("?")) {
            String rec1 = "#" + record.substring(1);
            answer = helper(rec1, memo, currGroup) + helper(record.substring(1), memo, currGroup);
        }

        memo.put(memoKey, answer);
        return answer;
    }

    private boolean check(List<Integer> groups, String record, int currGroup) {
        int sum = sums[currGroup];
        int sum2 = sum + groups.size() - 1 - currGroup;
        long hashes = (record.charAt(0) == '#'?1:0) + hashesCount[hashesCount.length - record.length()];
        long questions = (record.charAt(0) == '?'?1:0) +questionsCount[questionsCount.length - record.length()];
        return sum >= hashes && sum <= (hashes + questions) && record.length() >= sum2;
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

    private long[] precomputeCounts(String record, char target) {
        long[] counts = new long[record.length() + 1];
        long count = 0;
        for (int i = record.length() - 1; i >= 0; i--) {
            count += record.charAt(i) == target ? 1 : 0;
            counts[i] = count;
        }
        return counts;
    }
}
