package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day03 implements DayTemplate {

    List<String> lines = new ArrayList<>();
    Map<Integer, List<SchematicNum>> nums = new HashMap<>();
    Map<Integer, List<Integer>> gears = new HashMap<>();
    Map<Integer, List<Integer>> symbols = new HashMap<>();

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
        int index = initialize(in);
        if (part1) {
            answer = part1(index);
        } else {
            answer = part2(index);
        }
        return answer + "";
    }

    private boolean matches(SchematicNum num, int coord) {
        return (coord >= num.start - 1 && coord <= num.end + 1);
    }

    private List<SchematicNum> addNums(String line) {
        List<SchematicNum> tmp = new ArrayList<>();
        int index = 0;
        while (index < line.length()) {
            if (Character.isDigit(line.charAt(index))) {
                int start = index;
                while (index < line.length() - 1 && Character.isDigit(line.charAt(index + 1))) {
                    index++;
                }
                tmp.add(new SchematicNum(start, index));
            }
            index++;
        }
        return tmp;
    }

    private List<Integer> addSymbols(String line, boolean justGears) {
        List<Integer> tmp = new ArrayList<>();
        for (int i = 0; i < line.length(); i++) {
            if (!Character.isDigit(line.charAt(i)) && line.charAt(i) != '.') {
                if (line.charAt(i) != '*' && justGears) {
                    continue;
                }
                tmp.add(i);
            }
        }
        return tmp;
    }

    private int initialize(Scanner in) {
        int index = 0;
        while (in.hasNext()) {
            String line = in.nextLine();
            lines.add(line);
            nums.put(index, addNums(line));
            gears.put(index, addSymbols(line, true));
            symbols.put(index, addSymbols(line, false));
            index++;
        }
        return index;
    }

    private long part1(int index) {
        long answer = 0;
        for (int i = 0; i < index; i++) {
            for (SchematicNum num : nums.get(i)) {
                for (int j = Math.max(0, i - 1); j <= Math.min(i + 1, index - 1); j++) {
                    for (Integer symbol : symbols.get(j)) {
                        if (matches(num, symbol)) {
                            answer += Integer.parseInt(lines.get(i).substring(num.start, num.end + 1));
                        }
                    }
                }
            }
        }
        return answer;
    }

    private long part2(int index) {
        long answer = 0;
        for (int i = 0; i < index; i++) {
            for (Integer gear : gears.get(i)) {
                int gearVal = 1;
                int nearNums = 0;
                for (int j = Math.max(0, i - 1); j <= Math.min(i + 1, index - 1); j++) {
                    for (SchematicNum num : nums.get(j)) {
                        if (matches(num, gear)) {
                            gearVal *= Integer.parseInt(lines.get(j).substring(num.start, num.end + 1));
                            nearNums++;
                        }
                    }
                }
                if (nearNums == 2) {
                    answer += gearVal;
                }
            }
        }
        return answer;
    }

}

class SchematicNum {
    int start;
    int end;

    public SchematicNum(int s, int e) {
        start = s;
        end = e;
    }
}