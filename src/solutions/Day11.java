package src.solutions;

import src.meta.DayTemplate;
import src.objects.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day11 implements DayTemplate {
    
    List<Coordinate> galaxies;
    List<Integer> emptyV;
    List<Integer> emptyH;


    
    public String solve(boolean part1, Scanner in) {
        parse(in);
        long ans = getUnstretched();
        ans += getStretched() * (part1?1:999999L);
        return ans + "";
    }

    long getUnstretched(){
        long ans = 0;
        for (int i = 0; i < galaxies.size(); i++) {
            for (int j = i + 1; j < galaxies.size(); j++) {
                Coordinate g1 = galaxies.get(i);
                Coordinate g2 = galaxies.get(j);
                ans += Math.abs(g1.x - g2.x) + Math.abs(g1.y - g2.y);
            }
        }
        return ans;
    }

    long getStretched(){
        long ans = 0;
        for (Integer ind : emptyV) {
            int left = 0;
            for (Coordinate g : galaxies) {
                if (g.x < ind) {
                    left++;
                }
            }
            ans += (long) left * (galaxies.size() - left);
        }
        for (Integer ind : emptyH) {
            int up = 0;
            for (Coordinate g : galaxies) {
                if (g.y < ind) {
                    up++;
                }
            }
            ans += (long) up * (galaxies.size() - up);
        }
        return ans;
    }

    void parse(Scanner in) {
        List<List<String>> space = new ArrayList<>();
        int index = 0;
        galaxies = new ArrayList<>();
        emptyV = new ArrayList<>();
        emptyH = new ArrayList<>();
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
        for (int i = 0; i < space.get(0).size(); i++) {
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
    }

}