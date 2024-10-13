package src.tests;

import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

import src.meta.BaseTest;
import src.solutions.Day24;

class Day24Test extends BaseTest {

    private static final String DAY = "day24";

    @Test
    void testDay24TestPart1() throws FileNotFoundException {
        Day24 day24 = new Day24();
        Scanner input = getInputScanner(DAY);
        String expectedPart1 = getExpectedSolutions(24)[0]; 

        String result = day24.solve(true, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 1 solve() is not implemented");
        } else {
            assertEquals(expectedPart1, result, "Part 1 solution is incorrect");
        }
    }

    @Test
    void testDay24TestPart2() throws FileNotFoundException {
        Day24 day24 = new Day24();
        Scanner input = getInputScanner(DAY);
        String expectedPart2 = getExpectedSolutions(24)[1]; 

        String result = day24.solve(false, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 2 solve() is not implemented");
        } else {
            assertEquals(expectedPart2, result, "Part 2 solution is incorrect");
        }
    }

    @Test
    void testDay24TestFullSolve() throws FileNotFoundException {
        Day24 day24 = new Day24();
        Scanner input = getInputScanner(DAY);
        String[] expectedSolutions = getExpectedSolutions(24); 

        String[] result = day24.fullSolve(input);
        if (isUnimplementedFullSolve(result)) {
            fail("fullSolve() is not implemented");
        } else {
            assertArrayEquals(expectedSolutions, result, "Full solve solution is incorrect");
        }
    }

}
