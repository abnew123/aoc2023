# Advent of Code 2023 performance

## Methodology and current full suite

All timings use the original personal input corpus with OpenJDK 23.0.1 on Apple silicon. The harness compiles the fixed pre-speed source and current source into separate empty classpaths, excludes one true-cold launch per side, then runs 10 separate JVM pairs in counterbalanced A/B order. Solver is the sum of the 25 `fullSolve` calls; main includes solver plus in-process harness work; startup is process launch through the child start marker; harness is main minus solver; wall is the complete child process.

Reproduce after compiling both trees with `java -Daoc.data.dir=<data-dir> -cp <current-cp> src.FreshJvmBenchmark --compare <previous-cp> <current-cp>`. The current state passes all 50 independent solves, all 25 combined solves, and independent/`fullSolve` equivalence.

| Metric | Pre-PR mean (ms) | Current mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|---:|
| wall | 546.175521 | 513.343017 | -32.832504 | [-44.206700, -21.458309] |
| main | 496.728075 | 465.832746 | -30.895330 | [-40.818077, -20.972582] |
| solver | 461.362517 | 430.768004 | -30.594512 | [-40.426446, -20.762578] |
| startup | 32.614475 | 29.978096 | -2.636379 | [-9.213872, 3.941114] |
| harness | 35.365559 | 35.064741 | -0.300817 | [-1.232026, 0.630392] |

Publication gate versus the preceding replay tip: summed solver 461.362517 ms → 430.768004 ms; delta -30.594512 ms, paired 95% CI [-40.426446, -20.762578] ms.

## Day 01

Unchanged from the pre-PR implementation. Current solver mean: 9.348342 ms.

## Day 02

Unchanged from the pre-PR implementation. Current solver mean: 3.775533 ms.

## Day 03

Unchanged from the pre-PR implementation. Current solver mean: 5.077062 ms.

## Day 04

Unchanged from the pre-PR implementation. Current solver mean: 5.912404 ms.

## Day 05

Unchanged from the pre-PR implementation. Current solver mean: 3.317458 ms.

## Day 06

Unchanged from the pre-PR implementation. Current solver mean: 2.351767 ms.

## Day 07

Unchanged from the pre-PR implementation. Current solver mean: 5.867904 ms.

## Day 08

Unchanged from the pre-PR implementation. Current solver mean: 15.372308 ms.

## Day 09

Unchanged from the pre-PR implementation. Current solver mean: 2.664967 ms.

## Day 10

Unchanged from the pre-PR implementation. Current solver mean: 11.573212 ms.

## Day 11

Unchanged from the pre-PR implementation. Current solver mean: 8.070329 ms.

## Day 12

Unchanged from the pre-PR implementation. Current solver mean: 8.805796 ms.

## Day 13

Unchanged from the pre-PR implementation. Current solver mean: 2.100600 ms.

## Day 14

Unchanged from the pre-PR implementation. Current solver mean: 22.178933 ms.

## Day 15

Unchanged from the pre-PR implementation. Current solver mean: 7.391996 ms.

## Day 16

Unchanged from the pre-PR implementation. Current solver mean: 100.521100 ms.

## Day 17

Unchanged from the pre-PR implementation. Current solver mean: 110.311438 ms.

## Day 18

Unchanged from the pre-PR implementation. Current solver mean: 1.663254 ms.

## Day 19

Unchanged from the pre-PR implementation. Current solver mean: 5.220200 ms.

## Day 20

Modules are parsed once into primitive IDs and arrays; one queue-driven pulse engine supplies the thousand-press count and cycle periods.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 36.403233 | — | — |
| Current | 11.330667 | -25.072567 | [-26.535352, -23.609781] |

## Day 21

Unchanged from the pre-PR implementation. Current solver mean: 7.072142 ms.

## Day 22

Unchanged from the pre-PR implementation. Current solver mean: 25.070346 ms.

## Day 23

Unchanged from the pre-PR implementation. Current solver mean: 33.861229 ms.

## Day 24

Unchanged from the pre-PR implementation. Current solver mean: 14.852417 ms.

## Day 25

Unchanged from the pre-PR implementation. Current solver mean: 7.056600 ms.
