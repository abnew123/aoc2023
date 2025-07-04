#!/bin/bash

echo "Compiling and running performance benchmark..."
echo "=============================================="

# Compile the project
javac -cp . src/OptimizationBenchmark.java

# Run the benchmark
java -cp . src.OptimizationBenchmark

echo ""
echo "Benchmark completed!" 