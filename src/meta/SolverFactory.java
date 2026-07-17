package src.meta;

import src.solutions.*;

public final class SolverFactory {
    private SolverFactory() {
    }

    public static DayTemplate create(int day) {
        return switch (day) {
            case 1 -> new Day01();
            case 2 -> new Day02();
            case 3 -> new Day03();
            case 4 -> new Day04();
            case 5 -> new Day05();
            case 6 -> new Day06();
            case 7 -> new Day07();
            case 8 -> new Day08();
            case 9 -> new Day09();
            case 10 -> new Day10();
            case 11 -> new Day11();
            case 12 -> new Day12();
            case 13 -> new Day13();
            case 14 -> new Day14();
            case 15 -> new Day15();
            case 16 -> new Day16();
            case 17 -> new Day17();
            case 18 -> new Day18();
            case 19 -> new Day19();
            case 20 -> new Day20();
            case 21 -> new Day21();
            case 22 -> new Day22();
            case 23 -> new Day23();
            case 24 -> new Day24();
            case 25 -> new Day25();
            default -> throw new IllegalArgumentException("Unsupported day " + day);
        };
    }
}
