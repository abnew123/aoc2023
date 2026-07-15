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
VERIFY_OK solve_answers=50 full_solve_pairs=25 checksum=3a7181d2751be2eaeaea0d454c243728b7e1f7ee48de10762828623b3101907f
```

Recovery note (2026-07-12): two leaked AoC JVMs invalidated speed measurements collected from 2026-07-11 22:08 EDT until their removal around 2026-07-12 11:30 EDT. The original Day 11 and Day 18 measurements have been withdrawn and replaced with clean process-isolated data below. The current-source table has since been replaced again by the correctness-fixed Day 24 run.

Correctness recovery (2026-07-13): the ignored expected-results file and every source revision since `bc693c7` contained the false-positive Day 24 part 2 value `1033313543348751`. That revision reduced candidate validation to comparisons with stone 0, skipped parallel relative paths, and truncated rational collision times. The exact solver now returns `711031616315001`, validates the derived rock against every hailstone with `BigInteger` cross-products and nonnegative rational times, and produces the corrected checksum above. Any historical statement below that quotes checksum `d6af64d6b36b5441bc0ca5d8ab99cf7568c6bc9b62ac807d37d96d3c3aec372b` or says all 50 answers matched describes consistency with the then-stale local baseline, not independent correctness. Historical whole-suite absolute totals include the incorrect Day 24 workload and are superseded by the current table; isolated A/B results for other days remain useful.

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
| Cold | 270.605 | 225.976 | 195.269 | 25.863 | 30.707 |
| 1 | 262.812 | 216.465 | 185.799 | 27.629 | 30.666 |
| 2 | 264.065 | 219.232 | 189.992 | 25.940 | 29.239 |
| 3 | 272.729 | 227.525 | 197.043 | 26.499 | 30.482 |
| 4 | 270.029 | 224.839 | 194.588 | 26.306 | 30.252 |
| 5 | 255.171 | 214.424 | 184.874 | 26.006 | 29.550 |
| 6 | 286.166 | 221.040 | 189.949 | 46.105 | 31.091 |
| 7 | 275.287 | 228.038 | 194.021 | 28.176 | 34.017 |
| 8 | 264.062 | 217.816 | 187.282 | 27.376 | 30.535 |
| 9 | 265.086 | 221.529 | 190.981 | 25.693 | 30.548 |
| 10 | 268.580 | 219.075 | 189.286 | 30.691 | 29.789 |

The standard deviation is the sample standard deviation (`n - 1`).

| Metric | Mean | Median | Sample SD |
| --- | ---: | ---: | ---: |
| Wall | 268.399 | 266.833 | 8.428 |
| Main | 220.998 | 220.136 | 4.569 |
| Solver | 190.381 | 189.971 | 3.918 |
| Startup | 29.042 | 26.937 | 6.177 |
| Harness | 30.617 | 30.508 | 1.320 |

## Day 24 exact rock recovery

Day 24 part 2 now subtracts the hailstone collision cross-product equations to form an exact six-unknown linear system for rock position and velocity. A rank-aware rational Gaussian elimination accepts arbitrarily long singular prefixes instead of assuming the first three stones are independent. Once six independent rows determine a candidate, exact validation against every original hailstone makes the remaining difference rows redundant; a globally rank-five system instead substitutes its affine family into the original quadratic collision equation. If the linear equations have still lower rank, or the rank-five quadratic vanishes identically, an unbounded integer-lattice enumeration covers every possible integral free-component tuple without a guessed coordinate or velocity limit. The rock's six components must be integral as required by the prompt; collision times remain exact rationals, must agree on all three axes, and must be nonnegative. This removes the previous componentwise velocity bounds, truncating division, parallel-case skips, mutable accumulated search state, and `-1` sentinel.

The official sample returns `47`. The personal input yields position `(129723668686742, 353939130278484, 227368817349775)`, velocity `(312, -116, 109)`, and answer `711031616315001`; the solver's exact final validation accepts all hailstones. Forty shuffled synthetic systems additionally covered fractional collision times, initially rank-deficient stones, rock velocities outside every hailstone velocity range, an `x = -1` rock, coordinates beyond `long` multiplication range, missing final newlines, repeat calls on one solver instance, and a perturbed inconsistent trajectory. Dedicated rank-five and rank-four systems verify the lower-rank lattice fallback. Exact part 1 checks covered stationary, overlapping, opposing, and separating collinear future paths. The corrected external expected-results record passes all 50 independent solves and all 25 combined solves with checksum `3a7181d2751be2eaeaea0d454c243728b7e1f7ee48de10762828623b3101907f`.

The current cold-plus-10 phase table above establishes the new correctness baseline. An immediately preceding current-source replication averaged 224.150ms solver and 296.288ms wall, with respective sample standard deviations of 7.884ms and 11.312ms. The drift and elevated dispersion make a speed comparison inconclusive; the lower-rank fallback is not entered by the full-rank personal input. These runs are intentionally not compared as a speed win against the prior branch: the previous workload returned the wrong answer and is not a valid performance baseline.

### Day 24 exact checked-long intersection path

Part 1 now precomputes checked `long` shadows for each hailstone and evaluates ordinary pair intersections with exact integer arithmetic. Checked multiply, subtract, negate, quotient, and remainder operations fall back to the unchanged `BigInteger` implementation on any overflow or out-of-range input. Parallel and collinear pairs also retain the complete `BigInteger` ray/segment treatment. The rational area comparison avoids overflowing `position * denominator`: it divides the nonnegative time numerator first, then normalizes the signed fractional remainder with `floorDiv`/`floorMod`. Bounds remain inclusive. The public area-bounds entry point makes the prompt's sample region directly testable without changing the personal-input bounds.

The official Part 1 sample returns 2 in `[7, 27]`. Two thousand deterministic inputs with two through eight stones matched the exact pre-change implementation, as did stationary and separating collinear paths, coordinates beyond `long`, and long-fitting values whose determinant multiplication overflows. All 50 independent answers and all 25 combined solves retain checksum `3a7181d2751be2eaeaea0d454c243728b7e1f7ee48de10762828623b3101907f`; Part 2 remains the corrected exact solver and returns `711031616315001`.

Ten fresh-JVM, counterbalanced Day 24 pairs compared the pushed `20cad33` baseline with the candidate. The candidate-minus-baseline `fullSolve` delta averaged -21.328ms with a paired 95% confidence interval of `[-21.630, -21.026]`ms (37.447ms to 16.119ms). One baseline and one candidate cold process were excluded.

The repository publication gate used a second clean, serial, counterbalanced set of ten full-25-day pairs against the same pushed tip. Paired candidate-minus-baseline results were:

| Metric | Baseline mean | Candidate mean | Mean delta | Paired 95% CI |
| --- | ---: | ---: | ---: | ---: |
| Wall | 287.755 | 268.399 | -19.356 | [-26.330, -12.382] |
| Main | 243.126 | 220.998 | -22.128 | [-25.514, -18.742] |
| Solver | 212.420 | 190.381 | -22.038 | [-25.125, -18.951] |
| Startup | 26.965 | 29.042 | +2.077 | [-2.133, 6.286] |
| Harness | 30.706 | 30.617 | -0.090 | [-1.056, 0.877] |

The aggregate solver interval is wholly below zero, so this batch passes the publication rule. Startup and harness movements are explicitly inconclusive. The current cold-plus-10 table above is the candidate half of these same pairs; its cold process is reported but excluded from statistics.

## Day 4 exact scratchcard scan

Day 4 now scans each line directly instead of running four regex splits per card, boxing match counts, and propagating copies through a `HashMap`. Decimal tokens are compared as normalized character spans, so signed values, leading zeros, and values beyond primitive numeric ranges remain exact without numeric conversion. Duplicate winning entries retain membership semantics, while each matching owned-number occurrence counts. Part 1 scores and Part 2 copy totals use `BigInteger`; an O(cards) range-difference propagation clips winnings to physical later cards rather than attempting to create nonexistent cards beyond the deck.

An isolated runner constructed `Day04` and its file-backed `Scanner` before timing `fullSolve`. One excluded cold pair (10.961ms baseline, 7.641ms candidate) was followed by 10 counterbalanced fresh-JVM pairs:

| Pair | Order | Baseline (ms) | Candidate (ms) | Delta (ms) |
| ---: | :---: | ---: | ---: | ---: |
| 1 | B-C | 11.406 | 6.986 | -4.420 |
| 2 | C-B | 10.627 | 7.069 | -3.558 |
| 3 | B-C | 11.193 | 6.854 | -4.339 |
| 4 | C-B | 10.745 | 7.237 | -3.508 |
| 5 | B-C | 11.258 | 7.002 | -4.256 |
| 6 | C-B | 11.477 | 7.676 | -3.801 |
| 7 | B-C | 10.822 | 6.893 | -3.929 |
| 8 | C-B | 10.652 | 7.124 | -3.528 |
| 9 | B-C | 10.874 | 7.070 | -3.804 |
| 10 | C-B | 11.023 | 7.019 | -4.004 |

The means were **11.008ms baseline** and **7.093ms candidate**, a **3.915ms (35.6%) reduction**. The paired-delta sample standard deviation was 0.338ms and the t(9) 95% confidence interval was **[-4.156ms, -3.673ms]**, so the per-day retention gate passes.

The pushed `b998f44` tip and queued candidate then ran one excluded cold pair plus 10 clean, serial, counterbalanced full-25-day pairs. Candidate-minus-baseline results were:

| Metric | Pushed-tip mean (ms) | Queued mean (ms) | Delta (ms) | Paired 95% CI (ms) |
| --- | ---: | ---: | ---: | ---: |
| Solver | 194.102 | 193.998 | -0.103 | [-5.450, +5.244] |
| Main | 224.831 | 224.764 | -0.067 | [-5.580, +5.446] |
| Startup | 27.593 | 27.798 | +0.205 | [-1.705, +2.115] |
| Harness | 30.730 | 30.766 | +0.036 | [-0.745, +0.817] |
| Wall | 271.696 | 270.277 | -1.419 | [-7.200, +4.362] |

The aggregate solver interval crossed zero, so this verified change remained deliberately uncommitted and unpushed. Day 9 has since joined the queue; the new combined publication gate is recorded below. The official sample returned `13 / 30`; personal answers remained `27454 / 6857330`. One thousand deterministic prompt-format decks matched the pushed implementation. Targeted checks covered duplicate semantics, clipping wins at the end of the deck, blank lines, CRLF, missing final newline, signed and arbitrary-length tokens, leading zeros, empty input, and a 70-match score beyond `long`. All 50 independent answers and all 25 combined solves retained checksum `3a7181d2751be2eaeaea0d454c243728b7e1f7ee48de10762828623b3101907f`.

## Day 9 primitive exact differences

Day 9 previously split every line with a regex, boxed every value into nested lists, recursively allocated each difference row, reversed every original history, and repeated the difference triangle for Part 2. It now scans whitespace-delimited signed decimals directly into one reusable primitive array and mutates that array through the finite-difference triangle, accumulating the right edge for Part 1 and the alternating left edge for Part 2 in the same pass. Checked `long` parsing and arithmetic retain the common fast path; a whole-line `BigInteger` fallback preserves exact answers for arbitrary prompt-valid integers and for any intermediate or total overflow.

An isolated runner constructed `Day09` and its file-backed `Scanner` before timing `fullSolve`. The excluded cold pair was 8.026ms baseline and 5.814ms candidate. Ten counterbalanced pairs of separate fresh JVMs followed:

| Pair | Order | Baseline (ms) | Candidate (ms) | Delta (ms) |
| ---: | :---: | ---: | ---: | ---: |
| 1 | B-C | 7.501 | 6.456 | -1.045 |
| 2 | C-B | 7.535 | 6.048 | -1.487 |
| 3 | B-C | 7.211 | 5.754 | -1.457 |
| 4 | C-B | 7.339 | 5.857 | -1.482 |
| 5 | B-C | 7.302 | 5.948 | -1.353 |
| 6 | C-B | 7.036 | 5.924 | -1.112 |
| 7 | B-C | 7.099 | 5.887 | -1.212 |
| 8 | C-B | 7.271 | 5.670 | -1.601 |
| 9 | B-C | 7.288 | 5.824 | -1.464 |
| 10 | C-B | 7.330 | 6.310 | -1.020 |

The measured means were **7.291ms baseline** and **5.968ms candidate**, a **1.323ms (18.2%) reduction**. The paired-delta sample standard deviation was 0.210ms and the t(9) 95% confidence interval was **[-1.473ms, -1.174ms]**, so the per-day retention gate passes.

The pushed `b998f44` tip and the combined Day 4 plus Day 9 queue then ran one excluded cold pair and 10 clean, serial, counterbalanced full-25-day pairs. The cold baseline/candidate values were respectively 272.531/269.538ms wall, 217.739/224.304ms main, 186.018/192.911ms solver, 35.915/26.447ms startup, and 31.721/31.393ms harness. Measured candidate-minus-baseline results were:

| Metric | Pushed-tip mean (ms) | Queued mean (ms) | Delta (ms) | Paired 95% CI (ms) |
| --- | ---: | ---: | ---: | ---: |
| Solver | 190.936 | 197.437 | +6.501 | [+1.043, +11.959] |
| Main | 221.891 | 228.572 | +6.680 | [+0.968, +12.393] |
| Startup | 27.780 | 27.135 | -0.645 | [-2.191, +0.902] |
| Harness | 30.955 | 31.134 | +0.179 | [-0.461, +0.819] |
| Wall | 267.587 | 274.154 | +6.568 | [-0.075, +13.211] |

A clean second cold-plus-10 replication immediately reversed the apparent direction, confirming substantial suite-level noise. Its excluded cold baseline/candidate solver values were 198.134/188.982ms, and its measured candidate-minus-baseline results were:

| Metric | Pushed-tip mean (ms) | Queued mean (ms) | Delta (ms) | Paired 95% CI (ms) |
| --- | ---: | ---: | ---: | ---: |
| Solver | 196.176 | 194.066 | -2.111 | [-8.069, +3.848] |
| Main | 227.608 | 225.277 | -2.331 | [-8.581, +3.920] |
| Startup | 28.054 | 27.862 | -0.192 | [-1.566, +1.181] |
| Harness | 31.431 | 31.211 | -0.220 | [-0.902, +0.461] |
| Wall | 274.364 | 269.291 | -5.073 | [-9.358, -0.787] |

Across all 20 counterbalanced pairs, summed solver means were 193.556ms pushed-tip and 195.751ms queued, a +2.195ms delta with paired 95% CI **[-1.990ms, +6.380ms]**. The combined interval crossed zero, so the aggregate publication gate remained unmet. Day 3 has since joined the queue; the latest gate is recorded below. The official example returned `114 / 2`; 1,000 deterministic polynomial histories matched direct exact polynomial evaluation at the next and preceding indices. Targeted checks covered blank histories, singleton and constant histories, repeated spaces, tabs, CRLF, optional leading signs, values and totals beyond `long`, and checked difference overflow. All 50 independent answers and all 25 combined solves retained checksum `3a7181d2751be2eaeaea0d454c243728b7e1f7ee48de10762828623b3101907f`.

## Day 3 single-pass schematic analysis

Day 3 previously built three boxed row maps, rescanned every line for numbers and symbols, and repeatedly reparsed number substrings during adjacency checks. It also added a part number once per neighboring symbol instead of once per number and retained mutable state across calls on the same solver. The replacement reads the grid once, finds each maximal decimal run once, visits only its perimeter, and accumulates distinct adjacent numbers directly into per-gear slots. Personal-input values stay on checked primitive arithmetic; arbitrary-length numbers, products, and totals promote exactly to `BigInteger`.

An isolated runner constructed `Day03` and its file-backed `Scanner` before timing `fullSolve`. The excluded cold pair was 8.355ms baseline and 5.831ms candidate. Ten counterbalanced fresh-JVM pairs followed:

| Pair | Order | Baseline (ms) | Candidate (ms) | Delta (ms) |
| ---: | :---: | ---: | ---: | ---: |
| 1 | B-C | 7.885 | 5.658 | -2.227 |
| 2 | C-B | 8.618 | 5.934 | -2.684 |
| 3 | B-C | 8.721 | 5.572 | -3.149 |
| 4 | C-B | 8.572 | 5.510 | -3.062 |
| 5 | B-C | 7.910 | 5.570 | -2.340 |
| 6 | C-B | 8.155 | 5.398 | -2.757 |
| 7 | B-C | 8.167 | 6.079 | -2.088 |
| 8 | C-B | 7.964 | 5.694 | -2.269 |
| 9 | B-C | 8.537 | 6.045 | -2.492 |
| 10 | C-B | 8.370 | 5.669 | -2.700 |

The means were **8.290ms baseline** and **5.713ms candidate**, a **2.577ms (31.1%) reduction**. The paired-delta sample standard deviation was 0.356ms and the t(9) 95% confidence interval was **[-2.831ms, -2.322ms]**, so the per-day retention gate passes.

The pushed `b998f44` tip and the combined Day 3, Day 4, and Day 9 queue then ran two independent clean cold-plus-10 full-suite sets. Their excluded cold baseline/candidate solver values were 196.618/204.350ms and 191.452/202.455ms. The first measured set was:

| Metric | Pushed-tip mean (ms) | Queued mean (ms) | Delta (ms) | Paired 95% CI (ms) |
| --- | ---: | ---: | ---: | ---: |
| Solver | 194.231 | 196.472 | +2.240 | [-3.317, +7.797] |
| Main | 224.992 | 227.710 | +2.718 | [-3.052, +8.489] |
| Startup | 27.985 | 28.968 | +0.983 | [-0.924, +2.890] |
| Harness | 30.760 | 31.239 | +0.478 | [-0.105, +1.061] |
| Wall | 271.103 | 274.786 | +3.683 | [-2.029, +9.396] |

The replication measured 194.092/195.055ms solver, a +0.963ms delta with paired 95% CI [-4.827ms, +6.752ms]. Across all 20 pairs, summed solver means were 194.162ms pushed-tip and 195.763ms queued, a +1.601ms delta with paired 95% CI **[-2.025ms, +5.228ms]**. The interval still crosses zero, so the three independently verified changes remain deliberately uncommitted and unpushed.

The official sample returned `4361 / 467835`; 1,000 deterministic ragged schematics matched an independent cell-neighborhood oracle. Targeted checks covered edge and diagonal symbols, one number touching multiple symbols, a gear touching multiple digits of one number, gears with one/two/three distinct numbers, blank rows, CRLF, missing final newlines, leading zeros, arbitrary-length values, overflowing products and totals, and repeated calls on one solver instance. All 50 independent answers and all 25 combined solves retained checksum `3a7181d2751be2eaeaea0d454c243728b7e1f7ee48de10762828623b3101907f`.

## Day 10 loop geometry

Day 10 previously assumed that `S` connected downward, allocated an expanded 281×281 integer raster for the personal 140×140 grid, allocated two expanded-grid flood queues, and flood-filled almost the entire raster to count the interior. It now discovers both reciprocal start connections, traces only the actual loop with four-bit pipe masks, accumulates its signed shoelace area, and obtains the enclosed cell-center lattice points from Pick's theorem. All state is invocation-local, disconnected junk pipes are ignored, and malformed grids or broken connections are rejected instead of indexing unpredictably.

An isolated runner constructed `Day10` and its file-backed `Scanner` before timing `fullSolve`. The excluded cold pair was 7.814ms baseline and 5.842ms candidate. Ten counterbalanced fresh-JVM pairs followed:

| Pair | Order | Baseline (ms) | Candidate (ms) | Delta (ms) |
| ---: | :---: | ---: | ---: | ---: |
| 1 | B-C | 7.810 | 5.823 | -1.987 |
| 2 | C-B | 8.091 | 6.418 | -1.673 |
| 3 | B-C | 7.921 | 6.127 | -1.794 |
| 4 | C-B | 7.999 | 5.823 | -2.176 |
| 5 | B-C | 8.004 | 5.804 | -2.200 |
| 6 | C-B | 7.708 | 6.349 | -1.359 |
| 7 | B-C | 7.799 | 5.762 | -2.037 |
| 8 | C-B | 7.722 | 5.783 | -1.939 |
| 9 | B-C | 8.424 | 5.941 | -2.482 |
| 10 | C-B | 7.951 | 6.066 | -1.884 |

The means were **7.943ms baseline** and **5.989ms candidate**, a **1.953ms (24.6%) reduction**. The paired-delta sample standard deviation was 0.309ms and the t(9) 95% confidence interval was **[-2.174ms, -1.732ms]**, so the per-day retention gate passes.

The pushed `b998f44` tip and the combined Day 3, Day 4, Day 9, and Day 10 queue then ran one excluded cold pair plus 10 clean, serial, counterbalanced full-25-day pairs. The cold baseline/candidate values were respectively 276.266/276.901ms wall, 225.672/225.774ms main, 192.282/191.896ms solver, 33.482/34.736ms startup, and 33.389/33.877ms harness. Measured candidate-minus-baseline results were:

| Metric | Pushed-tip mean (ms) | Queued mean (ms) | Delta (ms) | Paired 95% CI (ms) |
| --- | ---: | ---: | ---: | ---: |
| Solver | 197.273 | 196.575 | -0.698 | [-6.603, +5.207] |
| Main | 230.207 | 229.089 | -1.118 | [-7.088, +4.851] |
| Startup | 29.314 | 28.100 | -1.214 | [-3.128, +0.701] |
| Harness | 32.935 | 32.514 | -0.421 | [-1.450, +0.609] |
| Wall | 275.304 | 273.247 | -2.057 | [-7.228, +3.113] |

The summed solver interval crosses zero, so the four independently verified changes remain deliberately uncommitted and unpushed. The personal trace has 13,198 boundary edges and doubled signed area -14,150, yielding `6599 / 477`. The official small example returned `4 / 1`, and the complex official enclosure example returned 10. Another 3,770 generated rectangular loops placed `S` at every boundary position, covering all six effective pipe shapes, both orientations, borders, CRLF, and missing final newlines. Targeted failures covered empty/ragged grids, missing or duplicate starts, invalid start degree, broken reciprocity, and paths leaving the grid. Repeated calls on one solver remained independent. All 50 independent answers and all 25 combined solves retained checksum `3a7181d2751be2eaeaea0d454c243728b7e1f7ee48de10762828623b3101907f`.

## Day 19 primitive workflow dispatch

Day 19 now reads the input once, maps workflow names to primitive IDs, stores conditions in primitive arrays, and dispatches parts without regex splitting, per-part maps, or intermediate accepted-part lists. Part 2 follows the ordered workflow rules directly over packed inclusive bounds and accumulates accepted volumes instead of copying four maps for every branch. The range split explicitly handles all-pass and all-fail conditions, including thresholds at and beyond the `1..4000` prompt domain. Part 1 retains exact totals with lazy `BigInteger` promotion and rejects a nonterminating workflow cycle.

An isolated runner constructed `Day19` and its file-backed `Scanner` before timing `fullSolve`. The excluded cold pair was 21.436ms baseline and 9.341ms candidate. Ten counterbalanced fresh-JVM pairs followed:

| Pair | Order | Baseline (ms) | Candidate (ms) | Delta (ms) |
| ---: | :---: | ---: | ---: | ---: |
| 1 | B-C | 20.101 | 8.731 | -11.370 |
| 2 | C-B | 20.135 | 9.019 | -11.116 |
| 3 | B-C | 19.521 | 9.057 | -10.464 |
| 4 | C-B | 20.058 | 8.880 | -11.178 |
| 5 | B-C | 19.957 | 8.608 | -11.349 |
| 6 | C-B | 20.472 | 8.428 | -12.044 |
| 7 | B-C | 20.349 | 8.557 | -11.792 |
| 8 | C-B | 20.169 | 8.687 | -11.482 |
| 9 | B-C | 19.366 | 8.455 | -10.911 |
| 10 | C-B | 19.409 | 9.101 | -10.308 |

The means were **19.954ms baseline** and **8.752ms candidate**, a **11.201ms (56.1%) reduction**. The paired-delta t(9) 95% confidence interval was **[-11.588ms, -10.815ms]**, so the per-day retention gate passes.

The pushed `b998f44` tip and the combined Day 3, Day 4, Day 9, Day 10, and Day 19 queue then ran one excluded cold pair plus 10 clean, serial, counterbalanced full-25-day pairs. The cold baseline/candidate solver values were 221.230/216.279ms. Measured candidate-minus-baseline results were:

| Metric | Pushed-tip mean (ms) | Queued mean (ms) | Delta (ms) | Paired 95% CI (ms) |
| --- | ---: | ---: | ---: | ---: |
| Solver | 203.160 | 202.259 | -0.901 | [-6.847, +5.044] |
| Main | 239.409 | 238.214 | -1.195 | [-7.868, +5.477] |
| Startup | 37.497 | 36.727 | -0.770 | [-6.512, +4.971] |
| Harness | 36.249 | 35.955 | -0.294 | [-1.398, +0.809] |
| Wall | 294.674 | 292.620 | -2.054 | [-13.581, +9.472] |

The summed solver interval crosses zero, so all five independently verified changes remain deliberately uncommitted and unpushed. The official example returned `19114 / 167409079868000`; personal answers remained `425811 / 131796824371749`. Boundary tests cover empty, full-domain, narrowed all-pass, and narrowed all-fail splits. Three hundred deterministic acyclic workflow graphs, wrapped over domains of two through six values per category, matched independent exhaustive enumeration for both parts under LF/CRLF and optional-final-newline inputs. All 50 independent answers and all 25 combined solves retained checksum `3a7181d2751be2eaeaea0d454c243728b7e1f7ee48de10762828623b3101907f`.

## Day 13 single-pass reflection scoring

Day 13 now bulk-reads the input once into reusable flat character storage and counts mirrored-cell mismatches once for each possible split. A split contributes to Part 1 at zero mismatches or Part 2 at exactly one mismatch; comparisons stop as soon as a second mismatch makes both answers impossible. This replaces 1,390 line-level Scanner operations, boxed pattern/list structures, a transposed `int[][]` per pattern, and separate complete reflection traversals for the two parts. Rectangular dimensions, multiple blank separators, LF/CRLF, missing final newlines, empty input, and exact score totals are handled without personal-input assumptions.

An isolated runner constructed `Day13` and its file-backed `Scanner` before timing `fullSolve`. The excluded cold pair was 7.415ms baseline and 3.309ms candidate. Ten counterbalanced fresh-JVM pairs followed:

| Pair | Order | Baseline (ms) | Candidate (ms) | Delta (ms) |
| ---: | :---: | ---: | ---: | ---: |
| 1 | B-C | 7.207 | 3.181 | -4.026 |
| 2 | C-B | 7.407 | 3.036 | -4.371 |
| 3 | B-C | 7.185 | 2.912 | -4.274 |
| 4 | C-B | 7.583 | 2.952 | -4.631 |
| 5 | B-C | 7.251 | 2.957 | -4.295 |
| 6 | C-B | 7.144 | 3.106 | -4.038 |
| 7 | B-C | 7.160 | 3.198 | -3.962 |
| 8 | C-B | 7.667 | 3.150 | -4.517 |
| 9 | B-C | 7.040 | 3.144 | -3.895 |
| 10 | C-B | 6.972 | 3.032 | -3.940 |

The means were **7.262ms baseline** and **3.067ms candidate**, a **4.195ms (57.8%) reduction**. The paired-delta t(9) 95% confidence interval was **[-4.380ms, -4.010ms]**, so the per-day retention gate passes.

The pushed `b998f44` tip and the combined Day 3, Day 4, Day 9, Day 10, Day 13, and Day 19 queue ran two independent clean cold-plus-10 full-suite sets. The first solver means were 192.474/187.601ms, a -4.873ms delta with 95% CI [-10.008ms, +0.263ms]. The replication measured 197.313/192.279ms, a -5.034ms delta with 95% CI [-10.909ms, +0.841ms]. Neither set alone was sufficient; across the predeclared combined 20 pairs, candidate-minus-baseline results were:

| Metric | Pushed-tip mean (ms) | Six-day batch mean (ms) | Delta (ms) | Paired 95% CI (ms) |
| --- | ---: | ---: | ---: | ---: |
| Solver | 194.893 | 189.940 | -4.953 | [-8.467, -1.440] |
| Main | 226.540 | 221.078 | -5.462 | [-9.004, -1.919] |
| Startup | 28.640 | 28.460 | -0.180 | [-1.534, +1.174] |
| Harness | 31.647 | 31.138 | -0.508 | [-1.026, +0.009] |
| Wall | 273.526 | 267.368 | -6.158 | [-9.369, -2.947] |

The summed solver interval is wholly below zero, so the six-day batch passes the repository publication gate. The official example returned `405 / 400`; personal answers remained `29165 / 32192`. One thousand deterministic inputs containing one to five random rectangular patterns matched an independent mirrored-cell oracle for every zero- and one-mismatch split, including LF/CRLF, repeated separators, and optional final newlines. Immediately before publication, all 50 independent answers and all 25 combined solves retained checksum `3a7181d2751be2eaeaea0d454c243728b7e1f7ee48de10762828623b3101907f`.

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

The original measurements for this change overlapped leaked background AoC JVMs and are withdrawn. The clean recovery first verified through the OS process table that no AoC Java/Javac, benchmark, timing, or watchdog process was active. Every recovery Java/Javac invocation then ran in a tracked process group with a hard deadline and a final descendant check. An isolated runner constructed the solver and input `Scanner` before timing the exact `fullSolve` call, checked both answers, and ran one excluded cold JVM per variant followed by 10 counterbalanced pairs of separate JVMs against baseline `6a3ec0f`.

| Pair | Order | Objects/regex/history (ms) | Exact streaming geometry (ms) | Delta (ms) |
| ---: | :---: | ---: | ---: | ---: |
| 1 | B-C | 10.556 | 6.833 | -3.723 |
| 2 | C-B | 10.982 | 7.102 | -3.880 |
| 3 | B-C | 10.320 | 7.123 | -3.197 |
| 4 | C-B | 10.946 | 7.254 | -3.692 |
| 5 | B-C | 10.468 | 6.977 | -3.492 |
| 6 | C-B | 10.214 | 7.555 | -2.659 |
| 7 | B-C | 10.482 | 6.953 | -3.529 |
| 8 | C-B | 10.764 | 7.144 | -3.620 |
| 9 | B-C | 10.466 | 6.772 | -3.695 |
| 10 | C-B | 10.194 | 7.000 | -3.194 |

The excluded cold values were 10.600ms baseline and 6.903ms candidate. The measured means were **10.539ms baseline** and **7.071ms candidate**, a **3.468ms (32.9%) reduction**. The paired-delta sample standard deviation was 0.360ms and the t(9) 95% confidence interval was **[-3.725ms, -3.210ms]**.

The clean whole-suite comparison used the same counterbalanced order and separate child JVMs:

| Pair | Order | Baseline solver (ms) | Candidate solver (ms) | Delta (ms) |
| ---: | :---: | ---: | ---: | ---: |
| 1 | B-C | 190.970 | 194.159 | +3.188 |
| 2 | C-B | 197.241 | 193.115 | -4.126 |
| 3 | B-C | 185.963 | 191.786 | +5.823 |
| 4 | C-B | 189.957 | 190.637 | +0.679 |
| 5 | B-C | 194.903 | 194.403 | -0.499 |
| 6 | C-B | 187.964 | 190.108 | +2.144 |
| 7 | B-C | 187.754 | 201.173 | +13.419 |
| 8 | C-B | 197.251 | 190.967 | -6.285 |
| 9 | B-C | 197.889 | 196.900 | -0.990 |
| 10 | C-B | 203.815 | 189.644 | -14.171 |

Its solver means were 193.371ms baseline and 193.289ms candidate, a -0.082ms (-0.04%) delta. The paired-delta sample standard deviation was 7.361ms and the t(9) 95% confidence interval was **[-5.347ms, +5.184ms]**, so aggregate movement across the other 24 days remains explicitly inconclusive. Separate standard phase runs had these excluded cold processes:

| Variant | Wall (ms) | Main (ms) | Solver (ms) | Startup (ms) | Harness (ms) |
| --- | ---: | ---: | ---: | ---: | ---: |
| Baseline | 265.092 | 217.996 | 186.271 | 28.680 | 31.725 |
| Candidate | 268.881 | 220.773 | 189.909 | 29.616 | 30.864 |

Their 10-process means were:

| Metric | Baseline mean (ms) | Candidate mean (ms) | Change (ms) |
| --- | ---: | ---: | ---: |
| Wall | 265.267 | 266.044 | +0.777 |
| Main | 221.175 | 222.023 | +0.847 |
| Solver | 190.974 | 191.989 | +1.015 |
| Startup | 25.391 | 25.380 | -0.011 |
| Harness | 30.201 | 30.034 | -0.168 |

The accepted evidence is the clean isolated paired interval; the clean whole-suite paired interval and separate phase means are retained transparently as inconclusive aggregate evidence. Verification retained all 50 expected answers and all 25 combined-solve pairs. The official sample returned `62 / 952408144115`; 300 deterministic rectangles with LF/CRLF and optional final newlines matched the exact pre-change implementation; and candidate-only cases covered reverse orientation plus a literal distance far beyond `long`, checked against independent rectangle arithmetic.

## Day 2 direct exact game parsing

Day 2 previously compiled multiple regex splits per line and draw, repeatedly split each draw again for its number and color, and maintained a boxed string-keyed map for the three maxima. It now scans each line once, updates three `BigInteger` maxima directly, and derives both answers together. Missing colors correctly contribute zero to the power, and IDs, cube counts, products, and sums are no longer limited to `int`.

An isolated runner constructed the solver and input `Scanner` before timing the exact `fullSolve` call, checked both answers, and ran one excluded cold JVM per variant followed by 10 counterbalanced pairs of separate JVMs against baseline `dca3492`.

| Pair | Order | Regex/map baseline (ms) | Direct exact parser (ms) | Delta (ms) |
| ---: | :---: | ---: | ---: | ---: |
| 1 | B-C | 6.714 | 5.735 | -0.979 |
| 2 | C-B | 6.912 | 6.121 | -0.791 |
| 3 | B-C | 6.017 | 5.631 | -0.387 |
| 4 | C-B | 6.425 | 5.680 | -0.745 |
| 5 | B-C | 6.458 | 5.269 | -1.189 |
| 6 | C-B | 7.167 | 5.865 | -1.302 |
| 7 | B-C | 6.752 | 6.094 | -0.658 |
| 8 | C-B | 6.393 | 5.808 | -0.585 |
| 9 | B-C | 6.235 | 6.098 | -0.137 |
| 10 | C-B | 6.696 | 5.262 | -1.434 |

The excluded cold values were 6.771ms baseline and 5.825ms candidate. The measured means were **6.577ms baseline** and **5.756ms candidate**, a **0.821ms (12.5%) reduction**. The paired-delta sample standard deviation was 0.410ms and the t(9) 95% confidence interval was **[-1.114ms, -0.528ms]**.

The clean counterbalanced whole-suite solver means were 200.246ms baseline and 199.136ms candidate, a -1.110ms delta with a 95% confidence interval of [-4.276ms, +2.056ms]. Separate standard phase runs had these excluded cold processes:

| Variant | Wall (ms) | Main (ms) | Solver (ms) | Startup (ms) | Harness (ms) |
| --- | ---: | ---: | ---: | ---: | ---: |
| Baseline | 276.577 | 228.765 | 194.951 | 31.433 | 33.814 |
| Candidate | 277.795 | 232.557 | 201.263 | 28.281 | 31.294 |

Their 10-process means were:

| Metric | Baseline mean (ms) | Candidate mean (ms) | Change (ms) |
| --- | ---: | ---: | ---: |
| Wall | 274.129 | 270.381 | -3.747 |
| Main | 230.636 | 227.464 | -3.172 |
| Solver | 198.475 | 195.587 | -2.887 |
| Startup | 27.232 | 26.694 | -0.538 |
| Harness | 32.161 | 31.876 | -0.285 |

The accepted evidence is the isolated paired interval; the whole-suite interval is explicitly inconclusive, and the complete phase split is retained transparently. All 50 independent answers and 25 combined solves retain checksum `d6af64d6b36b5441bc0ca5d8ab99cf7568c6bc9b62ac807d37d96d3c3aec372b`. The official sample returned `8 / 2286`; candidate-only checks covered omitted colors, flexible whitespace, and IDs/counts/products beyond `long` range.

## Day 25 exact three-wire min-cut

The previous endpoint/path-removal heuristic produced the personal and official answers but was not a general cut algorithm: a valid graph made from internally dense groups of six and seven vertices joined by exactly three wires returned `12` instead of `42`. Day 25 now deduplicates logical undirected wires and computes an exact global minimum cut with capped unit-capacity max flows. The prompt guarantees the required cut has three wires, so the search stops as soon as an exact cut of size three is proven. The answer product uses `long` arithmetic.

An isolated runner constructed the solver and input `Scanner` before timing the exact `fullSolve` call. One excluded cold pair was followed by 10 counterbalanced pairs of separate JVM processes:

| Pair | Order | Heuristic (ms) | Exact min-cut (ms) | Delta (ms) |
| ---: | :---: | ---: | ---: | ---: |
| 1 | B-C | 15.482 | 14.274 | -1.208 |
| 2 | C-B | 16.523 | 15.180 | -1.342 |
| 3 | B-C | 15.712 | 13.864 | -1.848 |
| 4 | C-B | 15.722 | 13.931 | -1.791 |
| 5 | B-C | 15.838 | 14.377 | -1.461 |
| 6 | C-B | 15.713 | 14.118 | -1.595 |
| 7 | B-C | 15.701 | 15.478 | -0.224 |
| 8 | C-B | 15.510 | 13.885 | -1.625 |
| 9 | B-C | 16.230 | 13.949 | -2.281 |
| 10 | C-B | 15.538 | 13.960 | -1.578 |

The excluded cold values were 16.244ms heuristic and 14.629ms exact. The measured means were **15.797ms heuristic** and **14.302ms exact**, a **1.495ms (9.5%) reduction**. The paired-delta sample standard deviation was 0.536ms and the t(9) 95% confidence interval was **[-1.879ms, -1.112ms]**. The current-source cold-plus-10 run and its complete wall/main/solver/startup/harness split are recorded at the top of this document.

All 50 independent answers and 25 combined solves retain checksum `d6af64d6b36b5441bc0ca5d8ab99cf7568c6bc9b62ac807d37d96d3c3aec372b`. The official example returned `54`; 250 generated pairs of internally dense five-to-eight-vertex groups joined by exactly three distinct wires returned the expected partition products, including reciprocal/duplicate edge declarations. Separate and combined entry points agreed throughout.

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
