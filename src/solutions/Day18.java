package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day18 implements DayTemplate {
    static int[] DX = {1, 0, -1, 0}, DY = {0, 1, 0, -1};

    public String solve(boolean part1, Scanner in) {
        long x = 0, y = 0, area = 0, edge = 0;
        while (in.hasNextLine()) {
            String[] p = in.nextLine().split(" ");
            int dir, len;
            if (part1) {
                dir = "RDLU".indexOf(p[0]);
                len = Integer.parseInt(p[1]);
            } else {
                String hex = p[2].substring(2, 8);
                dir = hex.charAt(5) - '0';
                len = Integer.parseInt(hex.substring(0, 5), 16);
            }
            long nx = x + (long) DX[dir] * len, ny = y + (long) DY[dir] * len;
            area += x * ny - nx * y;
            edge += len;
            x = nx;
            y = ny;
        }
        return "" + (Math.abs(area) / 2 + edge / 2 + 1);
    }
}
