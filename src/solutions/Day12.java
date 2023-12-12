package src.solutions;

import src.meta.DayTemplate;
import src.objects.Coordinate;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class Day12 extends DayTemplate {

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
        List<String> records = new ArrayList<>();
        List<List<Integer>> vals = new ArrayList<>();
        Map<String, Long> memo = new HashMap<>();
        while (in.hasNext()) {
            List<Integer> tmp = new ArrayList<>();
            String line = in.nextLine();
            records.add(line.split(" ")[0]);
            for(String v: line.split(" ")[1].split(",")){
                tmp.add(Integer.parseInt(v));
            }
            vals.add(tmp);
        }
        if(!part1){
            List<String> newRecords = new ArrayList<>();
            List<List<Integer>> newGroups = new ArrayList<>();
            for(int i = 0; i < records.size(); i++){
                String a = records.get(i);
                List<Integer> b = vals.get(i);
                newRecords.add(a + "?"+a + "?"+a + "?"+a + "?"+a );
                List<Integer> tmp = new ArrayList<>();
                for(int j = 0; j < 5; j++){
                    tmp.addAll(b);
                }
                newGroups.add(tmp);
            }
            records = newRecords;
            vals = newGroups;
        }
        for(int i = 0; i < records.size(); i++){
            List<Integer> groups = vals.get(i);
            String record = records.get(i);
            answer+= helper(groups,record, memo);
        }
        return answer + "";
    }

    private long helper(List<Integer> groups, String record, Map<String, Long> memo){
        long answer = 0;
        String m = "";
        for(Integer g: groups){
            m += " " + g;
        }

//        System.out.println(m + record);
        if(memo.containsKey(m + record)){
            return memo.get(m + record);
        }
        if(record.length() == 0){
            if(groups.size() == 0){
                return 1;
            }
            return 0;
        }
        if(groups.size() == 0){
            if(record.contains("#")){
                return 0;
            }
            return 1;
        }
        List<Integer> groupsCopy = new ArrayList<>();
        for(int i = 0; i < groups.size(); i++){
            groupsCopy.add(groups.get(i));
        }
        if(record.substring(0,1).equals(".")){
            answer = helper(groupsCopy, record.substring(1), memo);
        }
        if(record.substring(0,1).equals("#")){
            if(record.length() < groups.get(0)) {
                memo.put(m + record, 0L);
                return 0;
            }
            if(record.length() == groups.get(0)){
                if(record.contains(".") || groups.size() > 1) {
                    memo.put(m + record, 0L);
                    return 0;
                }
                else {
                    memo.put(m + record, 1L);
                    return 1;
                }
            }

            if(groups.get(0) == 1){
                if(record.substring(1,2).equals(".")){
                    groupsCopy.remove(0);
                    answer = helper(groupsCopy, record.substring(2), memo);
                }
                else{
                    if(record.substring(1,2).equals("?")){
                        groupsCopy.remove(0);
                        answer= helper(groupsCopy, "." + record.substring(2), memo);
                    }
                    else{
                        return 0;
                    }
                }
            }
            else{
                if(record.substring(1,2).equals(".")){
                    memo.put(m + record, 0L);
                    return 0;
                }
                groupsCopy.set(0, groups.get(0) - 1);
                answer= helper(groupsCopy, "#" + record.substring(2), memo);
            }
        }
        if(record.substring(0,1).equals("?")){
            String rec1 = "#" + record.substring(1);
            String rec2 = "." + record.substring(1);
            answer= helper(groupsCopy, rec1, memo) + helper(groupsCopy,rec2, memo);
        }
        memo.put(m + record, answer);
        return answer;
    }
}