package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day04 implements DayTemplate {


    
    public String solve(boolean part1, Scanner in) {
        long ans = 0;
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
                    ans += 1 << (card - 1);
                }
            }
        } else {
            for (int i = 0; i < cards.size(); i++) {
                for (int j = 0; j < matches.get(i); j++) {
                    cards.put(i + j + 1, cards.get(i + j + 1) + cards.get(i));
                }
                ans += cards.get(i);
            }
        }
        return ans + "";
    }

    int numMatches(String line) {
        int ans = 0;
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
                    ans+=1;
                    break;
                }
            }
        }
        return ans;
    }
}
