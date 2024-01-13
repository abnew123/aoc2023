package src.solutions;

import src.meta.DayTemplate;

import java.util.Arrays;
import java.util.Scanner;

public class Day06 extends DayTemplate {

    /**
     * Main solving method.
     *
     * @param part1 The solver will solve part 1 if param is set to true.
     *              The solver will solve part 2 if param is set to false.
     * @param in    The solver will read data from this Scanner.
     * @return Returns answer in string format.
     */
    public String solve(boolean part1, Scanner in) {
        long answer = 1;
        String timeLine = part1 ? in.nextLine().split(":")[1].trim() : in.nextLine().replace(" ", "").split(":")[1];
        String distLine = part1 ? in.nextLine().split(":")[1].trim() : in.nextLine().replace(" ", "").split(":")[1];
        double[] times = Arrays.stream(timeLine.split("\\s+")).mapToDouble(Double::parseDouble).toArray();
        double[] dists = Arrays.stream(distLine.split("\\s+")).mapToDouble(Double::parseDouble).toArray();
        for (int i = 0; i < times.length; i++) {
            double time = times[i];
            double dist = dists[i];
            long threshold = (long) Math.ceil((time -Math.sqrt(time * time - 4 * dist)) / 2);
            answer *= time - threshold * 2 + 1;
        }
        return answer + "";
    }
}