# Advent of Code 2023 performance

## Methodology and current full suite

The baseline is the normal Java solver source at pre-speed commit 98a7731647e246658055e85a765ab155b8a3847f, compiled with the current measurement adapters. The current revision uses the same adapters, JDK, input bytes, and process protocol. Each comparison used one excluded cold JVM per revision followed by 10 separate JVM pairs in alternating order. Values are means in milliseconds; confidence intervals are paired two-sided 95% intervals for current minus baseline.

Solver sums the 25 fullSolve calls. Startup ends at the child start marker, harness is main minus solver, and wall is parent-observed process lifetime. Verification reran all 50 expected independent answers, all 25 combined solves, and independent/fullSolve equivalence, including the corrected exact Day 24 result. The excluded cold solver values were 439.999 ms baseline and 192.097 ms current.

| Metric | Pre-PR | Current | Delta | Paired 95% CI |
| --- | ---: | ---: | ---: | ---: |
| Wall | 512.504 | 272.238 | -240.266 | [-251.263, -229.268] |
| Main | 463.363 | 224.696 | -238.667 | [-249.134, -228.200] |
| Solver | 428.046 | 191.797 | -236.249 | [-246.731, -225.768] |
| Startup | 29.012 | 29.406 | +0.395 | [-1.493, +2.282] |
| Harness | 35.317 | 32.899 | -2.418 | [-3.276, -1.560] |

The publication gate compared previous replacement tip 9062d5c with this replay. Solver time fell from 415.348 ms to 189.144 ms, a -226.204 ms paired delta with 95% CI [-234.239, -218.168].

Reproduce on an idle machine by compiling separate baseline and current classpaths, then run src.FreshJvmBenchmark --verify and src.FreshJvmBenchmark --compare BASELINE_CP CURRENT_CP with -Daoc.data.dir pointing to the repository data directory.

## Day 01

One forward scan recognizes numeric and overlapping word digits without line buffering, substrings, or four separate searches.

| Pre-PR | Current | Delta | 95% CI |
| ---: | ---: | ---: | ---: |
| 9.191 | 6.531 | -2.660 | [-2.865, -2.456] |

## Day 02

Each game is scanned once into three exact maxima, producing feasibility and power together without regex splitting or string-keyed maps.

| Pre-PR | Current | Delta | 95% CI |
| ---: | ---: | ---: | ---: |
| 3.633 | 2.945 | -0.688 | [-0.873, -0.503] |

## Day 03

One grid pass parses each maximal number once and inspects only its perimeter, sharing symbol and gear adjacency for both answers.

| Pre-PR | Current | Delta | 95% CI |
| ---: | ---: | ---: | ---: |
| 5.051 | 2.992 | -2.058 | [-2.244, -1.873] |

## Day 04

Direct token-span membership and range-difference copy propagation replace regex splits, boxed match counts, and per-card maps.

| Pre-PR | Current | Delta | 95% CI |
| ---: | ---: | ---: | ---: |
| 5.847 | 3.644 | -2.203 | [-2.447, -1.960] |

## Day 05

Unchanged from the pre-PR implementation. Current solver mean: 2.827 ms.

## Day 06

The spaced and concatenated races are parsed together, then an overflow-safe integer binary search counts strict winning holds.

| Pre-PR | Current | Delta | 95% CI |
| ---: | ---: | ---: | ---: |
| 2.395 | 0.282 | -2.113 | [-2.223, -2.003] |

## Day 07

Direct card ranks and two-largest frequency groups replace regex tokenization, rank-string scans, and frequency sorting.

| Pre-PR | Current | Delta | 95% CI |
| ---: | ---: | ---: | ---: |
| 5.856 | 3.200 | -2.656 | [-3.069, -2.242] |

## Day 08

The network is parsed once into primitive node IDs; shared traversal finds each start cycle and combines lengths with exact LCM arithmetic.

| Pre-PR | Current | Delta | 95% CI |
| ---: | ---: | ---: | ---: |
| 15.582 | 3.393 | -12.189 | [-12.689, -11.690] |

## Day 09

One reusable primitive difference triangle derives the next and previous values together, with exact fallback on overflow.

| Pre-PR | Current | Delta | 95% CI |
| ---: | ---: | ---: | ---: |
| 3.070 | 2.565 | -0.505 | [-0.692, -0.319] |

## Day 10

A reciprocal pipe-mask loop walk replaces doubled-grid flood fill; loop length and shoelace/Pick geometry provide both answers.

| Pre-PR | Current | Delta | 95% CI |
| ---: | ---: | ---: | ---: |
| 11.147 | 2.723 | -8.424 | [-8.677, -8.171] |

## Day 11

Primitive row and column counts plus prefix aggregates replace galaxy objects, pair enumeration, and repeated empty-axis scans.

| Pre-PR | Current | Delta | 95% CI |
| ---: | ---: | ---: | ---: |
| 7.850 | 0.956 | -6.894 | [-7.192, -6.595] |

## Day 12

Each record uses two rolling dynamic-programming columns instead of a full record-by-group matrix, sharing parsed work across both parts.

| Pre-PR | Current | Delta | 95% CI |
| ---: | ---: | ---: | ---: |
| 8.856 | 8.472 | -0.384 | [-0.589, -0.179] |

## Day 13

Flat character storage counts mirrored mismatches once per split and stops after the second mismatch, serving both reflection criteria.

| Pre-PR | Current | Delta | 95% CI |
| ---: | ---: | ---: | ---: |
| 2.138 | 2.027 | -0.111 | [-0.186, -0.036] |

## Day 14

One mutable grid supplies the north load and billion-cycle result, with compact cycle detection and allocation-free directional shifts.

| Pre-PR | Current | Delta | 95% CI |
| ---: | ---: | ---: | ---: |
| 19.349 | 14.381 | -4.968 | [-5.770, -4.166] |

## Day 15

The initialization sequence is parsed once by character index into direct box arrays, avoiding regex tokens and repeated label allocation.

| Pre-PR | Current | Delta | 95% CI |
| ---: | ---: | ---: | ---: |
| 7.384 | 2.919 | -4.465 | [-5.235, -3.695] |

## Day 16

Stamped primitive beam states and a reusable traversal stack replace per-start object graphs and repeated visited allocations.

| Pre-PR | Current | Delta | 95% CI |
| ---: | ---: | ---: | ---: |
| 89.810 | 16.420 | -73.390 | [-78.997, -67.783] |

## Day 17

A flat state model and bounded integer bucket queue replace object-heavy shortest-path states and a general priority queue.

| Pre-PR | Current | Delta | 95% CI |
| ---: | ---: | ---: | ---: |
| 98.295 | 27.472 | -70.822 | [-76.074, -65.571] |

## Day 18

Both instruction interpretations stream through exact shoelace and boundary accumulators without regexes, objects, or coordinate histories.

| Pre-PR | Current | Delta | 95% CI |
| ---: | ---: | ---: | ---: |
| 1.590 | 1.765 | +0.175 | [+0.124, +0.225] |

## Day 19

Workflows and parts are parsed once into primitive IDs and packed bounds, then ordered range splits accumulate accepted volume directly.

| Pre-PR | Current | Delta | 95% CI |
| ---: | ---: | ---: | ---: |
| 5.083 | 5.076 | -0.007 | [-0.161, +0.146] |

## Day 20

Modules are parsed once into primitive IDs and arrays; one queue-driven pulse engine supplies the thousand-press count and cycle periods.

| Pre-PR | Current | Delta | 95% CI |
| ---: | ---: | ---: | ---: |
| 34.300 | 12.321 | -21.979 | [-23.067, -20.891] |

## Day 21

One parsed garden and shared reachable-distance pass provide the finite-step count and the quadratic infinite-grid extrapolation.

| Pre-PR | Current | Delta | 95% CI |
| ---: | ---: | ---: | ---: |
| 6.964 | 6.418 | -0.546 | [-0.715, -0.377] |

## Day 22

Bricks settle once against an exact top surface, directly producing the support graph reused for removal and cascade counts.

| Pre-PR | Current | Delta | 95% CI |
| ---: | ---: | ---: | ---: |
| 24.777 | 6.871 | -17.906 | [-18.213, -17.599] |

## Day 23

Character-array grid access feeds the compressed junction graph and longest-path search without repeated String slicing and conversion.

| Pre-PR | Current | Delta | 95% CI |
| ---: | ---: | ---: | ---: |
| 35.528 | 37.507 | +1.979 | [+0.457, +3.501] |

## Day 24

Checked-long intersections with exact fallback and rank-aware rational rock recovery replace truncated division and incomplete validation.

| Pre-PR | Current | Delta | 95% CI |
| ---: | ---: | ---: | ---: |
| 14.281 | 9.642 | -4.640 | [-5.421, -3.859] |

## Day 25

An exact global three-wire minimum cut replaces the path-removal heuristic and reuses one deduplicated primitive graph.

| Pre-PR | Current | Delta | 95% CI |
| ---: | ---: | ---: | ---: |
| 6.984 | 8.447 | +1.463 | [+1.099, +1.827] |
