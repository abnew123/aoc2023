package src.solutions;

import src.meta.DayTemplate;

import java.util.Arrays;
import java.util.Scanner;

public class Day06 implements DayTemplate {

    public String[] fullSolve(Scanner in) {
        long answer1 = 1;
        long answer2 = 1;
        String timeLine = in.nextLine();
        String distLine = in.nextLine();
        String timeLine1 = timeLine.split(":")[1].trim();
        String timeLine2 = timeLine.replace(" ", "").split(":")[1];
        String distLine1 = distLine.split(":")[1].trim();
        String distLine2 = distLine.replace(" ", "").split(":")[1];
        long[] times1 = Arrays.stream(timeLine1.split("\\s+")).mapToLong(Long::parseLong).toArray();
        long[] dists1 = Arrays.stream(distLine1.split("\\s+")).mapToLong(Long::parseLong).toArray();
        long[] times2 = Arrays.stream(timeLine2.split("\\s+")).mapToLong(Long::parseLong).toArray();
        long[] dists2 = Arrays.stream(distLine2.split("\\s+")).mapToLong(Long::parseLong).toArray();
        for (int i = 0; i < times1.length; i++) {
            long time = times1[i];
            long dist = dists1[i];
            long threshold = (long) Math.ceil((time -Math.sqrt(time * time - 4.0 * dist)) / 2);
            answer1 *= time - threshold * 2 + 1;
        }
        for (int i = 0; i < times2.length; i++) {
            long time = times2[i];
            long dist = dists2[i];
            long threshold = (long) Math.ceil((time -Math.sqrt(time * time - 4.0 * dist)) / 2);
            answer2 *= time - threshold * 2 + 1;
        }
        return new String[]{answer1 + "", answer2 + ""};
    }

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
        long[] times = Arrays.stream(timeLine.split("\\s+")).mapToLong(Long::parseLong).toArray();
        long[] dists = Arrays.stream(distLine.split("\\s+")).mapToLong(Long::parseLong).toArray();
        for (int i = 0; i < times.length; i++) {
            long time = times[i];
            long dist = dists[i];
            long threshold = (long) Math.ceil((time -Math.sqrt(time * time - 4.0 * dist)) / 2);
            answer *= time - threshold * 2 + 1;
        }
        return answer + "";
    }
}