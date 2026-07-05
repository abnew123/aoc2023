package src.meta;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Scanner;

public class MasterSolver {

    private static final String DATA_DAY = "./data/day";
    private static final String SOLUTIONS_DAY = "src.solutions.Day";
    private static final String[] GOLFED_DAYS = "A B C D E F G H I J K L M N O P Q R S T U V W X Y".split(" ");
    private static final String PART = " part ";
    private static boolean useGolfed;

    public static void main(String[] args) throws Exception {

        // inputs.
        boolean runTimer = true;
        boolean totalTimer = false;
        boolean exclusionTimer = true;
        boolean correctnessCheck = true;
        boolean timePartsSeparately = false;
        useGolfed = false;
        int[] days = new int[]{};
        boolean[] parts = new boolean[]{true, false};
        // Do not change anything in the method below this comment

        for (int day : days) {
            String zeroFilledDay = (day < 10 ? "0" : "") + day;
            for (boolean part1 : parts) {
                File file = new File(DATA_DAY + zeroFilledDay + ".txt");
                Class<?> cls = Class.forName(className(day, zeroFilledDay));
                String answer = solve(cls, part1, file);
                System.out.println(
                        "Day " + zeroFilledDay + PART + (part1 ? 1 : 2) + " solution: " + answer);
            }
        }
        if (runTimer) {
            timer(totalTimer, exclusionTimer, timePartsSeparately);
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
     * @param exclusion Timer will exclude days that return exceptions if param is
     *                  set to true. Timer will execute all days if param is set to
     *                  false.
     * @throws Exception
     */

    public static void timer(boolean total, boolean exclusion, boolean parts) throws Exception {
        Double totalTime = 0.0;
        for (int day = 1; day <= 25; day++) {
            String zeroFilledDay = (day < 10 ? "0" : "") + day;
            if(parts){
                for (int part = 1; part <= 2; part++) {
                    Class<?> cls = Class.forName(className(day, zeroFilledDay));
                    File file = new File(DATA_DAY + zeroFilledDay + ".txt");
                    if (!useGolfed && exclusion && (boolean) cls.getMethod("exclude")
                            .invoke(cls.getDeclaredConstructor().newInstance())) {
                        continue;
                    }
                    Double time;
                    if (useGolfed) {
                        long start = System.nanoTime();
                        solve(cls, part == 1, file);
                        time = (System.nanoTime() - start) / 1000000.0;
                    } else {
                        time = (Double) cls.getMethod("timer", boolean.class, Scanner.class)
                                .invoke(cls.getDeclaredConstructor().newInstance(), part == 1, new Scanner(file));
                    }
                    if (!total) {
                        System.out.println("Day " + zeroFilledDay + PART + part + " execution time: " + time);
                    }
                    totalTime += time;
                }
            }
            else{
                Class<?> cls = Class.forName(className(day, zeroFilledDay));
                File file = new File(DATA_DAY + zeroFilledDay + ".txt");
                if (!useGolfed && exclusion && (boolean) cls.getMethod("exclude")
                        .invoke(cls.getDeclaredConstructor().newInstance())) {
                    continue;
                }
                Double time;
                if (useGolfed) {
                    long start = System.nanoTime();
                    solve(cls, true, file);
                    solve(cls, false, file);
                    time = (System.nanoTime() - start) / 1000000.0;
                } else {
                    time = (Double) cls.getMethod("dayTimer", Scanner.class)
                            .invoke(cls.getDeclaredConstructor().newInstance(), new Scanner(file));
                }
                if (!total) {
                    System.out.println("Day " + zeroFilledDay + " execution time: " + time);
                }
                totalTime += time;
            }
        }
        System.out.println("Total execution time (ms): " + totalTime);
    }

    public static boolean correctnessCheck() throws Exception {
        boolean allCorrect = true;
        try (Scanner expectedResultsScanner = new Scanner(new File("./data/expectedResults.txt"))) {
            for (int day = 1; day <= 25; day++) {
                String zeroFilledDay = (day < 10 ? "0" : "") + day;
                for (int part = 1; part <= 2; part++) {
                    String expectedAnswer = expectedResultsScanner.nextLine();
                    File file = new File(DATA_DAY + zeroFilledDay + ".txt");
                    Class<?> cls = Class.forName(className(day, zeroFilledDay));
                    String answer = solve(cls, part == 1, file);
                    if (!answer.equals(expectedAnswer)) {
                        System.out.println(
                                "Day " + zeroFilledDay + PART + (part == 1 ? 1 : 2) + " solution: " + answer + " doesn't match expected result of " + expectedAnswer);
                        allCorrect = false;
                    }
                }
            }
        }

        if (allCorrect) {
            System.out.println("Everything checks out!");
        }
        return allCorrect;
    }

    private static String className(int day, String zeroFilledDay) {
        return useGolfed ? GOLFED_DAYS[day - 1] : SOLUTIONS_DAY + zeroFilledDay;
    }

    private static String solve(Class<?> cls, boolean part1, File file) throws Exception {
        Object solver = cls.getDeclaredConstructor().newInstance();
        if (useGolfed) {
            Method m = cls.getDeclaredMethod("s", boolean.class, String.class);
            m.setAccessible(true);
            return (String) m.invoke(solver, part1, Files.readString(file.toPath()));
        }
        try (Scanner in = new Scanner(file)) {
            Method m = cls.getDeclaredMethod("solve", boolean.class, Scanner.class);
            return (String) m.invoke(solver, part1, in);
        }
    }
}
