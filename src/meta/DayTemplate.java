package src.meta;

import java.util.Scanner;

public interface DayTemplate {

    /**
     * Main solving method.
     *
     * @param part1 The solver will solve part 1 if param is set to true.
     *              The solver will solve part 2 if param is set to false.
     * @param in    The solver will read data from this Scanner.
     * @return Returns answer in string format.
     */
    String solve(boolean part1, Scanner in);

    /**
     * Solves both parts of the day.
     *
     * @param in    The solver will read data from this Scanner.
     * @return Returns answer as a string array, with part 1 as index 0 and part 2 as index 1
     */
    default String[] fullSolve(Scanner in) {
        String input = in.findWithinHorizon("(?s).*", 0);
        try (Scanner part1Input = new Scanner(input);
             Scanner part2Input = new Scanner(input)) {
            return new String[]{freshSolver().solve(true, part1Input),
                    freshSolver().solve(false, part2Input)};
        }
    }

    private DayTemplate freshSolver() {
        try {
            return getClass().getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(
                    "Solver must have an accessible no-argument constructor", exception);
        }
    }

    /**
     * Some classes require additional, non code steps (e.g. judge an image output).
     * In those cases, we do not want to run the solver.
     *
     * @return By default, returns false.
     * Subclasses can override in exceptional cases.
     */
    default boolean exclude() {
        return false;
    }
}
