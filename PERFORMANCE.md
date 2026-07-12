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

Recovery note (2026-07-12): two leaked AoC JVMs invalidated speed measurements collected from 2026-07-11 22:08 EDT until their removal around 2026-07-12 11:30 EDT. The Day 11 measurements below have been withdrawn and replaced with clean process-isolated data. The current-source headline/table and Day 18 measurements remain provisional until their clean recovery runs are recorded.

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

## Day 7 direct hand classification

Day 7 previously regex-split every input line repeatedly, split every five-card hand into strings, compared each card against a per-hand rank-string array, and sorted 13 frequency counters. It now tokenizes each line once, maps card characters directly for the applicable rules, and finds the two largest frequency groups in one scan. Joker cards remain weakest only for part 2 tie-breaking, are counted separately for category construction, and stable object sorting still preserves input order for identical hands.

An isolated runner constructed the solver and input `Scanner` before timing the exact `fullSolve` call, checked both answers, and ran one excluded cold JVM per variant followed by 10 counterbalanced pairs of separate JVMs. Odd pairs ran the `3560131` regex/frequency-sort baseline then candidate (`B-C`); even pairs reversed the order (`C-B`).

| Pair | Order | Regex/sort (ms) | Direct classification (ms) | Delta (ms) |
| ---: | :---: | ---: | ---: | ---: |
| 1 | B-C | 16.839 | 8.962 | -7.877 |
| 2 | C-B | 17.074 | 9.635 | -7.439 |
| 3 | B-C | 16.834 | 9.059 | -7.774 |
| 4 | C-B | 17.080 | 9.390 | -7.690 |
| 5 | B-C | 17.118 | 8.928 | -8.190 |
| 6 | C-B | 17.276 | 9.003 | -8.274 |
| 7 | B-C | 16.718 | 8.999 | -7.720 |
| 8 | C-B | 16.238 | 9.051 | -7.187 |
| 9 | B-C | 17.236 | 9.157 | -8.080 |
| 10 | C-B | 16.833 | 9.215 | -7.619 |

Table deltas and summary statistics use the unrounded nanosecond records. The excluded cold values were 17.254 ms baseline and 9.526 ms candidate. The measured means were **16.925 ms baseline** and **9.140 ms candidate**, a **7.785 ms (46.0%) reduction**. The paired-delta sample standard deviation was 0.336 ms and the t(9) 95% confidence interval was **[-8.025 ms, -7.545 ms]**.

The authoritative whole-suite counterbalanced comparison also showed a statistically clear solver reduction: its 10-pair means were 234.090 ms baseline and 225.537 ms candidate, a -8.553 ms (3.65%) delta with a 95% confidence interval of **[-15.064 ms, -2.041 ms]**. Separate standard phase runs had these excluded cold processes:

| Variant | Wall (ms) | Main (ms) | Solver (ms) | Startup (ms) | Harness (ms) |
| --- | ---: | ---: | ---: | ---: | ---: |
| Baseline | 339.591 | 283.643 | 246.575 | 37.979 | 37.068 |
| Candidate | 314.781 | 261.562 | 224.218 | 33.616 | 37.344 |

Their 10-process means were:

| Metric | Baseline mean (ms) | Candidate mean (ms) | Change (ms) |
| --- | ---: | ---: | ---: |
| Wall | 320.682 | 318.155 | -2.526 |
| Main | 271.658 | 268.455 | -3.203 |
| Solver | 234.545 | 231.164 | -3.381 |
| Startup | 33.416 | 32.842 | -0.574 |
| Harness | 37.113 | 37.291 | +0.178 |

All 50 independent answers and 25 combined solves retain checksum `d6af64d6b36b5441bc0ca5d8ab99cf7568c6bc9b62ac807d37d96d3c3aec372b`. The official sample returned `6440 / 5905`; 5,000 deterministic randomized hands including jokers and duplicate card strings matched the exact pre-change implementation, an identical-hand bid case confirmed stable ranking, and repeated-space/tab input matched the ordinary-space answers.

## Day 6 integer race search

Day 6 previously split and streamed each race line multiple times and used floating-point quadratic roots. The root formula counted equality as a win when the record was exactly on an integer root, contrary to the prompt's strict inequality, and `time * time` could overflow before conversion to `double`. The solver now parses the spaced and concatenated values together and binary-searches the first winning hold time using the overflow-safe predicate `hold > distance / (time - hold)`.

An isolated runner constructed the solver and input `Scanner` before timing the exact `fullSolve` call, checked both answers, and ran one excluded cold JVM per variant followed by 10 counterbalanced pairs of separate JVMs. Odd pairs ran the `a89fc47` regex/stream baseline then candidate (`B-C`); even pairs reversed the order (`C-B`).

| Pair | Order | Regex/roots (ms) | Direct/integer (ms) | Delta (ms) |
| ---: | :---: | ---: | ---: | ---: |
| 1 | B-C | 3.201 | 1.597 | -1.604 |
| 2 | C-B | 3.475 | 1.577 | -1.899 |
| 3 | B-C | 3.060 | 1.732 | -1.328 |
| 4 | C-B | 3.526 | 1.473 | -2.054 |
| 5 | B-C | 2.990 | 1.523 | -1.466 |
| 6 | C-B | 3.452 | 1.440 | -2.012 |
| 7 | B-C | 3.619 | 1.652 | -1.967 |
| 8 | C-B | 3.125 | 1.676 | -1.449 |
| 9 | B-C | 3.458 | 1.645 | -1.813 |
| 10 | C-B | 2.991 | 1.685 | -1.307 |

Table deltas and summary statistics use the unrounded nanosecond records. The excluded cold values were 3.186 ms baseline and 1.693 ms candidate. The measured means were **3.290 ms baseline** and **1.600 ms candidate**, a **1.690 ms (51.4%) reduction**. The paired-delta sample standard deviation was 0.292 ms and the t(9) 95% confidence interval was **[-1.898 ms, -1.481 ms]**.

The authoritative whole-suite counterbalanced comparison also showed a statistically clear solver reduction: its 10-pair means were 214.247 ms baseline and 207.282 ms candidate, a -6.965 ms delta with a 95% confidence interval of **[-11.909 ms, -2.022 ms]**. Separate standard phase runs had these excluded cold processes:

| Variant | Wall (ms) | Main (ms) | Solver (ms) | Startup (ms) | Harness (ms) |
| --- | ---: | ---: | ---: | ---: | ---: |
| Baseline | 290.434 | 243.784 | 210.617 | 29.857 | 33.167 |
| Candidate | 295.368 | 252.898 | 219.738 | 26.850 | 33.161 |

Their 10-process means were:

| Metric | Baseline mean (ms) | Candidate mean (ms) | Change (ms) |
| --- | ---: | ---: | ---: |
| Wall | 290.780 | 285.325 | -5.455 |
| Main | 247.751 | 242.493 | -5.258 |
| Solver | 215.824 | 210.216 | -5.609 |
| Startup | 26.543 | 26.486 | -0.057 |
| Harness | 31.927 | 32.278 | +0.351 |

All 50 independent answers and 25 combined solves retain checksum `d6af64d6b36b5441bc0ca5d8ab99cf7568c6bc9b62ac807d37d96d3c3aec372b`. The official sample returned `288 / 71503`; exact-root cases such as time 7 and distance 12 correctly return zero; and 1,200 deterministic races matched brute-force or `BigInteger` multiplication references across small and near-trillion times, zero-win cases, mixed whitespace, and distances near `Long.MAX_VALUE`.

## Day 1 single-pass calibration scan

Day 1 previously buffered every input line, scanned each line four times, recreated the nine-word array for every search, copied character arrays, and allocated substrings for word probes. It now processes lines immediately, scans forward once, and uses allocation-free prefix checks while retaining one-character advancement so overlapping words such as `twone` and `oneight` remain visible. Numeric recognition still uses `Character.isDigit`/`Character.digit`, and lines without a requested token retain the prior `-11` contribution.

An isolated runner constructed the solver and input `Scanner` before timing the exact `fullSolve` call, checked both answers, and ran one excluded cold JVM per variant followed by 10 counterbalanced pairs of separate JVMs. Odd pairs ran the `d708409` four-scan baseline then candidate (`B-C`); even pairs reversed the order (`C-B`).

| Pair | Order | Four scans (ms) | Single scan (ms) | Delta (ms) |
| ---: | :---: | ---: | ---: | ---: |
| 1 | B-C | 9.051 | 6.375 | -2.676 |
| 2 | C-B | 8.912 | 6.348 | -2.564 |
| 3 | B-C | 9.052 | 6.407 | -2.645 |
| 4 | C-B | 10.022 | 6.542 | -3.480 |
| 5 | B-C | 9.323 | 6.466 | -2.857 |
| 6 | C-B | 8.866 | 6.553 | -2.313 |
| 7 | B-C | 9.128 | 6.874 | -2.254 |
| 8 | C-B | 8.904 | 6.241 | -2.663 |
| 9 | B-C | 8.967 | 6.712 | -2.255 |
| 10 | C-B | 9.373 | 6.213 | -3.160 |

Table deltas and summary statistics use the unrounded nanosecond records. The excluded cold values were 9.839 ms baseline and 7.011 ms candidate. The measured means were **9.160 ms baseline** and **6.473 ms candidate**, a **2.687 ms (29.3%) reduction**. The paired-delta sample standard deviation was 0.396 ms and the t(9) 95% confidence interval was **[-2.970 ms, -2.403 ms]**.

The authoritative whole-suite child comparison was directionally consistent but noisy: its counterbalanced 10-pair solver means were 210.361 ms baseline and 205.331 ms candidate, a -5.030 ms delta with a 95% confidence interval of [-10.815 ms, +0.756 ms]. Separate standard phase runs had these excluded cold processes:

| Variant | Wall (ms) | Main (ms) | Solver (ms) | Startup (ms) | Harness (ms) |
| --- | ---: | ---: | ---: | ---: | ---: |
| Baseline | 277.638 | 246.362 | 213.114 | 27.377 | 33.247 |
| Candidate | 280.294 | 246.097 | 214.887 | 29.627 | 31.211 |

Their 10-process means were:

| Metric | Baseline mean (ms) | Candidate mean (ms) | Change (ms) |
| --- | ---: | ---: | ---: |
| Wall | 290.782 | 279.093 | -11.688 |
| Main | 247.385 | 240.784 | -6.602 |
| Solver | 215.708 | 210.345 | -5.364 |
| Startup | 28.424 | 26.217 | -2.207 |
| Harness | 31.677 | 30.439 | -1.238 |

The accepted evidence is the isolated paired Day 1 interval; the whole-suite paired interval is explicitly inconclusive under nonuniform interactive load, and the phase split is retained transparently. All 50 independent answers and 25 combined solves retain the established checksum. The official samples returned `142` and `281`; overlap, numeric-zero, uppercase, tokenless, Arabic-decimal-digit, and blank-line cases retained exact behavior; and 1,011 deterministic randomized lines matched the pre-change implementation.

## Day 11 primitive axis contributions

Day 11 previously split every grid row into one-character strings, retained the resulting nested lists, allocated a `Coordinate` for every galaxy, and checked every galaxy pair. It now counts galaxies per row and column while reading the rectangular map, then derives ordinary Manhattan distance and empty-axis crossings with prefix sums. Both answers come from that single analysis in linear time and invocation-local state.

The original measurements for this change overlapped leaked background AoC JVMs and are withdrawn. The clean recovery first verified through the OS process table that no AoC Java/Javac, benchmark, timing, or watchdog process was active. Every recovery Java/Javac invocation then ran in a tracked process group with a hard deadline and a final descendant check. An isolated runner constructed the solver and input `Scanner` before timing the exact `fullSolve` call, checked both answers, and ran one excluded cold JVM per variant followed by 10 counterbalanced pairs of separate JVMs against baseline `d8f3e71`.

| Pair | Order | Object/pair baseline (ms) | Primitive axes (ms) | Delta (ms) |
| ---: | :---: | ---: | ---: | ---: |
| 1 | B-C | 14.567 | 3.493 | -11.073 |
| 2 | C-B | 14.662 | 3.519 | -11.143 |
| 3 | B-C | 14.214 | 3.506 | -10.708 |
| 4 | C-B | 13.756 | 3.358 | -10.398 |
| 5 | B-C | 14.351 | 3.443 | -10.909 |
| 6 | C-B | 13.919 | 3.346 | -10.573 |
| 7 | B-C | 14.061 | 3.435 | -10.626 |
| 8 | C-B | 13.922 | 3.352 | -10.570 |
| 9 | B-C | 13.900 | 3.318 | -10.581 |
| 10 | C-B | 13.843 | 3.299 | -10.544 |

The excluded cold values were 13.907ms baseline and 3.307ms candidate. The measured means were **14.120ms baseline** and **3.407ms candidate**, a **10.713ms (75.9%) reduction**. The paired-delta sample standard deviation was 0.246ms and the t(9) 95% confidence interval was **[-10.889ms, -10.536ms]**.

The clean whole-suite comparison used the same counterbalanced order and separate child JVMs:

| Pair | Order | Baseline solver (ms) | Candidate solver (ms) | Delta (ms) |
| ---: | :---: | ---: | ---: | ---: |
| 1 | B-C | 204.160 | 197.879 | -6.281 |
| 2 | C-B | 197.897 | 187.089 | -10.808 |
| 3 | B-C | 205.460 | 198.951 | -6.509 |
| 4 | C-B | 207.458 | 187.872 | -19.586 |
| 5 | B-C | 209.682 | 186.541 | -23.141 |
| 6 | C-B | 199.052 | 192.000 | -7.052 |
| 7 | B-C | 207.983 | 188.037 | -19.946 |
| 8 | C-B | 207.975 | 185.827 | -22.148 |
| 9 | B-C | 199.009 | 196.597 | -2.412 |
| 10 | C-B | 199.791 | 194.264 | -5.527 |

Its solver means were **203.847ms baseline** and **191.506ms candidate**, a **12.341ms (6.05%) reduction**. The paired-delta sample standard deviation was 7.953ms and the t(9) 95% confidence interval was **[-18.030ms, -6.652ms]**. Separate standard phase runs had these excluded cold processes:

| Variant | Wall (ms) | Main (ms) | Solver (ms) | Startup (ms) | Harness (ms) |
| --- | ---: | ---: | ---: | ---: | ---: |
| Baseline | 284.000 | 237.816 | 207.777 | 27.570 | 30.038 |
| Candidate | 259.692 | 213.020 | 183.702 | 28.082 | 29.318 |

Their 10-process means were:

| Metric | Baseline mean (ms) | Candidate mean (ms) | Change (ms) |
| --- | ---: | ---: | ---: |
| Wall | 276.254 | 264.348 | -11.906 |
| Main | 232.371 | 219.863 | -12.508 |
| Solver | 202.537 | 190.253 | -12.284 |
| Startup | 25.529 | 26.234 | +0.705 |
| Harness | 29.835 | 29.610 | -0.224 |

Both clean paired intervals exclude zero, and the separate phase split independently shows the solver reduction while startup and harness remain essentially unchanged. Verification retained all 50 expected answers, all 25 combined-solve pairs, and checksum `d6af64d6b36b5441bc0ca5d8ab99cf7568c6bc9b62ac807d37d96d3c3aec372b`. The official map returned `374 / 82000210`, and 500 deterministic rectangular maps—including single-row, single-column, empty-axis, zero-galaxy, and optional-final-newline cases—matched both the exact pre-change implementation and an independent explicit-coordinate expansion oracle.

## Day 18 exact streaming lagoon geometry

Day 18 previously created an instruction object per interpretation, repeatedly compiled regex splits and direction maps, boxed four coordinate histories, and traversed those histories again for shoelace area. It now decodes both prompt instruction forms once and streams each edge through invocation-local `BigInteger` coordinate, twice-area, and boundary accumulators. Absolute shoelace area handles either orientation, arbitrary-size literal distances remain exact, and a nonclosed path is rejected explicitly.

An isolated runner constructed the solver and input `Scanner` before timing the exact `fullSolve` call, checked both answers, and ran one excluded cold JVM per variant followed by 10 counterbalanced pairs of separate JVMs against baseline `6a3ec0f`.

| Pair | Order | Objects/regex/history (ms) | Exact streaming geometry (ms) | Delta (ms) |
| ---: | :---: | ---: | ---: | ---: |
| 1 | B-C | 10.258 | 6.879 | -3.379 |
| 2 | C-B | 10.216 | 7.043 | -3.173 |
| 3 | B-C | 10.725 | 6.959 | -3.767 |
| 4 | C-B | 10.565 | 7.028 | -3.538 |
| 5 | B-C | 10.709 | 6.771 | -3.938 |
| 6 | C-B | 10.423 | 6.932 | -3.490 |
| 7 | B-C | 9.986 | 6.737 | -3.249 |
| 8 | C-B | 10.392 | 6.711 | -3.681 |
| 9 | B-C | 10.465 | 7.753 | -2.712 |
| 10 | C-B | 10.323 | 6.824 | -3.499 |

The excluded cold values were 10.582ms baseline and 7.219ms candidate. The measured means were **10.406ms baseline** and **6.964ms candidate**, a **3.443ms (33.1%) reduction**. The paired-delta sample standard deviation was 0.345ms and the t(9) 95% confidence interval was **[-3.689ms, -3.196ms]**.

The whole-suite comparison was overwhelmed by nonuniform interactive load: its counterbalanced 10-pair solver means were 202.933ms baseline and 216.986ms candidate, a +14.053ms delta with a 95% confidence interval of [-9.017ms, +37.123ms]. Separate standard phase runs had these excluded cold processes:

| Variant | Wall (ms) | Main (ms) | Solver (ms) | Startup (ms) | Harness (ms) |
| --- | ---: | ---: | ---: | ---: | ---: |
| Baseline | 302.790 | 244.297 | 206.685 | 42.904 | 37.611 |
| Candidate | 292.081 | 246.586 | 211.386 | 29.612 | 35.199 |

Their 10-process means were:

| Metric | Baseline mean (ms) | Candidate mean (ms) | Change (ms) |
| --- | ---: | ---: | ---: |
| Wall | 286.388 | 288.564 | +2.176 |
| Main | 242.032 | 244.785 | +2.753 |
| Solver | 205.840 | 209.222 | +3.382 |
| Startup | 29.175 | 29.714 | +0.539 |
| Harness | 36.191 | 35.563 | -0.628 |

The accepted evidence is the isolated paired interval; the aggregate suite is explicitly inconclusive. Verification retained all 50 expected answers and all 25 combined-solve pairs. The official sample returned `62 / 952408144115`; 300 deterministic rectangles with LF/CRLF and optional final newlines matched the exact pre-change implementation; and candidate-only cases covered reverse orientation plus a literal distance far beyond `long`, checked against independent rectangle arithmetic.

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
