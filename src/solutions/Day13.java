package src.solutions;

import src.meta.DayTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day13 implements DayTemplate {

    List<String> tmp = new ArrayList<>();
    List<Pattern> patterns = new ArrayList<>();


    
    public String solve(boolean part1, Scanner in) {
        long ans = 0;
        parse(in);
        for (Pattern pattern : patterns) {
            List<Long> vals = pattern.reflections(part1 ? 0 : 1);
            for (Long val : vals) {
                if(val < 0){
                    ans+= -100 * val;
                }
                else{
                    ans += val;
                }
            }
        }
        return ans + "";
    }

    void parse(Scanner in){
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

    List<Long> vert(int closeness) {
        List<Long> ret = new ArrayList<>();
        for (int i = 1; i < cols; i++) {
            if(vertHelper(i, closeness)){
                ret.add((long) i);
            }
        }
        return ret;
    }

    boolean vertHelper(int i, int closeness){
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

    List<Long> horiz(int closeness) {
        List<Long> ret = new ArrayList<>();
        for (int i = 1; i < rows; i++) {
            if(horizHelper(i, closeness)){
                ret.add((long) i);
            }
        }
        return ret;
    }

    boolean horizHelper(int i, int closeness){
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