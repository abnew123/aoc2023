package src.solutions;

import src.meta.DayTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day13 implements DayTemplate {

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
        List<String> tmp = new ArrayList<>();
        List<Pattern> patterns = new ArrayList<>();
        while (in.hasNext()) {
            String line = in.nextLine();
            if (line.equals("")) {
                patterns.add(new Pattern(tmp));
                tmp = new ArrayList<>();
            } else {
                tmp.add(line);
            }
        }
        patterns.add(new Pattern(tmp));
        for (Pattern pattern : patterns) {
            List<Long> vals = pattern.reflections(part1 ? 0 : 1);
            for (Long val : vals) {
                if(val < 0){
                    answer+= -100 * val;
                }
                else{
                    answer += val;
                }
            }
        }
        return answer + "";
    }

}

class Pattern {
    int[][] grid;

    public Pattern(List<String> tmp) {
        grid = new int[tmp.get(0).length()][tmp.size()];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
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
        for (int i = 1; i < grid.length; i++) {
            if(vertHelper(i, closeness)){
                ret.add((long) i);
            }
        }
        return ret;
    }

    private boolean vertHelper(int i, int closeness){
        boolean oneOff = false;
        for (int j = 0; j < grid[0].length; j++) {
            boolean firstHalf = i <= grid.length / 2;
            int column = firstHalf?1:grid.length;
            while(column != (firstHalf?i+1:i)){
                if (grid[column - 1][j] != grid[(2 * i + 1) - column - 1][j]) {
                    if(closeness == 0 || oneOff){
                        return false;
                    }
                    oneOff = true;
                }
                column+= firstHalf?1:-1;
            }
        }
        return closeness == 0 || oneOff;
    }

    private List<Long> horiz(int closeness) {
        List<Long> ret = new ArrayList<>();
        for (int i = 1; i < grid[0].length; i++) {
            if(horizHelper(i, closeness)){
                ret.add((long) i);
            }
        }
        return ret;
    }

    private boolean horizHelper(int i, int closeness){
        boolean oneOff = false;
        for (int j = 0; j < grid.length; j++) {
            boolean firstHalf = i <= grid[0].length / 2;
            int row = firstHalf?1:grid[0].length;
            while(row != (firstHalf?i+1:i)){
                if (grid[j][row - 1] != grid[j][(2 * i + 1) - row - 1]) {
                    if(closeness == 0 || oneOff){
                        return false;
                    }
                    oneOff = true;
                }
                row+= firstHalf?1:-1;
            }
        }
        return closeness == 0 || oneOff;
    }
}