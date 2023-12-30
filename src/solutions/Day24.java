package src.solutions;

import src.meta.DayTemplate;
import src.objects.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Day24 extends DayTemplate {

    public String solve(boolean part1, Scanner in) {
        int[] maxes = new int[3];
        int[] mins = new int[3];
        long answer = 0;
        List<Hailstone> stones = new ArrayList<>();
        while (in.hasNext()) {
            String line = in.nextLine();
            Hailstone tmp = new Hailstone(line);
            stones.add(tmp);
            maxes[0] = Math.max(maxes[0], tmp.xvel);
            maxes[1] = Math.max(maxes[1], tmp.yvel);
            maxes[2] = Math.max(maxes[2], tmp.zvel);
            mins[0] = Math.min(mins[0], tmp.xvel);
            mins[1] = Math.min(mins[1], tmp.yvel);
            mins[2] = Math.min(mins[2], tmp.zvel);
        }
        if (part1) {
            double min = 200000000000000.0;
            double max = 400000000000000.0;
            for (int i = 0; i < stones.size(); i++) {
                for (int j = i + 1; j < stones.size(); j++) {
                    Hailstone first = stones.get(i);
                    Hailstone second = stones.get(j);
                    double denom = (first.xvel * second.yvel) - (first.yvel * second.xvel);
                    if (denom == 0) {
                        if (first.x == second.x && first.y == second.y) {
                            if (first.x >= min && first.x <= max && first.y >= min && first.y <= max) {
                                answer++;
                            }
                        }
                    }
                    double numer1 = ((second.x - first.x) * second.yvel) - ((second.y - first.y) * second.xvel);
                    double numer2 = ((first.x - second.x) * first.yvel) - ((first.y - second.y) * first.xvel);
                    double intersectionX = (numer1 / denom) * first.xvel + first.x;
                    double intersectionY = (numer1 / denom) * first.yvel + first.y;
                    if (intersectionX >= min && intersectionX <= max && intersectionY >= min && intersectionY <= max) {
                        if ((numer1 / denom) > 0 && (numer2 / denom) < 0) {
                            answer++;
                        }
                    }
                }
            }
        } else {
            boolean[] invalidX = new boolean[maxes[0] - mins[0] + 1];
            boolean[] invalidY = new boolean[maxes[1] - mins[1] + 1];
            boolean[] invalidZ = new boolean[maxes[2] - mins[2] + 1];
            for (int i = 0; i < stones.size(); i++) {
                for (int j = 0; j < stones.size(); j++) {
                    Hailstone first = stones.get(i);
                    Hailstone second = stones.get(j);
                    if (first.xvel > second.xvel && first.x > second.x) {
                        for (int k = second.xvel; k <= first.xvel; k++) {
                            invalidX[k - mins[0]] = true;
                        }
                    }
                    if (first.yvel > second.yvel && first.y > second.y) {
                        for (int k = second.yvel; k <= first.yvel; k++) {
                            invalidY[k - mins[1]] = true;
                        }
                    }
                    if (first.zvel > second.zvel && first.z > second.z) {
                        for (int k = second.zvel; k <= first.zvel; k++) {
                            invalidZ[k - mins[2]] = true;
                        }
                    }
                }
            }

            // a smarter search would be to start at 0,0,0 and come up with increasingly larger vectors by magnitude
            for (int i = 0; i < invalidX.length; i++) {
                if (invalidX[i]) {
                    continue;
                }
                for (int j = 0; j < invalidY.length; j++) {
                    if (invalidY[j]) {
                        continue;
                    }
                    for (int k = 0; k < invalidZ.length; k++) {
                        if (invalidZ[k]) {
                            continue;
                        }
                        long[] positions = helper(new Coordinate(i + mins[0], j + mins[1], k + mins[2]), stones);
                        if (positions[0] != -1) {
                            return (positions[0] + positions[1] + positions[2]) + "";
                        }
                    }
                }
            }
        }
        return answer + "";
    }

    public long[] helper(Coordinate v, List<Hailstone> stones) {
        long x = Long.MAX_VALUE;
        long y = Long.MAX_VALUE;
        long z = Long.MAX_VALUE;
        for (int i = 1; i < stones.size(); i++) {
            Hailstone first = stones.get(0);
            Hailstone second = stones.get(i);
            int newFirstXVel = first.xvel - v.x;
            int newSecondXVel = second.xvel - v.x;
            int newFirstYVel = first.yvel - v.y;
            int newSecondYVel = second.yvel - v.y;
            long denom = (newFirstXVel * newSecondYVel) - (newFirstYVel * newSecondXVel);
            if (denom == 0) {
                continue;
            }
            long numer1 = ((second.x - first.x) * newSecondYVel) - ((second.y - first.y) * newSecondXVel);
            long numer2 = ((first.x - second.x) * newFirstYVel) - ((first.y - second.y) * newFirstXVel);
            if ((numer1 / denom) < 0 || (numer2 / denom) > 0) {
                return new long[]{-1, -1, -1};
            }
            long intersectionX = (numer1 / denom) * newFirstXVel + first.x;
            long intersectionY = (numer1 / denom) * newFirstYVel + first.y;
            long intersectionZ = (numer1 / denom) * (first.zvel - v.z) + first.z;
            if (x == Long.MAX_VALUE) {
                x = intersectionX;
            }
            if (y == Long.MAX_VALUE) {
                y = intersectionY;
            }
            if (z == Long.MAX_VALUE) {
                z = intersectionZ;
            }
            if (x != intersectionX || y != intersectionY || z != intersectionZ) {
                return new long[]{-1, -1, -1};
            }
        }
        return new long[]{x, y, z};
    }
}

class Hailstone {
    long x;
    long y;
    long z;
    int xvel;
    int yvel;
    int zvel;

    public Hailstone(String line) {
        String[] ends = line.split("@");
        x = Long.parseLong(ends[0].split(",")[0].trim());
        y = Long.parseLong(ends[0].split(",")[1].trim());
        z = Long.parseLong(ends[0].split(",")[2].trim());
        xvel = Integer.parseInt(ends[1].split(",")[0].trim());
        yvel = Integer.parseInt(ends[1].split(",")[1].trim());
        zvel = Integer.parseInt(ends[1].split(",")[2].trim());
    }
}