package src.tests;

import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

import src.meta.BaseTest;
import src.solutions.Day05;

class Day05Test extends BaseTest {

    private static final String DAY = "day05";

    @Test
    void testDay05TestPart1() throws FileNotFoundException {
        Day05 day05 = new Day05();
        Scanner input = getInputScanner(DAY);
        String expectedPart1 = getExpectedSolutions(5)[0]; 

        String result = day05.solve(true, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 1 solve() is not implemented");
        } else {
            assertEquals(expectedPart1, result, "Part 1 solution is incorrect");
        }
    }

    @Test
    void testDay05TestPart2() throws FileNotFoundException {
        Day05 day05 = new Day05();
        Scanner input = getInputScanner(DAY);
        String expectedPart2 = getExpectedSolutions(5)[1]; 

        String result = day05.solve(false, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 2 solve() is not implemented");
        } else {
            assertEquals(expectedPart2, result, "Part 2 solution is incorrect");
        }
    }

    @Test
    void testDay05TestFullSolve() throws FileNotFoundException {
        Day05 day05 = new Day05();
        Scanner input = getInputScanner(DAY);
        String[] expectedSolutions = getExpectedSolutions(5); 

        String[] result = day05.fullSolve(input);
        if (isUnimplementedFullSolve(result)) {
            fail("fullSolve() is not implemented");
        } else {
            assertArrayEquals(expectedSolutions, result, "Full solve solution is incorrect");
        }
    }

}
