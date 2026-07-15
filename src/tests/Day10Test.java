package src.tests;

import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import src.meta.BaseTest;
import src.solutions.Day10;

class Day10Test extends BaseTest {

    private static final String DAY = "day10";

    @Test
    void testDay10TestPart1() throws FileNotFoundException {
        Day10 day10 = new Day10();
        Scanner input = getInputScanner(DAY);
        String expectedPart1 = getExpectedSolutions(10)[0]; 

        String result = day10.solve(true, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 1 solve() is not implemented");
        } else {
            assertEquals(expectedPart1, result, "Part 1 solution is incorrect");
        }
    }

    @Test
    void testDay10TestPart2() throws FileNotFoundException {
        Day10 day10 = new Day10();
        Scanner input = getInputScanner(DAY);
        String expectedPart2 = getExpectedSolutions(10)[1]; 

        String result = day10.solve(false, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 2 solve() is not implemented");
        } else {
            assertEquals(expectedPart2, result, "Part 2 solution is incorrect");
        }
    }

    @Test
    void testDay10TestFullSolve() throws FileNotFoundException {
        Day10 day10 = new Day10();
        Scanner input = getInputScanner(DAY);
        String[] expectedSolutions = getExpectedSolutions(10); 

        String[] result = day10.fullSolve(input);
        if (isUnimplementedFullSolve(result)) {
            fail("fullSolve() is not implemented");
        } else {
            assertArrayEquals(expectedSolutions, result, "Full solve solution is incorrect");
        }
    }

    @Test
    void testOfficialExample() {
        String input = ".....\n.S-7.\n.|.|.\n.L-J.\n.....";
        assertArrayEquals(new String[]{"4", "1"},
                new Day10().fullSolve(new Scanner(input)));
    }

    @Test
    void testEveryStartShapeAndBoundaryLoop() {
        assertArrayEquals(new String[]{"2", "0"},
                new Day10().fullSolve(new Scanner("S7\nLJ")));
        assertArrayEquals(new String[]{"4", "0"},
                new Day10().fullSolve(new Scanner("F-S7\nL--J")));
        assertArrayEquals(new String[]{"5", "2"},
                new Day10().fullSolve(new Scanner("S--7\n|..|\nL--J")));
    }

    @Test
    void testMalformedGridsAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Day10().fullSolve(new Scanner("S-7\nL-JJ")));
        assertThrows(IllegalArgumentException.class,
                () -> new Day10().fullSolve(new Scanner("S-7\n|S|\nL-J")));
        assertThrows(IllegalArgumentException.class,
                () -> new Day10().fullSolve(new Scanner("S..\n...")));
    }

}
