package src.solutions;

import src.meta.DayTemplate;
import src.objects.Coordinate;

import java.util.*;

//Heavily inspired by https://gist.github.com/Voltara/ae028b17ba5cd69fa9b8b912e41e853b

public class Day23 extends DayTemplate {

    static Coordinate[][] nodeGrid = new Coordinate[6][6];

    static Set<State> nextStates = new HashSet<>();

    Map<Coordinate, Set<Coordinate>> neighbors = new HashMap<>();

    public String solve(boolean part1, Scanner in) {
        long answer = 0;
        List<String[]> tmp = new ArrayList<>();
        while (in.hasNext()) {
            String line = in.nextLine();
            tmp.add(line.split(""));
        }
        int[][] grid = new int[tmp.get(0).length][tmp.size()];
        Map<String, Integer> gridBuilder = Map.of(".", 1, "#", 2, ">", 3, "v", 4, "<", 5, "^", 6);
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = gridBuilder.get(tmp.get(j)[i]);
                if (grid[i][j] > 2 && !part1) {
                    grid[i][j] = 1;
                }
            }
        }
        int[] xs = new int[]{1, 0, -1, 0};
        int[] ys = new int[]{0, 1, 0, -1};
        Set<Coordinate> intersections = new HashSet<>();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                int neighbors = 0;
                if (grid[i][j] != 2) {
                    for (int k = 0; k < 4; k++) {
                        Coordinate next = new Coordinate(i + xs[k], j + ys[k]);
                        if (next.x >= 0 && next.y >= 0 && next.x < grid.length && next.y < grid[0].length) {
                            if (grid[next.x][next.y] != 2) {
                                neighbors++;
                            }
                        }
                    }
                }
                if (neighbors == 1 || neighbors > 2) {
                    intersections.add(new Coordinate(i, j));
                }
            }
        }
        for (Coordinate intersection : intersections) {
            bfs(intersection, neighbors, grid, intersections);
        }
        Coordinate start = null;
        Coordinate end = null;
        for (int i = 0; i < grid.length; i++) {
            if (grid[i][0] == 1) {
                start = new Coordinate(i, 0);
            }
            if (grid[i][grid.length - 1] == 1) {
                end = new Coordinate(i, grid.length - 1);
            }
        }
        if (part1) {
            Path path = new Path();
            path.path.add(start);
            path.latest = start;
            Stack<Path> stack = new Stack<>();
            stack.push(path);
            while (stack.size() > 0) {
                Path p = stack.pop();
                Coordinate current = p.latest;
                for (Coordinate next : neighbors.get(current)) {
                    if (next.equals(end)) {
                        if (p.pathLength + next.weight > answer) {
                            answer = p.pathLength + next.weight;
                        }
                        break;
                    }
                    if (p.path.contains(next)) {
                        continue;
                    }
                    Path newPath = new Path(p);
                    newPath.latest = next;
                    newPath.path.add(next);
                    newPath.pathLength += next.weight;
                    stack.push(newPath);
                }
            }
        } else {
            answer += neighbors.get(start).iterator().next().weight;
            answer += neighbors.get(end).iterator().next().weight;
            constructGrid(neighbors, start);
            Set<State> states = new HashSet<>();
            states.add(new State("+      ", 0));
            for (int i = 0; i < 6; i++) {
                for (State state : states) {
                    addNextRow(state, i, 0, " ", new State("", state.val));
                }
                states = nextStates;
                nextStates = new HashSet<>();
            }
            for (State state : states) {
                if (state.state.equals("     +")) {
                    answer += state.val;
                }
            }
        }
        return answer + "";
    }

    private void addNextRow(State state, int row, int column, String left, State possibility) {
        if (column == nodeGrid[row].length) {
            if (validSigns(possibility)) {
                conditionalPut(possibility);
            }
            return;
        }
        List<String> nextConnections = new ArrayList<>();
        String above = state.state.substring(column, column + 1);
        String newState = null;
        if (above.equals(" ")) {
            if (left.equals(" ")) {
                // node has no connections from above or left. It can either not have any connections at all, or start a new sub loop
                nextConnections.add("  ");
                if (column < nodeGrid[row].length - 1) {
                    // if we are in the last column, there's not enough room to start a new loop
                    nextConnections.add("+-");
                }
            }
            else{
                // node has connection from the left but not from above. It can either pass its connection down or to the right
                nextConnections.add(left + above);
                if (column < nodeGrid[row].length - 1) {
                    // if we are in the last column, there's no further node to pass the connection to
                    nextConnections.add(above + left);
                }
            }
        }
        if (above.equals("+")) {
            if (left.equals(" ")) {
                // node has connection from above but not from the eft. It can either pass its connection down or to the right
                if (column < nodeGrid[row].length - 1) {
                    // if we are in the last column, there's no further node to pass the connection to
                    nextConnections.add(" +");
                }
                nextConnections.add("+ ");
            }
            if (left.equals("+")) {
                // node is joining two loops together with different polarity. It cannot take more connections
                // to correct the polarity issue, find the original - that matches with the current + that's being turned into a -. Set that - to a + to keep balance.
                int index = possibility.state.length();
                int amount = 1;
                while (amount != 0) {
                    index++;
                    if (state.state.charAt(index) == '+') {
                        amount++;
                    }
                    if (state.state.charAt(index) == '-') {
                        amount--;
                    }
                }
                newState = state.state.substring(0, index) + "+" + state.state.substring(index + 1);
                nextConnections.add("  !");
            }
            if (left.equals("-")) {
                // node is joining two loops together with correct polarity. It cannot take more connections
                nextConnections.add("  ");
            }
        }
        if (above.equals("-")) {
            // node has connection from above but not from the eft. It can either pass its connection down or to the right
            if (left.equals(" ")) {
                if (column < nodeGrid[row].length - 1) {
                    // if we are in the last column, there's no further node to pass the connection to
                    nextConnections.add(" -");
                }
                nextConnections.add("- ");
            }
            if (left.equals("+")) {
                // The path has split into disparate loops. The case should be rejected
            }
            if (left.equals("-")) {
                // node is joining two loops together with different polarity. It cannot take more connections
                // to correct the polarity issue, find the original + that matches with the current - that's being turned into a +. Set that + to a - to keep balance.
                int index = possibility.state.length();
                int amount = 1;
                while (amount != 0) {
                    index--;
                    if (possibility.state.charAt(index) == '+') {
                        amount--;
                    }
                    if (possibility.state.charAt(index) == '-') {
                        amount++;
                    }
                }
                possibility.state = possibility.state.substring(0, index) + "-" + possibility.state.substring(index + 1);
                nextConnections.add("  ");
            }
        }
        for (String nextConnection : nextConnections) {
            State next = new State(possibility, nextConnection.substring(0, 1));
            if (nextConnection.charAt(1) != ' ') {
                Coordinate node = nodeGrid[row][column];
                Coordinate rightNode = nodeGrid[row][column + 1];
                if (node != null) {
                    if(rightNode == null){
                        rightNode = nodeGrid[row + 1][column+1];
                    }
                    for (Coordinate neighbor : neighbors.get(node)) {
                        if (neighbor.equals(rightNode)) {
                            next.val += neighbor.weight;
                        }
                    }
                }

            }
            if (nextConnection.charAt(0) != ' ' && row < nodeGrid.length - 1) {
                Coordinate node = nodeGrid[row][column];
                Coordinate underNode = nodeGrid[row + 1][column];
                if (node != null) {
                    if(underNode == null){
                        underNode = nodeGrid[row + 1][column + 1];
                    }
                    for (Coordinate neighbor : neighbors.get(node)) {
                        if (neighbor.equals(underNode)) {
                            next.val += neighbor.weight;
                        }
                    }
                }
            }
            if (nextConnection.length() > 2) {
                State fixedState = new State(state, "");
                fixedState.state = newState;
                addNextRow(fixedState, row, column + 1, nextConnection.substring(1, 2), next);
            } else {
                addNextRow(state, row, column + 1, nextConnection.substring(1, 2), next);
            }
        }
    }

    private void conditionalPut(State possibility) {
        boolean found = false;
        for (State state : nextStates) {
            if (state.state.equals(possibility.state)) {
                found = true;
            }
        }
        if (!found) {
            nextStates.add(possibility);
        } else {
            for (State state : nextStates) {
                if (state.state.equals(possibility.state) && possibility.val > state.val) {
                    state.val = possibility.val;
                }
            }
        }
    }

    private boolean validSigns(State possibility) {
        int sign = 0;
        for (String s : possibility.state.split("")) {
            if (s.equals("+")) {
                sign++;
            }
            if (s.equals("-")) {
                sign--;
            }
        }
        return sign == 1;
    }

    private void constructGrid(Map<Coordinate, Set<Coordinate>> neighbors, Coordinate start) {
        Set<Coordinate> used = new HashSet<>();
        nodeGrid[0][0] = neighbors.get(start).iterator().next();
        used.add(nodeGrid[0][0]);
        for (int i = 1; i < nodeGrid[0].length - 1; i++) {
            for (Coordinate neighbor : neighbors.get(nodeGrid[0][i - 1])) {
                if (neighbors.get(neighbor).size() == 3 && !used.contains(neighbor)) {
                    used.add(neighbor);
                    nodeGrid[0][i] = neighbor;
                    break;
                }
            }
        }
        for (int i = 1; i < nodeGrid.length; i++) {
            for (int j = 0; j < nodeGrid[0].length; j++) {
                if(i == 5 && j == 0){
                    continue;
                }
                for (Coordinate neighbor : neighbors.get(nodeGrid[i - 1][(i == 1) ? Math.min(j, 4) : j])) {
                    if (nodeGrid[i][j] == null && !used.contains(neighbor)) {
                        if (i == 1 && j == 4) {
                            if (neighbors.get(neighbor).size() > 3) {
                                used.add(neighbor);
                                nodeGrid[i][j] = neighbor;
                            }
                        } else {
                            if (neighbors.get(neighbor).size() > 1) {
                                used.add(neighbor);
                                nodeGrid[i][j] = neighbor;
                            }
                        }
                    }
                }
            }
        }
    }

    private void bfs(Coordinate start, Map<Coordinate, Set<Coordinate>> neighbors, int[][] grid, Set<Coordinate> intersections) {
        int[] xs = new int[]{1, 0, -1, 0};
        int[] ys = new int[]{0, 1, 0, -1};
        Queue<Coordinate> queue = new LinkedList<>();
        Set<Coordinate> visited = new HashSet<>();
        queue.add(start);
        neighbors.putIfAbsent(start, new HashSet<>());
        while (!queue.isEmpty()) {
            Coordinate curr = queue.poll();
            for (int k = 0; k < 4; k++) {
                if (grid[curr.x][curr.y] > 2 && k != grid[curr.x][curr.y] - 3) {
                    continue;
                }
                Coordinate next = new Coordinate(curr.x + xs[k], curr.y + ys[k]);
                if (next.x >= 0 && next.y >= 0 && next.x < grid.length && next.y < grid[0].length && !visited.contains(next)) {
                    next.weight = curr.weight + 1;
                    if (intersections.contains(next) && !next.equals(start)) {
                        neighbors.get(start).add(next);
                    } else {
                        if (grid[next.x][next.y] != 2) {
                            queue.add(next);
                            visited.add(next);
                        }
                    }
                }
            }
        }
    }
}

class Path {
    Set<Coordinate> path = new HashSet<>();
    Coordinate latest;
    int pathLength = 0;

    public Path(Path root) {
        path.addAll(root.path);
        pathLength = root.pathLength;
    }

    public Path() {

    }
}

class State {
    String state;
    int val;

    public State(String state, int val) {
        this.state = state;
        this.val = val;
    }

    public State(State original, String append) {
        state = original.state + append;
        val = original.val;
    }
}