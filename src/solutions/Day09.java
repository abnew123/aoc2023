package src.solutions;

import src.meta.DayTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Day09 implements DayTemplate {

    @Override
    public String[] fullSolve(Scanner in) {
        long answer1 = 0;
        long answer2 = 0;
        List<List<Integer>> sequences = parse(in);
        for(List<Integer> sequence: sequences){
            answer1 += helper(sequence);
            Collections.reverse(sequence);
            answer2 += helper(sequence);
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
        List<List<Integer>> sequences = parse(in);
        for(List<Integer> sequence: sequences){
            if (!part1) {
                Collections.reverse(sequence);
            }
            answer += helper(sequence);
        }
        return answer + "";
    }

    private List<List<Integer>> parse(Scanner in){
        List<List<Integer>> sequences = new ArrayList<>();
        while (in.hasNext()) {
            String line = in.nextLine();
            List<Integer> sequence = new ArrayList<>();
            for (String item : line.split(" ")) {
                sequence.add(Integer.parseInt(item));
            }
            sequences.add(sequence);
        }
        return sequences;
    }

    private int helper(List<Integer> sequence) {
        if (checker(sequence)) {
            return sequence.get(0);
        } else {
            List<Integer> tmp = new ArrayList<>();
            for (int i = 0; i < sequence.size() - 1; i++) {
                tmp.add(sequence.get(i + 1) - sequence.get(i));
            }
            return sequence.get(sequence.size() - 1) + helper(tmp);
        }
    }

    private boolean checker(List<Integer> sequence) {
        int first = sequence.get(0);
        for (Integer i : sequence) {
            if (i != first) {
                return false;
            }
        }
        return true;
    }
}