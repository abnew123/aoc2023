package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day12 implements DayTemplate {
    int[] possibleCount;
    List<String> conditionRecords;
    List<int[]> vals;

    @Override
    public String[] fullSolve(Scanner in) {
        parse(in);
        long answer1 = solveRecords();
        generateNewRecords();
        long answer2 = solveRecords();
        return new String[]{String.valueOf(answer1), String.valueOf(answer2)};
    }

    public String solve(boolean part1, Scanner in) {
        parse(in);
        if (!part1) {
            generateNewRecords();
        }
        long answer = solveRecords();
        return String.valueOf(answer);
    }

    private void generateNewRecords(){
        List<String> newRecords = new ArrayList<>(conditionRecords.size());
        List<int[]> newGroups = new ArrayList<>(vals.size());

        for (int i = 0; i < conditionRecords.size(); i++) {
            String record = conditionRecords.get(i);
            int[] groups = vals.get(i);

            // Use StringBuilder for efficient string concatenation with pre-allocated capacity
            StringBuilder sb = new StringBuilder(record.length() * 5 + 4);
            sb.append(record);
            for (int j = 1; j < 5; j++) {
                sb.append('?').append(record);
            }
            newRecords.add(sb.toString());

            // Create new array with 5x the groups using System.arraycopy for efficiency
            int[] newGroupArray = new int[groups.length * 5];
            for (int j = 0; j < 5; j++) {
                System.arraycopy(groups, 0, newGroupArray, j * groups.length, groups.length);
            }
            newGroups.add(newGroupArray);
        }
        conditionRecords = newRecords;
        vals = newGroups;
    }

    private long solveRecords(){
        long answer = 0;
        for (int i = 0; i < conditionRecords.size(); i++) {
            String recordWithDot = conditionRecords.get(i) + ".";
            possibleCount = precomputePossible(recordWithDot);
            answer += solveOne(recordWithDot, vals.get(i));
        }
        return answer;
    }

    private void parse(Scanner in){
        conditionRecords = new ArrayList<>();
        vals = new ArrayList<>();

        while (in.hasNext()) {
            String line = in.nextLine();
            String[] parts = line.split(" ");
            conditionRecords.add(parts[0]);

            String[] groupStrings = parts[1].split(",");
            int[] groups = new int[groupStrings.length];
            for (int i = 0; i < groupStrings.length; i++) {
                groups[i] = Integer.parseInt(groupStrings[i]);
            }
            vals.add(groups);
        }
    }

    private long solveOne(String conditionRecord, int[] groups) {
        int totalSprings = 0;
        for (int group : groups) {
            totalSprings += group;
        }
        int recordLength = conditionRecord.length();
        int wiggle = recordLength - totalSprings - groups.length + 1;
        if (wiggle <= 0) {
            return 0;
        }
        long[] previous = new long[wiggle];
        long[] current = new long[wiggle];

        boolean noHashesToLeft = true;
        long sum = 0;
        int firstGroup = groups[0];

        for (int i = 0; i < wiggle; i++) {
            if (conditionRecord.charAt(i + firstGroup) == '#') {
                sum = 0;
            } else {
                if (noHashesToLeft && (possibleCount[i + firstGroup] - possibleCount[i]) == firstGroup) {
                    sum++;
                }
            }
            previous[i] = sum;
            noHashesToLeft &= (conditionRecord.charAt(i) != '#');
        }

        int start = firstGroup + 1;
        for (int i = 1; i < groups.length; i++) {
            sum = 0;
            int currentGroup = groups[i];

            for (int offset = 0; offset < wiggle; offset++) {
                int j = start + offset;
                if (conditionRecord.charAt(j + currentGroup) == '#') {
                    sum = 0;
                } else {
                    if (previous[offset] > 0 && (conditionRecord.charAt(j - 1) != '#') &&
                            (possibleCount[j + currentGroup] - possibleCount[j]) == currentGroup) {
                        sum += previous[offset];
                    }
                }
                current[offset] = sum;
            }
            long[] swap = previous;
            previous = current;
            current = swap;
            start += currentGroup + 1;
        }
        return sum;
    }

    private int[] precomputePossible(String conditionRecord) {
        int[] counts = new int[conditionRecord.length() + 1];
        int count = 0;
        for (int i = 0; i < conditionRecord.length(); i++) {
            char c = conditionRecord.charAt(i);
            if (c == '#' || c == '?') {
                count++;
            }
            counts[i + 1] = count;
        }
        return counts;
    }
}
