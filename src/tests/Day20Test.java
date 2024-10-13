package src.tests;

import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

import src.meta.BaseTest;
import src.solutions.Day20;

class Day20Test extends BaseTest {

    private static final String DAY = "day20";

    @Test
    void testDay20TestPart1() throws FileNotFoundException {
        Day20 day20 = new Day20();
        Scanner input = getInputScanner(DAY);
        String expectedPart1 = getExpectedSolutions(20)[0]; 

        String result = day20.solve(true, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 1 solve() is not implemented");
        } else {
            assertEquals(expectedPart1, result, "Part 1 solution is incorrect");
        }
    }

    @Test
    void testDay20TestPart2() throws FileNotFoundException {
        Day20 day20 = new Day20();
        Scanner input = getInputScanner(DAY);
        String expectedPart2 = getExpectedSolutions(20)[1]; 

        String result = day20.solve(false, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 2 solve() is not implemented");
        } else {
            assertEquals(expectedPart2, result, "Part 2 solution is incorrect");
        }
    }

    @Test
    void testDay20TestFullSolve() throws FileNotFoundException {
        Day20 day20 = new Day20();
        Scanner input = getInputScanner(DAY);
        String[] expectedSolutions = getExpectedSolutions(20); 

        String[] result = day20.fullSolve(input);
        if (isUnimplementedFullSolve(result)) {
            fail("fullSolve() is not implemented");
        } else {
            assertArrayEquals(expectedSolutions, result, "Full solve solution is incorrect");
        }
    }

}
