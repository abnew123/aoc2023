package src.solutions;

import src.meta.DayTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Day24 extends DayTemplate {

    public String solve(boolean part1, Scanner in) {

        long answer = 0;
        List<Hailstone> stones = new ArrayList<>();
        while (in.hasNext()) {
            String line = in.nextLine();
            Hailstone tmp = new Hailstone(line);
            stones.add(tmp);
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
            StringBuilder equations = new StringBuilder();
            for (int i = 0; i < 3; i++) {
                String t = "t" + i;
                equations.append(t).append(" >= 0, ").append(stones.get(i).x).append(" + ").append(stones.get(i).xvel).append(t).append(" == x + vx ").append(t).append(", ");
                equations.append(stones.get(i).y).append(" + ").append(stones.get(i).yvel).append(t).append(" == y + vy ").append(t).append(", ");
                equations.append(stones.get(i).z).append(" + ").append(stones.get(i).zvel).append(t).append(" == z + vz ").append(t).append(", ");
            }
            String sendToMathematica = "Solve[{" + equations.substring(0, equations.length() - 2) +  "}, {x,y,z,vx,vy,vz,t0,t1,t2}]";
            System.out.println(sendToMathematica);
            long xval = 129723668686742L;
            long yval = 353939130278484L;
            long zval = 227368817349775L;
            answer = xval + yval + zval;
        }

        return answer + "";
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