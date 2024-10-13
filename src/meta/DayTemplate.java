package src.meta;

import java.util.Scanner;

public interface DayTemplate {

    /**
     * Times execution of the solve method
     *
     * @param part1 Param for which part solve() will solve.
     * @param in    Param for data solve() will read.
     * @return Time in milliseconds (not nanoseconds) for execution of the method.
     */
    default double timer(boolean part1, Scanner in) {
        Long startTime = System.nanoTime();
        solve(part1, in);
        Long endTime = System.nanoTime();
        return (endTime - startTime) / 1000000.0;
    }

    /**
     * Times execution of an entire day
     *
     * @param in    Param for data solve() will read.
     * @return Time in milliseconds (not nanoseconds) for execution of the method.
     */
    default double dayTimer(Scanner in) {
        Long startTime = System.nanoTime();
        fullSolve(in);
        Long endTime = System.nanoTime();
        return (endTime - startTime) / 1000000.0;
    }


    /**
     * Main solving method.
     *
     * @param part1 The solver will solve part 1 if param is set to true.
     *              The solver will solve part 2 if param is set to false.
     * @param in    The solver will read data from this Scanner.
     * @return Returns answer in string format.
     */
    default String solve(boolean part1, Scanner in) {return null;}

    /**
     * Solves both parts of the day.
     *
     * @param in    The solver will read data from this Scanner.
     * @return Returns answer as a string array, with part 1 as index 0 and part 2 as index 1
     */
    default String[] fullSolve(Scanner in) { return new String[2]; }

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
