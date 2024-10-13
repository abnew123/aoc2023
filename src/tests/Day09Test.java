package src.tests;

import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

import src.meta.BaseTest;
import src.solutions.Day09;

class Day09Test extends BaseTest {

    private static final String DAY = "day09";

    @Test
    void testDay09TestPart1() throws FileNotFoundException {
        Day09 day09 = new Day09();
        Scanner input = getInputScanner(DAY);
        String expectedPart1 = getExpectedSolutions(9)[0]; 

        String result = day09.solve(true, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 1 solve() is not implemented");
        } else {
            assertEquals(expectedPart1, result, "Part 1 solution is incorrect");
        }
    }

    @Test
    void testDay09TestPart2() throws FileNotFoundException {
        Day09 day09 = new Day09();
        Scanner input = getInputScanner(DAY);
        String expectedPart2 = getExpectedSolutions(9)[1]; 

        String result = day09.solve(false, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 2 solve() is not implemented");
        } else {
            assertEquals(expectedPart2, result, "Part 2 solution is incorrect");
        }
    }

    @Test
    void testDay09TestFullSolve() throws FileNotFoundException {
        Day09 day09 = new Day09();
        Scanner input = getInputScanner(DAY);
        String[] expectedSolutions = getExpectedSolutions(9); 

        String[] result = day09.fullSolve(input);
        if (isUnimplementedFullSolve(result)) {
            fail("fullSolve() is not implemented");
        } else {
            assertArrayEquals(expectedSolutions, result, "Full solve solution is incorrect");
        }
    }

}
