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
        while (in.hasNextLine()) {
            String line = in.nextLine();
            if (line.isBlank()) {
                continue;
            }
            StringTokenizer tokens = new StringTokenizer(line);
            String cards = tokens.nextToken();
            int bid = Integer.parseInt(tokens.nextToken());
            hands1.add(new Hand(cards, bid, true));
            hands2.add(new Hand(cards, bid, false));
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
        while (in.hasNextLine()) {
            String line = in.nextLine();
            if (line.isBlank()) {
                continue;
            }
            StringTokenizer tokens = new StringTokenizer(line);
            hands.add(new Hand(tokens.nextToken(), Integer.parseInt(tokens.nextToken()), part1));
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
    int[] cards = new int[5];

    public Hand(String line, int bid, boolean part1) {
        this.bid = bid;
        int[] frequencies = new int[13];
        int numJokers = 0;
        for (int i = 0; i < cards.length; i++) {
            char card = line.charAt(i);
            int rank = rank(card, part1);
            cards[i] = rank;
            if (card == 'J' && !part1) {
                numJokers++;
            } else {
                frequencies[rank]++;
            }
        }

        int largest = 0;
        int secondLargest = 0;
        for (int frequency : frequencies) {
            if (frequency > largest) {
                secondLargest = largest;
                largest = frequency;
            } else if (frequency > secondLargest) {
                secondLargest = frequency;
            }
        }
        largest += numJokers;
        strength = 2 * largest;
        if (secondLargest == 2) {
            strength += 1; //for full house and two pair
        }
    }

    private int rank(char card, boolean part1) {
        return switch (card) {
            case 'A' -> 0;
            case 'K' -> 1;
            case 'Q' -> 2;
            case 'J' -> part1 ? 3 : 12;
            case 'T' -> part1 ? 4 : 3;
            case '9' -> part1 ? 5 : 4;
            case '8' -> part1 ? 6 : 5;
            case '7' -> part1 ? 7 : 6;
            case '6' -> part1 ? 8 : 7;
            case '5' -> part1 ? 9 : 8;
            case '4' -> part1 ? 10 : 9;
            case '3' -> part1 ? 11 : 10;
            case '2' -> part1 ? 12 : 11;
            default -> throw new IllegalArgumentException("Unknown card: " + card);
        };
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
