package src.tests;

import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

import src.meta.BaseTest;
import src.solutions.Day18;

class Day18Test extends BaseTest {

    private static final String DAY = "day18";

    @Test
    void testDay18TestPart1() throws FileNotFoundException {
        Day18 day18 = new Day18();
        Scanner input = getInputScanner(DAY);
        String expectedPart1 = getExpectedSolutions(18)[0]; 

        String result = day18.solve(true, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 1 solve() is not implemented");
        } else {
            assertEquals(expectedPart1, result, "Part 1 solution is incorrect");
        }
    }

    @Test
    void testDay18TestPart2() throws FileNotFoundException {
        Day18 day18 = new Day18();
        Scanner input = getInputScanner(DAY);
        String expectedPart2 = getExpectedSolutions(18)[1]; 

        String result = day18.solve(false, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 2 solve() is not implemented");
        } else {
            assertEquals(expectedPart2, result, "Part 2 solution is incorrect");
        }
    }

    @Test
    void testDay18TestFullSolve() throws FileNotFoundException {
        Day18 day18 = new Day18();
        Scanner input = getInputScanner(DAY);
        String[] expectedSolutions = getExpectedSolutions(18); 

        String[] result = day18.fullSolve(input);
        if (isUnimplementedFullSolve(result)) {
            fail("fullSolve() is not implemented");
        } else {
            assertArrayEquals(expectedSolutions, result, "Full solve solution is incorrect");
        }
    }

}