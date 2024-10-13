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

}
