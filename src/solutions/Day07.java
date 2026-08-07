package src.solutions;

import src.meta.DayTemplate;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Scanner;

public class Day07 implements DayTemplate {
    private static final int CARD_COUNT = 13;
    private static final int JOKER_INDEX = 9;
    private static final int RADIX_BITS = 11;
    private static final int RADIX = 1 << RADIX_BITS;
    private static final int RADIX_MASK = RADIX - 1;

    @Override
    public String[] fullSolve(Scanner in) {
        Hands hands = readHands(in);
        return new String[]{score(hands, hands.part1Keys), score(hands, hands.part2Keys)};
    }

    @Override
    public String solve(boolean part1, Scanner in) {
        Hands hands = readHands(in);
        return score(hands, part1 ? hands.part1Keys : hands.part2Keys);
    }

    private static Hands readHands(Scanner in) {
        in.useDelimiter("\\A");
        String input = in.hasNext() ? in.next() : "";
        Hands hands = new Hands();
        int[] frequencies = new int[CARD_COUNT];
        int length = input.length();
        int lineStart = 0;
        while (lineStart < length) {
            int lineEnd = lineStart;
            while (lineEnd < length && input.charAt(lineEnd) != '\n') {
                lineEnd++;
            }
            int trimmedEnd = lineEnd;
            if (trimmedEnd > lineStart && input.charAt(trimmedEnd - 1) == '\r') {
                trimmedEnd--;
            }
            int index = lineStart;
            while (index < trimmedEnd && Character.isWhitespace(input.charAt(index))) {
                index++;
            }
            if (index < trimmedEnd) {
                int cardsStart = index;
                while (index < trimmedEnd && !Character.isWhitespace(input.charAt(index))) {
                    index++;
                }
                if (index - cardsStart != 5) {
                    throw new IllegalArgumentException("Expected five cards");
                }
                hands.ensureCapacity();
                Arrays.fill(frequencies, 0);
                int tie1 = 0;
                int tie2 = 0;
                for (int i = 0; i < 5; i++) {
                    int card = cardIndex(input.charAt(cardsStart + i));
                    frequencies[card]++;
                    tie1 = tie1 * CARD_COUNT + card;
                    int jokerRank = card == JOKER_INDEX ? 0 : card < JOKER_INDEX ? card + 1 : card;
                    tie2 = tie2 * CARD_COUNT + jokerRank;
                }
                int category1 = category(frequencies, -1, 0);
                int jokers = frequencies[JOKER_INDEX];
                int category2 = category(frequencies, JOKER_INDEX, jokers);
                hands.part1Keys[hands.size] = category1 * 371293 + tie1; // 13^5
                hands.part2Keys[hands.size] = category2 * 371293 + tie2;
                while (index < trimmedEnd && Character.isWhitespace(input.charAt(index))) {
                    index++;
                }
                int bidStart = index;
                while (index < trimmedEnd && !Character.isWhitespace(input.charAt(index))) {
                    index++;
                }
                if (bidStart == index) {
                    throw new IllegalArgumentException("Missing bid");
                }
                hands.setBid(input.substring(bidStart, index));
                hands.size++;
            }
            lineStart = lineEnd + 1;
        }
        return hands;
    }

    private static int category(int[] frequencies, int excluded, int jokers) {
        int largest = 0;
        int second = 0;
        for (int i = 0; i < frequencies.length; i++) {
            if (i == excluded) {
                continue;
            }
            int frequency = frequencies[i];
            if (frequency > largest) {
                second = largest;
                largest = frequency;
            } else if (frequency > second) {
                second = frequency;
            }
        }
        largest += jokers;
        return switch (largest) {
            case 5 -> 6;
            case 4 -> 5;
            case 3 -> second == 2 ? 4 : 3;
            case 2 -> second == 2 ? 2 : 1;
            default -> 0;
        };
    }

    private static String score(Hands hands, int[] keys) {
        int size = hands.size;
        int[] order = new int[size];
        int[] scratch = new int[size];
        int[] counts = new int[RADIX];
        for (int i = 0; i < size; i++) {
            order[i] = i;
        }
        int[] source = order;
        int[] target = scratch;
        for (int shift = 0; shift < 2 * RADIX_BITS; shift += RADIX_BITS) {
            Arrays.fill(counts, 0);
            for (int index : source) {
                counts[(keys[index] >>> shift) & RADIX_MASK]++;
            }
            int position = 0;
            for (int i = 0; i < counts.length; i++) {
                int count = counts[i];
                counts[i] = position;
                position += count;
            }
            for (int index : source) {
                target[counts[(keys[index] >>> shift) & RADIX_MASK]++] = index;
            }
            int[] swap = source;
            source = target;
            target = swap;
        }

        long total = 0;
        BigInteger exactTotal = null;
        for (int i = 0; i < size; i++) {
            int index = source[i];
            long rank = i + 1L;
            BigInteger largeBid = hands.largeBids == null ? null : hands.largeBids[index];
            if (largeBid != null) {
                if (exactTotal == null) {
                    exactTotal = BigInteger.valueOf(total);
                }
                exactTotal = exactTotal.add(largeBid.multiply(BigInteger.valueOf(rank)));
            } else if (exactTotal != null) {
                exactTotal = exactTotal.add(BigInteger.valueOf(hands.bids[index]).multiply(BigInteger.valueOf(rank)));
            } else {
                try {
                    total = Math.addExact(total, Math.multiplyExact(hands.bids[index], rank));
                } catch (ArithmeticException overflow) {
                    exactTotal = BigInteger.valueOf(total).add(
                            BigInteger.valueOf(hands.bids[index]).multiply(BigInteger.valueOf(rank)));
                }
            }
        }
        return exactTotal == null ? Long.toString(total) : exactTotal.toString();
    }

    private static int cardIndex(char card) {
        return switch (card) {
            case '2' -> 0;
            case '3' -> 1;
            case '4' -> 2;
            case '5' -> 3;
            case '6' -> 4;
            case '7' -> 5;
            case '8' -> 6;
            case '9' -> 7;
            case 'T' -> 8;
            case 'J' -> JOKER_INDEX;
            case 'Q' -> 10;
            case 'K' -> 11;
            case 'A' -> 12;
            default -> throw new IllegalArgumentException("Unknown card: " + card);
        };
    }

    private static final class Hands {
        private int size;
        private int[] part1Keys = new int[256];
        private int[] part2Keys = new int[256];
        private long[] bids = new long[256];
        private BigInteger[] largeBids;

        private void ensureCapacity() {
            if (size < bids.length) {
                return;
            }
            int capacity = bids.length * 2;
            part1Keys = Arrays.copyOf(part1Keys, capacity);
            part2Keys = Arrays.copyOf(part2Keys, capacity);
            bids = Arrays.copyOf(bids, capacity);
            if (largeBids != null) {
                largeBids = Arrays.copyOf(largeBids, capacity);
            }
        }

        private void setBid(String token) {
            try {
                bids[size] = Long.parseLong(token);
            } catch (NumberFormatException notLong) {
                if (largeBids == null) {
                    largeBids = new BigInteger[bids.length];
                }
                largeBids[size] = new BigInteger(token);
            }
        }
    }
}
