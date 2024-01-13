package src.solutions;

import src.meta.DayTemplate;
import src.objects.Coordinate;

import java.util.*;

public class Day10 implements DayTemplate {

    List<List<String>> grid;
    int[][] distances;
    Coordinate prev;
    Set<Coordinate> been;

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
        parse(in);
        //next five lines are a manual step, since S is unknown.
        been.add(prev);
        assert prev != null;
        Coordinate curr = new Coordinate(prev.x, prev.y + 1);
        distances[curr.x * 2 + 1][curr.y * 2 + 1] = 1;
        distances[curr.x + prev.x + 1][curr.y + prev.y + 1] = 1;
        answer++;
        answer += traverseGrid(curr);
        if (!part1) {
            floodFill();
            answer = 0;
            for (int i = 1; i < distances.length; i += 2) {
                for (int j = 1; j < distances[i].length; j += 2) {
                    if (distances[i][j] == 2) {
                        answer += 1;
                    }
                }
            }
        }
        if (part1) {
            answer /= 2;
        }
        return answer + "";
    }

    private void floodFill() {
        List<Coordinate> pointsToCheck = new ArrayList<>();
        pointsToCheck.add(new Coordinate(0, 0));
        int[] xs = new int[]{-1, 1, 0, 0};
        int[] ys = new int[]{0, 0, -1, 1};
        while (!pointsToCheck.isEmpty()) {
            List<Coordinate> tmp = new ArrayList<>();
            for (Coordinate point : pointsToCheck) {
                for (int k = 0; k < 4; k++) {
                    int newx = point.x + xs[k];
                    int newy = point.y + ys[k];
                    if (newx >= 0 && newy >= 0 && newx < distances.length && newy < distances[0].length && distances[newx][newy] == 2) {
                        distances[newx][newy] = 0;
                        tmp.add(new Coordinate(newx, newy));
                    }
                }
                pointsToCheck = tmp;
            }
        }
    }

    private int traverseGrid(Coordinate curr) {
        Map<String, Integer> types = Map.of("|", 10102, "-", 12010, "F", 11001, "J", 10220, "7", 12001, "L", 10210);
        int answer = 0;
        while (!been.contains(curr)) {
            Coordinate tmp = curr;
            answer += 1;
            Integer pipe = types.get(grid.get(curr.y).get(curr.x));
            int x1 = (pipe / 1000 % 10) == 2 ? -1 : (pipe / 1000 % 10);
            int y1 = (pipe / 100 % 10) == 2 ? -1 : (pipe / 100 % 10);
            int x2 = (pipe / 10 % 10) == 2 ? -1 : (pipe / 10 % 10);
            int y2 = (pipe % 10) == 2 ? -1 : (pipe % 10);
            Coordinate case1 = new Coordinate(curr.x + x1, curr.y + y1);
            if (prev.x == case1.x && prev.y == case1.y) {
                curr = new Coordinate(curr.x + x2, curr.y + y2);
            } else {
                curr = case1;
            }
            prev = tmp;
            been.add(prev);
            distances[curr.x * 2 + 1][curr.y * 2 + 1] = 1;
            distances[curr.x + prev.x + 1][curr.y + prev.y + 1] = 1;
        }
        answer++;
        return answer;
    }

    private void parse(Scanner in) {
        grid = new ArrayList<>();
        while (in.hasNext()) {
            String line = in.nextLine();
            grid.add(Arrays.stream(line.split("")).toList());
        }
        distances = new int[grid.size() * 2 + 1][grid.get(0).size() * 2 + 1];
        prev = null;
        been = new HashSet<>();
        for (int i = 0; i < distances.length; i++) {
            for (int j = 0; j < distances[0].length; j++) {
                if (i % 2 == 1 && j % 2 == 1 && grid.get(i / 2).get(j / 2).equals("S")) {
                    prev = new Coordinate(j / 2, i / 2);
                } else {
                    distances[i][j] = 2;
                }
            }
        }
    }
}