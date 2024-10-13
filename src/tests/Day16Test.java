package src.tests;

import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

import src.meta.BaseTest;
import src.solutions.Day16;

class Day16Test extends BaseTest {

    private static final String DAY = "day16";

    @Test
    void testDay16TestPart1() throws FileNotFoundException {
        Day16 day16 = new Day16();
        Scanner input = getInputScanner(DAY);
        String expectedPart1 = getExpectedSolutions(16)[0]; 

        String result = day16.solve(true, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 1 solve() is not implemented");
        } else {
            assertEquals(expectedPart1, result, "Part 1 solution is incorrect");
        }
    }

    @Test
    void testDay16TestPart2() throws FileNotFoundException {
        Day16 day16 = new Day16();
        Scanner input = getInputScanner(DAY);
        String expectedPart2 = getExpectedSolutions(16)[1]; 

        String result = day16.solve(false, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 2 solve() is not implemented");
        } else {
            assertEquals(expectedPart2, result, "Part 2 solution is incorrect");
        }
    }

    @Test
    void testDay16TestFullSolve() throws FileNotFoundException {
        Day16 day16 = new Day16();
        Scanner input = getInputScanner(DAY);
        String[] expectedSolutions = getExpectedSolutions(16); 

        String[] result = day16.fullSolve(input);
        if (isUnimplementedFullSolve(result)) {
            fail("fullSolve() is not implemented");
        } else {
            assertArrayEquals(expectedSolutions, result, "Full solve solution is incorrect");
        }
    }

}
