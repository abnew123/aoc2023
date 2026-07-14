package src.tests;

import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

import src.meta.BaseTest;
import src.solutions.Day24;

class Day24Test extends BaseTest {

    private static final String DAY = "day24";
    private static final String OFFICIAL_SAMPLE = "19, 13, 30 @ -2, 1, -2\n"
            + "18, 19, 22 @ -1, -1, -2\n"
            + "20, 25, 34 @ -2, -2, -4\n"
            + "12, 31, 28 @ -1, -2, -1\n"
            + "20, 19, 15 @ 1, -5, -3\n";

    @Test
    void testDay24TestPart1() throws FileNotFoundException {
        Day24 day24 = new Day24();
        Scanner input = getInputScanner(DAY);
        String expectedPart1 = getExpectedSolutions(24)[0]; 

        String result = day24.solve(true, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 1 solve() is not implemented");
        } else {
            assertEquals(expectedPart1, result, "Part 1 solution is incorrect");
        }
    }

    @Test
    void testDay24TestPart2() throws FileNotFoundException {
        Day24 day24 = new Day24();
        Scanner input = getInputScanner(DAY);
        String expectedPart2 = getExpectedSolutions(24)[1]; 

        String result = day24.solve(false, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 2 solve() is not implemented");
        } else {
            assertEquals(expectedPart2, result, "Part 2 solution is incorrect");
        }
    }

    @Test
    void testDay24TestFullSolve() throws FileNotFoundException {
        Day24 day24 = new Day24();
        Scanner input = getInputScanner(DAY);
        String[] expectedSolutions = getExpectedSolutions(24); 

        String[] result = day24.fullSolve(input);
        if (isUnimplementedFullSolve(result)) {
            fail("fullSolve() is not implemented");
        } else {
            assertArrayEquals(expectedSolutions, result, "Full solve solution is incorrect");
        }
    }

    @Test
    void testDay24OfficialSamplePart2() {
        assertEquals("47", new Day24().solve(false, new Scanner(OFFICIAL_SAMPLE)));
    }

    @Test
    void testDay24CorrectedPersonalPart2AfterReuse() throws FileNotFoundException {
        Day24 day24 = new Day24();
        assertEquals("47", day24.solve(false, new Scanner(OFFICIAL_SAMPLE)));
        assertEquals("711031616315001", day24.solve(false, getInputScanner(DAY)));
    }

    @Test
    void testDay24IntegerRockAtFractionalTimes() {
        String input = "11, 19, 31 @ 0, 1, 1\n"
                + "7, 23, 27 @ 4, -3, 5\n"
                + "20, 30, 35 @ -2, -5, 1\n"
                + "-4, 6, 44 @ 6, 3, -1\n";
        assertEquals("60", new Day24().solve(false, new Scanner(input)));
    }

    @Test
    void testDay24ExactCollinearPaths() {
        Day24 day24 = new Day24();
        String center = "300000000000000";
        String stationaryIntersection = center + ", " + center + ", 0 @ 0, 0, 0\n"
                + "299999999999990, " + center + ", 1 @ 1, 0, 0\n";
        assertEquals("1", day24.solve(true, new Scanner(stationaryIntersection)));

        String separatingRays = "299999999999990, " + center + ", 0 @ -1, 0, 0\n"
                + "300000000000010, " + center + ", 1 @ 1, 0, 0\n";
        assertEquals("0", day24.solve(true, new Scanner(separatingRays)));
    }

}
