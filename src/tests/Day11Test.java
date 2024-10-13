package src.tests;

import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

import src.meta.BaseTest;
import src.solutions.Day11;

class Day11Test extends BaseTest {

    private static final String DAY = "day11";

    @Test
    void testDay11TestPart1() throws FileNotFoundException {
        Day11 day11 = new Day11();
        Scanner input = getInputScanner(DAY);
        String expectedPart1 = getExpectedSolutions(11)[0]; 

        String result = day11.solve(true, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 1 solve() is not implemented");
        } else {
            assertEquals(expectedPart1, result, "Part 1 solution is incorrect");
        }
    }

    @Test
    void testDay11TestPart2() throws FileNotFoundException {
        Day11 day11 = new Day11();
        Scanner input = getInputScanner(DAY);
        String expectedPart2 = getExpectedSolutions(11)[1]; 

        String result = day11.solve(false, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 2 solve() is not implemented");
        } else {
            assertEquals(expectedPart2, result, "Part 2 solution is incorrect");
        }
    }

    @Test
    void testDay11TestFullSolve() throws FileNotFoundException {
        Day11 day11 = new Day11();
        Scanner input = getInputScanner(DAY);
        String[] expectedSolutions = getExpectedSolutions(11); 

        String[] result = day11.fullSolve(input);
        if (isUnimplementedFullSolve(result)) {
            fail("fullSolve() is not implemented");
        } else {
            assertArrayEquals(expectedSolutions, result, "Full solve solution is incorrect");
        }
    }

}
