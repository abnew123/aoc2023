package src.solutions;

import src.meta.DayTemplate;
import src.objects.Coordinate;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class Day12 extends DayTemplate {

    static int counter = 0;
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
            Map<String, Long> memo = new HashMap<>();
            answer+= helper(groups,record, memo);
        }
        System.out.println(counter);
        return answer + "";
    }

    private long helper(List<Integer> groups, String record, Map<String, Long> memo){
        counter++;
        long answer = 0;
        if(groups.size() == 0){
            if(record.contains("#")){
                return 0;
            }
            return 1;
        }
        if(record.length() == 0){
            return 0;
        }
        String memoKey = groups.size() + " " + record.length();
        if(memo.containsKey(memoKey)){
            return memo.get(memoKey);
        }
        if (!check(groups, record)){
            memo.put(memoKey, 0L);
            return 0;
        }
        List<Integer> groupsCopy = new ArrayList<>();
        int current = groups.get(0);
        for(int i = 0; i < groups.size(); i++){
            groupsCopy.add(groups.get(i));
        }
        if(record.substring(0,1).equals(".")){
            while(record.substring(0,1).equals(".")){
                record = record.substring(1);
            }
            answer = helper(groupsCopy, record, memo);
            memo.put(memoKey, answer);
            return answer;
        }
        if(record.substring(0,1).equals("#")){
            if(record.length() == current){
                if(record.contains(".")) {
                    return 0;
                }
                else {
                    return 1;
                }
            }
            if(record.substring(0,current).contains(".") || record.substring(current, current + 1).equals("#")) {
                return 0;
            }
            else{
                groupsCopy.remove(0);
                answer= helper(groupsCopy, record.substring(current + 1), memo);
            }
        }
        if(record.substring(0,1).equals("?")){
            String rec1 = "#" + record.substring(1);
            answer= helper(groupsCopy, rec1, memo) + helper(groupsCopy,record.substring(1), memo);
        }
        memo.put(memoKey, answer);
        return answer;
    }

    private boolean check(List<Integer> groups, String record){
        int sum = 0;
        for(Integer g: groups){
            sum+=g;
        }
        int sum2 = sum + groups.size() - 1;
        int hashes = 0;
        int questions = 0;
        for(int i = 0; i < record.length(); i++){
            if(record.charAt(i) == '#'){
                hashes++;
            }
            if(record.charAt(i) == '?'){
                questions++;
            }
        }
        if(sum < hashes || sum > (hashes + questions)){
            return false;
        }

        return record.length() >= sum2;
    }
}