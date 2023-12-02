package src.solutions;

import src.meta.DayTemplate;

import java.util.*;
import java.io.*;

public class Day02 extends DayTemplate {

    /**
     * Main solving method.
     * @param part1
     * The solver will solve part 1 if param is set to true.
     * The solver will solve part 2 if param is set to false.
     * @param in
     * The solver will read data from this Scanner.
     * @return
     * Returns answer in string format.
     * @throws FileNotFoundException
     */
    public String solve(boolean part1, Scanner in) throws FileNotFoundException{
        int answer = 0;
        List<String> lines = new ArrayList<>();
        while(in.hasNext()){
            lines.add(in.nextLine());
        }
        for(String line: lines){
            answer+= helper(line, part1);
        }
        return answer+"";
    }

    private int helper(String line,  boolean part1){
        String[] parts = line.split(":|;");
        Map<String, Integer> totals = new HashMap<>();
        int index = Integer.parseInt(parts[0].split(" ")[1]);
        for(int i = 1; i < parts.length; i++){
            String[] draws = parts[i].split(",");
            for(String draw: draws){
                draw = draw.trim();
                String color = draw.split(" ")[1];
                int number = Integer.parseInt(draw.split(" ")[0]);
                if(totals.containsKey(color)){
                    totals.put(color, Math.max(totals.get(color), number));
                }
                else{
                    totals.put(color,number);
                }
            }
        }
        if(!part1){
            return totals.get("red") * totals.get("green") * totals.get("blue");
        }
        if( totals.get("red") <= 12 && totals.get("green") <=13 && totals.get("blue") <= 14){
            return index;
        }
        return 0;
    }
}
