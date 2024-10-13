package src.tests;

import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

import src.meta.BaseTest;
import src.solutions.Day07;

class Day07Test extends BaseTest {

    private static final String DAY = "day07";

    @Test
    void testDay07TestPart1() throws FileNotFoundException {
        Day07 day07 = new Day07();
        Scanner input = getInputScanner(DAY);
        String expectedPart1 = getExpectedSolutions(7)[0]; 

        String result = day07.solve(true, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 1 solve() is not implemented");
        } else {
            assertEquals(expectedPart1, result, "Part 1 solution is incorrect");
        }
    }

    @Test
    void testDay07TestPart2() throws FileNotFoundException {
        Day07 day07 = new Day07();
        Scanner input = getInputScanner(DAY);
        String expectedPart2 = getExpectedSolutions(7)[1]; 

        String result = day07.solve(false, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 2 solve() is not implemented");
        } else {
            assertEquals(expectedPart2, result, "Part 2 solution is incorrect");
        }
    }

    @Test
    void testDay07TestFullSolve() throws FileNotFoundException {
        Day07 day07 = new Day07();
        Scanner input = getInputScanner(DAY);
        String[] expectedSolutions = getExpectedSolutions(7); 

        String[] result = day07.fullSolve(input);
        if (isUnimplementedFullSolve(result)) {
            fail("fullSolve() is not implemented");
        } else {
            assertArrayEquals(expectedSolutions, result, "Full solve solution is incorrect");
        }
    }

}
