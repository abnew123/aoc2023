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
        boolean correctnessCheck = true;
        int[] days = new int[]{};
        boolean[] parts = new boolean[]{true, false};
        // Do not change anything in the method below this comment

        for (int day : days) {
            String zeroFilledDay = (day < 10 ? "0" : "") + day;
            for (boolean part1 : parts) {
                File file = new File(DATA_DAY + zeroFilledDay + ".txt");
                try (Scanner in = new Scanner(file)) {
                    Class<?> cls = Class.forName( SOLUTIONS_DAY+ zeroFilledDay);
                    Method m = cls.getDeclaredMethod("solve", boolean.class, Scanner.class);
                    String answer = (String) m.invoke(cls.getDeclaredConstructor().newInstance(), part1, in);
                    System.out.println(
                            "Day " + zeroFilledDay + PART + (part1 ? 1 : 2) + " solution: " + answer);
                }
            }
        }
        if (correctnessCheck) {
            correctnessCheck();
        }
    }

    public static boolean correctnessCheck() throws FileNotFoundException, NoSuchMethodException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        boolean allCorrect = true;
        try (Scanner expectedResultsScanner = new Scanner(new File("./data/expectedResults.txt"))) {
            for (int day = 1; day <= 25; day++) {
                String zeroFilledDay = (day < 10 ? "0" : "") + day;
                for (int part = 1; part <= 2; part++) {
                    String expectedAnswer = expectedResultsScanner.nextLine();
                    File file = new File(DATA_DAY + zeroFilledDay + ".txt");
                    try (Scanner in = new Scanner(file)) {
                        Class<?> cls = Class.forName(SOLUTIONS_DAY + zeroFilledDay);
                        Method m = cls.getDeclaredMethod("solve", boolean.class, Scanner.class);
                        String answer = (String) m.invoke(cls.getDeclaredConstructor().newInstance(), part == 1, in);
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
