package src.tests;

import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

import src.meta.BaseTest;
import src.solutions.Day12;

class Day12Test extends BaseTest {

    private static final String DAY = "day12";

    @Test
    void testDay12TestPart1() throws FileNotFoundException {
        Day12 day12 = new Day12();
        Scanner input = getInputScanner(DAY);
        String expectedPart1 = getExpectedSolutions(12)[0]; 

        String result = day12.solve(true, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 1 solve() is not implemented");
        } else {
            assertEquals(expectedPart1, result, "Part 1 solution is incorrect");
        }
    }

    @Test
    void testDay12TestPart2() throws FileNotFoundException {
        Day12 day12 = new Day12();
        Scanner input = getInputScanner(DAY);
        String expectedPart2 = getExpectedSolutions(12)[1]; 

        String result = day12.solve(false, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 2 solve() is not implemented");
        } else {
            assertEquals(expectedPart2, result, "Part 2 solution is incorrect");
        }
    }

    @Test
    void testDay12TestFullSolve() throws FileNotFoundException {
        Day12 day12 = new Day12();
        Scanner input = getInputScanner(DAY);
        String[] expectedSolutions = getExpectedSolutions(12); 

        String[] result = day12.fullSolve(input);
        if (isUnimplementedFullSolve(result)) {
            fail("fullSolve() is not implemented");
        } else {
            assertArrayEquals(expectedSolutions, result, "Full solve solution is incorrect");
        }
    }

}
