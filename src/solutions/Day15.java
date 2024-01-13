package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day15 implements DayTemplate {

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
        List<String> lines = Arrays.stream(in.nextLine().split(",")).toList();
        Map<Integer, List<Lens>> map = new HashMap<>();
        if (part1) {
            for (String s : lines) {
                answer += hash(s);
            }
        } else {
            for (String s : lines) {
                String[] parts = s.split("[-=]");
                int hash = hash(parts[0]);
                if (parts.length == 1) {
                    removeKey(map, hash, parts[0]);
                } else {
                    if (map.containsKey(hash)) {
                        replaceKey(map, hash, parts);
                    } else {
                        addKey(map, hash, parts);
                    }
                }
            }
            for (Map.Entry<Integer, List<Lens>> entry : map.entrySet()) {
                int index = 0;
                for (Lens lens : entry.getValue()) {
                    index++;
                    answer += (entry.getKey() + 1) * index * lens.length;
                }
            }
        }

        return answer + "";
    }

    private void replaceKey(Map<Integer, List<Lens>> map, int hash, String[] parts) {
        boolean present = false;
        List<Lens> current = map.get(hash);
        for (int i = current.size() - 1; i >= 0; i--) {
            if (current.get(i).name.equals(parts[0])) {
                current.get(i).length = Integer.parseInt(parts[1]);
                present = true;
            }
        }
        if (!present) {
            current.add(new Lens(Integer.parseInt(parts[1]), parts[0]));
        }
        map.put(hash, current);
    }

    private void addKey(Map<Integer, List<Lens>> map, int hash, String[] parts) {
        List<Lens> tmp = new ArrayList<>();
        tmp.add(new Lens(Integer.parseInt(parts[1]), parts[0]));
        map.put(hash, tmp);
    }

    private void removeKey(Map<Integer, List<Lens>> map, int hash, String part) {
        if (map.containsKey(hash)) {
            List<Lens> current = map.get(hash);
            for (int i = current.size() - 1; i >= 0; i--) {
                if (current.get(i).name.equals(part)) {
                    current.remove(i);
                }
            }
        }
    }

    private int hash(String s) {
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            val += s.charAt(i);
            val *= 17;
            val %= 256;
        }
        return val;
    }
}

class Lens {
    int length;
    String name;

    public Lens(int l, String n) {
        length = l;
        name = n;
    }
}