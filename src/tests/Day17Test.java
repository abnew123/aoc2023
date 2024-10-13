package src.tests;

import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

import src.meta.BaseTest;
import src.solutions.Day17;

class Day17Test extends BaseTest {

    private static final String DAY = "day17";

    @Test
    void testDay17TestPart1() throws FileNotFoundException {
        Day17 day17 = new Day17();
        Scanner input = getInputScanner(DAY);
        String expectedPart1 = getExpectedSolutions(17)[0]; 

        String result = day17.solve(true, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 1 solve() is not implemented");
        } else {
            assertEquals(expectedPart1, result, "Part 1 solution is incorrect");
        }
    }

    @Test
    void testDay17TestPart2() throws FileNotFoundException {
        Day17 day17 = new Day17();
        Scanner input = getInputScanner(DAY);
        String expectedPart2 = getExpectedSolutions(17)[1]; 

        String result = day17.solve(false, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 2 solve() is not implemented");
        } else {
            assertEquals(expectedPart2, result, "Part 2 solution is incorrect");
        }
    }

    @Test
    void testDay17TestFullSolve() throws FileNotFoundException {
        Day17 day17 = new Day17();
        Scanner input = getInputScanner(DAY);
        String[] expectedSolutions = getExpectedSolutions(17); 

        String[] result = day17.fullSolve(input);
        if (isUnimplementedFullSolve(result)) {
            fail("fullSolve() is not implemented");
        } else {
            assertArrayEquals(expectedSolutions, result, "Full solve solution is incorrect");
        }
    }

}
