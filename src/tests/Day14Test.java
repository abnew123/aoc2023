package src.tests;

import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

import src.meta.BaseTest;
import src.solutions.Day14;

class Day14Test extends BaseTest {

    private static final String DAY = "day14";

    @Test
    void testDay14TestPart1() throws FileNotFoundException {
        Day14 day14 = new Day14();
        Scanner input = getInputScanner(DAY);
        String expectedPart1 = getExpectedSolutions(14)[0]; 

        String result = day14.solve(true, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 1 solve() is not implemented");
        } else {
            assertEquals(expectedPart1, result, "Part 1 solution is incorrect");
        }
    }

    @Test
    void testDay14TestPart2() throws FileNotFoundException {
        Day14 day14 = new Day14();
        Scanner input = getInputScanner(DAY);
        String expectedPart2 = getExpectedSolutions(14)[1]; 

        String result = day14.solve(false, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 2 solve() is not implemented");
        } else {
            assertEquals(expectedPart2, result, "Part 2 solution is incorrect");
        }
    }

    @Test
    void testDay14TestFullSolve() throws FileNotFoundException {
        Day14 day14 = new Day14();
        Scanner input = getInputScanner(DAY);
        String[] expectedSolutions = getExpectedSolutions(14); 

        String[] result = day14.fullSolve(input);
        if (isUnimplementedFullSolve(result)) {
            fail("fullSolve() is not implemented");
        } else {
            assertArrayEquals(expectedSolutions, result, "Full solve solution is incorrect");
        }
    }

}
