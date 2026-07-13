package src.solutions;

import src.meta.DayTemplate;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Day02 implements DayTemplate {

    static final String RED = "red";
    static final String GREEN = "green";
    static final String BLUE = "blue";

    @Override
    public String[] fullSolve(Scanner in) {
        return analyze(in);
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
        String[] answers = analyze(in);
        return answers[part1 ? 0 : 1];
    }

    private String[] analyze(Scanner in) {
        BigInteger possibleIdSum = BigInteger.ZERO;
        BigInteger powerSum = BigInteger.ZERO;
        while (in.hasNextLine()) {
            Game game = parseGame(in.nextLine());
            if (game.red.compareTo(BigInteger.valueOf(12)) <= 0
                    && game.green.compareTo(BigInteger.valueOf(13)) <= 0
                    && game.blue.compareTo(BigInteger.valueOf(14)) <= 0) {
                possibleIdSum = possibleIdSum.add(game.id);
            }
            powerSum = powerSum.add(game.red.multiply(game.green).multiply(game.blue));
        }
        return new String[]{possibleIdSum.toString(), powerSum.toString()};
    }

    private Game parseGame(String line) {
        int index = skipWhitespace(line, 0);
        if (!line.startsWith("Game", index)) {
            throw new IllegalArgumentException("Expected Game prefix");
        }
        index = skipWhitespace(line, index + 4);
        Number id = parseNumber(line, index);
        index = skipWhitespace(line, id.end);
        if (index >= line.length() || line.charAt(index++) != ':') {
            throw new IllegalArgumentException("Expected ':' after game ID");
        }

        BigInteger red = BigInteger.ZERO;
        BigInteger green = BigInteger.ZERO;
        BigInteger blue = BigInteger.ZERO;
        while (true) {
            index = skipWhitespace(line, index);
            if (index == line.length()) {
                return new Game(id.value, red, green, blue);
            }
            char separator = line.charAt(index);
            if (separator == ',' || separator == ';') {
                index = skipWhitespace(line, index + 1);
            }
            Number count = parseNumber(line, index);
            index = skipWhitespace(line, count.end);
            int colorStart = index;
            while (index < line.length() && Character.isLetter(line.charAt(index))) {
                index++;
            }
            if (colorStart == index) {
                throw new IllegalArgumentException("Expected cube color");
            }
            String color = line.substring(colorStart, index);
            switch (color) {
                case RED -> red = red.max(count.value);
                case GREEN -> green = green.max(count.value);
                case BLUE -> blue = blue.max(count.value);
                default -> throw new IllegalArgumentException("Unknown cube color: " + color);
            }
            index = skipWhitespace(line, index);
            if (index < line.length() && line.charAt(index) != ',' && line.charAt(index) != ';') {
                throw new IllegalArgumentException("Expected draw separator");
            }
        }
    }

    private int skipWhitespace(String line, int index) {
        while (index < line.length() && Character.isWhitespace(line.charAt(index))) {
            index++;
        }
        return index;
    }

    private Number parseNumber(String line, int index) {
        int start = index;
        while (index < line.length() && line.charAt(index) >= '0' && line.charAt(index) <= '9') {
            index++;
        }
        if (start == index) {
            throw new IllegalArgumentException("Expected nonnegative integer");
        }
        return new Number(new BigInteger(line.substring(start, index)), index);
    }

    public Map<String, Integer> computeTotals(String[] parts){
        Map<String, Integer> totals = new HashMap<>();
        for (int i = 1; i < parts.length; i++) {
            String[] draws = parts[i].split(",");
            for (String draw : draws) {
                draw = draw.trim();
                String color = draw.split(" ")[1];
                int number = Integer.parseInt(draw.split(" ")[0]);
                totals.putIfAbsent(color, 0);
                totals.put(color, Math.max(totals.get(color), number));
            }
        }
        return totals;
    }

    private int part1Add(Map<String, Integer> totals, int index){
        if (totals.getOrDefault(RED, 0) <= 12 && totals.getOrDefault(GREEN, 0) <= 13
                && totals.getOrDefault(BLUE, 0) <= 14) {
            return index;
        }
        return 0;
    }

    private int part2Add(Map<String, Integer> totals){
        return totals.getOrDefault(RED, 0) * totals.getOrDefault(GREEN, 0)
                * totals.getOrDefault(BLUE, 0);
    }

    private record Game(BigInteger id, BigInteger red, BigInteger green, BigInteger blue) {}

    private record Number(BigInteger value, int end) {}
}
