package src.solutions;

import src.meta.DayTemplate;

import java.util.Arrays;
import java.util.Scanner;

/**
 * 2023 Day 20 (Pulse Propagation) as a dense int-indexed pulse simulator.
 *
 * <p>The module graph is parsed once (single slurp, manual scanning, no regex
 * splits) into flat arrays. Every (source, target) edge is pre-encoded as one
 * long carrying the destination id (bits 1..24), the destination
 * conjunction's global memory-bit index (bits 25..), and a level bit (bit 0),
 * so firing a module is a straight copy of precomputed constants, OR'd with
 * the output level, into a preallocated long ring. Pulses to untyped sink
 * modules (rx, output, ...) are never enqueued; they are counted through each
 * module's precomputed total fan-out. Conjunction state is a shared long[]
 * bitset plus a per-module remembered-high count, so the high/low decision is
 * O(1) with no per-input scan. Flip-flop state is a small int array.
 *
 * <p>Part 2 locates the single conjunction feeding rx and records each of its
 * inputs' first-high press inside the same simulation that serves part 1's
 * 1000 presses, continuing only as long as needed. The standard periodicity
 * assumption is validated cheaply: when a feeder first fires high, its
 * upstream subsystem (every stateful module that can reach it) must be back
 * in its initial state at the end of that press, which proves the feeder
 * fires high exactly at multiples of that press count. Feeders that fail the
 * check are validated by observing their second high (must be exactly twice
 * the first). If validation still fails, or the graph does not have the
 * one-conjunction-into-rx shape, the solver falls back to direct simulation
 * until rx receives a low pulse, so it stays puzzle-general. Recorded press
 * counts include the button press that completes each feeder, and the final
 * combination uses the LCM of the validated periods. Inputs without rx keep
 * the incumbent behavior (part 2 answer 1).
 */
public class Day20 implements DayTemplate {

    private static final int T_BROADCAST = 0;
    private static final int T_FLIP = 1;
    private static final int T_CONJ = 2;
    private static final int T_CONJ_WATCH = 3;
    private static final int T_FLIP_WATCH = 4;
    private static final int T_SINK = 5;

    private static final int MODE_NONE = 0;
    private static final int MODE_STANDARD = 1;
    private static final int MODE_DIRECT = 2;

    private static final int PART1_PRESSES = 1000;
    private static final int STANDARD_CAP = 200_000;
    private static final int DIRECT_CAP = 30_000_000;
    private static final int[] EMPTY = new int[0];

    // Parsed graph.
    private int moduleCount;
    private int[] types;
    private int[][] rawTargets;
    private int[] fanAll;
    private int broadcasterId = -1;
    private int rxId = -1;

    // Hot CSR (sink targets excluded), one encoded long per edge.
    private long[] flat;
    private int[] off;
    private int maxFan;

    // Conjunction memory layout.
    private int[] need;
    private int memWords;

    // Part 2 structure.
    private int mode;
    private int[] feeders = EMPTY;
    private int[][] scopeOf;

    // Name interning scratch (only used during build).
    private String text;
    private int[] keyStart;
    private int[] keyEnd;
    private int[] keyId;
    private int tableCap;
    private int idCount;
    private int[] typesTmp;
    private int[][] targetsTmp;

    @Override
    public String solve(boolean part1, Scanner in) {
        build(readAll(in));
        long[] answers = run(part1, !part1);
        return Long.toString(part1 ? answers[0] : answers[1]);
    }

    @Override
    public String[] fullSolve(Scanner in) {
        build(readAll(in));
        long[] answers = run(true, true);
        return new String[]{Long.toString(answers[0]), Long.toString(answers[1])};
    }

    private static String readAll(Scanner in) {
        return in.useDelimiter("\\A").hasNext() ? in.next() : "";
    }

    // ------------------------------------------------------------------
    // Simulation
    // ------------------------------------------------------------------

    private long[] run(boolean doPart1, boolean doPart2) {
        int n = moduleCount;
        int[] ts = types;
        long[] fl = flat;
        int[] of = off;
        int[] fa = fanAll;
        int[] nd = need;
        int[] ff = new int[n];
        long[] mw = new long[memWords];
        int[] ch = new int[n];
        long[] cnts = new long[2];

        boolean watching = mode == MODE_STANDARD;
        boolean part2InLoop = doPart2 && watching;
        int feederCount = watching ? feeders.length : 0;
        long[] first = new long[n];
        long[] second = new long[n];
        boolean[] validated = new boolean[n];
        int[] pending = new int[feederCount];
        int pendingN = 0;
        int remaining = feederCount;
        boolean needSecond = false;

        int bc = broadcasterId;
        int bcStart = bc >= 0 ? of[bc] : 0;
        int bcEnd = bc >= 0 ? of[bc + 1] : 0;
        long bcFan = bc >= 0 ? fa[bc] : 0;
        int mf = maxFan;
        long[] q = new long[8192];
        long part1Answer = 0;

        for (int press = 1;
             (doPart1 && press <= PART1_PRESSES)
                     || (part2InLoop && remaining > 0 && press <= STANDARD_CAP);
             press++) {
            int h = 0;
            int t = 0;
            cnts[0] += 1 + bcFan;
            for (int i = bcStart; i < bcEnd; i++) {
                q[t++] = fl[i];
            }
            while (h < t) {
                if (t + mf > q.length) {
                    q = Arrays.copyOf(q, q.length << 1);
                }
                long p = q[h++];
                int dst = ((int) (p >>> 1)) & 0xFFFFFF;
                int lvl = (int) p & 1;
                switch (ts[dst]) {
                    case T_FLIP -> {
                        if (lvl == 0) {
                            int st = ff[dst] ^ 1;
                            ff[dst] = st;
                            cnts[st] += fa[dst];
                            long lv = st;
                            for (int i = of[dst], e = of[dst + 1]; i < e; i++) {
                                q[t++] = fl[i] | lv;
                            }
                        }
                    }
                    case T_CONJ -> {
                        int g = (int) (p >>> 25);
                        long word = mw[g >>> 6];
                        if (((int) (word >>> g) & 1) != lvl) {
                            mw[g >>> 6] = word ^ (1L << g);
                            ch[dst] += (lvl << 1) - 1;
                        }
                        int out = ch[dst] == nd[dst] ? 0 : 1;
                        cnts[out] += fa[dst];
                        long lv = out;
                        for (int i = of[dst], e = of[dst + 1]; i < e; i++) {
                            q[t++] = fl[i] | lv;
                        }
                    }
                    case T_BROADCAST -> {
                        cnts[lvl] += fa[dst];
                        long lv = lvl;
                        for (int i = of[dst], e = of[dst + 1]; i < e; i++) {
                            q[t++] = fl[i] | lv;
                        }
                    }
                    case T_CONJ_WATCH -> {
                        int g = (int) (p >>> 25);
                        long word = mw[g >>> 6];
                        if (((int) (word >>> g) & 1) != lvl) {
                            mw[g >>> 6] = word ^ (1L << g);
                            ch[dst] += (lvl << 1) - 1;
                        }
                        int out = ch[dst] == nd[dst] ? 0 : 1;
                        cnts[out] += fa[dst];
                        long lv = out;
                        for (int i = of[dst], e = of[dst + 1]; i < e; i++) {
                            q[t++] = fl[i] | lv;
                        }
                        if (out == 1) {
                            if (first[dst] == 0) {
                                first[dst] = press;
                                pending[pendingN++] = dst;
                            } else if (needSecond && !validated[dst] && second[dst] == 0) {
                                second[dst] = press;
                                remaining--;
                            }
                        }
                    }
                    case T_FLIP_WATCH -> {
                        if (lvl == 0) {
                            int st = ff[dst] ^ 1;
                            ff[dst] = st;
                            cnts[st] += fa[dst];
                            long lv = st;
                            for (int i = of[dst], e = of[dst + 1]; i < e; i++) {
                                q[t++] = fl[i] | lv;
                            }
                            if (st == 1) {
                                if (first[dst] == 0) {
                                    first[dst] = press;
                                    pending[pendingN++] = dst;
                                } else if (needSecond && !validated[dst] && second[dst] == 0) {
                                    second[dst] = press;
                                    remaining--;
                                }
                            }
                        }
                    }
                    default -> {
                    }
                }
            }
            if (press == PART1_PRESSES) {
                part1Answer = cnts[0] * cnts[1];
            }
            if (pendingN != 0) {
                for (int i = 0; i < pendingN; i++) {
                    int f = pending[i];
                    if (scopeAtInitial(f, ff, ch)) {
                        validated[f] = true;
                        remaining--;
                    } else {
                        needSecond = true;
                    }
                }
                pendingN = 0;
            }
        }

        long part2Answer = 1;
        if (doPart2) {
            if (mode == MODE_DIRECT) {
                part2Answer = directSim();
            } else if (mode == MODE_STANDARD) {
                boolean fallback = remaining > 0;
                if (!fallback) {
                    for (int f : feeders) {
                        if (!validated[f] && second[f] != 2L * first[f]) {
                            fallback = true;
                            break;
                        }
                    }
                }
                if (fallback) {
                    part2Answer = directSim();
                } else {
                    for (int f : feeders) {
                        part2Answer = lcm(part2Answer, first[f]);
                    }
                }
            }
        }
        return new long[]{part1Answer, part2Answer};
    }

    private boolean scopeAtInitial(int feeder, int[] ff, int[] ch) {
        for (int m : scopeOf[feeder]) {
            int t = types[m];
            if (t == T_FLIP || t == T_FLIP_WATCH) {
                if (ff[m] != 0) {
                    return false;
                }
            } else if (ch[m] != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Puzzle-general fallback: fresh state, press until rx receives a low
     * pulse (detected at the moment its sender fires). Returns 0 if the cap
     * is exhausted, which only happens on degenerate graphs where rx never
     * receives a low pulse within the cap.
     */
    private long directSim() {
        int n = moduleCount;
        int[] ts = types;
        long[] fl = flat;
        int[] of = off;
        int[] nd = need;
        int[] ff = new int[n];
        long[] mw = new long[memWords];
        int[] ch = new int[n];
        boolean[] toRx = new boolean[n];
        for (int u = 0; u < n; u++) {
            for (int v : rawTargets[u]) {
                if (v == rxId) {
                    toRx[u] = true;
                }
            }
        }
        int bc = broadcasterId;
        int bcStart = bc >= 0 ? of[bc] : 0;
        int bcEnd = bc >= 0 ? of[bc + 1] : 0;
        boolean bcToRx = bc >= 0 && toRx[bc];
        int mf = maxFan;
        long[] q = new long[8192];

        for (int press = 1; press <= DIRECT_CAP; press++) {
            if (bcToRx) {
                return press;
            }
            int h = 0;
            int t = 0;
            for (int i = bcStart; i < bcEnd; i++) {
                q[t++] = fl[i];
            }
            while (h < t) {
                if (t + mf > q.length) {
                    q = Arrays.copyOf(q, q.length << 1);
                }
                long p = q[h++];
                int dst = ((int) (p >>> 1)) & 0xFFFFFF;
                int lvl = (int) p & 1;
                int type = ts[dst];
                if (type == T_FLIP || type == T_FLIP_WATCH) {
                    if (lvl == 0) {
                        int st = ff[dst] ^ 1;
                        ff[dst] = st;
                        if (st == 0 && toRx[dst]) {
                            return press;
                        }
                        long lv = st;
                        for (int i = of[dst], e = of[dst + 1]; i < e; i++) {
                            q[t++] = fl[i] | lv;
                        }
                    }
                } else if (type == T_CONJ || type == T_CONJ_WATCH) {
                    int g = (int) (p >>> 25);
                    long word = mw[g >>> 6];
                    if (((int) (word >>> g) & 1) != lvl) {
                        mw[g >>> 6] = word ^ (1L << g);
                        ch[dst] += (lvl << 1) - 1;
                    }
                    int out = ch[dst] == nd[dst] ? 0 : 1;
                    if (out == 0 && toRx[dst]) {
                        return press;
                    }
                    long lv = out;
                    for (int i = of[dst], e = of[dst + 1]; i < e; i++) {
                        q[t++] = fl[i] | lv;
                    }
                } else if (type == T_BROADCAST) {
                    if (lvl == 0 && toRx[dst]) {
                        return press;
                    }
                    long lv = lvl;
                    for (int i = of[dst], e = of[dst + 1]; i < e; i++) {
                        q[t++] = fl[i] | lv;
                    }
                }
            }
        }
        return 0;
    }

    private static long gcd(long a, long b) {
        while (b != 0) {
            long r = a % b;
            a = b;
            b = r;
        }
        return a;
    }

    private static long lcm(long a, long b) {
        return a / gcd(a, b) * b;
    }

    // ------------------------------------------------------------------
    // Parsing and graph construction
    // ------------------------------------------------------------------

    private void build(String s) {
        text = s;
        tableCap = 1024;
        keyStart = new int[tableCap];
        keyEnd = new int[tableCap];
        keyId = new int[tableCap];
        Arrays.fill(keyStart, -1);
        idCount = 0;
        typesTmp = new int[128];
        targetsTmp = new int[128][];
        Arrays.fill(typesTmp, T_SINK);

        int len = s.length();
        int[] tbuf = new int[64];
        int pos = 0;
        while (pos < len) {
            char c = s.charAt(pos);
            if (c == '\n' || c == '\r') {
                pos++;
                continue;
            }
            int type = T_BROADCAST;
            if (c == '%') {
                type = T_FLIP;
                pos++;
            } else if (c == '&') {
                type = T_CONJ;
                pos++;
            }
            int nameStart = pos;
            while (pos < len && s.charAt(pos) != ' ') {
                pos++;
            }
            int source = intern(nameStart, pos);
            pos += 4; // " -> "
            int targetCount = 0;
            while (pos < len) {
                int targetStart = pos;
                while (pos < len) {
                    char d = s.charAt(pos);
                    if (d == ',' || d == '\n' || d == '\r') {
                        break;
                    }
                    pos++;
                }
                if (targetCount == tbuf.length) {
                    tbuf = Arrays.copyOf(tbuf, targetCount * 2);
                }
                tbuf[targetCount++] = intern(targetStart, pos);
                if (pos < len && s.charAt(pos) == ',') {
                    pos += 2; // ", "
                } else {
                    break;
                }
            }
            typesTmp[source] = type;
            targetsTmp[source] = Arrays.copyOf(tbuf, targetCount);
        }

        moduleCount = idCount;
        if (moduleCount >= (1 << 24)) {
            throw new IllegalStateException("module count exceeds encoding capacity");
        }
        types = Arrays.copyOf(typesTmp, moduleCount);
        rawTargets = new int[moduleCount][];
        fanAll = new int[moduleCount];
        for (int u = 0; u < moduleCount; u++) {
            rawTargets[u] = targetsTmp[u] == null ? EMPTY : targetsTmp[u];
            fanAll[u] = rawTargets[u].length;
        }
        broadcasterId = lookupLiteral("broadcaster");
        rxId = lookupLiteral("rx");
        text = null;
        keyStart = null;
        keyEnd = null;
        keyId = null;
        typesTmp = null;
        targetsTmp = null;

        // Conjunction memory layout: one global bit per (input edge -> conj).
        need = new int[moduleCount];
        long totalIn = 0;
        for (int u = 0; u < moduleCount; u++) {
            for (int v : rawTargets[u]) {
                if (types[v] == T_CONJ) {
                    need[v]++;
                    totalIn++;
                }
            }
        }
        if (totalIn >= (1L << 31)) {
            throw new IllegalStateException("conjunction input count exceeds encoding capacity");
        }
        int[] cursor = new int[moduleCount];
        int base = 0;
        for (int v = 0; v < moduleCount; v++) {
            if (types[v] == T_CONJ) {
                cursor[v] = base;
                base += need[v];
            }
        }
        memWords = (base + 63) >>> 6;

        // Hot CSR excluding sink targets.
        off = new int[moduleCount + 1];
        maxFan = 0;
        for (int u = 0; u < moduleCount; u++) {
            int rowLen = 0;
            for (int v : rawTargets[u]) {
                if (types[v] != T_SINK) {
                    rowLen++;
                }
            }
            off[u + 1] = off[u] + rowLen;
            if (rowLen > maxFan) {
                maxFan = rowLen;
            }
        }
        flat = new long[off[moduleCount]];
        int k = 0;
        for (int u = 0; u < moduleCount; u++) {
            for (int v : rawTargets[u]) {
                long g = 0;
                if (types[v] == T_CONJ) {
                    g = cursor[v]++;
                }
                if (types[v] != T_SINK) {
                    flat[k++] = (g << 25) | ((long) v << 1);
                }
            }
        }

        analyzeStructure();
    }

    private void analyzeStructure() {
        mode = MODE_NONE;
        feeders = EMPTY;
        scopeOf = null;
        if (rxId < 0) {
            return;
        }
        int writer = -1;
        int writerCount = 0;
        for (int u = 0; u < moduleCount; u++) {
            for (int v : rawTargets[u]) {
                if (v == rxId && u != writer) {
                    writer = u;
                    writerCount++;
                }
            }
        }
        if (writerCount != 1 || types[writer] != T_CONJ) {
            mode = MODE_DIRECT;
            return;
        }
        mode = MODE_STANDARD;
        boolean[] isFeeder = new boolean[moduleCount];
        int feederCount = 0;
        for (int u = 0; u < moduleCount; u++) {
            for (int v : rawTargets[u]) {
                if (v == writer && !isFeeder[u]) {
                    isFeeder[u] = true;
                    feederCount++;
                }
            }
        }
        feeders = new int[feederCount];
        int fi = 0;
        for (int u = 0; u < moduleCount; u++) {
            if (isFeeder[u]) {
                feeders[fi++] = u;
                if (types[u] == T_CONJ) {
                    types[u] = T_CONJ_WATCH;
                } else if (types[u] == T_FLIP) {
                    types[u] = T_FLIP_WATCH;
                }
            }
        }

        // Reverse adjacency for scope computation.
        int[] revOff = new int[moduleCount + 1];
        for (int u = 0; u < moduleCount; u++) {
            for (int v : rawTargets[u]) {
                revOff[v + 1]++;
            }
        }
        for (int v = 0; v < moduleCount; v++) {
            revOff[v + 1] += revOff[v];
        }
        int[] revFlat = new int[revOff[moduleCount]];
        int[] fill = new int[moduleCount];
        for (int u = 0; u < moduleCount; u++) {
            for (int v : rawTargets[u]) {
                revFlat[revOff[v] + fill[v]++] = u;
            }
        }

        scopeOf = new int[moduleCount][];
        int[] stack = new int[moduleCount];
        int[] scope = new int[moduleCount];
        for (int f : feeders) {
            boolean[] visited = new boolean[moduleCount];
            int top = 0;
            int scopeN = 0;
            visited[f] = true;
            stack[top++] = f;
            while (top > 0) {
                int m = stack[--top];
                int t = types[m];
                boolean isFlip = t == T_FLIP || t == T_FLIP_WATCH;
                boolean isConj = t == T_CONJ || t == T_CONJ_WATCH;
                if (isFlip || (isConj && need[m] >= 2)) {
                    scope[scopeN++] = m;
                }
                for (int i = revOff[m], e = revOff[m + 1]; i < e; i++) {
                    int u = revFlat[i];
                    if (!visited[u]) {
                        visited[u] = true;
                        stack[top++] = u;
                    }
                }
            }
            scopeOf[f] = Arrays.copyOf(scope, scopeN);
        }
    }

    private int intern(int start, int end) {
        if (idCount * 2 >= tableCap) {
            growTable();
        }
        int mask = tableCap - 1;
        int h = 0;
        for (int i = start; i < end; i++) {
            h = h * 31 + text.charAt(i);
        }
        int slot = h & mask;
        while (true) {
            int ks = keyStart[slot];
            if (ks < 0) {
                keyStart[slot] = start;
                keyEnd[slot] = end;
                keyId[slot] = idCount;
                if (idCount == typesTmp.length) {
                    typesTmp = Arrays.copyOf(typesTmp, idCount * 2);
                    Arrays.fill(typesTmp, idCount, idCount * 2, T_SINK);
                    targetsTmp = Arrays.copyOf(targetsTmp, idCount * 2);
                }
                return idCount++;
            }
            if (keyEnd[slot] - ks == end - start && regionMatches(ks, start, end)) {
                return keyId[slot];
            }
            slot = (slot + 1) & mask;
        }
    }

    private boolean regionMatches(int aStart, int bStart, int bEnd) {
        for (int i = bStart; i < bEnd; i++) {
            if (text.charAt(aStart + i - bStart) != text.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private void growTable() {
        int newCap = tableCap * 2;
        int[] ns = new int[newCap];
        int[] ne = new int[newCap];
        int[] ni = new int[newCap];
        Arrays.fill(ns, -1);
        int mask = newCap - 1;
        for (int i = 0; i < tableCap; i++) {
            int ks = keyStart[i];
            if (ks < 0) {
                continue;
            }
            int h = 0;
            for (int j = ks; j < keyEnd[i]; j++) {
                h = h * 31 + text.charAt(j);
            }
            int slot = h & mask;
            while (ns[slot] >= 0) {
                slot = (slot + 1) & mask;
            }
            ns[slot] = ks;
            ne[slot] = keyEnd[i];
            ni[slot] = keyId[i];
        }
        keyStart = ns;
        keyEnd = ne;
        keyId = ni;
        tableCap = newCap;
    }

    private int lookupLiteral(String name) {
        int mask = tableCap - 1;
        int h = 0;
        for (int i = 0; i < name.length(); i++) {
            h = h * 31 + name.charAt(i);
        }
        int slot = h & mask;
        while (true) {
            int ks = keyStart[slot];
            if (ks < 0) {
                return -1;
            }
            if (keyEnd[slot] - ks == name.length() && literalMatches(ks, name)) {
                return keyId[slot];
            }
            slot = (slot + 1) & mask;
        }
    }

    private boolean literalMatches(int start, String name) {
        for (int i = 0; i < name.length(); i++) {
            if (text.charAt(start + i) != name.charAt(i)) {
                return false;
            }
        }
        return true;
    }
}
