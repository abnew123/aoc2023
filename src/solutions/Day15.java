package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day15 extends DayTemplate {

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
        List<String> lines = new ArrayList<>();
        Map<Integer, List<Lens>> map = new HashMap<>();
        lines = Arrays.stream(in.nextLine().split(",")).toList();
        if (part1) {
            for (String s : lines) {
                answer += helper(s);
            }
        } else {
            for (String s : lines) {
                String[] parts = s.split("[-=]");
                int hash = helper(parts[0]);
                if (parts.length == 1) {
                    if (map.containsKey(hash)) {
                        List<Lens> current = map.get(hash);
                        for (int i = current.size() - 1; i >= 0; i--) {
                            if (current.get(i).name.equals(parts[0])) {
                                current.remove(i);
                            }
                        }
                    }
                } else {
                    if (map.containsKey(hash)) {
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
                    } else {
                        List<Lens> tmp = new ArrayList<>();
                        tmp.add(new Lens(Integer.parseInt(parts[1]), parts[0]));
                        map.put(hash, tmp);
                    }
                }
            }
            for(Integer key: map.keySet()){
                int index = 0;
                for(Lens lens: map.get(key)){
                    index ++;
                    answer+= (key + 1) * index * lens.length;
                }
            }
        }

        return answer + "";
    }

    public int helper(String s) {
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