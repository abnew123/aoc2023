# Advent of Code 2023 performance

## Methodology and current full suite

All timings use the original personal input corpus with OpenJDK 23.0.1 on Apple silicon. The harness compiles the fixed pre-speed source and current source into separate empty classpaths, excludes one true-cold launch per side, then runs 10 separate JVM pairs in counterbalanced A/B order. Solver is the sum of the 25 `fullSolve` calls; main includes solver plus in-process harness work; startup is process launch through the child start marker; harness is main minus solver; wall is the complete child process.

Reproduce after compiling both trees with `java -Daoc.data.dir=<data-dir> -cp <current-cp> src.FreshJvmBenchmark --compare <previous-cp> <current-cp>`. Every published state passes all 50 independent solves, all 25 combined solves, and independent/`fullSolve` equivalence.

Current 25-day means:

| Wall (ms) | Main (ms) | Solver (ms) | Startup (ms) | Harness (ms) |
|---:|---:|---:|---:|---:|
| 449.437329 | 396.852042 | 359.775317 | 34.623104 | 37.076725 |

Latest publication gate versus the preceding replay tip:

| Metric | Previous mean (ms) | Current mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|---:|
| wall | 501.142188 | 449.437329 | -51.704859 | [-66.486894, -36.922823] |
| main | 451.227696 | 396.852042 | -54.375654 | [-67.900450, -40.850859] |
| solver | 414.518221 | 359.775317 | -54.742905 | [-67.634057, -41.851752] |
| startup | 32.284438 | 34.623104 | 2.338667 | [-0.811143, 5.488476] |
| harness | 36.709475 | 37.076725 | 0.367251 | [-0.522046, 1.256547] |

## Day 01

Unchanged from the pre-PR implementation. Current solver mean: 9.405412 ms.

## Day 02

Unchanged from the pre-PR implementation. Current solver mean: 3.723892 ms.

## Day 03

Unchanged from the pre-PR implementation. Current solver mean: 5.109267 ms.

## Day 04

Unchanged from the pre-PR implementation. Current solver mean: 6.007217 ms.

## Day 05

Unchanged from the pre-PR implementation. Current solver mean: 2.965113 ms.

## Day 06

Unchanged from the pre-PR implementation. Current solver mean: 2.491629 ms.

## Day 07

Unchanged from the pre-PR implementation. Current solver mean: 6.099983 ms.

## Day 08

Unchanged from the pre-PR implementation. Current solver mean: 16.422763 ms.

## Day 09

Unchanged from the pre-PR implementation. Current solver mean: 3.223125 ms.

## Day 10

Unchanged from the pre-PR implementation. Current solver mean: 11.648288 ms.

## Day 11

Unchanged from the pre-PR implementation. Current solver mean: 8.847454 ms.

## Day 12

Unchanged from the pre-PR implementation. Current solver mean: 9.014617 ms.

## Day 13

Unchanged from the pre-PR implementation. Current solver mean: 2.156604 ms.

## Day 14

One mutable grid supplies the north load and billion-cycle result, with compact cycle detection and allocation-free directional shifts.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 21.278437 | — | — |
| Current | 18.039067 | -3.239371 | [-5.396386, -1.082355] |

## Day 15

Unchanged from the pre-PR implementation. Current solver mean: 6.881475 ms.

## Day 16

Stamped primitive beam states and a reusable traversal stack replace per-start object graphs and repeated visited allocations.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 88.265783 | — | — |
| Current | 23.601092 | -64.664692 | [-71.166871, -58.162512] |

## Day 17

Unchanged from the pre-PR implementation. Current solver mean: 107.486304 ms.

## Day 18

Unchanged from the pre-PR implementation. Current solver mean: 1.859988 ms.

## Day 19

Unchanged from the pre-PR implementation. Current solver mean: 6.169683 ms.

## Day 20

Modules are parsed once into primitive IDs and arrays; one queue-driven pulse engine supplies the thousand-press count and cycle periods.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 36.403233 | — | — |
| Current | 11.330667 | -25.072567 | [-26.535352, -23.609781] |

## Day 21

Unchanged from the pre-PR implementation. Current solver mean: 7.871475 ms.

## Day 22

Unchanged from the pre-PR implementation. Current solver mean: 28.806963 ms.

## Day 23

Unchanged from the pre-PR implementation. Current solver mean: 38.450629 ms.

## Day 24

Unchanged from the pre-PR implementation. Current solver mean: 14.100779 ms.

## Day 25

Unchanged from the pre-PR implementation. Current solver mean: 7.008296 ms.
