package src.tests;

import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

import src.meta.BaseTest;
import src.solutions.Day03;

class Day03Test extends BaseTest {

    private static final String DAY = "day03";

    @Test
    void testDay03TestPart1() throws FileNotFoundException {
        Day03 day03 = new Day03();
        Scanner input = getInputScanner(DAY);
        String expectedPart1 = getExpectedSolutions(3)[0]; 

        String result = day03.solve(true, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 1 solve() is not implemented");
        } else {
            assertEquals(expectedPart1, result, "Part 1 solution is incorrect");
        }
    }

    @Test
    void testDay03TestPart2() throws FileNotFoundException {
        Day03 day03 = new Day03();
        Scanner input = getInputScanner(DAY);
        String expectedPart2 = getExpectedSolutions(3)[1]; 

        String result = day03.solve(false, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 2 solve() is not implemented");
        } else {
            assertEquals(expectedPart2, result, "Part 2 solution is incorrect");
        }
    }

    @Test
    void testDay03TestFullSolve() throws FileNotFoundException {
        Day03 day03 = new Day03();
        Scanner input = getInputScanner(DAY);
        String[] expectedSolutions = getExpectedSolutions(3); 

        String[] result = day03.fullSolve(input);
        if (isUnimplementedFullSolve(result)) {
            fail("fullSolve() is not implemented");
        } else {
            assertArrayEquals(expectedSolutions, result, "Full solve solution is incorrect");
        }
    }

    @Test
    void testOfficialExample() {
        String input = """
                467..114..
                ...*......
                ..35..633.
                ......#...
                617*......
                .....+.58.
                ..592.....
                ......755.
                ...$.*....
                .664.598..
                """;
        assertArrayEquals(new String[]{"4361", "467835"},
                new Day03().fullSolve(new Scanner(input)));
    }

    @Test
    void testDistinctNumberAndSymbolSemantics() {
        assertArrayEquals(new String[]{"123", "0"},
                new Day03().fullSolve(new Scanner(".*.\n123\n.+.")));
        assertArrayEquals(new String[]{"12", "0"},
                new Day03().fullSolve(new Scanner("12\n.*")));
    }

    @Test
    void testArbitraryPrecisionGearRatio() {
        assertArrayEquals(new String[]{"1000000000000000000000001", "1999999999999999999999998"},
                new Day03().fullSolve(new Scanner("999999999999999999999999*2")));
    }

}
