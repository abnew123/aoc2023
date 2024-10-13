package src.tests;

import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

import src.meta.BaseTest;
import src.solutions.Day06;

class Day06Test extends BaseTest {

    private static final String DAY = "day06";

    @Test
    void testDay06TestPart1() throws FileNotFoundException {
        Day06 day06 = new Day06();
        Scanner input = getInputScanner(DAY);
        String expectedPart1 = getExpectedSolutions(6)[0]; 

        String result = day06.solve(true, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 1 solve() is not implemented");
        } else {
            assertEquals(expectedPart1, result, "Part 1 solution is incorrect");
        }
    }

    @Test
    void testDay06TestPart2() throws FileNotFoundException {
        Day06 day06 = new Day06();
        Scanner input = getInputScanner(DAY);
        String expectedPart2 = getExpectedSolutions(6)[1]; 

        String result = day06.solve(false, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 2 solve() is not implemented");
        } else {
            assertEquals(expectedPart2, result, "Part 2 solution is incorrect");
        }
    }

    @Test
    void testDay06TestFullSolve() throws FileNotFoundException {
        Day06 day06 = new Day06();
        Scanner input = getInputScanner(DAY);
        String[] expectedSolutions = getExpectedSolutions(6); 

        String[] result = day06.fullSolve(input);
        if (isUnimplementedFullSolve(result)) {
            fail("fullSolve() is not implemented");
        } else {
            assertArrayEquals(expectedSolutions, result, "Full solve solution is incorrect");
        }
    }

}
