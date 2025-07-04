package src;

import src.meta.DayTemplate;
import src.solutions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class SimpleBenchmark {
    
    private static final int WARMUP_RUNS = 5;
    private static final int BENCHMARK_RUNS = 50;
    private static final String DATA_DIR = "data/";
    
    // Focus on the most important optimizations
    private static final String[] KEY_DAYS = {
        "Day20", "Day23" // These had the most significant optimizations
    };
    
    public static void main(String[] args) {
        System.out.println("=== Simple Performance Benchmark ===\n");
        System.out.println("Testing key optimizations with " + BENCHMARK_RUNS + " runs each\n");
        
        for (String dayName : KEY_DAYS) {
            System.out.println("Testing " + dayName + "...");
            benchmarkDay(dayName);
            System.out.println();
        }
    }
    
    private static void benchmarkDay(String dayName) {
        DayTemplate solution = createSolution(dayName);
        
        try {
            String fileName = dayName.toLowerCase() + ".txt";
            
            // Test Part 1
            System.out.println("  Part 1:");
            List<Long> part1Times = runBenchmark(solution, fileName, true);
            printStats("    ", part1Times);
            
            // Test Part 2
            System.out.println("  Part 2:");
            List<Long> part2Times = runBenchmark(solution, fileName, false);
            printStats("    ", part2Times);
            
        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }
    
    private static List<Long> runBenchmark(DayTemplate solution, String fileName, boolean part1) {
        List<Long> times = new ArrayList<>();
        
        try {
            // Warmup
            for (int i = 0; i < WARMUP_RUNS; i++) {
                Scanner input = new Scanner(new File(DATA_DIR + fileName));
                solution.solve(part1, input);
                if (i % 2 == 0) System.gc();
            }
            
            // Benchmark
            for (int i = 0; i < BENCHMARK_RUNS; i++) {
                Scanner input = new Scanner(new File(DATA_DIR + fileName));
                long startTime = System.nanoTime();
                solution.solve(part1, input);
                long endTime = System.nanoTime();
                times.add(endTime - startTime);
                
                if (i % 10 == 0) System.gc();
            }
            
        } catch (FileNotFoundException e) {
            System.out.println("    Data file not found: " + fileName);
        } catch (OutOfMemoryError e) {
            System.out.println("    Out of memory - skipping");
        }
        
        return times;
    }
    
    private static void printStats(String prefix, List<Long> times) {
        if (times.isEmpty()) {
            System.out.println(prefix + "No data");
            return;
        }
        
        Collections.sort(times);
        long sum = 0;
        for (long time : times) {
            sum += time;
        }
        
        double mean = sum / (double) times.size();
        double median = times.get(times.size() / 2);
        
        System.out.printf("%sAvg: %.2fms, Median: %.2fms, Min: %.2fms, Max: %.2fms%n",
            prefix,
            mean / 1_000_000.0,
            median / 1_000_000.0,
            times.get(0) / 1_000_000.0,
            times.get(times.size() - 1) / 1_000_000.0);
    }
    
    private static DayTemplate createSolution(String dayName) {
        switch (dayName) {
            case "Day20": return new Day20();
            case "Day23": return new Day23();
            default: throw new IllegalArgumentException("Unknown day: " + dayName);
        }
    }
} 