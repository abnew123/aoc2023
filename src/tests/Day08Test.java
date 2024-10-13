package src.tests;

import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

import src.meta.BaseTest;
import src.solutions.Day08;

class Day08Test extends BaseTest {

    private static final String DAY = "day08";

    @Test
    void testDay08TestPart1() throws FileNotFoundException {
        Day08 day08 = new Day08();
        Scanner input = getInputScanner(DAY);
        String expectedPart1 = getExpectedSolutions(8)[0]; 

        String result = day08.solve(true, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 1 solve() is not implemented");
        } else {
            assertEquals(expectedPart1, result, "Part 1 solution is incorrect");
        }
    }

    @Test
    void testDay08TestPart2() throws FileNotFoundException {
        Day08 day08 = new Day08();
        Scanner input = getInputScanner(DAY);
        String expectedPart2 = getExpectedSolutions(8)[1]; 

        String result = day08.solve(false, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 2 solve() is not implemented");
        } else {
            assertEquals(expectedPart2, result, "Part 2 solution is incorrect");
        }
    }

    @Test
    void testDay08TestFullSolve() throws FileNotFoundException {
        Day08 day08 = new Day08();
        Scanner input = getInputScanner(DAY);
        String[] expectedSolutions = getExpectedSolutions(8); 

        String[] result = day08.fullSolve(input);
        if (isUnimplementedFullSolve(result)) {
            fail("fullSolve() is not implemented");
        } else {
            assertArrayEquals(expectedSolutions, result, "Full solve solution is incorrect");
        }
    }

}
