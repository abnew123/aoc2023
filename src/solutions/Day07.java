package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day07 implements DayTemplate {

    @Override
    public String[] fullSolve(Scanner in) {
        long answer1 = 0;
        long answer2 = 0;
        List<Hand> hands1 = new ArrayList<>();
        List<Hand> hands2 = new ArrayList<>();
        while (in.hasNext()) {
            String line = in.nextLine();
            hands1.add(new Hand(line.split(" ")[0], Integer.parseInt(line.split(" ")[1]), true));
            hands2.add(new Hand(line.split(" ")[0], Integer.parseInt(line.split(" ")[1]), false));
        }
        Collections.sort(hands1);
        Collections.sort(hands2);
        for (int i = 0; i < hands1.size(); i++) {
            answer1 += (long) hands1.get(i).bid * (i + 1);
            answer2 += (long) hands2.get(i).bid * (i + 1);
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
        List<Hand> hands = new ArrayList<>();
        while (in.hasNext()) {
            String line = in.nextLine();
            hands.add(new Hand(line.split(" ")[0], Integer.parseInt(line.split(" ")[1]), part1));
        }
        Collections.sort(hands);
        for (int i = 0; i < hands.size(); i++) {
            answer += (long) hands.get(i).bid * (i + 1);
        }
        return answer + "";
    }
}

class Hand implements Comparable<Hand> {
    int bid;
    int strength;
    int[] freqs = new int[13];
    int[] cards = new int[5];
    String[] ranks = new String[]{"A", "K", "Q", "J", "T", "9", "8", "7", "6", "5", "4", "3", "2"};

    public Hand(String line, int bid, boolean part1) {
        if (!part1) {
            ranks = new String[]{"A", "K", "Q", "T", "9", "8", "7", "6", "5", "4", "3", "2", "J"};
        }
        this.bid = bid;
        int numJokers = 0;
        String[] s = line.split("");
        for (int i = 0; i < s.length; i++) {
            for (int j = 0; j < ranks.length; j++) {
                if (s[i].equals(ranks[j])) {
                    if (!s[i].equals("J") || part1) {
                        freqs[j]++;
                    }
                    if (s[i].equals("J") && !part1) {
                        numJokers++;
                    }
                    cards[i] = j;
                }
            }
        }
        Arrays.sort(freqs);
        freqs[freqs.length - 1] += numJokers;
        strength = 2 * freqs[freqs.length - 1];
        if (freqs[freqs.length - 2] == 2) {
            strength += 1; //for full house and two pair
        }
    }

    @Override
    public int compareTo(Hand o) {
        if (strength != o.strength) {
            return strength - o.strength;
        } else {
            for (int i = 0; i < cards.length; i++) {
                if (cards[i] != o.cards[i]) {
                    return o.cards[i] - cards[i];
                }
            }
            return 0;
        }
    }

    @Override
    public boolean equals(Object other){
        if(other instanceof Hand o){
            if (strength != o.strength) {
                return false;
            } else {
                for (int i = 0; i < cards.length; i++) {
                    if (cards[i] != o.cards[i]) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode(){
        return strength;
    }
}