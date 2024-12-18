package src.tests;

import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

import src.meta.BaseTest;
import src.solutions.Day02;

class Day02Test extends BaseTest {

    private static final String DAY = "day02";

    @Test
    void testDay02TestPart1() throws FileNotFoundException {
        Day02 day02 = new Day02();
        Scanner input = getInputScanner(DAY);
        String expectedPart1 = getExpectedSolutions(2)[0]; 

        String result = day02.solve(true, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 1 solve() is not implemented");
        } else {
            assertEquals(expectedPart1, result, "Part 1 solution is incorrect");
        }
    }

    @Test
    void testDay02TestPart2() throws FileNotFoundException {
        Day02 day02 = new Day02();
        Scanner input = getInputScanner(DAY);
        String expectedPart2 = getExpectedSolutions(2)[1]; 

        String result = day02.solve(false, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 2 solve() is not implemented");
        } else {
            assertEquals(expectedPart2, result, "Part 2 solution is incorrect");
        }
    }

    @Test
    void testDay02TestFullSolve() throws FileNotFoundException {
        Day02 day02 = new Day02();
        Scanner input = getInputScanner(DAY);
        String[] expectedSolutions = getExpectedSolutions(2); 

        String[] result = day02.fullSolve(input);
        if (isUnimplementedFullSolve(result)) {
            fail("fullSolve() is not implemented");
        } else {
            assertArrayEquals(expectedSolutions, result, "Full solve solution is incorrect");
        }
    }

}
