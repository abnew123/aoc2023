package src.tests;

import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

import src.meta.BaseTest;
import src.solutions.Day04;

class Day04Test extends BaseTest {

    private static final String DAY = "day04";

    @Test
    void testDay04TestPart1() throws FileNotFoundException {
        Day04 day04 = new Day04();
        Scanner input = getInputScanner(DAY);
        String expectedPart1 = getExpectedSolutions(4)[0]; 

        String result = day04.solve(true, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 1 solve() is not implemented");
        } else {
            assertEquals(expectedPart1, result, "Part 1 solution is incorrect");
        }
    }

    @Test
    void testDay04TestPart2() throws FileNotFoundException {
        Day04 day04 = new Day04();
        Scanner input = getInputScanner(DAY);
        String expectedPart2 = getExpectedSolutions(4)[1]; 

        String result = day04.solve(false, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 2 solve() is not implemented");
        } else {
            assertEquals(expectedPart2, result, "Part 2 solution is incorrect");
        }
    }

    @Test
    void testDay04TestFullSolve() throws FileNotFoundException {
        Day04 day04 = new Day04();
        Scanner input = getInputScanner(DAY);
        String[] expectedSolutions = getExpectedSolutions(4); 

        String[] result = day04.fullSolve(input);
        if (isUnimplementedFullSolve(result)) {
            fail("fullSolve() is not implemented");
        } else {
            assertArrayEquals(expectedSolutions, result, "Full solve solution is incorrect");
        }
    }

}