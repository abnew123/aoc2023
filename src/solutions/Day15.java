package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day15 implements DayTemplate {


    
    public String solve(boolean part1, Scanner in) {
        long ans = 0;
        List<String> lines = Arrays.stream(in.nextLine().split(",")).toList();
        if (part1) {
            for (String s : lines) {
                ans += hash(s);
            }
        } else {
            return sumMap(buildMap(lines)) + "";
        }
        return ans + "";
    }

    Map<Integer, List<Lens>> buildMap(List<String> lines){
        Map<Integer, List<Lens>> map = new HashMap<>();
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
        return map;
    }

    long sumMap( Map<Integer, List<Lens>> map){
        long ans = 0;
        for (Map.Entry<Integer, List<Lens>> entry : map.entrySet()) {
            int index = 0;
            for (Lens lens : entry.getValue()) {
                index++;
                ans += (entry.getKey() + 1) * index * (long)lens.length;
            }
        }
        return ans;
    }

    void replaceKey(Map<Integer, List<Lens>> map, int hash, String[] parts) {
        boolean present = false;
        List<Lens> cur = map.get(hash);
        for (int i = cur.size() - 1; i >= 0; i--) {
            if (cur.get(i).name.equals(parts[0])) {
                cur.get(i).length = Integer.parseInt(parts[1]);
                present = true;
            }
        }
        if (!present) {
            cur.add(new Lens(Integer.parseInt(parts[1]), parts[0]));
        }
        map.put(hash, cur);
    }

    void addKey(Map<Integer, List<Lens>> map, int hash, String[] parts) {
        List<Lens> tmp = new ArrayList<>();
        tmp.add(new Lens(Integer.parseInt(parts[1]), parts[0]));
        map.put(hash, tmp);
    }

    void removeKey(Map<Integer, List<Lens>> map, int hash, String part) {
        if (map.containsKey(hash)) {
            List<Lens> cur = map.get(hash);
            for (int i = cur.size() - 1; i >= 0; i--) {
                if (cur.get(i).name.equals(part)) {
                    cur.remove(i);
                }
            }
        }
    }

    int hash(String s) {
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