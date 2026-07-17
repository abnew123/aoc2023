# Performance

## Methodology and current full suite

- Baseline: normal Java solvers at pre-speed commit `98a7731647e246658055e85a765ab155b8a3847f`.
- Harness: `src.FreshJvmBenchmark`; one unmeasured cold launch, then ten separate JVMs per revision in alternating baseline/current order.
- Values below are means in milliseconds. Confidence intervals are paired two-sided 95% intervals for `current - baseline`.
- `solver` sums the 25 `fullSolve` calls. `startup` ends at the child start marker; `harness = main - solver`; `wall` is parent-observed process time.
- Correctness: all 50 expected answers passed, and all 25 `fullSolve` pairs equaled independent `solve` calls.
- Environment: OpenJDK 23.0.1, macOS arm64, 14 available processors.
- Reproduce on an idle machine with `java -Daoc.data.dir=data -cp <current-cp> src.FreshJvmBenchmark --compare <baseline-cp> <current-cp>`.

| Metric | Baseline | Current | Delta | 95% CI |
| --- | ---: | ---: | ---: | ---: |
| wall | 504.131 | 491.694 | -12.437 | [-22.856, -2.018] |
| main | 457.217 | 445.435 | -11.782 | [-21.391, -2.174] |
| solver | 422.238 | 410.729 | -11.509 | [-21.390, -1.628] |
| startup | 28.997 | 28.671 | -0.325 | [-1.904, 1.253] |
| harness | 34.979 | 34.705 | -0.273 | [-1.032, 0.485] |

The publication gate passes because the summed-solver interval is entirely below zero.

## Day 01

Unchanged source. Current solver mean: 9.031 ms.

## Day 02

Unchanged source. Current solver mean: 3.559 ms.

## Day 03

Unchanged source. Current solver mean: 4.925 ms.

## Day 04

Unchanged source. Current solver mean: 5.840 ms.

## Day 05

Unchanged source. Current solver mean: 3.239 ms.

## Day 06

Unchanged source. Current solver mean: 2.390 ms.

## Day 07

Token parsing, direct character-to-rank mapping, and two-largest-frequency classification replace regex splitting, rank scans, and frequency sorting.

| Baseline | Current | Delta | 95% CI |
| ---: | ---: | ---: | ---: |
| 5.705 | 3.542 | -2.163 | [-2.511, -1.814] |

## Day 08

Unchanged source. Current solver mean: 15.029 ms.

## Day 09

Unchanged source. Current solver mean: 3.131 ms.

## Day 10

A reciprocal bitmask loop walk replaces doubled-grid flood fill. Loop length gives Part 1; shoelace area and Pick's theorem give Part 2 for any valid start connectivity.

| Baseline | Current | Delta | 95% CI |
| ---: | ---: | ---: | ---: |
| 11.183 | 2.744 | -8.439 | [-8.603, -8.276] |

## Day 11

Row/column counts and prefix aggregates replace pairwise galaxy enumeration and repeated empty-line scans for both base distance and expansion crossings.

| Baseline | Current | Delta | 95% CI |
| ---: | ---: | ---: | ---: |
| 8.887 | 0.915 | -7.971 | [-9.260, -6.683] |

## Day 12

Unchanged source. Current solver mean: 10.818 ms.

## Day 13

Unchanged source. Current solver mean: 2.150 ms.

## Day 14

Unchanged source. Current solver mean: 20.292 ms.

## Day 15

Unchanged source. Current solver mean: 5.289 ms.

## Day 16

Unchanged source. Current solver mean: 83.227 ms.

## Day 17

Unchanged source. Current solver mean: 107.376 ms.

## Day 18

Unchanged source. Current solver mean: 1.658 ms.

## Day 19

Unchanged source. Current solver mean: 5.469 ms.

## Day 20

Unchanged source. Current solver mean: 34.033 ms.

## Day 21

Unchanged source. Current solver mean: 6.881 ms.

## Day 22

Unchanged source. Current solver mean: 24.128 ms.

## Day 23

Unchanged source. Current solver mean: 35.440 ms.

## Day 24

Exact rational geometry replaces integer-division validation: Part 1 uses checked-long arithmetic with a BigInteger fallback, while Part 2 solves the six rock variables and validates every future collision.

| Baseline | Current | Delta | 95% CI |
| ---: | ---: | ---: | ---: |
| 13.706 | 12.617 | -1.089 | [-2.046, -0.132] |

## Day 25

Unchanged source. Current solver mean: 7.005 ms.
