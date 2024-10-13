package src.tests;

import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

import src.meta.BaseTest;
import src.solutions.Day22;

class Day22Test extends BaseTest {

    private static final String DAY = "day22";

    @Test
    void testDay22TestPart1() throws FileNotFoundException {
        Day22 day22 = new Day22();
        Scanner input = getInputScanner(DAY);
        String expectedPart1 = getExpectedSolutions(22)[0]; 

        String result = day22.solve(true, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 1 solve() is not implemented");
        } else {
            assertEquals(expectedPart1, result, "Part 1 solution is incorrect");
        }
    }

    @Test
    void testDay22TestPart2() throws FileNotFoundException {
        Day22 day22 = new Day22();
        Scanner input = getInputScanner(DAY);
        String expectedPart2 = getExpectedSolutions(22)[1]; 

        String result = day22.solve(false, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 2 solve() is not implemented");
        } else {
            assertEquals(expectedPart2, result, "Part 2 solution is incorrect");
        }
    }

    @Test
    void testDay22TestFullSolve() throws FileNotFoundException {
        Day22 day22 = new Day22();
        Scanner input = getInputScanner(DAY);
        String[] expectedSolutions = getExpectedSolutions(22); 

        String[] result = day22.fullSolve(input);
        if (isUnimplementedFullSolve(result)) {
            fail("fullSolve() is not implemented");
        } else {
            assertArrayEquals(expectedSolutions, result, "Full solve solution is incorrect");
        }
    }

}
