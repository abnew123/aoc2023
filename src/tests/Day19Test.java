package src.tests;

import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

import src.meta.BaseTest;
import src.solutions.Day19;

class Day19Test extends BaseTest {

    private static final String DAY = "day19";

    @Test
    void testDay19TestPart1() throws FileNotFoundException {
        Day19 day19 = new Day19();
        Scanner input = getInputScanner(DAY);
        String expectedPart1 = getExpectedSolutions(19)[0]; 

        String result = day19.solve(true, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 1 solve() is not implemented");
        } else {
            assertEquals(expectedPart1, result, "Part 1 solution is incorrect");
        }
    }

    @Test
    void testDay19TestPart2() throws FileNotFoundException {
        Day19 day19 = new Day19();
        Scanner input = getInputScanner(DAY);
        String expectedPart2 = getExpectedSolutions(19)[1]; 

        String result = day19.solve(false, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 2 solve() is not implemented");
        } else {
            assertEquals(expectedPart2, result, "Part 2 solution is incorrect");
        }
    }

    @Test
    void testDay19TestFullSolve() throws FileNotFoundException {
        Day19 day19 = new Day19();
        Scanner input = getInputScanner(DAY);
        String[] expectedSolutions = getExpectedSolutions(19); 

        String[] result = day19.fullSolve(input);
        if (isUnimplementedFullSolve(result)) {
            fail("fullSolve() is not implemented");
        } else {
            assertArrayEquals(expectedSolutions, result, "Full solve solution is incorrect");
        }
    }

}
