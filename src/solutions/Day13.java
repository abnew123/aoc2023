package src.solutions;

import src.meta.DayTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day13 implements DayTemplate {

    List<String> tmp = new ArrayList<>();
    List<Pattern> patterns = new ArrayList<>();

    @Override
    public String[] fullSolve(Scanner in) {
        parse(in);
        long answer1 = 0;
        long answer2 = 0;
        for (Pattern pattern : patterns) {
            answer1 += summarizePattern(pattern, 0);
            answer2 += summarizePattern(pattern, 1);
        }
        return new String[]{answer1 + "", answer2 + ""};
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
        long answer = 0;
        parse(in);
        for (Pattern pattern : patterns) {
            answer += summarizePattern(pattern, part1 ? 0 : 1);
        }
        return answer + "";
    }

    private long summarizePattern(Pattern pattern, int closeness) {
        long summary = 0;
        for (Long reflection : pattern.reflections(closeness)) {
            summary += scoreReflection(reflection);
        }
        return summary;
    }

    private long scoreReflection(Long reflection) {
        if (reflection < 0) {
            return -100 * reflection;
        }
        return reflection;
    }

    private void parse(Scanner in){
        tmp = new ArrayList<>();
        patterns = new ArrayList<>();
        while (in.hasNext()) {
            String line = in.nextLine();
            if (line.isEmpty()) {
                patterns.add(new Pattern(tmp));
                tmp = new ArrayList<>();
            } else {
                tmp.add(line);
            }
        }
        patterns.add(new Pattern(tmp));
    }

}

class Pattern {
    int[][] grid;
    int rows;
    int cols;

    public Pattern(List<String> tmp) {
        rows = tmp.size();
        cols = tmp.get(0).length();
        grid = new int[cols][rows];
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                grid[i][j] = tmp.get(j).charAt(i) == '.' ? 0 : 1;
            }
        }
    }

    public List<Long> reflections(int closeness) {
        List<Long> ret = new ArrayList<>();
        ret.addAll(vert(closeness));
        for(Long h: horiz(closeness)){
            ret.add(-1 * h);
        }
        return ret;
    }

    private List<Long> vert(int closeness) {
        List<Long> ret = new ArrayList<>();
        for (int i = 1; i < cols; i++) {
            if(vertHelper(i, closeness)){
                ret.add((long) i);
            }
        }
        return ret;
    }

    private boolean vertHelper(int i, int closeness){
        boolean oneOff = false;
        for (int j = 0; j < rows; j++) {
            boolean firstHalf = i <= cols / 2;
            int column = firstHalf ? 1 : cols;
            while(column != (firstHalf ? i + 1 : i)){
                if (grid[column - 1][j] != grid[(2 * i + 1) - column - 1][j]) {
                    if(closeness == 0 || oneOff){
                        return false;
                    }
                    oneOff = true;
                }
                column += firstHalf ? 1 : -1;
            }
        }
        return closeness == 0 || oneOff;
    }

    private List<Long> horiz(int closeness) {
        List<Long> ret = new ArrayList<>();
        for (int i = 1; i < rows; i++) {
            if(horizHelper(i, closeness)){
                ret.add((long) i);
            }
        }
        return ret;
    }

    private boolean horizHelper(int i, int closeness){
        boolean oneOff = false;
        for (int j = 0; j < cols; j++) {
            boolean firstHalf = i <= rows / 2;
            int row = firstHalf ? 1 : rows;
            while(row != (firstHalf ? i + 1 : i)){
                if (grid[j][row - 1] != grid[j][(2 * i + 1) - row - 1]) {
                    if(closeness == 0 || oneOff){
                        return false;
                    }
                    oneOff = true;
                }
                row += firstHalf ? 1 : -1;
            }
        }
        return closeness == 0 || oneOff;
    }
}
