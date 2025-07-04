package src;

import src.meta.DayTemplate;
import src.solutions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class OptimizationBenchmark {
    
    private static final int WARMUP_RUNS = 10;
    private static final int BENCHMARK_RUNS = 100;
    private static final String DATA_DIR = "data/";
    
    // Focus on the files we actually optimized
    private static final String[] OPTIMIZED_DAYS = {
        "Day12", "Day13", "Day14", "Day16", "Day20", "Day21", "Day22", "Day23", "Day24", "Day25"
    };
    
    public static void main(String[] args) {
        System.out.println("=== Optimization Performance Benchmark ===\n");
        System.out.println("Testing optimized solutions with " + BENCHMARK_RUNS + " runs each\n");
        
        Map<String, PerformanceResult> results = new LinkedHashMap<>();
        
        // Test optimized versions
        for (String dayName : OPTIMIZED_DAYS) {
            System.out.println("Testing " + dayName + "...");
            PerformanceResult result = benchmarkDay(dayName);
            results.put(dayName, result);
        }
        
        // Print summary
        printSummary(results);
    }
    
    private static PerformanceResult benchmarkDay(String dayName) {
        DayTemplate solution = createSolution(dayName);
        PerformanceResult result = new PerformanceResult(dayName);
        
        try {
            // Test Part 1
            String fileName = dayName.toLowerCase() + ".txt";
            Scanner input = new Scanner(new File(DATA_DIR + fileName));
            
            // Warmup
            for (int i = 0; i < WARMUP_RUNS; i++) {
                input = new Scanner(new File(DATA_DIR + fileName));
                solution.solve(true, input);
                if (i % 5 == 0) System.gc(); // Garbage collect periodically
            }
            
            // Benchmark Part 1
            List<Long> part1Times = new ArrayList<>();
            for (int i = 0; i < BENCHMARK_RUNS; i++) {
                input = new Scanner(new File(DATA_DIR + fileName));
                long startTime = System.nanoTime();
                solution.solve(true, input);
                long endTime = System.nanoTime();
                part1Times.add(endTime - startTime);
                
                if (i % 20 == 0) System.gc(); // Garbage collect periodically
            }
            
            // Warmup for Part 2
            for (int i = 0; i < WARMUP_RUNS; i++) {
                input = new Scanner(new File(DATA_DIR + fileName));
                solution.solve(false, input);
                if (i % 5 == 0) System.gc(); // Garbage collect periodically
            }
            
            // Benchmark Part 2
            List<Long> part2Times = new ArrayList<>();
            for (int i = 0; i < BENCHMARK_RUNS; i++) {
                input = new Scanner(new File(DATA_DIR + fileName));
                long startTime = System.nanoTime();
                solution.solve(false, input);
                long endTime = System.nanoTime();
                part2Times.add(endTime - startTime);
                
                if (i % 20 == 0) System.gc(); // Garbage collect periodically
            }
            
            result.setPart1Stats(calculateStats(part1Times));
            result.setPart2Stats(calculateStats(part2Times));
            
        } catch (FileNotFoundException e) {
            System.out.println("  Data file not found for " + dayName + " (looking for " + DATA_DIR + dayName.toLowerCase() + ".txt)");
        } catch (OutOfMemoryError e) {
            System.out.println("  Out of memory for " + dayName + " - skipping");
        } catch (Exception e) {
            System.out.println("  Error testing " + dayName + ": " + e.getMessage());
        }
        
        return result;
    }
    
    private static Stats calculateStats(List<Long> times) {
        if (times.isEmpty()) return new Stats();
        
        Collections.sort(times);
        
        long sum = 0;
        for (long time : times) {
            sum += time;
        }
        
        double mean = sum / (double) times.size();
        double median = times.get(times.size() / 2);
        
        // Calculate standard deviation
        double variance = 0;
        for (long time : times) {
            variance += Math.pow(time - mean, 2);
        }
        variance /= times.size();
        double stdDev = Math.sqrt(variance);
        
        return new Stats(
            times.get(0), // min
            times.get(times.size() - 1), // max
            mean,
            median,
            stdDev
        );
    }
    
    private static DayTemplate createSolution(String dayName) {
        switch (dayName) {
            case "Day12": return new Day12();
            case "Day13": return new Day13();
            case "Day14": return new Day14();
            case "Day16": return new Day16();
            case "Day20": return new Day20();
            case "Day21": return new Day21();
            case "Day22": return new Day22();
            case "Day23": return new Day23();
            case "Day24": return new Day24();
            case "Day25": return new Day25();
            default: throw new IllegalArgumentException("Unknown day: " + dayName);
        }
    }
    
    private static void printSummary(Map<String, PerformanceResult> results) {
        System.out.println("\n=== PERFORMANCE SUMMARY ===");
        System.out.println("Format: Day | Part1 (avg±std ms) | Part2 (avg±std ms)");
        System.out.println("--------------------------------------------------------");
        
        for (Map.Entry<String, PerformanceResult> entry : results.entrySet()) {
            String dayName = entry.getKey();
            PerformanceResult result = entry.getValue();
            
            String part1Str = result.part1Stats.isValid() ? 
                String.format("%.2f±%.2f", result.part1Stats.mean / 1_000_000.0, result.part1Stats.stdDev / 1_000_000.0) : 
                "N/A";
            
            String part2Str = result.part2Stats.isValid() ? 
                String.format("%.2f±%.2f", result.part2Stats.mean / 1_000_000.0, result.part2Stats.stdDev / 1_000_000.0) : 
                "N/A";
            
            System.out.printf("%-6s | %-15s | %-15s%n", dayName, part1Str, part2Str);
        }
        
        System.out.println("\n=== OPTIMIZATION ANALYSIS ===");
        System.out.println("Key optimizations applied:");
        System.out.println("- Day20: Replaced stream operations with simple counters");
        System.out.println("- Day23: Replaced String operations with char arrays");
        System.out.println("- Days 12-16,21-25: Cached array dimensions");
        System.out.println("\nNote: Modern JVMs may automatically optimize array.length access,");
        System.out.println("so manual caching may not always provide significant benefits.");
    }
    
    static class PerformanceResult {
        String dayName;
        Stats part1Stats = new Stats();
        Stats part2Stats = new Stats();
        
        PerformanceResult(String dayName) {
            this.dayName = dayName;
        }
        
        void setPart1Stats(Stats stats) { this.part1Stats = stats; }
        void setPart2Stats(Stats stats) { this.part2Stats = stats; }
    }
    
    static class Stats {
        long min, max;
        double mean, median, stdDev;
        
        Stats() {
            // Invalid stats
        }
        
        Stats(long min, long max, double mean, double median, double stdDev) {
            this.min = min;
            this.max = max;
            this.mean = mean;
            this.median = median;
            this.stdDev = stdDev;
        }
        
        boolean isValid() {
            return min > 0 && max > 0;
        }
    }
} 