package src.tests;

import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

import src.meta.BaseTest;
import src.solutions.Day25;

class Day25Test extends BaseTest {

    private static final String DAY = "day25";

    @Test
    void testDay25TestPart1() throws FileNotFoundException {
        Day25 day25 = new Day25();
        Scanner input = getInputScanner(DAY);
        String expectedPart1 = getExpectedSolutions(25)[0]; 

        String result = day25.solve(true, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 1 solve() is not implemented");
        } else {
            assertEquals(expectedPart1, result, "Part 1 solution is incorrect");
        }
    }

    @Test
    void testDay25TestPart2() throws FileNotFoundException {
        Day25 day25 = new Day25();
        Scanner input = getInputScanner(DAY);
        String expectedPart2 = getExpectedSolutions(25)[1]; 

        String result = day25.solve(false, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 2 solve() is not implemented");
        } else {
            assertEquals(expectedPart2, result, "Part 2 solution is incorrect");
        }
    }

    @Test
    void testDay25TestFullSolve() throws FileNotFoundException {
        Day25 day25 = new Day25();
        Scanner input = getInputScanner(DAY);
        String[] expectedSolutions = getExpectedSolutions(25); 

        String[] result = day25.fullSolve(input);
        if (isUnimplementedFullSolve(result)) {
            fail("fullSolve() is not implemented");
        } else {
            assertArrayEquals(expectedSolutions, result, "Full solve solution is incorrect");
        }
    }

}
