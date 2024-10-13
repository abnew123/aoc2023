package src.meta;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Scanner;


public class MasterSolver {

    private static final String DATA_DAY = "./data/day";
    private static final String SOLUTIONS_DAY = "src.solutions.Day";
    private static final String PART = " part ";

    public static void main(String[] args) throws Exception {

        // inputs.
        boolean runTimer = true;
        boolean totalTimer = false;
        boolean exclusionTimer = true;
        boolean timePartsSeparately = false;
        int[] days = new int[]{};
        boolean[] parts = new boolean[]{true, false};
        // Do not change anything in the method below this comment

        for (int day : days) {
            String zeroFilledDay = (day < 10 ? "0" : "") + day;
            for (boolean part1 : parts) {
                File file = new File(DATA_DAY + zeroFilledDay + ".txt");
                try (Scanner in = new Scanner(file)) {
                    Class<?> cls = Class.forName(SOLUTIONS_DAY + zeroFilledDay);
                    Method m = cls.getDeclaredMethod("solve", boolean.class, Scanner.class);
                    String answer = (String) m.invoke(cls.getDeclaredConstructor().newInstance(), part1, in);
                    System.out.println(
                            "Day " + zeroFilledDay + PART + (part1 ? 1 : 2) + " solution: " + answer);
                }
            }
        }
        if (runTimer) {
            timer(totalTimer, exclusionTimer, timePartsSeparately);
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

    public static void timer(boolean total, boolean exclusion, boolean parts) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, FileNotFoundException {
        Double totalTime = 0.0;
        for (int day = 1; day <= 25; day++) {
            String zeroFilledDay = (day < 10 ? "0" : "") + day;
            if (parts) {
                for (int part = 1; part <= 2; part++) {
                    boolean exclude = (boolean) Class.forName(SOLUTIONS_DAY + zeroFilledDay).getMethod("exclude")
                            .invoke(Class.forName(SOLUTIONS_DAY + zeroFilledDay).getDeclaredConstructor().newInstance());
                    if (exclusion && exclude) {
                        continue;
                    }
                    Double time = (Double) Class.forName(SOLUTIONS_DAY + zeroFilledDay)
                            .getMethod("timer", boolean.class, Scanner.class)
                            .invoke(Class.forName(SOLUTIONS_DAY + zeroFilledDay).getDeclaredConstructor().newInstance(),
                                    part == 1, new Scanner(new File(DATA_DAY + zeroFilledDay + ".txt")));
                    if (!total) {
                        System.out.println("Day " + zeroFilledDay + PART + part + " execution time: " + time);
                    }
                    totalTime += time;
                }
            } else {
                boolean exclude = (boolean) Class.forName(SOLUTIONS_DAY + zeroFilledDay).getMethod("exclude")
                        .invoke(Class.forName(SOLUTIONS_DAY + zeroFilledDay).getDeclaredConstructor().newInstance());
                if (exclusion && exclude) {
                    continue;
                }
                Double time = (Double) Class.forName(SOLUTIONS_DAY + zeroFilledDay)
                        .getMethod("dayTimer", Scanner.class)
                        .invoke(Class.forName(SOLUTIONS_DAY + zeroFilledDay).getDeclaredConstructor().newInstance(),
                                new Scanner(new File(DATA_DAY + zeroFilledDay + ".txt")));
                if (!total) {
                    System.out.println("Day " + zeroFilledDay + " execution time: " + time);
                }
                totalTime += time;
            }
        }
        System.out.println("Total execution time (ms): " + totalTime);
    }
}