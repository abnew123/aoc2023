package src.meta;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MasterSolver {

    private static final String DATA_DAY = "./data/day";
    private static final String PART = " part ";

    public static void main(String[] args) throws Exception {

        // inputs.
        boolean runTimer = true;
        boolean totalTimer = false;
        boolean correctnessCheck = true;
        int[] days = new int[]{};
        boolean[] parts = new boolean[]{true, false};
        // Do not change anything in the method below this comment

        for (int day : days) {
            String zeroFilledDay = (day < 10 ? "0" : "") + day;
            for (boolean part1 : parts) {
                File file = new File(DATA_DAY + zeroFilledDay + ".txt");
                try (Scanner in = new Scanner(file)) {
                    String answer = SolverFactory.create(day).solve(part1, in);
                    System.out.println(
                            "Day " + zeroFilledDay + PART + (part1 ? 1 : 2) + " solution: " + answer);
                }
            }
        }
        if (runTimer) {
            timer(totalTimer);
        }
        if (correctnessCheck) {
            correctnessCheck();
        }
    }

    /**
     * New timer method. Supports modality
     *
     * @param total     Timer will give the sum total execution time if param set to
     *                  true. Timer will give individual days times by part if param
     *                  is set to false. Note that even if param is set to false,
     *                  total time will be given.
     * @throws FileNotFoundException when a puzzle input cannot be opened
     */

    public static void timer(boolean total) throws FileNotFoundException {
        Double totalTime = 0.0;
        for (int day = 1; day <= 25; day++) {
            String zeroFilledDay = (day < 10 ? "0" : "") + day;
            double time;
            try (Scanner scanner = new Scanner(new File(DATA_DAY + zeroFilledDay + ".txt"))) {
                time = SolverFactory.create(day).dayTimer(scanner);
            }
            if (!total) {
                System.out.println("Day " + zeroFilledDay + " execution time: " + time);
            }
            totalTime += time;
        }
        System.out.println("Total execution time (ms): " + totalTime);
    }

    public static boolean correctnessCheck() throws FileNotFoundException {
        boolean allCorrect = true;
        try (Scanner expectedResultsScanner = new Scanner(new File("./data/expectedResults.txt"))) {
            for (int day = 1; day <= 25; day++) {
                String zeroFilledDay = (day < 10 ? "0" : "") + day;
                for (int part = 1; part <= 2; part++) {
                    String expectedAnswer = expectedResultsScanner.nextLine();
                    File file = new File(DATA_DAY + zeroFilledDay + ".txt");
                    try (Scanner in = new Scanner(file)) {
                        String answer = SolverFactory.create(day).solve(part == 1, in);
                        if (!answer.equals(expectedAnswer)) {
                            System.out.println(
                                    "Day " + zeroFilledDay + PART + (part == 1 ? 1 : 2) + " solution: " + answer + " doesn't match expected result of " + expectedAnswer);
                            allCorrect = false;
                        }
                    }
                }
            }
        }

        if (allCorrect) {
            System.out.println("Everything checks out!");
        }
        return allCorrect;
    }
}
