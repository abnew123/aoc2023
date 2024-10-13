package src.tests;

import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

import src.meta.BaseTest;
import src.solutions.Day23;

class Day23Test extends BaseTest {

    private static final String DAY = "day23";

    @Test
    void testDay23TestPart1() throws FileNotFoundException {
        Day23 day23 = new Day23();
        Scanner input = getInputScanner(DAY);
        String expectedPart1 = getExpectedSolutions(23)[0]; 

        String result = day23.solve(true, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 1 solve() is not implemented");
        } else {
            assertEquals(expectedPart1, result, "Part 1 solution is incorrect");
        }
    }

    @Test
    void testDay23TestPart2() throws FileNotFoundException {
        Day23 day23 = new Day23();
        Scanner input = getInputScanner(DAY);
        String expectedPart2 = getExpectedSolutions(23)[1]; 

        String result = day23.solve(false, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 2 solve() is not implemented");
        } else {
            assertEquals(expectedPart2, result, "Part 2 solution is incorrect");
        }
    }

    @Test
    void testDay23TestFullSolve() throws FileNotFoundException {
        Day23 day23 = new Day23();
        Scanner input = getInputScanner(DAY);
        String[] expectedSolutions = getExpectedSolutions(23); 

        String[] result = day23.fullSolve(input);
        if (isUnimplementedFullSolve(result)) {
            fail("fullSolve() is not implemented");
        } else {
            assertArrayEquals(expectedSolutions, result, "Full solve solution is incorrect");
        }
    }

}
