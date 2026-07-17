# Advent of Code 2023 performance

## Methodology and current full suite

All timings use the original personal input corpus with OpenJDK 23.0.1 on Apple silicon. The harness compiles the fixed pre-speed source and current source into separate empty classpaths, excludes one true-cold launch per side, then runs 10 separate JVM pairs in counterbalanced A/B order. Solver is the sum of the 25 `fullSolve` calls; main includes solver plus in-process harness work; startup is process launch through the child start marker; harness is main minus solver; wall is the complete child process.

Reproduce after compiling both trees with `java -Daoc.data.dir=<data-dir> -cp <current-cp> src.FreshJvmBenchmark --compare <previous-cp> <current-cp>`. Every published state passes all 50 independent solves, all 25 combined solves, and independent/`fullSolve` equivalence.

Current 25-day means:

| Wall (ms) | Main (ms) | Solver (ms) | Startup (ms) | Harness (ms) |
|---:|---:|---:|---:|---:|
| 332.478763 | 286.887254 | 252.441880 | 30.727833 | 34.445374 |

Latest publication gate versus the preceding replay tip:

| Metric | Previous mean (ms) | Current mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|---:|
| wall | 419.217387 | 332.478763 | -86.738625 | [-93.803798, -79.673452] |
| main | 372.453750 | 286.887254 | -85.566496 | [-93.008458, -78.124534] |
| solver | 337.860677 | 252.441880 | -85.418797 | [-93.109759, -77.727834] |
| startup | 30.644921 | 30.727833 | 0.082913 | [-1.624174, 1.789999] |
| harness | 34.593074 | 34.445374 | -0.147699 | [-1.424188, 1.128789] |

## Day 01

Unchanged from the pre-PR implementation. Current solver mean: 9.021429 ms.

## Day 02

Unchanged from the pre-PR implementation. Current solver mean: 3.492471 ms.

## Day 03

Unchanged from the pre-PR implementation. Current solver mean: 5.086834 ms.

## Day 04

Unchanged from the pre-PR implementation. Current solver mean: 5.940421 ms.

## Day 05

Unchanged from the pre-PR implementation. Current solver mean: 2.919400 ms.

## Day 06

Unchanged from the pre-PR implementation. Current solver mean: 2.286233 ms.

## Day 07

Unchanged from the pre-PR implementation. Current solver mean: 6.188412 ms.

## Day 08

Unchanged from the pre-PR implementation. Current solver mean: 16.812946 ms.

## Day 09

Unchanged from the pre-PR implementation. Current solver mean: 2.402566 ms.

## Day 10

Unchanged from the pre-PR implementation. Current solver mean: 11.535087 ms.

## Day 11

Unchanged from the pre-PR implementation. Current solver mean: 8.045275 ms.

## Day 12

Each record uses two rolling dynamic-programming columns instead of a full record-by-group matrix, sharing parsed work across both parts.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 9.825717 | — | — |
| Current | 7.410284 | -2.415433 | [-3.171659, -1.659207] |

## Day 13

Flat character storage counts mirrored mismatches once per split and stops after the second mismatch, serving both reflection criteria.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 2.222617 | — | — |
| Current | 1.493121 | -0.729496 | [-0.873994, -0.584998] |

## Day 14

One mutable grid supplies the north load and billion-cycle result, with compact cycle detection and allocation-free directional shifts.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 21.278437 | — | — |
| Current | 18.039067 | -3.239371 | [-5.396386, -1.082355] |

## Day 15

Unchanged from the pre-PR implementation. Current solver mean: 7.856713 ms.

## Day 16

Stamped primitive beam states and a reusable traversal stack replace per-start object graphs and repeated visited allocations.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 88.265783 | — | — |
| Current | 23.601092 | -64.664692 | [-71.166871, -58.162512] |

## Day 17

A flat state model and bounded integer bucket queue replace object-heavy shortest-path states and a general priority queue.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 111.916142 | — | — |
| Current | 31.164063 | -80.752079 | [-87.750467, -73.753691] |

## Day 18

Unchanged from the pre-PR implementation. Current solver mean: 1.775796 ms.

## Day 19

Unchanged from the pre-PR implementation. Current solver mean: 6.148337 ms.

## Day 20

Modules are parsed once into primitive IDs and arrays; one queue-driven pulse engine supplies the thousand-press count and cycle periods.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 36.403233 | — | — |
| Current | 11.330667 | -25.072567 | [-26.535352, -23.609781] |

## Day 21

One parsed garden and shared reachable-distance pass provide the finite-step count and the quadratic infinite-grid extrapolation.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 7.754142 | — | — |
| Current | 4.968513 | -2.785629 | [-3.091764, -2.479494] |

## Day 22

Bricks settle once against an exact top surface, directly producing the support graph reused for removal and cascade counts.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 28.294933 | — | — |
| Current | 7.307504 | -20.987429 | [-22.131220, -19.843638] |

## Day 23

Character-array grid access feeds the compressed junction graph and longest-path search without repeated String slicing and conversion.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 36.417317 | — | — |
| Current | 35.747725 | -0.669592 | [-2.712620, 1.373436] |

## Day 24

Checked-long intersections with exact fallback and rank-aware rational rock recovery replace truncated division and incomplete validation.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 13.352154 | — | — |
| Current | 13.236475 | -0.115679 | [-0.732269, 0.500910] |

## Day 25

An exact global three-wire minimum cut replaces the path-removal heuristic and reuses one deduplicated primitive graph.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 7.967013 | — | — |
| Current | 8.606029 | 0.639017 | [-0.421396, 1.699429] |
