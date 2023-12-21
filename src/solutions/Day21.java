package src.solutions;

import src.meta.DayTemplate;
import src.objects.Coordinate;

import java.util.*;

public class Day21 extends DayTemplate {

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
        List<String[]> tmp = new ArrayList<>();
        while (in.hasNext()) {
            String line = in.nextLine();
            tmp.add(line.split(""));
        }
        int x = 0;
        int y = 0;
        int[][] grid = new int[tmp.get(0).length][tmp.size()];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (tmp.get(j)[i].equals("S")) {
                    grid[i][j] = 9999;
                    x = i;
                    y = j;
                } else {
                    grid[i][j] = tmp.get(j)[i].equals("#") ? -1 : 9999;
                }
            }
        }


        Map<Coordinate, Integer> distances = new HashMap<>();
        List<Coordinate> reachablePoints = new ArrayList<>();
        reachablePoints.add(new Coordinate(x, y));
        distances.put(new Coordinate(x, y), 0);
        int[] xs = new int[]{-1, 1, 0, 0};
        int[] ys = new int[]{0, 0, -1, 1};
        int index = 0;
        List<Long> totals = new ArrayList<>();
        List<Long> deltas = new ArrayList<>();
        List<Long> deltaDeltas = new ArrayList<>();
        long totalReached = 0;
        while (index < (part1?64: 1000)) {
            index++;
            List<Coordinate> tmp2 = new ArrayList<>();
            for (Coordinate c : reachablePoints) {
                for (int i = 0; i < 4; i++) {
                    int newx = c.x + xs[i];
                    int newy = c.y + ys[i];
                    Coordinate candidate = new Coordinate(newx, newy);
                    if (distances.get(candidate) == null) {
                        if (grid[((newx % grid.length) + grid.length) % grid.length][((newy % grid.length) + grid.length) % grid.length] != -1) {
                            tmp2.add(candidate);
                            distances.put(candidate, index);
                        }
                    }
                }
            }
            if (index % 2 == (part1?0:1)) {
                totalReached += tmp2.size();
                if (index % 262 == 65) {
                    totals.add(totalReached);
                    int currTotals= totals.size();
                    if(currTotals > 1){
                        deltas.add(totals.get(currTotals - 1) - totals.get(currTotals - 2));
                    }
                    int currDeltas= deltas.size();
                    if(currDeltas > 1){
                        deltaDeltas.add(deltas.get(currDeltas - 1) - deltas.get(currDeltas - 2));
                    }
                    if(deltaDeltas.size() > 1){
                        break;
                    }
                }

            }
            reachablePoints = tmp2;
        }
        if(part1){
            answer = totalReached+1; //to account for the origin location
        }
        if(!part1){
            long neededLoopCount = 26501365 / 262 - 1;
            long currentLoopCount = index / 262 - 1;
            long deltaLoopCount = neededLoopCount - currentLoopCount;
            long deltaLoopCountTriangular = (neededLoopCount * (neededLoopCount + 1))/2 - (currentLoopCount * (currentLoopCount + 1))/2;
            long deltaDelta = deltaDeltas.get(deltaDeltas.size() - 1);
            long initialDelta = deltas.get(0);
            answer = (deltaDelta * deltaLoopCountTriangular + initialDelta * deltaLoopCount + totalReached );
        }
        return answer + "";
    }
}
