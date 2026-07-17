# Advent of Code 2023 performance

## Methodology and current full suite

All timings use the original personal input corpus with OpenJDK 23.0.1 on Apple silicon. The harness compiles the fixed pre-speed source and current source into separate empty classpaths, excludes one true-cold launch per side, then runs 10 separate JVM pairs in counterbalanced A/B order. Solver is the sum of the 25 `fullSolve` calls; main includes solver plus in-process harness work; startup is process launch through the child start marker; harness is main minus solver; wall is the complete child process.

Reproduce after compiling both trees with `java -Daoc.data.dir=<data-dir> -cp <current-cp> src.FreshJvmBenchmark --compare <previous-cp> <current-cp>`. Every published state passes all 50 independent solves, all 25 combined solves, and independent/`fullSolve` equivalence.

Current 25-day means:

| Wall (ms) | Main (ms) | Solver (ms) | Startup (ms) | Harness (ms) |
|---:|---:|---:|---:|---:|
| 437.233554 | 384.978163 | 347.205876 | 34.864458 | 37.772287 |

Latest publication gate versus the preceding replay tip:

| Metric | Previous mean (ms) | Current mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|---:|
| wall | 464.834413 | 437.233554 | -27.600858 | [-42.468238, -12.733479] |
| main | 409.469804 | 384.978163 | -24.491642 | [-34.471592, -14.511692] |
| solver | 371.300101 | 347.205876 | -24.094225 | [-33.306318, -14.882132] |
| startup | 37.697046 | 34.864458 | -2.832587 | [-11.213689, 5.548514] |
| harness | 38.169704 | 37.772287 | -0.397417 | [-1.583738, 0.788904] |

## Day 01

Unchanged from the pre-PR implementation. Current solver mean: 10.390383 ms.

## Day 02

Unchanged from the pre-PR implementation. Current solver mean: 3.802075 ms.

## Day 03

Unchanged from the pre-PR implementation. Current solver mean: 5.256867 ms.

## Day 04

Unchanged from the pre-PR implementation. Current solver mean: 6.183071 ms.

## Day 05

Unchanged from the pre-PR implementation. Current solver mean: 2.956225 ms.

## Day 06

Unchanged from the pre-PR implementation. Current solver mean: 2.514258 ms.

## Day 07

Unchanged from the pre-PR implementation. Current solver mean: 6.411317 ms.

## Day 08

Unchanged from the pre-PR implementation. Current solver mean: 16.434442 ms.

## Day 09

Unchanged from the pre-PR implementation. Current solver mean: 3.070929 ms.

## Day 10

Unchanged from the pre-PR implementation. Current solver mean: 11.754288 ms.

## Day 11

Unchanged from the pre-PR implementation. Current solver mean: 7.975554 ms.

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

Unchanged from the pre-PR implementation. Current solver mean: 6.980779 ms.

## Day 16

Stamped primitive beam states and a reusable traversal stack replace per-start object graphs and repeated visited allocations.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 88.265783 | — | — |
| Current | 23.601092 | -64.664692 | [-71.166871, -58.162512] |

## Day 17

Unchanged from the pre-PR implementation. Current solver mean: 119.245450 ms.

## Day 18

Unchanged from the pre-PR implementation. Current solver mean: 1.852325 ms.

## Day 19

Unchanged from the pre-PR implementation. Current solver mean: 6.330938 ms.

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

Unchanged from the pre-PR implementation. Current solver mean: 14.004554 ms.

## Day 25

Unchanged from the pre-PR implementation. Current solver mean: 7.523825 ms.
