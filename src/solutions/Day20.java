package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day20 implements DayTemplate {
    static final int OUT = -1, BC = 0, FF = 1, CJ = 2;
    Map<String, Integer> ids;
    ArrayList<Integer> tl;
    ArrayList<int[]> ol;
    int button, broadcaster, rx = -1, head, tail;
    int[] type, qi = new int[256], qt = new int[256];
    int[][] out, inputs;
    boolean[] flip, qh;
    boolean[][] mem;

    public String solve(boolean part1, Scanner in) {
        build(in);
        long ans = part1 ? pulses() : cycles();
        return "" + ans;
    }

    void build(Scanner in) {
        ids = new HashMap<>();
        tl = new ArrayList<>();
        ol = new ArrayList<>();
        button = id("button");
        broadcaster = id("broadcaster");
        while (in.hasNextLine()) {
            String[] s = in.nextLine().split(" -> "), ts = s[1].split(", ");
            int t = s[0].charAt(0) == '%' ? FF : s[0].charAt(0) == '&' ? CJ : BC;
            String name = t == BC ? s[0] : s[0].substring(1);
            int src = id(name);
            tl.set(src, t);
            int[] a = new int[ts.length];
            for (int i = 0; i < ts.length; i++) {
                a[i] = id(ts[i]);
                if (ts[i].equals("rx")) {
                    rx = src;
                }
            }
            ol.set(src, a);
        }
        int n = ids.size();
        type = new int[n];
        out = new int[n][];
        ArrayList<ArrayList<Integer>> ins = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            type[i] = tl.get(i);
            out[i] = ol.get(i);
            ins.add(new ArrayList<>());
        }
        for (int i = 0; i < n; i++) {
            for (int j : out[i]) {
                if (type[j] == CJ) {
                    ins.get(j).add(i);
                }
            }
        }
        inputs = new int[n][];
        mem = new boolean[n][];
        for (int i = 0; i < n; i++) {
            inputs[i] = ins.get(i).stream().mapToInt(Integer::intValue).toArray();
            if (type[i] == CJ) {
                mem[i] = new boolean[inputs[i].length];
            }
        }
        flip = new boolean[n];
        qh = new boolean[256];
    }

    int id(String s) {
        Integer old = ids.get(s);
        if (old != null) {
            return old;
        }
        int n = ids.size();
        ids.put(s, n);
        tl.add(OUT);
        ol.add(new int[0]);
        return n;
    }

    long pulses() {
        long hi = 0, lo = 0;
        for (int i = 0; i < 1000; i++) {
            start();
            while (head < tail) {
                if (qh[head]) {
                    hi++;
                } else {
                    lo++;
                }
                send(qt[head], qi[head], qh[head++]);
            }
        }
        return hi * lo;
    }

    long cycles() {
        int[] want = inputsTo(rx), seen = new int[want.length];
        for (int press = 1, found = 0; press < 10000 && found < want.length; press++) {
            start();
            while (head < tail) {
                for (int i = 0; qh[head] && i < want.length; i++) {
                    if (qi[head] == want[i] && seen[i] == 0) {
                        seen[i] = press;
                        found++;
                    }
                }
                send(qt[head], qi[head], qh[head++]);
            }
        }
        long ans = 1;
        for (int n : seen) {
            ans *= n;
        }
        return ans;
    }

    void start() {
        head = tail = 0;
        add(false, broadcaster, button);
    }

    void send(int tar, int input, boolean high) {
        if (type[tar] == OUT) {
            return;
        }
        if (type[tar] == FF) {
            if (high) {
                return;
            }
            flip[tar] = !flip[tar];
            high = flip[tar];
        } else if (type[tar] == CJ) {
            boolean all = true;
            for (int i = 0; i < inputs[tar].length; i++) {
                if (inputs[tar][i] == input) {
                    mem[tar][i] = high;
                }
                all &= mem[tar][i];
            }
            high = !all;
        } else {
            high = false;
        }
        for (int t : out[tar]) {
            add(high, t, tar);
        }
    }

    void add(boolean high, int tar, int input) {
        if (tail == qt.length) {
            qt = Arrays.copyOf(qt, tail * 2);
            qi = Arrays.copyOf(qi, tail * 2);
            qh = Arrays.copyOf(qh, tail * 2);
        }
        qh[tail] = high;
        qt[tail] = tar;
        qi[tail++] = input;
    }

    int[] inputsTo(int mod) {
        ArrayList<Integer> a = new ArrayList<>();
        for (int i = 0; i < out.length; i++) {
            for (int j : out[i]) {
                if (j == mod) {
                    a.add(i);
                }
            }
        }
        return a.stream().mapToInt(Integer::intValue).toArray();
    }
}
