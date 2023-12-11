package src.solutions;

import src.meta.DayTemplate;
import src.objects.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day11 extends DayTemplate {

    /**
     * Main solving method.
     *
     * @param part1 The solver will solve part 1 if param is set to true.
     *              The solver will solve part 2 if param is set to false.
     * @param in    The solver will read data from this Scanner.
     * @return Returns answer in string format.
     */
    public String solve(boolean part1, Scanner in) {
        long answer = 0;
        List<List<String>> space = new ArrayList<>();
        int index = 0;
        List<Coordinate> galaxies = new ArrayList<>();
        List<Integer> emptyV = new ArrayList<>();
        List<Integer> emptyH = new ArrayList<>();
        while (in.hasNext()) {
            List<String> tmp = new ArrayList<>();
            String[] line = in.nextLine().split("");
            for (int i = 0; i < line.length; i++) {
                if (line[i].equals("#")) {
                    galaxies.add(new Coordinate(i, index));
                }
                tmp.add(line[i]);
            }
            if (!tmp.contains("#")) {
                emptyH.add(index);
            }
            index++;
            space.add(tmp);
        }
        for (int i = space.get(0).size() - 1; i >= 0; i--) {
            boolean noGalaxy = true;
            for (int j = 0; j < space.size(); j++) {
                if (space.get(j).get(i).equals("#")) {
                    noGalaxy = false;
                    break;
                }
            }
            if (noGalaxy) {
                emptyV.add(i);
            }
        }
        for (Coordinate g1 : galaxies) {
            for (Coordinate g2 : galaxies) {
                if (!g1.equals(g2)) {
                    for (Integer i : emptyV) {
                        if ((g1.x > i && g2.x < i) || (g1.x < i && g2.x > i)) {
                            answer += part1 ? 1 : 999999;
                        }
                    }
                    for (Integer i : emptyH) {
                        if ((g1.y > i && g2.y < i) || (g1.y < i && g2.y > i)) {
                            answer += part1 ? 1 : 999999;
                        }
                    }
                    answer += Math.abs(g1.x - g2.x) + Math.abs(g1.y - g2.y);
                }
            }
        }
        answer /= 2;
        return answer + "";
    }
}