# Performance Notes

## Reproduce the current benchmark

The tracked [`FreshJvmBenchmark`](src/FreshJvmBenchmark.java) is Java-only. Its verification mode checks all 50 independent `solve` answers and all 25 `fullSolve` pairs against `data/expectedResults.txt`. Default mode performs that verification first, launches one excluded cold child JVM, and then launches 10 measured child JVMs strictly sequentially.

```shell
mkdir -p /tmp/aoc2023-classes
javac -d /tmp/aoc2023-classes $(git ls-files '*.java' ':!src/tests/**')
java -cp /tmp/aoc2023-classes src.FreshJvmBenchmark --verify
java -cp /tmp/aoc2023-classes src.FreshJvmBenchmark
```

The recorded environment was:

- 2024 MacBook Pro
- macOS 15.6, build 24G84
- aarch64, 14 available processors
- OpenJDK 23.0.1

Verification passed with this deterministic, length-framed SHA-256 answer checksum:

```text
VERIFY_OK solve_answers=50 full_solve_pairs=25 checksum=d6af64d6b36b5441bc0ca5d8ab99cf7568c6bc9b62ac807d37d96d3c3aec372b
```

## Timing definitions

Each child JVM runs all 25 `fullSolve` paths exactly once. The fields intentionally describe different timing boundaries:

| Field | Definition |
| --- | --- |
| `wall` | Parent-observed time from starting the Java child through protocol output, process exit, and stream completion. |
| `startup` | Parent-observed time from starting the child until its flushed start marker is received. This includes JVM/bootstrap and benchmark-class initialization. |
| `main` | Child-observed time from the first executable line of `main` through checksum/result preparation. It stops before the final result record is formatted and printed. |
| `solver` | Sum of the 25 exact intervals around `solver.fullSolve(outerScanner)`. Reflective class lookup, solver construction, and construction of the outer input-file `Scanner` occur before each interval. Reading/parsing that happens inside `fullSolve` is included, as is any additional `Scanner` construction performed by a `fullSolve` implementation. |
| `harness` | `main - solver`, covering orchestration outside those 25 calls, including class lookup, solver and outer-scanner construction, checksum generation, and protocol work. |

The start marker is emitted shortly after the first line of `main`, so `startup` and `main` overlap slightly at that boundary and must not be added together. Conversely, `wall` continues through result delivery and JVM shutdown after the child captures `main`; therefore `wall - main` is not a pure startup number, and the five columns are not intended to form an additive identity.

## Current fresh-JVM run

All values are milliseconds. The cold process is reported but excluded from the 10-sample statistics.

| Run | Wall | Main | Solver | Startup | Harness |
| --- | ---: | ---: | ---: | ---: | ---: |
| Cold | 307.829 | 262.402 | 230.312 | 26.105 | 32.090 |
| 1 | 305.116 | 261.840 | 230.161 | 24.240 | 31.679 |
| 2 | 307.384 | 263.848 | 232.323 | 24.543 | 31.525 |
| 3 | 307.414 | 263.604 | 232.587 | 24.797 | 31.017 |
| 4 | 302.852 | 260.261 | 228.771 | 23.494 | 31.490 |
| 5 | 310.532 | 268.609 | 236.605 | 23.884 | 32.004 |
| 6 | 303.568 | 260.535 | 228.663 | 23.919 | 31.872 |
| 7 | 308.050 | 263.797 | 230.029 | 25.157 | 33.767 |
| 8 | 304.781 | 260.815 | 229.258 | 24.888 | 31.557 |
| 9 | 319.404 | 275.382 | 242.771 | 24.727 | 32.611 |
| 10 | 309.679 | 269.174 | 236.334 | 24.592 | 32.840 |

The standard deviation is the sample standard deviation (`n - 1`).

| Metric | Mean | Median | Sample SD |
| --- | ---: | ---: | ---: |
| Wall | 307.878 | 307.399 | 4.769 |
| Main | 264.786 | 263.700 | 4.858 |
| Solver | 232.750 | 231.242 | 4.560 |
| Startup | 24.424 | 24.567 | 0.524 |
| Harness | 32.036 | 31.775 | 0.814 |

## Day 16 stack reuse

Day 16 now allocates one invocation-local traversal stack and reuses it for the part 1 trace and every part 2 edge trace. The scratch array is passed explicitly rather than stored statically, so separate solver calls do not share it. On the 110x110 input, the previous `fullSolve` path allocated roughly 85MB of `int` stack payload across 441 traces; the new path allocates one 48,404-element scratch array, about 194KB.

Correctness was checked before and after the change. All 50 personal-input answers were unchanged, all 25 `fullSolve` pairs matched the independent solves and expected results, and the official Day 16 sample returned 46/51. Pre/post snapshots also matched on 1x5, 5x1, 2x7, 7x2, 3x11, and 11x3 rectangular grids synthesized from all five valid tile characters.

### Alternating paired A/B comparison

The baseline classpath was compiled from the pristine `79b08f2` solver sources plus the exact same `FreshJvmBenchmark` source used by the candidate; the candidate classpath was compiled from the current sources. Both ran through the same Java `ProcessBuilder` child protocol, with the same JVM executable, working directory, data files, and environment. All processes ran strictly sequentially—never concurrently.

After one cold baseline child followed by one cold candidate child, the measured phase used 10 counterbalanced pairs. Odd-numbered pairs ran baseline then candidate (`B-C`); even-numbered pairs ran candidate then baseline (`C-B`). This alternation reduces sensitivity to gradual machine drift. Paired deltas below are candidate minus baseline.

| Pair | Order | Baseline wall | Candidate wall | Baseline solver | Candidate solver |
| ---: | :---: | ---: | ---: | ---: | ---: |
| 1 | B-C | 309.611 | 307.343 | 232.799 | 229.259 |
| 2 | C-B | 309.632 | 304.151 | 233.753 | 228.036 |
| 3 | B-C | 307.335 | 305.344 | 230.838 | 230.386 |
| 4 | C-B | 302.828 | 305.068 | 231.188 | 228.977 |
| 5 | B-C | 314.402 | 312.508 | 237.719 | 235.559 |
| 6 | C-B | 308.132 | 308.277 | 231.516 | 229.961 |
| 7 | B-C | 313.760 | 302.153 | 234.964 | 227.767 |
| 8 | C-B | 304.332 | 304.163 | 228.143 | 228.389 |
| 9 | B-C | 307.423 | 303.060 | 231.075 | 226.479 |
| 10 | C-B | 314.461 | 308.626 | 235.527 | 231.680 |

The excluded cold pair was:

| Metric | Pristine | Candidate | Delta |
| --- | ---: | ---: | ---: |
| Wall | 306.430 | 306.923 | +0.493 |
| Solver | 230.275 | 229.537 | -0.738 |

The 10-pair means were:

| Metric | Pristine mean | Candidate mean | Change | Paired-delta SD |
| --- | ---: | ---: | ---: | ---: |
| Wall | 309.192 | 306.069 | -3.123 (-1.01%) | 3.917 |
| Main | 265.108 | 262.055 | -3.053 (-1.15%) | 2.837 |
| Solver | 232.752 | 229.650 | -3.102 (-1.33%) | 2.331 |
| Startup | 24.865 | 24.935 | +0.070 | not reported |
| Harness | 32.356 | 32.406 | +0.050 | not reported |

For the primary solver metric, the paired mean improvement was 3.102ms and the approximate 95% confidence interval for the candidate-minus-pristine delta was **[-4.77ms, -1.44ms]**. Because that interval excludes zero, this run supports a real solver improvement; the near-flat startup and harness means are also consistent with the change being inside Day 16 rather than in process orchestration.

## Day 17 bounded bucket queue

Day 17 now uses a circular integer bucket queue for Dijkstra instead of a binary heap. A segment edge adds between zero and `maximum grid digit × maximum straight-run length`; using one more bucket than that exact bound preserves nondecreasing-cost polling, including zero-cost edges and stale duplicate entries, while removing heap sift operations.

Two serial full-suite replications used separate baseline/candidate classpaths. Each classpath ran one excluded cold JVM plus 10 measured fresh JVMs with the standard wall/main/solver/startup/harness split.

| Replication | Baseline solver (ms) | Candidate solver (ms) | Change |
| ---: | ---: | ---: | ---: |
| 1 | 222.449 | 215.663 | -6.785 |
| 2 (reverse order) | 225.697 | 214.463 | -11.235 |
| Combined 20-process mean | 224.073 | 215.063 | -9.010 (-4.02%) |

For attribution, the same final classpaths also ran a cold Day 17 process and 10 counterbalanced pairs of fresh Day 17 JVMs. This diagnostic includes scanner construction, parsing, both searches, and scanner close; every process returned `724 / 877`.

| Pair | Order | Binary heap (ms) | Bucket queue (ms) | Delta (ms) |
| ---: | :---: | ---: | ---: | ---: |
| 1 | B-C | 45.286 | 34.813 | -10.473 |
| 2 | C-B | 45.931 | 36.092 | -9.839 |
| 3 | B-C | 45.650 | 44.977 | -0.672 |
| 4 | C-B | 43.059 | 45.198 | +2.139 |
| 5 | B-C | 42.677 | 45.205 | +2.527 |
| 6 | C-B | 43.666 | 35.587 | -8.078 |
| 7 | B-C | 43.772 | 35.303 | -8.470 |
| 8 | C-B | 45.624 | 34.439 | -11.185 |
| 9 | B-C | 42.977 | 34.751 | -8.226 |
| 10 | C-B | 45.316 | 34.981 | -10.335 |

The excluded cold processes were 48.140ms heap and 38.171ms bucket. Measured means were **44.396ms heap** and **38.135ms bucket**, a **6.261ms (14.10%)** reduction. The paired-delta sample standard deviation was 5.400ms and the approximate 95% confidence interval was **[-10.13ms, -2.40ms]**.

Correctness retained the established 50-answer checksum and agreement between all independent and combined solves. Day 17 also matched the official `102 / 94` sample, the official ultra-crucible `71` sample, and the binary heap on 50 deterministic rectangular grids containing heat digits from zero through nine.

## Day 22 top-surface settling

Day 22 now parses the six coordinates without regex splitting and settles bricks in ascending initial height against an exact `(x,y)` top surface offset by the observed signed coordinate bounds. Each brick lands one level above the maximum height under its footprint; owners at that maximum are precisely its direct supporters. This derives the same support graph without allocating a 3-D voxel grid or moving every brick downward one `z` level at a time.

Two serial full-suite replications each used separate baseline/candidate classpaths, one excluded cold JVM per classpath, and 10 measured fresh JVMs per classpath. Startup, main, solver, and harness retain the definitions above. The full-suite solver means were:

| Replication | Baseline solver (ms) | Candidate solver (ms) | Change |
| ---: | ---: | ---: | ---: |
| 1 | 234.185 | 229.350 | -4.835 |
| 2 (reverse order) | 229.255 | 230.028 | +0.772 |
| Combined 20-process mean | 231.720 | 229.689 | -2.031 (-0.88%) |

The aggregate full-suite direction is favorable but noisy because it includes the other 24 days. A final post-review candidate-only cold-plus-10 set had a 232.529ms solver mean and 6.394ms sample standard deviation, further illustrating that whole-suite noise. To isolate whether Day 22 itself improved, the final compiled classpaths ran a cold Day 22 process followed by 10 counterbalanced pairs of fresh Day 22 JVMs. The timer includes scanner construction, parsing, settling, both answers, and scanner close; every process returned `401 / 63491`.

| Pair | Order | Baseline Day 22 (ms) | Candidate Day 22 (ms) | Delta (ms) |
| ---: | :---: | ---: | ---: | ---: |
| 1 | B-C | 29.120 | 23.694 | -5.425 |
| 2 | C-B | 31.039 | 23.597 | -7.442 |
| 3 | B-C | 29.886 | 24.442 | -5.443 |
| 4 | C-B | 30.092 | 23.704 | -6.389 |
| 5 | B-C | 30.180 | 23.846 | -6.333 |
| 6 | C-B | 30.778 | 23.146 | -7.632 |
| 7 | B-C | 32.477 | 24.043 | -8.434 |
| 8 | C-B | 28.844 | 23.255 | -5.589 |
| 9 | B-C | 29.840 | 23.320 | -6.520 |
| 10 | C-B | 30.144 | 24.248 | -5.896 |

The excluded cold processes were 28.722ms baseline and 23.591ms candidate. Measured means were **30.240ms baseline** and **23.730ms candidate**, a **6.510ms (21.53%)** reduction. The paired-delta sample standard deviation was 1.023ms and the approximate 95% confidence interval was **[-7.24ms, -5.78ms]**.

Correctness checks retained the established 50-answer checksum and agreement between all independent and combined solves. Day 22 additionally matched the official `5 / 7` example, a signed-coordinate two-brick support chain, and the previous voxel solver on 50 deterministic, non-overlapping generated brick stacks containing point, x-axis, y-axis, and vertical bricks.

## Direct solver factory

The fresh-JVM and normal master harnesses previously formatted a class name, called `Class.forName`, looked up a constructor, and reflectively instantiated each of the 25 solvers. They now share a direct switch-based factory. Solver construction remains outside the solver timer, so this change targets only main/harness/wall work and cannot make the solver metric appear faster.

The exact final factory and reflective baseline ran as one excluded cold pair followed by 10 counterbalanced pairs of fresh JVMs. The machine was under nonuniform interactive load, so the decision was based on the predeclared harness phase rather than the much noisier wall or solver samples. The cold harness values were 34.307ms reflection and 31.953ms factory.

| Pair | Order | Reflection harness (ms) | Factory harness (ms) | Delta (ms) |
| ---: | :---: | ---: | ---: | ---: |
| 1 | B-C | 33.390 | 33.889 | +0.499 |
| 2 | C-B | 33.820 | 31.956 | -1.864 |
| 3 | B-C | 34.127 | 32.783 | -1.344 |
| 4 | C-B | 34.640 | 34.835 | +0.195 |
| 5 | B-C | 35.247 | 33.355 | -1.892 |
| 6 | C-B | 33.993 | 34.813 | +0.820 |
| 7 | B-C | 33.733 | 32.623 | -1.110 |
| 8 | C-B | 34.663 | 32.966 | -1.697 |
| 9 | B-C | 34.850 | 33.822 | -1.028 |
| 10 | C-B | 33.443 | 32.225 | -1.218 |

| Metric | Reflection mean (ms) | Factory mean (ms) | Delta (ms) | Paired 95% interval (ms) |
| --- | ---: | ---: | ---: | ---: |
| Wall | 306.640 | 309.521 | +2.880 | [-13.24, 19.00] |
| Main | 264.749 | 268.684 | +3.936 | [-8.76, 16.63] |
| Solver | 230.558 | 235.358 | +4.800 | [-7.66, 17.26] |
| Startup | 27.975 | 29.590 | +1.615 | [0.24, 2.99] |
| Harness | 34.191 | 33.327 | **-0.864** | **[-1.58, -0.15]** |

Among the targeted work metrics, only the harness reduction is statistically clear in this exact run; wall, main, and solver are explicitly treated as inconclusive. Startup also shows a statistically clear 1.615ms regression in this sample, but the preceding direct-switch replication moved startup by -1.984ms with an interval crossing zero, so the startup effect did not reproduce. That preceding replication independently favored the candidate harness (35.625ms to 32.582ms, paired interval [-4.50, -1.59] ms). Both implementations produced the same established 50-answer checksum.

## Historical warm measurements

The per-part table in the README is retained from the earlier July warm benchmark pass. Those values came from repeated calls within an already-running JVM and are useful for historical solver context, but they are not directly comparable with the fresh-process wall, main, or solver samples above. First invocations can be slower because the JVM is loading and verifying classes, linking methods, compiling hot paths, filling caches, and sometimes paying one-time allocation or GC costs.

## Visual Examples

These retained bars compare an earlier README-listed combined day timing with the July warm combined part timing. They predate the fresh-JVM runner and the latest Day 16 scratch-buffer A/B test. Lower is better.

### Day 16: The Floor Will Be Lava

The solver still simulates beams over the mirror grid, but the optimized version avoids much of the queue/object churn with compact direction/state tracking. The latest change additionally reuses one traversal stack across beam starts.

```text
Before 137.0 ms | ############################
After   13.9 ms | ###
```

### Day 17: Clumsy Crucible

The solver still runs a constrained shortest-path search, but the optimized version uses flatter primitive state and tighter queue handling.

```text
Before 174.0 ms | ###################################
After   21.6 ms | ####
```

### Day 22: Sand Slabs

The solver still settles bricks and evaluates support cascades, but it reuses the support graph more directly for each disintegration check.

```text
Before  40.0 ms | ########
After   12.0 ms | ##
```
