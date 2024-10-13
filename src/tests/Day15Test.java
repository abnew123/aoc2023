package src.tests;

import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

import src.meta.BaseTest;
import src.solutions.Day15;

class Day15Test extends BaseTest {

    private static final String DAY = "day15";

    @Test
    void testDay15TestPart1() throws FileNotFoundException {
        Day15 day15 = new Day15();
        Scanner input = getInputScanner(DAY);
        String expectedPart1 = getExpectedSolutions(15)[0]; 

        String result = day15.solve(true, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 1 solve() is not implemented");
        } else {
            assertEquals(expectedPart1, result, "Part 1 solution is incorrect");
        }
    }

    @Test
    void testDay15TestPart2() throws FileNotFoundException {
        Day15 day15 = new Day15();
        Scanner input = getInputScanner(DAY);
        String expectedPart2 = getExpectedSolutions(15)[1]; 

        String result = day15.solve(false, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 2 solve() is not implemented");
        } else {
            assertEquals(expectedPart2, result, "Part 2 solution is incorrect");
        }
    }

    @Test
    void testDay15TestFullSolve() throws FileNotFoundException {
        Day15 day15 = new Day15();
        Scanner input = getInputScanner(DAY);
        String[] expectedSolutions = getExpectedSolutions(15); 

        String[] result = day15.fullSolve(input);
        if (isUnimplementedFullSolve(result)) {
            fail("fullSolve() is not implemented");
        } else {
            assertArrayEquals(expectedSolutions, result, "Full solve solution is incorrect");
        }
    }

}
