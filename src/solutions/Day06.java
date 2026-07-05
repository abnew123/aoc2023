package src.solutions;

import src.meta.DayTemplate;

import java.util.Arrays;
import java.util.Scanner;

public class Day06 implements DayTemplate {


    
    public String solve(boolean part1, Scanner in) {
        long ans = 1;
        String timeLine = part1 ? in.nextLine().split(":")[1].trim() : in.nextLine().replace(" ", "").split(":")[1];
        String distLine = part1 ? in.nextLine().split(":")[1].trim() : in.nextLine().replace(" ", "").split(":")[1];
        long[] times = Arrays.stream(timeLine.split("\\s+")).mapToLong(Long::parseLong).toArray();
        long[] dists = Arrays.stream(distLine.split("\\s+")).mapToLong(Long::parseLong).toArray();
        for (int i = 0; i < times.length; i++) {
            long time = times[i];
            long dist = dists[i];
            long threshold = (long) Math.ceil((time -Math.sqrt(time * time - 4.0 * dist)) / 2);
            ans *= time - threshold * 2 + 1;
        }
        return ans + "";
    }
}