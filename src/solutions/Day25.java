package src.solutions;

import src.meta.DayTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

public class Day25 implements DayTemplate {

    private static final int REQUIRED_CUT = 3;

    @Override
    public String[] fullSolve(Scanner in) {
        return new String[]{solvePart1(in), "Merry Christmas!"};
    }

    @Override
    public String solve(boolean part1, Scanner in) {
        return part1 ? solvePart1(in) : "Merry Christmas!";
    }

    private String solvePart1(Scanner in) {
        Graph graph = parse(in);
        MinCut cut = minimumCut(graph);
        if (cut.weight != REQUIRED_CUT) {
            throw new IllegalArgumentException("Expected a three-wire cut, found " + cut.weight);
        }
        long size = cut.partitionSize;
        return Long.toString(size * (graph.vertexCount - size));
    }

    private Graph parse(Scanner in) {
        Map<String, Integer> ids = new HashMap<>();
        Set<Long> edges = new HashSet<>();
        while (in.hasNextLine()) {
            String line = in.nextLine();
            if (line.isBlank()) {
                continue;
            }
            int colon = line.indexOf(':');
            if (colon < 1) {
                throw new IllegalArgumentException("Invalid component line: " + line);
            }
            String sourceName = line.substring(0, colon).trim();
            if (sourceName.isEmpty()) {
                throw new IllegalArgumentException("Missing component name");
            }
            int source = id(sourceName, ids);
            StringTokenizer targets = new StringTokenizer(line.substring(colon + 1));
            while (targets.hasMoreTokens()) {
                int target = id(targets.nextToken(), ids);
                if (source == target) {
                    continue;
                }
                int low = Math.min(source, target);
                int high = Math.max(source, target);
                edges.add(((long) low << 32) | (high & 0xffffffffL));
            }
        }

        long[] sortedEdges = new long[edges.size()];
        int edgeIndex = 0;
        for (long edge : edges) {
            sortedEdges[edgeIndex++] = edge;
        }
        Arrays.sort(sortedEdges);
        int[] first = new int[sortedEdges.length];
        int[] second = new int[sortedEdges.length];
        int[] degree = new int[ids.size()];
        for (int edge = 0; edge < sortedEdges.length; edge++) {
            first[edge] = (int) (sortedEdges[edge] >>> 32);
            second[edge] = (int) sortedEdges[edge];
            degree[first[edge]]++;
            degree[second[edge]]++;
        }
        int[][] incident = new int[ids.size()][];
        for (int vertex = 0; vertex < incident.length; vertex++) {
            incident[vertex] = new int[degree[vertex]];
        }
        Arrays.fill(degree, 0);
        for (int edge = 0; edge < first.length; edge++) {
            incident[first[edge]][degree[first[edge]]++] = edge;
            incident[second[edge]][degree[second[edge]]++] = edge;
        }
        return new Graph(ids.size(), first, second, incident);
    }

    private int id(String name, Map<String, Integer> ids) {
        Integer existing = ids.get(name);
        if (existing != null) {
            return existing;
        }
        int next = ids.size();
        ids.put(name, next);
        return next;
    }

    // The minimum fixed-source/any-target cut is the exact global minimum cut.
    private MinCut minimumCut(Graph graph) {
        int vertexCount = graph.vertexCount;
        if (vertexCount < 2) {
            throw new IllegalArgumentException("At least two components are required");
        }

        int bestWeight = Integer.MAX_VALUE;
        int bestSize = 0;
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            if (graph.incident[vertex].length < bestWeight) {
                bestWeight = graph.incident[vertex].length;
                bestSize = 1;
            }
        }
        int[] flow = new int[graph.first.length];
        int[] parentEdge = new int[vertexCount];
        int[] queue = new int[vertexCount];
        // The prompt guarantees that the required global cut has exactly three wires,
        // so finding a cut of that size completes the exact search.
        for (int target = 1; target < vertexCount && bestWeight > REQUIRED_CUT; target++) {
            Arrays.fill(flow, 0);
            int totalFlow = 0;
            while (totalFlow < bestWeight) {
                Arrays.fill(parentEdge, -1);
                parentEdge[0] = -2;
                int read = 0;
                int write = 1;
                queue[0] = 0;
                while (read < write && parentEdge[target] == -1) {
                    int vertex = queue[read++];
                    for (int edge : graph.incident[vertex]) {
                        int next;
                        boolean residual;
                        if (graph.first[edge] == vertex) {
                            next = graph.second[edge];
                            residual = flow[edge] < 1;
                        } else {
                            next = graph.first[edge];
                            residual = flow[edge] > -1;
                        }
                        if (residual && parentEdge[next] == -1) {
                            parentEdge[next] = edge;
                            queue[write++] = next;
                        }
                    }
                }
                if (parentEdge[target] == -1) {
                    bestWeight = totalFlow;
                    bestSize = write;
                    break;
                }
                int vertex = target;
                while (vertex != 0) {
                    int edge = parentEdge[vertex];
                    if (graph.second[edge] == vertex) {
                        flow[edge]++;
                        vertex = graph.first[edge];
                    } else {
                        flow[edge]--;
                        vertex = graph.second[edge];
                    }
                }
                totalFlow++;
            }
        }
        return new MinCut(bestWeight, bestSize);
    }

    private record Graph(int vertexCount, int[] first, int[] second, int[][] incident) {}
    private record MinCut(int weight, int partitionSize) {}
}
