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
| `solver` | Sum of the 25 exact intervals around `solver.fullSolve(outerScanner)`. Solver selection, solver construction, and construction of the outer input-file `Scanner` occur before each interval. Reading/parsing that happens inside `fullSolve` is included, as is any additional `Scanner` construction performed by a `fullSolve` implementation. |
| `harness` | `main - solver`, covering orchestration outside those 25 calls, including class lookup, solver and outer-scanner construction, checksum generation, and protocol work. |

The start marker is emitted shortly after the first line of `main`, so `startup` and `main` overlap slightly at that boundary and must not be added together. Conversely, `wall` continues through result delivery and JVM shutdown after the child captures `main`; therefore `wall - main` is not a pure startup number, and the five columns are not intended to form an additive identity.

## Current fresh-JVM run

All values are milliseconds. The cold process is reported but excluded from the 10-sample statistics.

| Run | Wall | Main | Solver | Startup | Harness |
| --- | ---: | ---: | ---: | ---: | ---: |
| Cold | 295.312 | 249.184 | 218.383 | 26.747 | 30.802 |
| 1 | 297.560 | 251.920 | 220.728 | 26.688 | 31.192 |
| 2 | 288.975 | 247.783 | 216.101 | 25.806 | 31.682 |
| 3 | 294.015 | 254.123 | 222.970 | 25.741 | 31.152 |
| 4 | 293.587 | 249.893 | 219.165 | 24.644 | 30.727 |
| 5 | 289.456 | 249.636 | 218.237 | 24.561 | 31.399 |
| 6 | 302.453 | 250.254 | 216.713 | 33.191 | 33.541 |
| 7 | 309.554 | 263.666 | 231.123 | 26.879 | 32.543 |
| 8 | 300.144 | 255.482 | 220.423 | 25.648 | 35.059 |
| 9 | 292.883 | 253.696 | 219.771 | 24.858 | 33.925 |
| 10 | 305.627 | 263.173 | 231.789 | 27.276 | 31.384 |

The standard deviation is the sample standard deviation (`n - 1`).

| Metric | Mean | Median | Sample SD |
| --- | ---: | ---: | ---: |
| Wall | 297.425 | 295.787 | 6.904 |
| Main | 253.963 | 252.808 | 5.505 |
| Solver | 221.702 | 220.097 | 5.509 |
| Startup | 26.529 | 25.774 | 2.522 |
| Harness | 32.260 | 31.540 | 1.449 |

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

## Day 12 rolling dynamic-programming columns

Day 12's placement DP previously allocated `long[recordLength][groupCount]` for every condition record even though group `i` reads only group `i - 1`. It now stores two `long[wiggle]` columns, where `wiggle` is the number of legal placement offsets, and swaps them after every group. Each destination slot is overwritten before the swap, so no clearing or stale-state assumption is involved. Impossible minimum lengths still return zero.

An isolated runner constructed the solver and input `Scanner` before timing the exact `fullSolve` call, verified both answers, and ran one excluded cold JVM per variant followed by 10 counterbalanced pairs of separate JVMs. Odd pairs ran the `0121a58` matrix baseline then rolling candidate (`B-C`); even pairs reversed the order (`C-B`).

| Pair | Order | Matrix DP (ms) | Rolling DP (ms) | Delta (ms) |
| ---: | :---: | ---: | ---: | ---: |
| 1 | B-C | 15.572 | 14.588 | -0.984 |
| 2 | C-B | 15.223 | 14.505 | -0.718 |
| 3 | B-C | 15.300 | 13.937 | -1.362 |
| 4 | C-B | 15.543 | 14.179 | -1.365 |
| 5 | B-C | 15.422 | 14.361 | -1.061 |
| 6 | C-B | 15.462 | 13.873 | -1.589 |
| 7 | B-C | 14.930 | 13.854 | -1.076 |
| 8 | C-B | 15.260 | 14.032 | -1.228 |
| 9 | B-C | 15.678 | 14.329 | -1.350 |
| 10 | C-B | 15.379 | 14.029 | -1.350 |

The excluded cold values were 15.535 ms matrix and 13.918 ms rolling. The measured means were **15.377 ms matrix** and **14.169 ms rolling**, a 1.208 ms (7.86%) reduction. The paired-delta sample standard deviation was 0.250 ms and the t(9) 95% confidence interval was **[-1.39 ms, -1.03 ms]**.

The exact final classpaths also ran through the authoritative whole-suite child. Its counterbalanced 10-pair solver means were 221.516 ms baseline and 225.944 ms candidate; the +4.427 ms paired delta had a wide 95% confidence interval of [-0.38 ms, +9.23 ms]. The standard phase runs had these excluded cold processes:

| Variant | Wall (ms) | Main (ms) | Solver (ms) | Startup (ms) | Harness (ms) |
| --- | ---: | ---: | ---: | ---: | ---: |
| Baseline | 305.653 | 256.276 | 223.860 | 30.165 | 32.416 |
| Candidate | 307.915 | 258.126 | 226.674 | 30.517 | 31.452 |

Their 10-process means were:

| Metric | Baseline mean (ms) | Candidate mean (ms) | Change (ms) |
| --- | ---: | ---: | ---: |
| Wall | 301.305 | 303.327 | +2.022 |
| Main | 255.704 | 258.103 | +2.400 |
| Solver | 223.299 | 226.464 | +3.165 |
| Startup | 27.411 | 26.321 | -1.090 |
| Harness | 32.404 | 31.639 | -0.765 |

Interactive machine load was explicitly nonuniform, so these aggregate movements across 24 unrelated days are retained transparently but are not used to judge the change. The accepted evidence is the isolated paired Day 12 interval. Verification retained all 50 expected answers, all 25 combined-solve pairs, and checksum `d6af64d6b36b5441bc0ca5d8ab99cf7568c6bc9b62ac807d37d96d3c3aec372b`. The official sample returned `21 / 525152`, and 2,000 deterministic short records matched an exhaustive replacement-and-run-count oracle, including impossible layouts and single-group cases.

## Day 15 single-pass initialization sequence

Day 15 now walks the prompt's single comma-delimited sequence once. It accumulates each complete step hash for part 1 while hashing the label and applying the operation for part 2, and uses a 256-slot box array instead of regex-created step/field arrays, a stream-backed list, and boxed `HashMap` keys. Labels are allocated only when a new lens is inserted; exact-label replacement mutates in place and preserves slot order. Multi-digit focal lengths are parsed with checked arithmetic.

An isolated runner constructed the solver and input `Scanner` before timing the exact `fullSolve` call, checked both answers, and ran one excluded cold JVM per variant followed by 10 counterbalanced pairs of separate JVMs. Odd pairs ran the `5e95797` regex/`HashMap` baseline then candidate (`B-C`); even pairs reversed the order (`C-B`).

| Pair | Order | Regex/HashMap (ms) | One pass (ms) | Delta (ms) |
| ---: | :---: | ---: | ---: | ---: |
| 1 | B-C | 18.432 | 9.211 | -9.221 |
| 2 | C-B | 18.467 | 9.210 | -9.257 |
| 3 | B-C | 18.715 | 9.100 | -9.615 |
| 4 | C-B | 18.736 | 9.206 | -9.530 |
| 5 | B-C | 18.613 | 9.111 | -9.502 |
| 6 | C-B | 18.540 | 9.504 | -9.035 |
| 7 | B-C | 19.055 | 9.111 | -9.943 |
| 8 | C-B | 18.685 | 9.285 | -9.400 |
| 9 | B-C | 18.477 | 9.340 | -9.136 |
| 10 | C-B | 18.851 | 9.312 | -9.539 |

The excluded cold values were 18.818 ms baseline and 9.799 ms candidate. The measured means were **18.657 ms baseline** and **9.239 ms candidate**, a **9.418 ms (50.5%) reduction**. The paired-delta sample standard deviation was 0.267 ms and the t(9) 95% confidence interval was **[-9.61 ms, -9.23 ms]**.

The authoritative whole-suite child point estimate also favored the candidate but remained noisy: its counterbalanced 10-pair solver means were 224.226 ms baseline and 222.370 ms candidate, a -1.856 ms delta with a 95% confidence interval of [-6.72 ms, +3.01 ms]. Separate standard phase runs had these excluded cold processes:

| Variant | Wall (ms) | Main (ms) | Solver (ms) | Startup (ms) | Harness (ms) |
| --- | ---: | ---: | ---: | ---: | ---: |
| Baseline | 304.064 | 258.399 | 225.687 | 29.055 | 32.712 |
| Candidate | 295.312 | 249.184 | 218.383 | 26.747 | 30.802 |

Their 10-process means were:

| Metric | Baseline mean (ms) | Candidate mean (ms) | Change (ms) |
| --- | ---: | ---: | ---: |
| Wall | 300.811 | 297.425 | -3.386 |
| Main | 256.936 | 253.963 | -2.974 |
| Solver | 225.616 | 221.702 | -3.914 |
| Startup | 26.778 | 26.529 | -0.249 |
| Harness | 31.320 | 32.260 | +0.941 |

The accepted evidence is the isolated paired Day 15 interval; the whole-suite paired interval is explicitly inconclusive under interactive load, while the separate phase means are retained as the latest current-source run. Verification retained all 50 expected answers, all 25 combined-solve pairs, and the established checksum. The official sequence returned `1320 / 145`, the standalone `HASH` example returned 52, and 1,000 deterministic sequences containing hash collisions, removals, replacements, varied labels, and multi-digit focal lengths matched the exact pre-change implementation.

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
