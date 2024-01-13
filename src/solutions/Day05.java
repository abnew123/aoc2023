package src.solutions;

import src.meta.DayTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day05 implements DayTemplate {

    /**
     * Main solving method.
     *
     * @param part1 The solver will solve part 1 if param is set to true.
     *              The solver will solve part 2 if param is set to false.
     * @param in    The solver will read data from this Scanner.
     * @return Returns answer in string format.
     */
    public String solve(boolean part1, Scanner in) {
        long answer = 10000000000L;
        List<RangeMap> rangeMaps = new ArrayList<>();
        List<Range> tmp = new ArrayList<>();
        String[] stringSeeds = in.nextLine().split(" ");
        long[] seeds = new long[stringSeeds.length - 1];
        for (int i = 1; i < stringSeeds.length; i++) {
            seeds[i - 1] = Long.parseLong(stringSeeds[i]);
        }
        while (in.hasNext()) {
            String line = in.nextLine();
            if (line.equals("")) {
                continue;
            }
            if (line.contains("map")) {
                if (!tmp.isEmpty()) {
                    rangeMaps.add(new RangeMap(tmp));
                }
                tmp = new ArrayList<>();
            } else {
                tmp.add(new Range(line));
            }
        }
        rangeMaps.add(new RangeMap(tmp));
        if (part1) {
            for (Long seed : seeds) {
                long val = seed;
                for (RangeMap rangeMap : rangeMaps) {
                    val = rangeMap.convert(val);
                }
                if (val < answer) {
                    answer = val;
                }
            }
        } else {
            for (int i = 0; i < seeds.length; i += 2) {
                long index = seeds[i];
                while(index < seeds[i] + seeds[i+1]){
                    long[] ret = returnValAndBound(index, rangeMaps);
                    if (ret[0] < answer) {
                        answer = ret[0];
                    }
                    index += ret[1] + 1;
                }
            }
        }
        return answer + "";
    }

    private long[] returnValAndBound(long val, List<RangeMap> rangeMaps) {
        long bound = 10000000000L;
        for (RangeMap rangeMap : rangeMaps) {
            bound = Math.min(bound, rangeMap.convert2(val)[1]);
            val = rangeMap.convert2(val)[0];
        }
        return new long[]{val, bound};
    }
}

class Range {
    long destination;
    long source;
    long specificRange;

    public Range(long des, long src, long r) {
        destination = des;
        source = src;
        specificRange = r;
    }

    public Range(String line) {
        String[] pieces = line.split(" ");
        destination = Long.parseLong(pieces[0]);
        source = Long.parseLong(pieces[1]);
        specificRange = Long.parseLong(pieces[2]);
    }
}

class RangeMap {
    List<Long> starts;
    List<Long> ends;
    List<Long> betweens;

    public RangeMap(List<Range> ranges) {
        starts = new ArrayList<>();
        ends = new ArrayList<>();
        betweens = new ArrayList<>();
        for (Range range : ranges) {
            starts.add(range.source);
            ends.add(range.destination);
            betweens.add(range.specificRange);
        }
    }

    public long convert(long val) {
        for (int i = 0; i < starts.size(); i++) {
            if (starts.get(i) <= val && starts.get(i) + betweens.get(i) > val) {
                return ends.get(i) + (val - starts.get(i));
            }
        }
        return val;
    }

    public long[] convert2(long val) {
        long nextStart = 10000000000L;
        for (int i = 0; i < starts.size(); i++) {
            if (starts.get(i) > val) {
                nextStart = Math.min(nextStart, starts.get(i) - val - 1);
            }
            if (starts.get(i) <= val && starts.get(i) + betweens.get(i) > val) {
                return new long[]{ends.get(i) + (val - starts.get(i)), betweens.get(i) - (val - starts.get(i)) - 1};
            }
        }
        return new long[]{val, nextStart == 10000000000L ? 0 : nextStart};
    }
}
