package src.solutions;

import src.meta.DayTemplate;

import java.util.Arrays;
import java.util.Scanner;

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

    // The puzzle statement guarantees the graph splits into two components by cutting
    // exactly three wires. Pick source s = first node; pick target t = BFS-farthest node
    // from s (in practice across the cut); run unit-capacity augmenting-path max-flow
    // s->t capped at four paths. Flow of exactly three proves the cut, and the residual
    // s-reachable set is one component. If a fourth augmenting path exists, t was on
    // s's side; retry with the next-farthest candidate. Deterministic and exact.
    private String solvePart1(Scanner in) {
        String text = in.findWithinHorizon("(?s).*", 0);
        char[] buf = text == null ? new char[0] : text.toCharArray();
        NameTable names = new NameTable(buf);
        EdgeSet edges = new EdgeSet();

        int length = buf.length;
        int cursor = 0;
        int source = -1;
        while (cursor < length) {
            char c = buf[cursor];
            if (c == '\n' || c == '\r') {
                source = -1;
                cursor++;
                continue;
            }
            if (c == ' ' || c == '\t' || c == ':') {
                cursor++;
                continue;
            }
            int start = cursor;
            int hash = 0;
            while (cursor < length) {
                char d = buf[cursor];
                if (d == ' ' || d == ':' || d == '\n' || d == '\r' || d == '\t') {
                    break;
                }
                hash = hash * 31 + d;
                cursor++;
            }
            int id = names.intern(start, cursor - start, hash);
            if (source < 0) {
                source = id;
            } else if (source != id) {
                edges.add(source, id);
            }
        }

        int vertexCount = names.size;
        int edgeCount = edges.size;
        if (vertexCount < 2) {
            throw new IllegalArgumentException("At least two components are required");
        }
        int[] edgeA = edges.first;
        int[] edgeB = edges.second;

        int[] adjStart = new int[vertexCount + 1];
        for (int edge = 0; edge < edgeCount; edge++) {
            adjStart[edgeA[edge] + 1]++;
            adjStart[edgeB[edge] + 1]++;
        }
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjStart[vertex + 1] += adjStart[vertex];
        }
        int[] adjEdge = new int[2 * edgeCount];
        int[] adjTo = new int[2 * edgeCount];
        int[] fill = Arrays.copyOf(adjStart, vertexCount);
        for (int edge = 0; edge < edgeCount; edge++) {
            int a = edgeA[edge];
            int b = edgeB[edge];
            adjEdge[fill[a]] = edge;
            adjTo[fill[a]++] = b;
            adjEdge[fill[b]] = edge;
            adjTo[fill[b]++] = a;
        }

        // Plain BFS from vertex 0; the visit order ranks candidates by distance.
        int[] order = new int[vertexCount];
        int[] parentEdge = new int[vertexCount];
        Arrays.fill(parentEdge, -1);
        parentEdge[0] = -2;
        int reached = 1;
        for (int read = 0; read < reached; read++) {
            int vertex = order[read];
            for (int idx = adjStart[vertex]; idx < adjStart[vertex + 1]; idx++) {
                int next = adjTo[idx];
                if (parentEdge[next] == -1) {
                    parentEdge[next] = -2;
                    order[reached++] = next;
                }
            }
        }

        int[] flow = new int[edgeCount];
        int[] queue = new int[vertexCount];
        candidate:
        for (int rank = reached - 1; rank >= 1; rank--) {
            int target = order[rank];
            int totalFlow = 0;
            while (true) {
                Arrays.fill(parentEdge, -1);
                parentEdge[0] = -2;
                queue[0] = 0;
                int write = 1;
                for (int read = 0; read < write && parentEdge[target] == -1; read++) {
                    int vertex = queue[read];
                    for (int idx = adjStart[vertex]; idx < adjStart[vertex + 1]; idx++) {
                        int next = adjTo[idx];
                        if (parentEdge[next] != -1) {
                            continue;
                        }
                        int edge = adjEdge[idx];
                        if (edgeA[edge] == vertex ? flow[edge] < 1 : flow[edge] > -1) {
                            parentEdge[next] = edge;
                            queue[write++] = next;
                        }
                    }
                }
                if (parentEdge[target] == -1) {
                    // No augmenting path: totalFlow is the exact s-t min cut and the
                    // residual s-reachable set (size == write) is s's component.
                    if (totalFlow != REQUIRED_CUT) {
                        throw new IllegalArgumentException(
                                "Expected a three-wire cut, found " + totalFlow);
                    }
                    long size = write;
                    return Long.toString(size * (vertexCount - size));
                }
                if (totalFlow == REQUIRED_CUT) {
                    // A fourth augmenting path exists, so target sits on the source's
                    // side of the three-wire cut. Try the next-farthest candidate.
                    Arrays.fill(flow, 0);
                    continue candidate;
                }
                int vertex = target;
                while (vertex != 0) {
                    int edge = parentEdge[vertex];
                    if (edgeB[edge] == vertex) {
                        flow[edge]++;
                        vertex = edgeA[edge];
                    } else {
                        flow[edge]--;
                        vertex = edgeB[edge];
                    }
                }
                totalFlow++;
            }
        }
        throw new IllegalArgumentException("Expected a three-wire cut, found none");
    }

    private static int mix(int hash) {
        int mixed = hash * 0x9E3779B1;
        return mixed ^ (mixed >>> 16);
    }

    /** Open-addressing interner mapping token character runs to dense node ids. */
    private static final class NameTable {
        private final char[] text;
        private int[] table = new int[1 << 12];
        private int mask = table.length - 1;
        private int[] offset = new int[1 << 11];
        private int[] length = new int[1 << 11];
        private int[] hash = new int[1 << 11];
        int size;

        NameTable(char[] text) {
            this.text = text;
        }

        int intern(int off, int len, int h) {
            int slot = mix(h) & mask;
            while (true) {
                int entry = table[slot];
                if (entry == 0) {
                    return insert(slot, off, len, h);
                }
                int id = entry - 1;
                if (hash[id] == h && length[id] == len && sameName(offset[id], off, len)) {
                    return id;
                }
                slot = (slot + 1) & mask;
            }
        }

        private boolean sameName(int a, int b, int len) {
            for (int i = 0; i < len; i++) {
                if (text[a + i] != text[b + i]) {
                    return false;
                }
            }
            return true;
        }

        private int insert(int slot, int off, int len, int h) {
            int id = size;
            if (id == offset.length) {
                offset = Arrays.copyOf(offset, id * 2);
                length = Arrays.copyOf(length, id * 2);
                hash = Arrays.copyOf(hash, id * 2);
            }
            offset[id] = off;
            length[id] = len;
            hash[id] = h;
            table[slot] = ++size;
            if (size * 2 > mask) {
                grow();
            }
            return id;
        }

        private void grow() {
            int[] old = table;
            table = new int[old.length * 2];
            mask = table.length - 1;
            for (int entry : old) {
                if (entry == 0) {
                    continue;
                }
                int slot = mix(hash[entry - 1]) & mask;
                while (table[slot] != 0) {
                    slot = (slot + 1) & mask;
                }
                table[slot] = entry;
            }
        }
    }

    /** Deduplicating undirected edge list keyed on packed endpoint pairs. */
    private static final class EdgeSet {
        private long[] keys = new long[1 << 13];
        private int mask = keys.length - 1;
        int[] first = new int[1 << 12];
        int[] second = new int[1 << 12];
        int size;

        void add(int u, int v) {
            // low < high, so a packed key is never zero and zero marks empty slots.
            long key = u < v ? ((long) u << 32) | v : ((long) v << 32) | u;
            int slot = (int) ((key * 0x9E3779B97F4A7C15L) >>> 32) & mask;
            while (true) {
                long existing = keys[slot];
                if (existing == key) {
                    return;
                }
                if (existing == 0) {
                    break;
                }
                slot = (slot + 1) & mask;
            }
            keys[slot] = key;
            if (size == first.length) {
                first = Arrays.copyOf(first, size * 2);
                second = Arrays.copyOf(second, size * 2);
            }
            first[size] = (int) (key >>> 32);
            second[size] = (int) key;
            size++;
            if (size * 2 > mask) {
                grow();
            }
        }

        private void grow() {
            long[] old = keys;
            keys = new long[old.length * 2];
            mask = keys.length - 1;
            for (long key : old) {
                if (key == 0) {
                    continue;
                }
                int slot = (int) ((key * 0x9E3779B97F4A7C15L) >>> 32) & mask;
                while (keys[slot] != 0) {
                    slot = (slot + 1) & mask;
                }
                keys[slot] = key;
            }
        }
    }
}
