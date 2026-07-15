package src.tests;

import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

import src.meta.BaseTest;
import src.solutions.Day04;

class Day04Test extends BaseTest {

    private static final String DAY = "day04";
    private static final String OFFICIAL_SAMPLE = "Card 1: 41 48 83 86 17 | 83 86 6 31 17 9 48 53\n"
            + "Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19\n"
            + "Card 3: 1 21 53 59 44 | 69 82 63 72 16 21 14 1\n"
            + "Card 4: 41 92 73 84 69 | 59 84 76 51 58 5 54 83\n"
            + "Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36\n"
            + "Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11";

    @Test
    void testDay04TestPart1() throws FileNotFoundException {
        Day04 day04 = new Day04();
        Scanner input = getInputScanner(DAY);
        String expectedPart1 = getExpectedSolutions(4)[0]; 

        String result = day04.solve(true, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 1 solve() is not implemented");
        } else {
            assertEquals(expectedPart1, result, "Part 1 solution is incorrect");
        }
    }

    @Test
    void testDay04TestPart2() throws FileNotFoundException {
        Day04 day04 = new Day04();
        Scanner input = getInputScanner(DAY);
        String expectedPart2 = getExpectedSolutions(4)[1]; 

        String result = day04.solve(false, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 2 solve() is not implemented");
        } else {
            assertEquals(expectedPart2, result, "Part 2 solution is incorrect");
        }
    }

    @Test
    void testDay04TestFullSolve() throws FileNotFoundException {
        Day04 day04 = new Day04();
        Scanner input = getInputScanner(DAY);
        String[] expectedSolutions = getExpectedSolutions(4); 

        String[] result = day04.fullSolve(input);
        if (isUnimplementedFullSolve(result)) {
            fail("fullSolve() is not implemented");
        } else {
            assertArrayEquals(expectedSolutions, result, "Full solve solution is incorrect");
        }
    }

    @Test
    void testDay04OfficialSample() {
        assertArrayEquals(new String[]{"13", "30"},
                new Day04().fullSolve(new Scanner(OFFICIAL_SAMPLE)));
    }

    @Test
    void testDay04ExactDuplicatesAndDeckClamping() {
        String input = "Card 1: 1 1 2 | 01 +1 2 3\n"
                + "Card 2: 4 5 | 4 5";
        assertArrayEquals(new String[]{"6", "3"},
                new Day04().fullSolve(new Scanner(input)));

        StringBuilder overflow = new StringBuilder("Card 1: 7 |");
        for (int index = 0; index < 70; index++) {
            overflow.append(" 7");
        }
        assertArrayEquals(new String[]{"590295810358705651712", "1"},
                new Day04().fullSolve(new Scanner(overflow.toString())));
    }

}
