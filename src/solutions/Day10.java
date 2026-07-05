package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day10 implements DayTemplate {

    private char[][] grid;
    private int[][] distances;
    private boolean[][] visited;
    private int width;
    private int height;
    private int startX;
    private int startY;

    @Override
    public String[] fullSolve(Scanner in) {
        parse(in);
        long loopLength = markLoop();
        long enclosed = countEnclosedTiles();
        return new String[]{(loopLength / 2) + "", enclosed + ""};
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
        parse(in);
        long loopLength = markLoop();
        long answer = part1 ? loopLength / 2 : countEnclosedTiles();
        return answer + "";
    }

    private long markLoop() {
        // The original solution assumes the start connects to the tile directly below it.
        int prevX = startX;
        int prevY = startY;
        int currX = startX;
        int currY = startY + 1;
        long length = 1;

        visited[prevY][prevX] = true;
        distances[currX * 2 + 1][currY * 2 + 1] = 1;
        distances[currX + prevX + 1][currY + prevY + 1] = 1;

        while (!visited[currY][currX]) {
            visited[currY][currX] = true;
            int[] dirs = directions(grid[currY][currX]);
            int nextX1 = currX + dirs[0];
            int nextY1 = currY + dirs[1];
            int nextX;
            int nextY;
            if (prevX == nextX1 && prevY == nextY1) {
                nextX = currX + dirs[2];
                nextY = currY + dirs[3];
            } else {
                nextX = nextX1;
                nextY = nextY1;
            }

            prevX = currX;
            prevY = currY;
            currX = nextX;
            currY = nextY;
            length++;

            distances[currX * 2 + 1][currY * 2 + 1] = 1;
            distances[currX + prevX + 1][currY + prevY + 1] = 1;
        }
        return length;
    }

    private int[] directions(char pipe) {
        return switch (pipe) {
            case '|' -> new int[]{0, 1, 0, -1};
            case '-' -> new int[]{1, 0, -1, 0};
            case 'F' -> new int[]{1, 0, 0, 1};
            case 'J' -> new int[]{0, -1, -1, 0};
            case '7' -> new int[]{-1, 0, 0, 1};
            case 'L' -> new int[]{0, -1, 1, 0};
            default -> throw new IllegalStateException("Unexpected pipe: " + pipe);
        };
    }

    private long countEnclosedTiles() {
        floodFillOutside();
        long answer = 0;
        for (int x = 1; x < distances.length; x += 2) {
            for (int y = 1; y < distances[x].length; y += 2) {
                if (distances[x][y] == 2) {
                    answer++;
                }
            }
        }
        return answer;
    }

    private void floodFillOutside() {
        int[] queueX = new int[distances.length * distances[0].length];
        int[] queueY = new int[queueX.length];
        int head = 0;
        int tail = 0;
        queueX[tail] = 0;
        queueY[tail++] = 0;
        distances[0][0] = 0;
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        while (head < tail) {
            int x = queueX[head];
            int y = queueY[head++];
            for (int i = 0; i < 4; i++) {
                int nextX = x + dx[i];
                int nextY = y + dy[i];
                if (nextX >= 0 && nextY >= 0 && nextX < distances.length
                        && nextY < distances[0].length && distances[nextX][nextY] == 2) {
                    distances[nextX][nextY] = 0;
                    queueX[tail] = nextX;
                    queueY[tail++] = nextY;
                }
            }
        }
    }

    private void parse(Scanner in) {
        List<String> lines = new ArrayList<>();
        while (in.hasNextLine()) {
            lines.add(in.nextLine());
        }
        height = lines.size();
        width = lines.get(0).length();
        grid = new char[height][width];
        visited = new boolean[height][width];
        distances = new int[width * 2 + 1][height * 2 + 1];
        for (int y = 0; y < height; y++) {
            String line = lines.get(y);
            for (int x = 0; x < width; x++) {
                char ch = line.charAt(x);
                grid[y][x] = ch;
                if (ch == 'S') {
                    startX = x;
                    startY = y;
                } else {
                    distances[x * 2 + 1][y * 2 + 1] = 2;
                }
            }
        }
        for (int x = 0; x < distances.length; x++) {
            for (int y = 0; y < distances[x].length; y++) {
                if (x % 2 == 0 || y % 2 == 0) {
                    distances[x][y] = 2;
                }
            }
        }
    }
}
