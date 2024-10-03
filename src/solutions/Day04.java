package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day04 implements DayTemplate {

    public String[] fullSolve(Scanner in) {
        long answer1 = 0;
        long answer2 = 0;
        int index = 0;
        Map<Integer, Integer> cards = new HashMap<>();
        List<Integer> matches = new ArrayList<>();
        while (in.hasNext()) {
            matches.add(numMatches(in.nextLine()));
            cards.put(index, 1);
            index++;
        }
        for (Integer card : matches) {
            if (card > 0) {
                answer1 += 1 << (card - 1);
            }
        }
        for (int i = 0; i < cards.size(); i++) {
            for (int j = 0; j < matches.get(i); j++) {
                cards.put(i + j + 1, cards.get(i + j + 1) + cards.get(i));
            }
            answer2 += cards.get(i);
        }
        return new String[]{answer1+"", answer2+""};
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
        int index = 0;
        Map<Integer, Integer> cards = new HashMap<>();
        List<Integer> matches = new ArrayList<>();
        while (in.hasNext()) {
            matches.add(numMatches(in.nextLine()));
            cards.put(index, 1);
            index++;
        }
        if (part1) {
            for (Integer card : matches) {
                if (card > 0) {
                    answer += 1 << (card - 1);
                }
            }
        } else {
            for (int i = 0; i < cards.size(); i++) {
                for (int j = 0; j < matches.get(i); j++) {
                    cards.put(i + j + 1, cards.get(i + j + 1) + cards.get(i));
                }
                answer += cards.get(i);
            }
        }
        return answer + "";
    }

    private int numMatches(String line) {
        int answer = 0;
        String[] winningStrings = line.split("[:\\|]")[1].trim().split("\\s+");
        int[] winningNumbers = new int[winningStrings.length];
        for (int i = 0; i < winningStrings.length; i++) {
            winningNumbers[i] = Integer.parseInt(winningStrings[i]);
        }
        String[] myStrings = line.split("[:\\|]")[2].trim().split("\\s+");
        int[] myNumbers = new int[myStrings.length];
        for (int i = 0; i < myStrings.length; i++) {
            myNumbers[i] = Integer.parseInt(myStrings[i]);
        }
        for(int myNumber: myNumbers){
            for(int winningNumber: winningNumbers){
                if(myNumber == winningNumber){
                    answer+=1;
                    break;
                }
            }
        }
        return answer;
    }
}
