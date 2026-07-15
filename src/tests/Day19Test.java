package src.tests;

import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

import src.meta.BaseTest;
import src.solutions.Day19;

class Day19Test extends BaseTest {

    private static final String DAY = "day19";
    private static final String OFFICIAL_SAMPLE = """
            px{a<2006:qkq,m>2090:A,rfg}
            pv{a>1716:R,A}
            lnx{m>1548:A,A}
            rfg{s<537:gd,x>2440:R,A}
            qs{s>3448:A,lnx}
            qkq{x<1416:A,crn}
            crn{x>2662:A,R}
            in{s<1351:px,qqz}
            qqz{s>2770:qs,m<1801:hdj,R}
            gd{a>3333:R,R}
            hdj{m>838:A,pv}

            {x=787,m=2655,a=1222,s=2876}
            {x=1679,m=44,a=2067,s=496}
            {x=2036,m=264,a=79,s=2244}
            {x=2461,m=1339,a=466,s=291}
            {x=2127,m=1623,a=2188,s=1013}
            """;

    @Test
    void testDay19TestPart1() throws FileNotFoundException {
        Day19 day19 = new Day19();
        Scanner input = getInputScanner(DAY);
        String expectedPart1 = getExpectedSolutions(19)[0]; 

        String result = day19.solve(true, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 1 solve() is not implemented");
        } else {
            assertEquals(expectedPart1, result, "Part 1 solution is incorrect");
        }
    }

    @Test
    void testDay19TestPart2() throws FileNotFoundException {
        Day19 day19 = new Day19();
        Scanner input = getInputScanner(DAY);
        String expectedPart2 = getExpectedSolutions(19)[1]; 

        String result = day19.solve(false, input);
        if (isUnimplementedSolve(result)) {
            fail("Part 2 solve() is not implemented");
        } else {
            assertEquals(expectedPart2, result, "Part 2 solution is incorrect");
        }
    }

    @Test
    void testDay19TestFullSolve() throws FileNotFoundException {
        Day19 day19 = new Day19();
        Scanner input = getInputScanner(DAY);
        String[] expectedSolutions = getExpectedSolutions(19); 

        String[] result = day19.fullSolve(input);
        if (isUnimplementedFullSolve(result)) {
            fail("fullSolve() is not implemented");
        } else {
            assertArrayEquals(expectedSolutions, result, "Full solve solution is incorrect");
        }
    }

    @Test
    void testOfficialSampleAndRangeBoundaries() {
        assertArrayEquals(new String[] {"19114", "167409079868000"},
                new Day19().fullSolve(new Scanner(OFFICIAL_SAMPLE)));

        String narrowedAllTrue = "in{x>2000:a,R}\na{x>1000:A,R}\n\n{x=3000,m=1,a=1,s=1}";
        assertArrayEquals(new String[] {"3003", "128000000000000"},
                new Day19().fullSolve(new Scanner(narrowedAllTrue)));

        String emptyBoundary = "in{x<1:A,R}\n\n{x=1,m=1,a=1,s=1}";
        assertArrayEquals(new String[] {"0", "0"},
                new Day19().fullSolve(new Scanner(emptyBoundary)));
    }

}
