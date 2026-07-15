package src.tests;

import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

import src.meta.BaseTest;
import src.solutions.Day09;

class Day09Test extends BaseTest {

    private static final String DAY = "day09";

    @Test
    void testDay09TestPart1() throws FileNotFoundException {
        Day09 day09 = new Day09();
        Scanner input = getInputScanner(DAY);
        String expectedPart1 = getExpectedSolutions(9)[0]; 

        String result = day09.solve(true, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 1 solve() is not implemented");
        } else {
            assertEquals(expectedPart1, result, "Part 1 solution is incorrect");
        }
    }

    @Test
    void testDay09TestPart2() throws FileNotFoundException {
        Day09 day09 = new Day09();
        Scanner input = getInputScanner(DAY);
        String expectedPart2 = getExpectedSolutions(9)[1]; 

        String result = day09.solve(false, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 2 solve() is not implemented");
        } else {
            assertEquals(expectedPart2, result, "Part 2 solution is incorrect");
        }
    }

    @Test
    void testDay09TestFullSolve() throws FileNotFoundException {
        Day09 day09 = new Day09();
        Scanner input = getInputScanner(DAY);
        String[] expectedSolutions = getExpectedSolutions(9); 

        String[] result = day09.fullSolve(input);
        if (isUnimplementedFullSolve(result)) {
            fail("fullSolve() is not implemented");
        } else {
            assertArrayEquals(expectedSolutions, result, "Full solve solution is incorrect");
        }
    }

    @Test
    void testOfficialExample() {
        String input = """
                0 3 6 9 12 15
                1 3 6 10 15 21
                10 13 16 21 30 45
                """;
        assertArrayEquals(new String[]{"114", "2"},
                new Day09().fullSolve(new Scanner(input)));
    }

    @Test
    void testExactFallbackAndFlexibleWhitespace() {
        String input = " \t9223372036854775808  9223372036854775809\r\n"
                + "-9223372036854775808\t9223372036854775807";
        assertArrayEquals(new String[]{"36893488147419103232", "-18446744073709551616"},
                new Day09().fullSolve(new Scanner(input)));
    }

    @Test
    void testEmptyAndSingletonHistories() {
        assertArrayEquals(new String[]{"0", "0"},
                new Day09().fullSolve(new Scanner(" \n\t\n")));
        assertArrayEquals(new String[]{"5", "5"},
                new Day09().fullSolve(new Scanner("+5")));
    }

}
