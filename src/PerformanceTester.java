package src;

import src.meta.DayTemplate;
import src.solutions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class PerformanceTester {
    
    private static final int WARMUP_RUNS = 10;
    private static final int BENCHMARK_RUNS = 100;
    private static final String DATA_DIR = "data/";
    
    public static void main(String[] args) {
        System.out.println("=== Advent of Code 2023 Performance Testing ===\n");
        
        // Test both original and optimized versions
        testOptimizations();
    }
    
    private static void testOptimizations() {
        Map<String, DayTemplate> solutions = createSolutions();
        
        for (Map.Entry<String, DayTemplate> entry : solutions.entrySet()) {
            String dayName = entry.getKey();
            DayTemplate solution = entry.getValue();
            
            System.out.println("Testing " + dayName + ":");
            
            // Test Part 1
            testPart(solution, dayName, true);
            
            // Test Part 2
            testPart(solution, dayName, false);
            
            System.out.println();
        }
    }
    
    private static void testPart(DayTemplate solution, String dayName, boolean part1) {
        String partName = part1 ? "Part 1" : "Part 2";
        String fileName = dayName.toLowerCase().replace("day", "") + ".txt";
        
        try {
            Scanner input = new Scanner(new File(DATA_DIR + fileName));
            
            // Warmup runs
            for (int i = 0; i < WARMUP_RUNS; i++) {
                input = new Scanner(new File(DATA_DIR + fileName));
                solution.solve(part1, input);
            }
            
            // Benchmark runs
            long totalTime = 0;
            long minTime = Long.MAX_VALUE;
            long maxTime = 0;
            
            for (int i = 0; i < BENCHMARK_RUNS; i++) {
                input = new Scanner(new File(DATA_DIR + fileName));
                
                long startTime = System.nanoTime();
                String result = solution.solve(part1, input);
                long endTime = System.nanoTime();
                
                long duration = endTime - startTime;
                totalTime += duration;
                minTime = Math.min(minTime, duration);
                maxTime = Math.max(maxTime, duration);
            }
            
            long avgTime = totalTime / BENCHMARK_RUNS;
            
            System.out.printf("  %s: Avg=%.2fms, Min=%.2fms, Max=%.2fms%n", 
                partName,
                avgTime / 1_000_000.0,
                minTime / 1_000_000.0,
                maxTime / 1_000_000.0);
                
        } catch (FileNotFoundException e) {
            System.out.println("  " + partName + ": Data file not found (" + fileName + ")");
        } catch (Exception e) {
            System.out.println("  " + partName + ": Error - " + e.getMessage());
        }
    }
    
    private static Map<String, DayTemplate> createSolutions() {
        Map<String, DayTemplate> solutions = new LinkedHashMap<>();
        
        // Add all solutions
        solutions.put("Day01", new Day01());
        solutions.put("Day02", new Day02());
        solutions.put("Day03", new Day03());
        solutions.put("Day04", new Day04());
        solutions.put("Day05", new Day05());
        solutions.put("Day06", new Day06());
        solutions.put("Day07", new Day07());
        solutions.put("Day08", new Day08());
        solutions.put("Day09", new Day09());
        solutions.put("Day10", new Day10());
        solutions.put("Day11", new Day11());
        solutions.put("Day12", new Day12());
        solutions.put("Day13", new Day13());
        solutions.put("Day14", new Day14());
        solutions.put("Day15", new Day15());
        solutions.put("Day16", new Day16());
        solutions.put("Day17", new Day17());
        solutions.put("Day18", new Day18());
        solutions.put("Day19", new Day19());
        solutions.put("Day20", new Day20());
        solutions.put("Day21", new Day21());
        solutions.put("Day22", new Day22());
        solutions.put("Day23", new Day23());
        solutions.put("Day24", new Day24());
        solutions.put("Day25", new Day25());
        
        return solutions;
    }
} 