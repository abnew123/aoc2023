package src.meta;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public abstract class BaseTest {

    protected Scanner getInputScanner(String day) throws FileNotFoundException {
        return new Scanner(new File("data/" + day + ".txt"));
    }

    protected String[] getExpectedSolutions(int day) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("data/expectedResults.txt"));
        String[] solutions = new String[2];
        int lineNumber = (day - 1) * 2; // Day 1 -> lines 0 and 1, Day 2 -> lines 2 and 3, etc.

        for (int i = 0; i < lineNumber; i++) {
            scanner.nextLine();
        }

        solutions[0] = scanner.nextLine();
        solutions[1] = scanner.nextLine();
        return solutions;
    }

    protected boolean isUnimplementedSolve(String result) {
        return result == null;
    }

    protected boolean isUnimplementedFullSolve(String[] result) {
        return result != null && result.length == 2 && result[0] == null && result[1] == null;
    }
}
